#!/bin/sh

set -eu

ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
APP_RELEASE_DIR="$ROOT_DIR/app/build/outputs/apk/release"
TINKER_DIR="$ROOT_DIR/build/tinker"
APK_DIR="$ROOT_DIR/apk"
RELEASE_ARCHIVE_DIR="$APK_DIR/release"
BASELINE_DIR="$APK_DIR/base"
LATEST_BASELINE_APK="$BASELINE_DIR/latest-base.apk"
LEGACY_BASELINE_DIR="$TINKER_DIR/baselines"
LEGACY_LATEST_BASELINE_APK="$LEGACY_BASELINE_DIR/latest-base.apk"

usage() {
    cat <<'EOF'
用法:
    sh apk.sh newbase [版本号] [--force-pin-base]
    sh apk.sh rebuild [--pin-base] [--force-pin-base]
  sh apk.sh update [create_tinker_patch.sh 的参数]

命令:
    newbase   一键升级新基线：自动升版本号(versionCode+1, versionName patch+1 或用传入版本) -> 编全量 -> 固定为新基线
    rebuild   执行 ./gradlew assembleRelease，并把 APK 复制到 apk/release/（加 --pin-base 固定为基线，不升版本号）
    update    执行热更新补丁生成；如果没传 --old-apk，默认使用 apk/base/latest-base.apk

常用示例:
  sh apk.sh newbase                 # 自动 patch+1(如 0.1.4 -> 0.1.5) 出新基线
  sh apk.sh newbase 0.2.0           # 指定版本号出新基线
  sh apk.sh rebuild --pin-base      # 用当前版本号出基线(不升号)
  sh apk.sh update --skip-upload    # 基于最新基线出热更补丁

说明:
  1. 第一次立基线，建议执行 sh apk.sh rebuild --pin-base
    2. 同一个 versionName/TINKER_ID 下，--pin-base 默认不允许用不同 APK 静默覆盖旧基线
    3. 后续继续出 patch，直接执行 sh apk.sh update
  3. update 的其他参数会原样透传给 scripts/create_tinker_patch.sh
EOF
}

fail() {
    echo "错误: $*" >&2
    exit 1
}

detect_release_apk() {
    metadata_file="$APP_RELEASE_DIR/output-metadata.json"
    if [ -f "$metadata_file" ]; then
        output_name=$(grep -E '"outputFile"' "$metadata_file" | head -n 1 | sed -E 's/.*"outputFile"[[:space:]]*:[[:space:]]*"([^"]+)".*/\1/')
        if [ -n "$output_name" ] && [ -f "$APP_RELEASE_DIR/$output_name" ]; then
            printf '%s\n' "$APP_RELEASE_DIR/$output_name"
            return
        fi
    fi
    find "$APP_RELEASE_DIR" -maxdepth 1 -type f -name 'APP*.apk' | sort | tail -n 1
}

detect_version_name() {
    sed -nE 's/^[[:space:]]*def[[:space:]]+appVersionName[[:space:]]*=[[:space:]]*"([^"]+)".*/\1/p' "$ROOT_DIR/app/build.gradle" | head -n 1
}

detect_version_code() {
    sed -nE 's/^[[:space:]]*def[[:space:]]+appVersionCode[[:space:]]*=[[:space:]]*([0-9]+).*/\1/p' "$ROOT_DIR/app/build.gradle" | head -n 1
}

# 升级 app/build.gradle 里的版本号：versionCode +1；versionName 用传入值或自动 patch +1。
bump_version() {
    target_name="$1"
    gradle_file="$ROOT_DIR/app/build.gradle"
    cur_code=$(detect_version_code)
    cur_name=$(detect_version_name)
    [ -n "$cur_code" ] || fail "读不到 appVersionCode"
    [ -n "$cur_name" ] || fail "读不到 appVersionName"
    new_code=$((cur_code + 1))
    if [ -n "$target_name" ]; then
        new_name="$target_name"
    else
        major=$(printf '%s' "$cur_name" | cut -d. -f1)
        minor=$(printf '%s' "$cur_name" | cut -d. -f2)
        patch=$(printf '%s' "$cur_name" | cut -d. -f3)
        [ -n "$patch" ] || patch=0
        new_name="$major.$minor.$((patch + 1))"
    fi
    tmp_gradle=$(mktemp)
    sed -E \
        -e "s/^([[:space:]]*def[[:space:]]+appVersionCode[[:space:]]*=[[:space:]]*)[0-9]+/\1$new_code/" \
        -e "s/^([[:space:]]*def[[:space:]]+appVersionName[[:space:]]*=[[:space:]]*\")[^\"]+(\")/\1$new_name\2/" \
        "$gradle_file" > "$tmp_gradle"
    mv "$tmp_gradle" "$gradle_file"
    if [ "$(detect_version_name)" != "$new_name" ] || [ "$(detect_version_code)" != "$new_code" ]; then
        fail "版本号写回失败，请检查 app/build.gradle"
    fi
    echo "版本号已升级: versionCode $cur_code -> $new_code / versionName $cur_name -> $new_name"
}

