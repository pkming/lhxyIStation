#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
APP_DIR="$ROOT_DIR/app"
SIGNING_FILE="$ROOT_DIR/config/signing.local.properties"
OSS_LOCAL_CONFIG_FILE="$ROOT_DIR/config/oss-config.local.properties"
OSS_TEMPLATE_CONFIG_FILE="$ROOT_DIR/config/oss-config.template.properties"
TOOLS_DIR="$ROOT_DIR/build/tinker"
HOTFIX_DIR="$ROOT_DIR/build/hotfix"
APK_INPUTS_DIR="$TOOLS_DIR/apk-inputs"
CLI_VERSION="1.9.15.2"
CLI_JAR_DEFAULT="$TOOLS_DIR/tinker-patch-cli-${CLI_VERSION}.jar"
CLI_URL_DEFAULT="https://github.com/Tencent/tinker/releases/download/v${CLI_VERSION}/tinker-patch-cli-${CLI_VERSION}.jar"
DEFAULT_HOTFIX_ROOT="hotfix/android11"
DEFAULT_MANIFEST_OBJECT_KEY="${DEFAULT_HOTFIX_ROOT}/manifest.json"
DEFAULT_RELEASES_PREFIX="${DEFAULT_HOTFIX_ROOT}/releases"
PROXY_SAFE_JAVA_TOOL_OPTIONS="-Dhttp.proxyHost= -Dhttp.proxyPort=0 -Dhttps.proxyHost= -Dhttps.proxyPort=0 -DsocksProxyHost= -DsocksProxyPort=0 -Djava.net.useSystemProxies=false"

OLD_APK=""
NEW_APK=""
OUTPUT_DIR=""
PATCH_VERSION=""
PATCH_OBJECT_KEY=""
MANIFEST_OBJECT_KEY="$DEFAULT_MANIFEST_OBJECT_KEY"
ARCHIVE_MANIFEST_OBJECT_KEY=""
INDEX_OBJECT_KEY=""
CLI_JAR="$CLI_JAR_DEFAULT"
CLI_URL="$CLI_URL_DEFAULT"
TARGET_VERSION_CODE=""
TARGET_VERSION_NAME=""
TINKER_ID=""
RELEASE_NOTES=""
SKIP_BUILD=0
SKIP_UPLOAD=0
OSS_CONFIG_FILE=""
OSS_BUCKET=""
OSS_ENDPOINT=""
OSS_ACCESS_KEY_ID=""
OSS_ACCESS_KEY_SECRET=""
OSS_SECURE="true"
IGNORE_CHANGE_WARNING=""

