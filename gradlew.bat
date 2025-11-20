@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set GRADLE_VERSION=8.5
set DISTRIBUTION_URL=https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip
set GRADLE_INSTALL_DIR=%APP_HOME%\gradle\wrapper\gradle-%GRADLE_VERSION%

if exist "%GRADLE_INSTALL_DIR%\bin\gradle.bat" goto haveGradle

if not exist "%APP_HOME%\gradle\wrapper" mkdir "%APP_HOME%\gradle\wrapper"
set WRAPPER_ZIP=%TEMP%\gradle-wrapper.zip
if exist "%WRAPPER_ZIP%" del "%WRAPPER_ZIP%"
if exist "%SystemRoot%\System32\curl.exe" (
    curl --fail --location --output "%WRAPPER_ZIP%" "%DISTRIBUTION_URL%"
    if %ERRORLEVEL% neq 0 goto downloadError
) else if exist "%SystemRoot%\System32\wget.exe" (
    wget -O "%WRAPPER_ZIP%" "%DISTRIBUTION_URL%"
    if %ERRORLEVEL% neq 0 goto downloadError
) else (
    echo Please install curl or wget to download the Gradle distribution
    goto fail
)

powershell -Command "Add-Type -AssemblyName 'System.IO.Compression.FileSystem';[System.IO.Compression.ZipFile]::ExtractToDirectory('%WRAPPER_ZIP%','%APP_HOME%\gradle\wrapper')"
if %ERRORLEVEL% neq 0 goto downloadError

:haveGradle
"%GRADLE_INSTALL_DIR%\bin\gradle.bat" %*
goto end


@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

:downloadError
echo Could not download or extract the Gradle distribution from %DISTRIBUTION_URL%
goto fail

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%GRADLE_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
