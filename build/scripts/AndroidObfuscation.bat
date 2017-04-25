@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  AndroidObfuscation startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and ANDROID_OBFUSCATION_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\AndroidObfuscation-1.0-SNAPSHOT.jar;%APP_HOME%\lib\commons-cli-1.4.jar;%APP_HOME%\lib\jboss-common-core-2.5.0.Final.jar;%APP_HOME%\lib\slf4j-simple-1.6.1.jar;%APP_HOME%\lib\rewrite-core-0.18.4.jar;%APP_HOME%\lib\jboss-logging-spi-2.1.2.GA.jar;%APP_HOME%\lib\annotation-detector-3.0.5.jar;%APP_HOME%\lib\commons-lang-2.6.jar;%APP_HOME%\lib\kotlin-reflect-1.1.0-beta-38.jar;%APP_HOME%\lib\koloboke-api-jdk8-1.0.0.jar;%APP_HOME%\lib\koloboke-impl-jdk8-1.0.0.jar;%APP_HOME%\lib\kotlin-stdlib-jre8-1.1.0-beta-38.jar;%APP_HOME%\lib\antlr4-4.2.2.jar;%APP_HOME%\lib\org.eclipse.jgit-4.4.1.201607150455-r.jar;%APP_HOME%\lib\kotlin-stdlib-1.1.0-beta-38.jar;%APP_HOME%\lib\koloboke-impl-common-jdk8-1.0.0.jar;%APP_HOME%\lib\kotlin-stdlib-jre7-1.1.0-beta-38.jar;%APP_HOME%\lib\antlr4-runtime-4.2.2.jar;%APP_HOME%\lib\antlr4-annotations-4.2.2.jar;%APP_HOME%\lib\antlr-runtime-3.5.2.jar;%APP_HOME%\lib\ST4-4.0.8.jar;%APP_HOME%\lib\jsch-0.1.53.jar;%APP_HOME%\lib\JavaEWAH-0.7.9.jar;%APP_HOME%\lib\httpclient-4.3.6.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\org.abego.treelayout.core-1.0.1.jar;%APP_HOME%\lib\httpcore-4.3.3.jar;%APP_HOME%\lib\commons-logging-1.1.3.jar;%APP_HOME%\lib\commons-codec-1.6.jar;%APP_HOME%\lib\slf4j-api-1.7.22.jar

@rem Execute AndroidObfuscation
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %ANDROID_OBFUSCATION_OPTS%  -classpath "%CLASSPATH%" JavaObfuscator.Core.Main %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable ANDROID_OBFUSCATION_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%ANDROID_OBFUSCATION_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