usage() {
    cat <<'EOF'
用法:
  ./scripts/create_tinker_patch.sh --old-apk /path/to/base.apk [选项]

默认行为:
  1. 如果没有传 --new-apk, 自动执行 :app:assembleRelease
  2. 自动下载 tinker-patch-cli 到 build/tinker/
    3. 自动把 old/new apk 快照到 build/tinker/apk-inputs/，避免 release 目录互相覆盖
    4. 自动按 releases/<tinkerId>/<patchVersion>/ 规划版本目录
    5. 在 build/hotfix/<patchVersion>/ 生成 patch 和 manifest.json
    6. 先上传归档产物，最后覆盖线上 manifest 指针

参数:
  --old-apk PATH              基线 APK, 必填
  --new-apk PATH              新 APK, 不传则自动打 release
  --skip-build                与 --new-apk 配套, 跳过 assembleRelease
    --patch-version VALUE       补丁版本号, 不传时自动递增为 <tinkerId>-pNNN
  --output-dir DIR            输出目录, 默认 build/hotfix/<patchVersion>
  --patch-object-key KEY      写入 manifest.json 的 patchObjectKey
  --manifest-object-key KEY   设备端读取的 manifest object key, 默认 hotfix/android11/manifest.json
  --target-version-code NUM   覆盖 manifest.json 的 targetVersionCode
  --target-version-name NAME  覆盖 manifest.json 的 targetVersionName
  --tinker-id VALUE           覆盖 CLI 配置中的 TINKER_ID
    --ignore-change-warning P   透传到 Tinker 的 ignoreChangeWarning，例如 res/RD.xml
  --release-notes TEXT        写入 manifest.json 的 releaseNotes
    --skip-upload               只生成本地 patch, 不上传 OSS
    --oss-config PATH           指定 OSS 配置文件, 默认优先 config/oss-config.local.properties
  --cli-jar PATH              指定本地 tinker-patch-cli.jar
  --cli-url URL               覆盖默认下载地址
  --help                      显示帮助

示例:
  ./scripts/create_tinker_patch.sh --old-apk ~/Desktop/base.apk
  ./scripts/create_tinker_patch.sh --old-apk ~/Desktop/base.apk --new-apk app/build/outputs/apk/release/APP20260514155358.apk --skip-build
    ./scripts/create_tinker_patch.sh --old-apk ~/Desktop/base.apk --ignore-change-warning res/RD.xml --skip-upload
    ./scripts/create_tinker_patch.sh --old-apk ~/Desktop/base.apk --skip-upload

注意:
  设备端当前固定读 hotfix/android11/manifest.json。
  基线包必须是补过 TINKER_ID 之后重新构建并安装的 release 包，否则补丁无法正确匹配。
    默认会读取 OSS 配置并自动上传；如果只想本地生成，显式加 --skip-upload。
    连续生成多个补丁时，--old-apk 仍然必须指向设备最初安装的那版完整基线包，不能改成上一次补丁对应的新 APK。
EOF
}

fail() {
    echo "错误: $*" >&2
    exit 1
}

prop_value() {
    local key="$1"
    local file="$2"
    local line
    line="$(grep -E "^${key}=" "$file" | head -n 1 || true)"
    if [[ -z "$line" ]]; then
        echo ""
        return
    fi
    printf '%s' "${line#*=}" | tr -d '\r'
}

