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
  sh apk.sh rebuild [--pin-base]
  sh apk.sh update [create_tinker_patch.sh 的参数]

命令:
    rebuild   执行 ./gradlew assembleRelease，并把 APK 复制到 apk/release/
    update    执行热更新补丁生成；如果没传 --old-apk，默认使用 apk/base/latest-base.apk

常用示例:
  sh apk.sh rebuild
  sh apk.sh rebuild --pin-base
  sh apk.sh update --skip-upload
  sh apk.sh update --old-apk /path/to/base.apk --skip-upload

说明:
  1. 第一次立基线，建议执行 sh apk.sh rebuild --pin-base
  2. 后续继续出 patch，直接执行 sh apk.sh update
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

archive_release_apk() {
    source_apk="$1"
    mkdir -p "$RELEASE_ARCHIVE_DIR"
    archived_apk="$RELEASE_ARCHIVE_DIR/$(basename "$source_apk")"
    cp -f "$source_apk" "$archived_apk"
    printf '%s\n' "$archived_apk"
}

pin_baseline_apk() {
    source_apk="$1"
    version_name=$(detect_version_name)
    if [ -z "$version_name" ]; then
        version_name="manual"
    fi
    mkdir -p "$BASELINE_DIR"
    baseline_apk="$BASELINE_DIR/base-$version_name.apk"
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
    while [ "$#" -gt 0 ]; do
        case "$1" in
            --pin-base)
                pin_base=1
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
        baseline_apk=$(pin_baseline_apk "$archived_apk")
        echo "baseline apk: $baseline_apk"
        echo "latest base:  $LATEST_BASELINE_APK"
    fi
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
    help|--help|-h)
        usage
        ;;
    *)
        fail "未知命令: $command_name"
        ;;
esac