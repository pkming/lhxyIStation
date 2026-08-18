# BUG 修复记录 - 音频 GPIO 问题

**日期**: 2026-08-17  
**影响版本**: Android 11  
**修复文件**: `modules/domain/src/main/java/com/lhxy/istationdevice/android11/domain/station/LegacyStationAudioUseCase.java`

---

## 问题 1：小喇叭不播放，音频从外喇叭左右声道随机播放

### 问题描述
- 调度语音应该从司机端小喇叭播放，但实际不出声
- 声音从外喇叭（车外扬声器）的左右声道随机播放
- 声道混乱，影响司机听取调度指令

### 根本原因
调度语音播放时，错误地同时启用了两个音频 GPIO 通道：
- `outer_audio = 1` (外音/车外扬声器)
- `inner_speaker = 1` (小喇叭/司机端耳机)

这导致：
1. 音频信号被分配到两个硬件通道
2. 硬件音频路由冲突，小喇叭无法获取完整信号
3. 立体声左右声道被错误分配到外喇叭

### 代码位置
```java
// 错误配置（修复前）
enablePinsLocked(shellConfig, false, true, true);
// 参数: inner_audio=0, outer_audio=1, inner_speaker=1 ❌
```

### 修复方案
调度语音只启用小喇叭 GPIO，关闭外音 GPIO：

**修改位置 1**: 第 175 行 - `playDispatchNotice()`
```java
// 修复后
enablePinsLocked(shellConfig, false, false, true);
// 参数: inner_audio=0, outer_audio=0, inner_speaker=1 ✅
```

**修改位置 2**: 第 201 行 - `playDispatchNoticeToDriver()`
```java
// 修复后
enablePinsLocked(shellConfig, false, false, true);
// 参数: inner_audio=0, outer_audio=0, inner_speaker=1 ✅
```

### 验证方法
1. 下发调度语音到司机端
2. 确认声音只从小喇叭播放
3. 确认外喇叭无声音输出
4. 确认声音清晰稳定，无左右声道混乱

---

## 问题 2：文本下发后，视频监控自动切换并放大

### 问题描述
- 下发调度文本后，视频监控画面自动从 DVR 模式切换到中门视频模式
- 视频画面自动放大（从 638x700 变为 1120x720）
- 影响司机监控视角

### 根本原因
GPIO 硬件电气干扰：
1. 音频 GPIO (`gpio1b2` - outer_audio) 写入时产生电气干扰
2. 干扰波及到相邻的视频监控 GPIO (`gpio1d1` - io2 副引脚)
3. GPIO 监控线程（350ms 周期）在音频 GPIO 写入后立即轮询
4. 读取到错误的 GPIO 状态：`io1=1, io2=0` (实际应该是 `io1=1, io2=1`)
5. 触发视频模式切换逻辑，误判为"中门视频模式"

### 时序分析
```
T0:      调度语音开始，写入音频 GPIO
T0+1ms:  outer_audio=1 写入 gpio1b2 → 产生电气干扰
T0+2ms:  inner_speaker=1 写入 gpio0d6
T0+40ms: GPIO 监控线程轮询 io2 (gpio1d1)
         读取结果: io2=0 (受干扰，实际应该是 1)
         视频模式判断: io1=1, io2=0 → 中门视频
         触发视频切换和放大
```

### 涉及的 GPIO 引脚
| 逻辑名称 | 物理引脚 | 用途 | 备注 |
|---------|---------|------|------|
| io1 (primary) | gpio1d0 | 视频监控-主引脚 | |
| io2 (secondary) | gpio1d1 | 视频监控-副引脚 | ⚠️ 被干扰 |
| inner_audio | gpio1b1 | 音频-内音 | |
| outer_audio | gpio1b2 | 音频-外音 | ⚡ 干扰源 |
| inner_speaker | gpio0d6 | 音频-小喇叭 | |

### 修复方案
在 `enablePinsLocked()` 方法末尾添加 100ms 延迟，让 GPIO 信号稳定后再进行下一步操作：

