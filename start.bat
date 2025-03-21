@echo off
echo 正在启动坦克大战游戏...

REM 检查是否安装了Java
java -version >nul 2>&1
if errorlevel 1 (
    echo 错误：未找到Java，请先安装Java 11或更高版本
    pause
    exit /b 1
)

REM 检查是否安装了Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo 错误：未找到Maven，请先安装Maven 3.6或更高版本
    pause
    exit /b 1
)

REM 构建项目
echo 正在构建项目...
call mvn clean package

REM 运行游戏
echo 正在启动游戏...
java -jar target/tank-game-1.0-SNAPSHOT.jar

pause 