make_absolute_path() {
    local path="$1"
    if [[ "$path" = /* ]]; then
        echo "$path"
        return
    fi
    echo "$(cd "$ROOT_DIR" && cd "$(dirname "$path")" && pwd)/$(basename "$path")"
}

archive_input_apk() {
    local source_file="$1"
    local role="$2"
    local target_dir="$APK_INPUTS_DIR/$role"
    local base_name
    local timestamp
    local archived_file
    base_name="$(basename "$source_file")"
    timestamp="$(date +%Y%m%d%H%M%S)"
    mkdir -p "$target_dir"
    archived_file="$target_dir/${timestamp}-$$-${base_name}"
    cp -f "$source_file" "$archived_file"
    echo "$archived_file"
}

xml_escape() {
    local value="$1"
    value="${value//&/&amp;}"
    value="${value//</&lt;}"
    value="${value//>/&gt;}"
    value="${value//\"/&quot;}"
    echo "$value"
}

json_escape() {
    local value="$1"
    value="${value//\\/\\\\}"
    value="${value//\"/\\\"}"
    value="${value//$'\n'/\\n}"
    value="${value//$'\r'/}"
    echo "$value"
}

strip_scheme() {
    local value="$1"
    if [[ "$value" == https://* ]]; then
        echo "${value#https://}"
        return
    fi
    if [[ "$value" == http://* ]]; then
        echo "${value#http://}"
        return
    fi
    echo "$value"
}

normalize_object_key() {
    local value="$1"
    while [[ "$value" == /* ]]; do
        value="${value#/}"
    done
    echo "$value"
}

append_java_tool_options() {
    if [[ -n "${JAVA_TOOL_OPTIONS:-}" ]]; then
        echo "${JAVA_TOOL_OPTIONS} ${PROXY_SAFE_JAVA_TOOL_OPTIONS}"
        return
    fi
    echo "$PROXY_SAFE_JAVA_TOOL_OPTIONS"
}

run_gradle() {
    local java_tool_options
    java_tool_options="$(append_java_tool_options)"
    (
        cd "$ROOT_DIR"
        env JAVA_TOOL_OPTIONS="$java_tool_options" ./gradlew "$@"
    )
}

run_java() {
    local java_tool_options
    java_tool_options="$(append_java_tool_options)"
    env JAVA_TOOL_OPTIONS="$java_tool_options" java "$@"
}

resolve_oss_config_file() {
    if [[ -n "$OSS_CONFIG_FILE" ]]; then
        echo "$OSS_CONFIG_FILE"
        return
    fi
    if [[ -f "$OSS_LOCAL_CONFIG_FILE" ]]; then
        echo "$OSS_LOCAL_CONFIG_FILE"
        return
    fi
    if [[ -f "$OSS_TEMPLATE_CONFIG_FILE" ]]; then
        echo "$OSS_TEMPLATE_CONFIG_FILE"
        return
    fi
    echo ""
}

resolve_oss_endpoint() {
    local config_file="$1"
    local endpoint
    local region
    endpoint="$(strip_scheme "$(prop_value endpoint "$config_file")")"
    if [[ -n "$endpoint" ]]; then
        echo "$endpoint"
        return
    fi
    region="$(strip_scheme "$(prop_value region "$config_file")")"
    if [[ -n "$region" ]]; then
        echo "${region}.aliyuncs.com"
        return
    fi
    echo ""
}

load_oss_upload_config() {
    local config_file
    config_file="$(resolve_oss_config_file)"
    [[ -n "$config_file" ]] || fail "缺少 OSS 配置文件，无法自动上传；可改用 --skip-upload"
    [[ -f "$config_file" ]] || fail "OSS 配置文件不存在: $config_file"
    OSS_CONFIG_FILE="$config_file"
    OSS_BUCKET="$(prop_value bucket "$config_file")"
    OSS_ENDPOINT="$(resolve_oss_endpoint "$config_file")"
    OSS_ACCESS_KEY_ID="$(prop_value accessKeyId "$config_file")"
    OSS_ACCESS_KEY_SECRET="$(prop_value accessKeySecret "$config_file")"
    OSS_SECURE="$(prop_value secure "$config_file")"
    [[ -n "$OSS_BUCKET" ]] || fail "OSS 配置缺少 bucket；可改用 --skip-upload"
    [[ -n "$OSS_ENDPOINT" ]] || fail "OSS 配置缺少 endpoint/region；可改用 --skip-upload"
    [[ -n "$OSS_ACCESS_KEY_ID" ]] || fail "OSS 配置缺少 accessKeyId；可改用 --skip-upload"
    [[ -n "$OSS_ACCESS_KEY_SECRET" ]] || fail "OSS 配置缺少 accessKeySecret；可改用 --skip-upload"
    if [[ -z "$OSS_SECURE" ]]; then
        OSS_SECURE="true"
    fi
}

format_rfc1123_date() {
    LC_ALL=C TZ=GMT date '+%a, %d %b %Y %H:%M:%S GMT'
}

build_oss_authorization() {
    local method="$1"
    local content_type="$2"
    local date_header="$3"
    local object_key="$4"
    local canonical_resource="/${OSS_BUCKET}/${object_key}"
    local string_to_sign="${method}\n\n${content_type}\n${date_header}\n${canonical_resource}"
    printf '%b' "$string_to_sign" \
        | openssl dgst -sha1 -hmac "$OSS_ACCESS_KEY_SECRET" -binary \
        | openssl base64 -A \
        | awk -v key="$OSS_ACCESS_KEY_ID" '{print "OSS " key ":" $0}'
}

build_oss_url() {
    local object_key="$1"
    local normalized_object_key
    local scheme
    normalized_object_key="$(normalize_object_key "$object_key")"
    if [[ "$OSS_SECURE" == "false" ]]; then
        scheme="http"
    else
        scheme="https"
    fi
    echo "${scheme}://${OSS_BUCKET}.${OSS_ENDPOINT}/${normalized_object_key}"
}

fetch_oss_text() {
    local object_key="$1"
    local normalized_object_key
    local date_header
    local authorization
    local target_url
    local temp_file
    local status_code
    normalized_object_key="$(normalize_object_key "$object_key")"
    date_header="$(format_rfc1123_date)"
    authorization="$(build_oss_authorization "GET" "" "$date_header" "$normalized_object_key")"
    target_url="$(build_oss_url "$normalized_object_key")"
    temp_file="$TOOLS_DIR/oss-fetch-$$.tmp"
    status_code="$(curl --silent --show-error -o "$temp_file" -w '%{http_code}' \
        -X GET \
        -H "Date: ${date_header}" \
        -H "Authorization: ${authorization}" \
        "$target_url")"
    if [[ "$status_code" == "200" ]]; then
        cat "$temp_file"
        rm -f "$temp_file"
        return 0
    fi
    rm -f "$temp_file"
    if [[ "$status_code" == "404" ]]; then
        return 1
    fi
    fail "读取 OSS 对象失败 status=${status_code} / objectKey=${normalized_object_key}"
}

resolve_next_patch_sequence() {
    local index_payload="$1"
    local latest_sequence
    latest_sequence="$(printf '%s\n' "$index_payload" | sed -nE 's/.*"latestSequence"[[:space:]]*:[[:space:]]*([0-9]+).*/\1/p' | head -n 1)"
    if [[ -z "$latest_sequence" ]]; then
        echo "1"
        return
    fi
    echo $((latest_sequence + 1))
}

derive_patch_version() {
    local index_payload=""
    local next_sequence
    if [[ -n "$PATCH_VERSION" ]]; then
        return
    fi
    if [[ "$SKIP_UPLOAD" -eq 0 ]]; then
        if index_payload="$(fetch_oss_text "$INDEX_OBJECT_KEY" 2>/dev/null)"; then
            next_sequence="$(resolve_next_patch_sequence "$index_payload")"
        else
            next_sequence="1"
        fi
        PATCH_VERSION="$(printf '%s-p%03d' "$TINKER_ID" "$next_sequence")"
        return
    fi
    PATCH_VERSION="${TINKER_ID}-local-$(date +%Y%m%d%H%M%S)"
}

generate_index_file() {
    local index_file="$1"
    local latest_sequence="$2"
    local updated_at
    updated_at="$(TZ=GMT date '+%Y-%m-%dT%H:%M:%SZ')"
    cat > "$index_file" <<EOF
{
  "tinkerId": "$(json_escape "$TINKER_ID")",
  "targetVersionCode": ${TARGET_VERSION_CODE},
  "targetVersionName": "$(json_escape "$TARGET_VERSION_NAME")",
  "latestSequence": ${latest_sequence},
  "latestPatchVersion": "$(json_escape "$PATCH_VERSION")",
  "latestPatchObjectKey": "$(json_escape "$PATCH_OBJECT_KEY")",
  "latestManifestObjectKey": "$(json_escape "$ARCHIVE_MANIFEST_OBJECT_KEY")",
  "updatedAt": "${updated_at}"
}
EOF
}

upload_to_oss() {
    local source_file="$1"
    local object_key="$2"
    local content_type="$3"
    local normalized_object_key
    local date_header
    local authorization
    local target_url
    [[ -f "$source_file" ]] || fail "待上传文件不存在: $source_file"
    command -v curl >/dev/null 2>&1 || fail "缺少 curl, 无法上传 OSS"
    command -v openssl >/dev/null 2>&1 || fail "缺少 openssl, 无法生成 OSS 签名"
    normalized_object_key="$(normalize_object_key "$object_key")"
    date_header="$(format_rfc1123_date)"
    authorization="$(build_oss_authorization "PUT" "$content_type" "$date_header" "$normalized_object_key")"
    target_url="$(build_oss_url "$normalized_object_key")"
    echo "上传 OSS: ${normalized_object_key}"
    curl --fail --silent --show-error \
        -X PUT \
        -T "$source_file" \
        -H "Date: ${date_header}" \
        -H "Content-Type: ${content_type}" \
        -H "Authorization: ${authorization}" \
        "$target_url" >/dev/null
}

detect_release_apk() {
    local metadata_file="$APP_DIR/build/outputs/apk/release/output-metadata.json"
    if [[ -f "$metadata_file" ]]; then
        local output_name
        output_name="$(grep -E '"outputFile"' "$metadata_file" | head -n 1 | sed -E 's/.*"outputFile"[[:space:]]*:[[:space:]]*"([^"]+)".*/\1/')"
        if [[ -n "$output_name" && -f "$APP_DIR/build/outputs/apk/release/$output_name" ]]; then
            echo "$APP_DIR/build/outputs/apk/release/$output_name"
            return
        fi
    fi
    find "$APP_DIR/build/outputs/apk/release" -maxdepth 1 -type f -name 'APP*.apk' | sort | tail -n 1
}

detect_string_value() {
    local build_file="$APP_DIR/build.gradle"
    local key="$1"
    local value
    value="$(sed -nE "s/^[[:space:]]*def[[:space:]]+${key}[[:space:]]*=[[:space:]]*\"([^\"]+)\".*/\1/p" "$build_file" | head -n 1)"
    if [[ -n "$value" ]]; then
        echo "$value"
        return
    fi
    sed -nE "s/^[[:space:]]*${key}[[:space:]]+\"([^\"]+)\".*/\1/p" "$build_file" | head -n 1
}

detect_number_value() {
    local build_file="$APP_DIR/build.gradle"
    local key="$1"
    local value
    value="$(sed -nE "s/^[[:space:]]*def[[:space:]]+${key}[[:space:]]*=[[:space:]]*([0-9]+).*/\1/p" "$build_file" | head -n 1)"
    if [[ -n "$value" ]]; then
        echo "$value"
        return
    fi
    sed -nE "s/^[[:space:]]*${key}[[:space:]]+([0-9]+).*/\1/p" "$build_file" | head -n 1
}

compute_md5() {
    local file="$1"
    if command -v md5 >/dev/null 2>&1; then
        md5 -q "$file"
        return
    fi
    if command -v md5sum >/dev/null 2>&1; then
        md5sum "$file" | awk '{print $1}'
        return
    fi
    fail "当前机器没有 md5 或 md5sum"
}

ensure_cli() {
    mkdir -p "$TOOLS_DIR"
    if [[ -f "$CLI_JAR" ]]; then
        return
    fi
    command -v curl >/dev/null 2>&1 || fail "缺少 curl, 无法下载 tinker-patch-cli"
    echo "下载 tinker-patch-cli: $CLI_URL"
    curl -L --fail --output "$CLI_JAR" "$CLI_URL"
}

resolve_patch_output() {
    local output_dir="$1"
    local candidate
    for candidate in patch_signed_7zip.apk patch_signed.apk patch_unsigned.apk; do
        if [[ -f "$output_dir/$candidate" ]]; then
            echo "$output_dir/$candidate"
            return
        fi
    done
    fail "未在 $output_dir 找到 patch 输出文件"
}

generate_tinker_config() {
    local config_file="$1"
    local store_file="$2"
    local store_password="$3"
    local key_alias="$4"
    local key_password="$5"
    local tinker_id="$6"
    local seven_zip_path="$7"
    local ignore_change_warning="$8"
    cat > "$config_file" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<tinkerPatch>
    <issue id="property">
        <ignoreWarning value="false"/>
        <useSign value="true"/>
        <sevenZipPath value="$(xml_escape "$seven_zip_path")"/>
        <isProtectedApp value="false"/>
        <supportHotplugComponent value="false"/>
    </issue>

    <issue id="dex">
        <dexMode value="jar"/>
        <pattern value="classes*.dex"/>
        <pattern value="assets/secondary-dex-?.jar"/>
        <loader value="com.tencent.tinker.entry.*"/>
        <loader value="com.tencent.tinker.loader.*"/>
        <loader value="com.lhxy.istationdevice.android11.app.ShellTinkerApplication"/>
        <loader value="com.lhxy.istationdevice.android11.app.ShellApplication"/>
    </issue>

    <issue id="lib">
        <pattern value="lib/*/*.so"/>
    </issue>

    <issue id="resource">
        <pattern value="res/*"/>
        <pattern value="assets/*"/>
        <pattern value="resources.arsc"/>
        <pattern value="AndroidManifest.xml"/>
        <ignoreChangeWarning value="$(xml_escape "$ignore_change_warning")"/>
        <largeModSize value="100"/>
    </issue>

    <issue id="packageConfig">
        <configField name="platform" value="all"/>
        <configField name="TINKER_ID" value="$(xml_escape "$tinker_id")"/>
    </issue>

    <issue id="sign">
        <path value="$(xml_escape "$store_file")"/>
        <storepass value="$(xml_escape "$store_password")"/>
        <keypass value="$(xml_escape "$key_password")"/>
        <alias value="$(xml_escape "$key_alias")"/>
    </issue>
</tinkerPatch>
EOF
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --old-apk)
            OLD_APK="$2"
            shift 2
            ;;
        --new-apk)
            NEW_APK="$2"
            shift 2
            ;;
        --skip-build)
            SKIP_BUILD=1
            shift
            ;;
        --patch-version)
            PATCH_VERSION="$2"
            shift 2
            ;;
        --output-dir)
            OUTPUT_DIR="$2"
            shift 2
            ;;
        --patch-object-key)
            PATCH_OBJECT_KEY="$2"
            shift 2
            ;;
        --manifest-object-key)
            MANIFEST_OBJECT_KEY="$2"
            shift 2
            ;;
        --target-version-code)
            TARGET_VERSION_CODE="$2"
            shift 2
            ;;
        --target-version-name)
            TARGET_VERSION_NAME="$2"
            shift 2
            ;;
        --tinker-id)
            TINKER_ID="$2"
            shift 2
            ;;
        --ignore-change-warning)
            IGNORE_CHANGE_WARNING="$2"
            shift 2
            ;;
        --release-notes)
            RELEASE_NOTES="$2"
            shift 2
            ;;
        --skip-upload)
            SKIP_UPLOAD=1
            shift
            ;;
        --oss-config)
            OSS_CONFIG_FILE="$2"
            shift 2
            ;;
        --cli-jar)
            CLI_JAR="$2"
            shift 2
            ;;
        --cli-url)
            CLI_URL="$2"
            shift 2
            ;;
        --help|-h)
            usage
            exit 0
            ;;
        *)
            fail "未知参数: $1"
            ;;
    esac
