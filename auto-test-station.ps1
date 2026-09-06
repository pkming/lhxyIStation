# 自动报站测试脚本
# 生成时间: 2026-09-01
# 说明: 模拟点击报站按钮，测试busNo修复是否生效

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  自动报站测试脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 等待APP完全加载
Write-Host "[准备] 等待APP加载..." -ForegroundColor Yellow
Start-Sleep -Seconds 3

# 第一次报站（站序1预报）
Write-Host ""
Write-Host "[1/2] 第一次点击 - 站序1预报..." -ForegroundColor Yellow
adb shell input tap 540 960
Start-Sleep -Seconds 3
Write-Host "✓ 完成" -ForegroundColor Green

# 第二次报站（站序1到站）
Write-Host ""
Write-Host "[2/2] 第二次点击 - 站序1到站..." -ForegroundColor Yellow
adb shell input tap 540 960
Start-Sleep -Seconds 3
Write-Host "✓ 完成" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  测试完成！" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "请检查：" -ForegroundColor White
Write-Host "1. 网页车辆是否从起点推进到1.5再到2" -ForegroundColor White
Write-Host "2. 日志文件: g:\lhxyproduct\lhxyIStation\logs\realtime-test.txt" -ForegroundColor White
