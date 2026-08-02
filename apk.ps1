[CmdletBinding()]
param(
    [Parameter(Position = 0)]
    [Alias("help")]
    [string]$Command,
    [Parameter(Position = 1)]
    [string]$Version,
    [Alias("force-pin-base")]
    [switch]$ForcePinBase
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$gradleFile = Join-Path $root "app/build.gradle"
$releaseDir = Join-Path $root "app/build/outputs/apk/release"
$apkDir = Join-Path $root "apk"
$archiveDir = Join-Path $apkDir "release"
$baseDir = Join-Path $apkDir "base"

function Fail([string]$Message) {
    throw $Message
}

function Get-VersionInfo {
    $content = [IO.File]::ReadAllText($gradleFile)
    $codeMatch = [regex]::Match($content, '(?m)^\s*def\s+appVersionCode\s*=\s*(\d+)')
    $nameMatch = [regex]::Match($content, '(?m)^\s*def\s+appVersionName\s*=\s*"([^"]+)"')
    if (!$codeMatch.Success -or !$nameMatch.Success) {
        Fail "Cannot read appVersionCode/appVersionName from app/build.gradle"
    }
    [pscustomobject]@{
        Content = $content
        Code = [int]$codeMatch.Groups[1].Value
        Name = $nameMatch.Groups[1].Value
    }
}

function Set-Version([int]$Code, [string]$Name) {
    $info = Get-VersionInfo
    $codeRegex = [regex]::new('(?m)^(\s*def\s+appVersionCode\s*=\s*)\d+')
    $nameRegex = [regex]::new('(?m)^(\s*def\s+appVersionName\s*=\s*)"[^\"]+"')
    $updated = $codeRegex.Replace($info.Content, { param($match) $match.Groups[1].Value + $Code }, 1)
    $updated = $nameRegex.Replace($updated, { param($match) $match.Groups[1].Value + '"' + $Name + '"' }, 1)
    [IO.File]::WriteAllText($gradleFile, $updated, [Text.UTF8Encoding]::new($false))
}

function Ensure-Java {
    function Is-CompatibleJavaHome([string]$JavaHome) {
        if (!$JavaHome -or !(Test-Path (Join-Path $JavaHome "bin/java.exe"))) {
            return $false
        }
        $releaseFile = Join-Path $JavaHome "release"
        if (!(Test-Path $releaseFile)) {
            return $false
        }
        $versionLine = Get-Content $releaseFile | Where-Object { $_ -match '^JAVA_VERSION=' } | Select-Object -First 1
        if ($versionLine -notmatch '"(\d+)') {
            return $false
        }
        $major = [int]$Matches[1]
        return $major -ge 17 -and $major -le 21
    }

    if (Is-CompatibleJavaHome $env:JAVA_HOME) {
        return
    }

    $candidates = @()
    $userJdkRoot = Join-Path $env:USERPROFILE ".jdks"
    if (Test-Path $userJdkRoot) {
        $candidates += Get-ChildItem $userJdkRoot -Directory |
            Sort-Object Name -Descending |
            Select-Object -ExpandProperty FullName
    }
    $candidates += @(
        "C:\Program Files\Android\Android Studio\jbr",
        "C:\Program Files\Android\Android Studio\jre"
    )
    foreach ($candidate in $candidates) {
        if (Is-CompatibleJavaHome $candidate) {
            $env:JAVA_HOME = $candidate
            $env:Path = (Join-Path $candidate "bin") + ";" + $env:Path
            Write-Output ("Using compatible JDK: " + $candidate)
            return
        }
    }
    Fail "Java 17-21 was not found. Install JDK 17/21 or set JAVA_HOME."
}

function Find-ReleaseApk {
    $metadata = Join-Path $releaseDir "output-metadata.json"
    if (Test-Path $metadata) {
        $output = (Get-Content -Raw $metadata | ConvertFrom-Json).elements[0].outputFile
        $candidate = Join-Path $releaseDir $output
        if (Test-Path $candidate) {
            return (Resolve-Path $candidate).Path
        }
    }
    $candidate = Get-ChildItem $releaseDir -Filter "APP*.apk" -File | Sort-Object LastWriteTime | Select-Object -Last 1
    if (!$candidate) {
        Fail "assembleRelease completed but no release APK was found"
    }
    return $candidate.FullName
}

if ([string]::IsNullOrWhiteSpace($Command) -or $Command -in @("help", "--help", "-h") -or $Version -in @("help", "--help", "-h")) {
    Write-Output "Usage: .\apk.bat newbase [version] [--force-pin-base]"
    exit 0
}
if ($Command -ne "newbase") {
    Fail "Windows wrapper currently supports only: newbase"
}
if ($Version -and $Version -notmatch '^\d+\.\d+\.\d+$') {
    Fail "Version must use MAJOR.MINOR.PATCH, for example 0.2.0"
}

$info = Get-VersionInfo
if (!$Version) {
    $parts = $info.Name.Split(".")
    if ($parts.Count -ne 3) {
        Fail ("Current versionName is not MAJOR.MINOR.PATCH: " + $info.Name)
    }
    $Version = $parts[0] + "." + $parts[1] + "." + ([int]$parts[2] + 1)
}
$newCode = $info.Code + 1
try {
    Set-Version $newCode $Version
    Write-Output ("Version upgraded: " + $info.Code + " -> " + $newCode + " / " + $info.Name + " -> " + $Version)
    Ensure-Java

    Push-Location $root
    try {
        & (Join-Path $root "gradlew.bat") assembleRelease
        if ($LASTEXITCODE -ne 0) {
            Fail ("assembleRelease failed with exit code " + $LASTEXITCODE)
        }
    } finally {
        Pop-Location
    }

    $releaseApk = Find-ReleaseApk
    New-Item -ItemType Directory -Force $archiveDir, $baseDir | Out-Null
    $archivedApk = Join-Path $archiveDir (Split-Path $releaseApk -Leaf)
    Copy-Item $releaseApk $archivedApk -Force
    $baselineApk = Join-Path $baseDir ("base-" + $Version + ".apk")
    if ((Test-Path $baselineApk) -and !$ForcePinBase) {
        $oldHash = (Get-FileHash $baselineApk -Algorithm SHA256).Hash
        $newHash = (Get-FileHash $archivedApk -Algorithm SHA256).Hash
        if ($oldHash -ne $newHash) {
            Fail ("A different baseline already exists for version " + $Version + ". Use --force-pin-base only when intentional.")
        }
    }
    Copy-Item $archivedApk $baselineApk -Force
    Copy-Item $baselineApk (Join-Path $baseDir "latest-base.apk") -Force
    Write-Output ("New baseline completed: version " + $Version)
    Write-Output ("Release APK: " + $releaseApk)
    Write-Output ("Baseline APK: " + $baselineApk)
} catch {
    [IO.File]::WriteAllText($gradleFile, $info.Content, [Text.UTF8Encoding]::new($false))
    Write-Warning ("newbase failed; version restored to " + $info.Name + " (" + $info.Code + ")")
    throw
}