done

[[ -n "$OLD_APK" ]] || fail "必须传 --old-apk"
OLD_APK="$(make_absolute_path "$OLD_APK")"
[[ -f "$OLD_APK" ]] || fail "old apk 不存在: $OLD_APK"
OLD_APK="$(archive_input_apk "$OLD_APK" base)"

if [[ "$SKIP_UPLOAD" -eq 0 ]]; then
    load_oss_upload_config
fi

if [[ "$SKIP_BUILD" -eq 1 && -z "$NEW_APK" ]]; then
    fail "--skip-build 需要配合 --new-apk 使用"
fi

if [[ -z "$NEW_APK" ]]; then
    echo "未提供 --new-apk, 开始构建 release APK"
    run_gradle :app:assembleRelease
    NEW_APK="$(detect_release_apk)"
else
    NEW_APK="$(make_absolute_path "$NEW_APK")"
fi

[[ -n "$NEW_APK" ]] || fail "无法定位 new apk"
[[ -f "$NEW_APK" ]] || fail "new apk 不存在: $NEW_APK"
NEW_APK="$(archive_input_apk "$NEW_APK" new)"

if [[ -z "$TARGET_VERSION_CODE" ]]; then
    TARGET_VERSION_CODE="$(detect_number_value appVersionCode)"
