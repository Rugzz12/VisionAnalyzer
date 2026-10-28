@echo off
setlocal
set ROOT_DIR=%~dp0
set GRADLE_VERSION=8.7
set INSTALL_DIR=%ROOT_DIR%.gradle-install
set GRADLE_HOME=%INSTALL_DIR%\gradle-%GRADLE_VERSION%
set GRADLE_BIN=%GRADLE_HOME%\bin\gradle.bat
set ARCHIVE=%INSTALL_DIR%\gradle-%GRADLE_VERSION%-bin.zip

if not exist "%GRADLE_BIN%" (
    if not exist "%ARCHIVE%" (
        echo Downloading Gradle %GRADLE_VERSION%...
        powershell -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%ARCHIVE%'"
    )
    if not exist "%GRADLE_HOME%" (
        powershell -Command "Expand-Archive -Path '%ARCHIVE%' -DestinationPath '%INSTALL_DIR%' -Force"
    )
)

call "%GRADLE_BIN%" %*
