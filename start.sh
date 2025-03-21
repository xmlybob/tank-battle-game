#!/bin/bash

echo "正在启动坦克大战游戏..."

# 检查是否安装了Java
if ! command -v java &> /dev/null; then
    echo "错误：未找到Java，请先安装Java 11或更高版本"
    exit 1
fi

# 检查是否安装了Maven
if ! command -v mvn &> /dev/null; then
    echo "错误：未找到Maven，请先安装Maven 3.6或更高版本"
    exit 1
fi

# 构建项目
echo "正在构建项目..."
mvn clean package

# 运行游戏
echo "正在启动游戏..."
java -jar target/tank-game-1.0-SNAPSHOT.jar 