fi
if [[ -z "$TARGET_VERSION_NAME" ]]; then
    TARGET_VERSION_NAME="$(detect_string_value appVersionName)"
fi
if [[ -z "$TARGET_VERSION_CODE" ]]; then
    TARGET_VERSION_CODE="0"
fi
if [[ -z "$TINKER_ID" ]]; then
    TINKER_ID="$TARGET_VERSION_NAME"
fi

INDEX_OBJECT_KEY="${DEFAULT_RELEASES_PREFIX}/${TINKER_ID}/index.json"
derive_patch_version

if [[ -z "$OUTPUT_DIR" ]]; then
    OUTPUT_DIR="$HOTFIX_DIR/$PATCH_VERSION"
else
    OUTPUT_DIR="$(make_absolute_path "$OUTPUT_DIR")"
fi
mkdir -p "$OUTPUT_DIR"

if [[ -z "$PATCH_OBJECT_KEY" ]]; then
    PATCH_OBJECT_KEY="${DEFAULT_RELEASES_PREFIX}/${TINKER_ID}/${PATCH_VERSION}/patch_signed.apk"
fi
ARCHIVE_MANIFEST_OBJECT_KEY="${DEFAULT_RELEASES_PREFIX}/${TINKER_ID}/${PATCH_VERSION}/manifest.json"

