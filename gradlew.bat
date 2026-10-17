@echo off
setlocal
set "GRADLE_VERSION=8.13"
set "GRADLE_HOME_DIR=%USERPROFILE%\.gradle\wrapper\dists\klaarkompas\gradle-%GRADLE_VERSION%-bin"
set "GRADLE_BIN=%GRADLE_HOME_DIR%\gradle-%GRADLE_VERSION%\bin\gradle.bat"
if exist "%GRADLE_BIN%" goto runGradle
if not exist "%GRADLE_HOME_DIR%" mkdir "%GRADLE_HOME_DIR%"
set "GRADLE_ZIP=%GRADLE_HOME_DIR%\gradle-%GRADLE_VERSION%-bin.zip"
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest -UseBasicParsing -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%GRADLE_ZIP%'; Expand-Archive -Force -Path '%GRADLE_ZIP%' -DestinationPath '%GRADLE_HOME_DIR%'; Remove-Item -Force '%GRADLE_ZIP%'"
if errorlevel 1 exit /b %errorlevel%
:runGradle
call "%GRADLE_BIN%" %*
exit /b %errorlevel%
