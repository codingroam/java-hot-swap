REM 设置UTF-8编码，防止中文乱码
chcp 65001

REM 关闭回显，防止脚本语句一行一行打印在控制台
@echo off

REM Setup JAVA_HOME
set JAVA_HOME: %JAVA_HOME%
if JAVA_HOME == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if not exist "%JAVA_HOME%\lib\tools.jar" (
    echo Can not find lib\tools.jar under %JAVA_HOME%!
    echo If java version ^<^= 1.8, please make sure JAVA_HOME point to a JDK not a JRE.
    echo If java version ^>^= 9, try to run as.bat ^<pid^> --ignore-tools
    goto exit_bat
  )
set BOOT_CLASSPATH="-Xbootclasspath/a:%JAVA_HOME%\lib\tools.jar")


goto userinput


REM according to param choose different way




:noJavaHome
echo The JAVA_HOME environment variable is not defined correctly.
echo It is needed to run this program.
echo NB: JAVA_HOME should point to a JDK not a JRE.
goto exit_bat





:exit_bat
if "%exitProcess%"=="1" exit %ERROR_CODE%
exit /B %ERROR_CODE%

:monitor
java  %BOOT_CLASSPATH% -jar hot-swap-monitor.jar

:server
java  %BOOT_CLASSPATH% -jar hot-swap-server.jar

:userinput
set /p var=请输入需要启动的服务(monitor or server)
REM if else if要在右括号")"后跟 “^”
if %var%==monitor (goto monitor)^
else if %var%==server (goto server)^
else (
echo %var%无法匹配服务请重新输入
goto userinput
)