[[ -f "$SIGNING_FILE" ]] || fail "缺少签名文件: $SIGNING_FILE"

STORE_FILE_RELATIVE="$(prop_value storeFile "$SIGNING_FILE")"
STORE_PASSWORD="$(prop_value storePassword "$SIGNING_FILE")"
KEY_ALIAS="$(prop_value keyAlias "$SIGNING_FILE")"
KEY_PASSWORD="$(prop_value keyPassword "$SIGNING_FILE")"

[[ -n "$STORE_FILE_RELATIVE" ]] || fail "signing.local.properties 未配置 storeFile"
[[ -n "$STORE_PASSWORD" ]] || fail "signing.local.properties 未配置 storePassword"
[[ -n "$KEY_ALIAS" ]] || fail "signing.local.properties 未配置 keyAlias"
[[ -n "$KEY_PASSWORD" ]] || fail "signing.local.properties 未配置 keyPassword"

STORE_FILE="$ROOT_DIR/$STORE_FILE_RELATIVE"
[[ -f "$STORE_FILE" ]] || fail "签名文件不存在: $STORE_FILE"

ensure_cli

SEVEN_ZIP_PATH=""
if command -v 7za >/dev/null 2>&1; then
    SEVEN_ZIP_PATH="$(command -v 7za)"