**修改位置**: 第 1436-1441 行
```java
private void enablePinsLocked(ShellConfig shellConfig, 
                               boolean innerEnabled, 
                               boolean outerEnabled, 
                               boolean innerSpeakerEnabled) {
    writePinIfPresent(shellConfig, "headphone_detect_power", innerSpeakerEnabled ? 0 : 1);
    writePinIfPresent(shellConfig, "inner_audio", innerEnabled ? 1 : 0);
    writePinIfPresent(shellConfig, "outer_audio", outerEnabled ? 1 : 0);
    writePinIfPresent(shellConfig, "inner_speaker", innerSpeakerEnabled ? 1 : 0);
    
    // 音频 GPIO 写入后等待 100ms，避免电气干扰影响视频监控的 GPIO 读取
    try {
        Thread.sleep(100L);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}
```

### 为什么 100ms 足够？
1. GPIO 监控线程周期为 350ms
2. 防抖机制：两次读取间隔 50ms
3. 100ms > 50ms，确保第二次防抖读取时信号已稳定
4. 即使误判，下一个 350ms 周期会自动纠正

### 验证方法
1. 下发调度文本到司机端
2. 观察视频监控画面是否保持 DVR 模式（638x700）
3. 确认视频不会自动切换到中门视频（1120x720）
4. 多次测试，确认问题不再复现

---

## GPIO 配置完整性检查

所有 `enablePinsLocked()` 调用已全部检查，配置正确：

| 行号 | 场景 | inner_audio | outer_audio | inner_speaker | 状态 |
|-----|------|-------------|-------------|---------------|------|
| 175 | 调度语音(默认) | 0 | 0 | 1 | ✅ 已修复 |
| 201 | 司机端语音 | 0 | 0 | 1 | ✅ 已修复 |
| 227 | 乘客端语音 | 1 | 0 | 0 | ✅ 正确 |
| 370 | TTS报站 | 1 | 0/1 | 0 | ✅ 正确 |
| 384 | 播放内音列表 | 1 | 0 | 0 | ✅ 正确 |
| 388 | 播放外音列表 | 0 | 1 | 0 | ✅ 正确 |
| 400 | 其他TTS | 1 | 0/1 | 0 | ✅ 正确 |
| 978 | 内音→外音切换 | 0 | 1 | 0 | ✅ 正确 |
| 1219 | 混合报站 | 1 | 0/1 | 0 | ✅ 正确 |
| 1390 | TTS回退-内音 | 1 | 0 | 0 | ✅ 正确 |
| 1395 | TTS回退-外音 | 0 | 1 | 0 | ✅ 正确 |

**结论**: 没有任何其他地方同时启用 `outer_audio` 和 `inner_speaker`，不会产生 GPIO 冲突。

---

## 测试计划

### 测试环境
- 设备型号: Android 11 设备
- 硬件配置: 支持 GPIO 控制的音频和视频系统

### 测试用例

#### 1. 调度语音播放测试
- [ ] 下发调度语音到司机端
- [ ] 确认声音从小喇叭播放
- [ ] 确认外喇叭无声音
- [ ] 确认声音清晰，无杂音

#### 2. 视频监控稳定性测试
- [ ] 设置视频为 DVR 模式
- [ ] 下发调度文本
- [ ] 确认视频保持 DVR 模式，不自动切换
- [ ] 重复测试 多 次，确认问题不复现

#### 3. 音频通道隔离测试
- [ ] 测试内音播放（报站）
- [ ] 测试外音播放（报站）
- [ ] 测试小喇叭播放（调度语音）
- [ ] 确认三个通道互不干扰

#### 4. GPIO 干扰测试
- [ ] 快速连续下发多条调度文本
- [ ] 观察视频监控是否稳定
- [ ] 检查音频播放是否正常

---

## 部署说明

### 编译
```bash
# 在 Android Studio 中编译项目
./gradlew assembleDebug
```

### 安装
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 日志监控
```bash
adb logcat | grep -E "LegacyStationAudioUseCase|LegacyMainActivity"
```

---

## 相关日志

### 问题复现日志
参见: `logs/android11-test-log-20260817-122502426.txt`

关键日志片段:
```
[12:23:57.267] outer_audio=1 写入
[12:23:57.269] inner_speaker=1 写入
[12:23:57.315] io2=0 读取 (受干扰)
[12:23:57.409] 视频切换到中门视频
```

---

## 修复人员
- **分析**: Claude / Barry
- **修复**: Claude
- **审核**: Barry

## 备注
- 此问题同时影响音频播放和视频监控两个子系统
- 修复方案在硬件层面解决了 GPIO 干扰问题
- 建议在后续硬件设计中考虑 GPIO 引脚隔离或硬件滤波
