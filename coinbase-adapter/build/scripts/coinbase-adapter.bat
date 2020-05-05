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

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  coinbase-adapter startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and COINBASE_ADAPTER_OPTS to pass JVM options to this script.
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

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\coinbase-adapter-1.0-SNAPSHOT.jar;%APP_HOME%\lib\http4k-contract-3.243.0.jar;%APP_HOME%\lib\http4k-security-oauth-3.243.0.jar;%APP_HOME%\lib\http4k-format-jackson-3.243.0.jar;%APP_HOME%\lib\http4k-server-jetty-3.243.0.jar;%APP_HOME%\lib\http4k-client-apache-3.243.0.jar;%APP_HOME%\lib\http4k-client-websocket-3.243.0.jar;%APP_HOME%\lib\http4k-core-3.243.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.3.72.jar;%APP_HOME%\lib\jackson-module-kotlin-2.10.3.jar;%APP_HOME%\lib\kotlin-reflect-1.3.72.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.3.72.jar;%APP_HOME%\lib\kotlin-stdlib-1.3.72.jar;%APP_HOME%\lib\config-1.4.0.jar;%APP_HOME%\lib\HikariCP-3.4.1.jar;%APP_HOME%\lib\slf4j-simple-2.0.0-alpha1.jar;%APP_HOME%\lib\rethinkdb-driver-2.4.2.jar;%APP_HOME%\lib\Java-WebSocket-1.4.1.jar;%APP_HOME%\lib\slf4j-api-2.0.0-alpha1.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.3.72.jar;%APP_HOME%\lib\annotations-19.0.0.jar;%APP_HOME%\lib\http2-server-9.4.28.v20200408.jar;%APP_HOME%\lib\javax-websocket-server-impl-9.4.28.v20200408.jar;%APP_HOME%\lib\websocket-server-9.4.28.v20200408.jar;%APP_HOME%\lib\jetty-annotations-9.4.28.v20200408.jar;%APP_HOME%\lib\jetty-plus-9.4.28.v20200408.jar;%APP_HOME%\lib\jetty-webapp-9.4.28.v20200408.jar;%APP_HOME%\lib\jetty-servlet-9.4.28.v20200408.jar;%APP_HOME%\lib\jetty-security-9.4.28.v20200408.jar;%APP_HOME%\lib\jetty-alpn-conscrypt-server-9.4.28.v20200408.jar;%APP_HOME%\lib\jetty-alpn-server-9.4.28.v20200408.jar;%APP_HOME%\lib\jetty-server-9.4.28.v20200408.jar;%APP_HOME%\lib\websocket-servlet-9.4.28.v20200408.jar;%APP_HOME%\lib\javax.servlet-api-4.0.1.jar;%APP_HOME%\lib\jackson-dataformat-xml-2.10.3.jar;%APP_HOME%\lib\jackson-module-jaxb-annotations-2.10.3.jar;%APP_HOME%\lib\jackson-databind-2.10.3.jar;%APP_HOME%\lib\result4k-2.0.0.jar;%APP_HOME%\lib\alpn-boot-8.1.13.v20181017.jar;%APP_HOME%\lib\httpclient-4.5.12.jar;%APP_HOME%\lib\jackson-annotations-2.10.3.jar;%APP_HOME%\lib\jackson-core-2.10.3.jar;%APP_HOME%\lib\woodstox-core-6.1.1.jar;%APP_HOME%\lib\stax2-api-4.2.jar;%APP_HOME%\lib\http2-common-9.4.28.v20200408.jar;%APP_HOME%\lib\http2-hpack-9.4.28.v20200408.jar;%APP_HOME%\lib\javax-websocket-client-impl-9.4.28.v20200408.jar;%APP_HOME%\lib\websocket-client-9.4.28.v20200408.jar;%APP_HOME%\lib\jetty-client-9.4.28.v20200408.jar;%APP_HOME%\lib\jetty-http-9.4.28.v20200408.jar;%APP_HOME%\lib\websocket-common-9.4.28.v20200408.jar;%APP_HOME%\lib\jetty-io-9.4.28.v20200408.jar;%APP_HOME%\lib\javax.websocket-api-1.0.jar;%APP_HOME%\lib\conscrypt-openjdk-uber-2.4.0.jar;%APP_HOME%\lib\httpcore-4.4.13.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-codec-1.11.jar;%APP_HOME%\lib\jakarta.xml.bind-api-2.3.2.jar;%APP_HOME%\lib\jakarta.activation-api-1.2.1.jar;%APP_HOME%\lib\jetty-jndi-9.4.28.v20200408.jar;%APP_HOME%\lib\jetty-xml-9.4.28.v20200408.jar;%APP_HOME%\lib\jetty-util-9.4.28.v20200408.jar;%APP_HOME%\lib\javax.annotation-api-1.3.jar;%APP_HOME%\lib\asm-commons-7.2.jar;%APP_HOME%\lib\asm-analysis-7.2.jar;%APP_HOME%\lib\asm-tree-7.2.jar;%APP_HOME%\lib\asm-7.2.jar;%APP_HOME%\lib\javax.websocket-client-api-1.0.jar;%APP_HOME%\lib\websocket-api-9.4.28.v20200408.jar

@rem Execute coinbase-adapter
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %COINBASE_ADAPTER_OPTS%  -classpath "%CLASSPATH%" me.pysquad.cryptobot.CoinbaseAdapterAppKt %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable COINBASE_ADAPTER_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%COINBASE_ADAPTER_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