elif command -v 7zz >/dev/null 2>&1; then
    SEVEN_ZIP_PATH="$(command -v 7zz)"
elif command -v 7z >/dev/null 2>&1; then
    SEVEN_ZIP_PATH="$(command -v 7z)"
fi

CLI_CONFIG_DIR="$TOOLS_DIR/configs"
mkdir -p "$CLI_CONFIG_DIR"
CONFIG_FILE="$CLI_CONFIG_DIR/tinker_config_${PATCH_VERSION}.xml"
generate_tinker_config "$CONFIG_FILE" "$STORE_FILE" "$STORE_PASSWORD" "$KEY_ALIAS" "$KEY_PASSWORD" "$TINKER_ID" "$SEVEN_ZIP_PATH" "$IGNORE_CHANGE_WARNING"

echo "开始生成 patch"
mkdir -p "$TOOLS_DIR/logs"
PATCH_LOG_FILE="$TOOLS_DIR/logs/tinker-patch_${PATCH_VERSION}.log"
if ! run_java -jar "$CLI_JAR" -old "$OLD_APK" -new "$NEW_APK" -config "$CONFIG_FILE" -out "$OUTPUT_DIR" 2>&1 | tee "$PATCH_LOG_FILE"; then
    if grep -q "some loader class has been changed in new primary dex" "$PATCH_LOG_FILE"; then
        fail "检测到 loader class 发生变化（例如 ShellApplication）。这类改动不能直接基于当前基线走热更新；请先执行 sh apk.sh rebuild --pin-base，安装新的完整 APK 作为新基线，再继续出后续 Java 补丁。"
    fi
    fail "Tinker patch 生成失败，请查看日志: $PATCH_LOG_FILE"
