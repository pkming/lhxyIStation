@echo off
setlocal
if /I "%~1"=="--help" (
    powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0apk.ps1" help
    exit /b %ERRORLEVEL%
)
if /I "%~1"=="-h" (
    powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0apk.ps1" help
    exit /b %ERRORLEVEL%
)
if /I "%~2"=="--help" (
    powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0apk.ps1" "%~1" help
    exit /b %ERRORLEVEL%
)
if /I "%~2"=="-h" (
    powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0apk.ps1" "%~1" help
    exit /b %ERRORLEVEL%
)
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0apk.ps1" %*
exit /b %ERRORLEVEL%
