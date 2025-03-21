# 坦克大战游戏安装说明

## 系统要求

- Java 11 或更高版本
- Maven 3.6 或更高版本（用于构建项目）

## 安装步骤

### Windows 系统

1. 安装 Java
   - 访问 [Oracle Java 下载页面](https://www.oracle.com/java/technologies/downloads/#java11)
   - 下载并安装 Windows 版本的 JDK 11
   - 设置 JAVA_HOME 环境变量

2. 安装 Maven
   - 访问 [Maven 下载页面](https://maven.apache.org/download.cgi)
   - 下载 Binary zip archive
   - 解压到合适的目录（如 C:\Program Files\Apache\maven）
   - 将 Maven 的 bin 目录添加到系统环境变量 PATH 中

3. 运行游戏
   - 双击 `start.bat` 文件
   - 或在命令提示符中运行：
     ```bash
     start.bat
     ```

### Linux/Mac 系统

1. 安装 Java
   ```bash
   # Ubuntu/Debian
   sudo apt update
   sudo apt install openjdk-11-jdk

   # CentOS/RHEL
   sudo yum install java-11-openjdk-devel

   # macOS (使用 Homebrew)
   brew install openjdk@11
   ```

2. 安装 Maven
   ```bash
   # Ubuntu/Debian
   sudo apt install maven

   # CentOS/RHEL
   sudo yum install maven

   # macOS (使用 Homebrew)
   brew install maven
   ```

3. 运行游戏
   ```bash
   # 添加执行权限
   chmod +x start.sh

   # 运行游戏
   ./start.sh
   ```

## 常见问题

1. 如果遇到"找不到Java"错误：
   - 确保已正确安装 Java 11 或更高版本
   - 检查 JAVA_HOME 环境变量是否正确设置
   - 在命令行中运行 `java -version` 验证安装

2. 如果遇到"找不到Maven"错误：
   - 确保已正确安装 Maven 3.6 或更高版本
   - 检查 Maven 是否已添加到系统环境变量 PATH 中
   - 在命令行中运行 `mvn -version` 验证安装

3. 如果游戏无法启动：
   - 检查是否所有必需的文件都在正确的位置
   - 查看错误信息以获取更多详细信息
   - 确保有足够的系统权限

## 游戏控制

- WASD 或方向键：移动坦克
- 鼠标移动：瞄准
- 空格键：发射子弹
- M 键：开关音效
- 回车键：开始新游戏/进入下一关

## 技术支持

如果遇到问题，请检查：
1. 系统要求是否满足
2. 环境变量是否正确设置
3. 所有必需文件是否完整

## 许可证

MIT License 