fi

PATCH_FILE="$(resolve_patch_output "$OUTPUT_DIR")"
PATCH_FILE_NAME="$(basename "$PATCH_FILE")"
PATCH_SIZE_BYTES="$(wc -c < "$PATCH_FILE" | tr -d '[:space:]')"
PATCH_MD5="$(compute_md5 "$PATCH_FILE")"

if [[ -z "$PATCH_OBJECT_KEY" ]]; then
    PATCH_OBJECT_KEY="${DEFAULT_RELEASES_PREFIX}/${TINKER_ID}/${PATCH_VERSION}/${PATCH_FILE_NAME}"
fi

MANIFEST_FILE="$OUTPUT_DIR/manifest.json"
INDEX_FILE="$OUTPUT_DIR/index.json"
cat > "$MANIFEST_FILE" <<EOF
{
  "enabled": true,
  "patchVersion": "$(json_escape "$PATCH_VERSION")",
  "targetVersionCode": ${TARGET_VERSION_CODE},
  "targetVersionName": "$(json_escape "$TARGET_VERSION_NAME")",
  "patchObjectKey": "$(json_escape "$PATCH_OBJECT_KEY")",
  "patchMd5": "$(json_escape "$PATCH_MD5")",
  "patchSizeBytes": ${PATCH_SIZE_BYTES},
  "releaseNotes": "$(json_escape "$RELEASE_NOTES")"
}
EOF

PATCH_SEQUENCE_VALUE="$(printf '%s\n' "$PATCH_VERSION" | sed -nE 's/.*-p([0-9]+)$/\1/p' | head -n 1)"
if [[ -z "$PATCH_SEQUENCE_VALUE" ]]; then
    PATCH_SEQUENCE_VALUE="1"
fi
PATCH_SEQUENCE_VALUE="$((10#$PATCH_SEQUENCE_VALUE))"
generate_index_file "$INDEX_FILE" "$PATCH_SEQUENCE_VALUE"

echo "生成完成"
echo "base apk:      $OLD_APK"
echo "new apk:       $NEW_APK"
echo "patch file:    $PATCH_FILE"
echo "manifest file: $MANIFEST_FILE"
echo "index file:    $INDEX_FILE"
echo "manifest key:  $MANIFEST_OBJECT_KEY"
echo "archive key:   $ARCHIVE_MANIFEST_OBJECT_KEY"
echo "index key:     $INDEX_OBJECT_KEY"
echo "patch key:     $PATCH_OBJECT_KEY"
echo "patch md5:     $PATCH_MD5"
echo "base note:     后续继续打补丁时，仍然复用这次对应的基线包，不要切成后续新 APK"

if [[ "$SKIP_UPLOAD" -eq 0 ]]; then
    upload_to_oss "$PATCH_FILE" "$PATCH_OBJECT_KEY" "application/vnd.android.package-archive"
    upload_to_oss "$MANIFEST_FILE" "$ARCHIVE_MANIFEST_OBJECT_KEY" "application/json; charset=utf-8"
    upload_to_oss "$INDEX_FILE" "$INDEX_OBJECT_KEY" "application/json; charset=utf-8"
    upload_to_oss "$MANIFEST_FILE" "$MANIFEST_OBJECT_KEY" "application/json; charset=utf-8"
    echo "OSS 上传完成"
    echo "oss config:    $OSS_CONFIG_FILE"
fi