archive_release_apk() {
    source_apk="$1"
    mkdir -p "$RELEASE_ARCHIVE_DIR"
    archived_apk="$RELEASE_ARCHIVE_DIR/$(basename "$source_apk")"
    cp -f "$source_apk" "$archived_apk"
    printf '%s\n' "$archived_apk"
}

pin_baseline_apk() {
    source_apk="$1"
    force_pin_base="$2"
    version_name=$(detect_version_name)
    if [ -z "$version_name" ]; then
        version_name="manual"
    fi
    mkdir -p "$BASELINE_DIR"
    baseline_apk="$BASELINE_DIR/base-$version_name.apk"
    if [ -f "$baseline_apk" ] && ! cmp -s "$source_apk" "$baseline_apk"; then
        if [ "$force_pin_base" -ne 1 ]; then
            fail "检测到同一 versionName=$version_name 已存在不同内容的基线: $baseline_apk\n这会让 latest-base.apk 漂移，后续 hotfix 容易打错基线。\n如果设备确实要切到这版完整包，请先安装新完整包后再执行 sh apk.sh rebuild --pin-base --force-pin-base"
        fi
    fi
    cp -f "$source_apk" "$baseline_apk"
    cp -f "$baseline_apk" "$LATEST_BASELINE_APK"
    printf '%s\n' "$baseline_apk"
}

find_default_baseline_apk() {
    if [ -f "$LATEST_BASELINE_APK" ]; then
        printf '%s\n' "$LATEST_BASELINE_APK"
        return
    fi
    if [ -f "$LEGACY_LATEST_BASELINE_APK" ]; then
        printf '%s\n' "$LEGACY_LATEST_BASELINE_APK"
        return
    fi
    find "$BASELINE_DIR" -maxdepth 1 -type f -name 'base-*.apk' | sort | tail -n 1
}

has_old_apk_arg() {
    for arg in "$@"; do
        if [ "$arg" = "--old-apk" ]; then
            return 0
        fi
    done
    return 1
}

run_rebuild() {
    pin_base=0
    force_pin_base=0
    while [ "$#" -gt 0 ]; do
        case "$1" in
            --pin-base)
                pin_base=1
                ;;
            --force-pin-base)
                force_pin_base=1
                ;;
            --help|-h)
                usage
                exit 0
                ;;
            *)
                fail "rebuild 不支持参数: $1"
                ;;
        esac
        shift
    done

    (
        cd "$ROOT_DIR"
        ./gradlew assembleRelease
    )

    release_apk=$(detect_release_apk)
    [ -n "$release_apk" ] || fail "assembleRelease 完成后未找到 release APK"
    [ -f "$release_apk" ] || fail "release APK 不存在: $release_apk"

    archived_apk=$(archive_release_apk "$release_apk")
    echo "rebuild 完成"
    echo "release apk: $release_apk"
    echo "archive apk: $archived_apk"

    if [ "$pin_base" -eq 1 ]; then
        baseline_apk=$(pin_baseline_apk "$archived_apk" "$force_pin_base")
        echo "baseline apk: $baseline_apk"
        echo "latest base:  $LATEST_BASELINE_APK"
    fi
}

# 一键升级新基线：升版本号 -> 编全量 -> 固定为新基线。
run_newbase() {
    target_name=""
    force_pin_base=0
    while [ "$#" -gt 0 ]; do
        case "$1" in
            --version)
                shift
                target_name="${1:-}"
                [ -n "$target_name" ] || fail "--version 需要一个版本号，例如 --version 0.2.0"
                ;;
            --force-pin-base)
                force_pin_base=1
                ;;
            --help|-h)
                usage
                exit 0
                ;;
            [0-9]*.[0-9]*)
                target_name="$1"
                ;;
            *)
                fail "newbase 不支持参数: $1"
                ;;
        esac
        shift
    done

    bump_version "$target_name"
    if [ "$force_pin_base" -eq 1 ]; then
        run_rebuild --pin-base --force-pin-base
    else
        run_rebuild --pin-base
    fi
    new_name=$(detect_version_name)
    echo ""
    echo "新基线完成 ✅ 版本 $new_name"
    echo "  - 把上面的 baseline/archive 全量包装到设备（一次）。"
    echo "  - 之后小改用: sh apk.sh update  出热更补丁。"
    echo "  - 提醒: 建议把这次版本号改动 + 代码改动一起提交，让基线对应一个确定的 git commit。"
}

run_update() {
    if has_old_apk_arg "$@"; then
        :
    else
        baseline_apk=$(find_default_baseline_apk)
        [ -n "$baseline_apk" ] || fail "update 缺少基线包；先执行 sh apk.sh rebuild --pin-base，或显式传 --old-apk"
        [ -f "$baseline_apk" ] || fail "默认基线包不存在: $baseline_apk"
        set -- --old-apk "$baseline_apk" "$@"
    fi

    (
        cd "$ROOT_DIR"
        bash ./scripts/create_tinker_patch.sh "$@"
    )
}

command_name="${1:-}"
if [ -z "$command_name" ]; then
    usage
    exit 1
fi
shift

case "$command_name" in
    rebuild)
        run_rebuild "$@"
        ;;
    update)
        run_update "$@"
        ;;
    newbase)
        run_newbase "$@"
        ;;
    help|--help|-h)
        usage
        ;;
    *)
        fail "未知命令: $command_name"
        ;;
esac
