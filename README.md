# 坦克大战游戏

一个使用Java Swing开发的2D坦克大战游戏。

## 系统要求

- Java 11 或更高版本
- Maven 3.6 或更高版本（用于构建项目）

## 游戏特性

- 玩家控制坦克进行战斗
- 多个关卡，难度递增
- 不同类型的Boss敌人
- 计分系统
- 音效系统
- 支持键盘和鼠标控制

## 控制方式

- WASD或方向键：移动坦克
- 鼠标移动：瞄准
- 空格键：发射子弹
- M键：开关音效
- 回车键：开始新游戏/进入下一关

## 构建和运行

### 方法1：使用Maven（推荐）

1. 确保已安装Java 11和Maven
2. 克隆或下载项目到本地
3. 在项目根目录下运行：
   ```bash
   mvn clean package
   ```
4. 运行生成的jar文件：
   ```bash
   java -jar target/tank-game-1.0-SNAPSHOT.jar
   ```

### 方法2：直接编译运行

1. 确保已安装Java 11
2. 克隆或下载项目到本地
3. 编译所有Java文件：
   ```bash
   javac src/main/java/com/tankgame/*.java
   ```
4. 运行游戏：
   ```bash
   java -cp src/main/java:src/main/resources com.tankgame.TankGame
   ```

## 项目结构

```
tank-game/
├── pom.xml                 # Maven项目配置文件
├── README.md              # 项目说明文档
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── tankgame/
        │           ├── TankGame.java
        │           ├── GamePanel.java
        │           ├── Tank.java
        │           ├── Bullet.java
        │           ├── Boss.java
        │           └── SoundManager.java
        └── resources/
            └── sounds/    # 音效文件目录
```

## 注意事项

- 确保Java环境变量正确配置
- 如果使用Maven构建，确保Maven环境变量正确配置
- 游戏需要音效文件才能正常运行，请确保音效文件存在于正确的位置

## 常见问题

1. 如果遇到"找不到主类"错误，请确保在正确的目录下运行命令
2. 如果遇到音效相关错误，请检查音效文件是否存在
3. 如果游戏无法启动，请检查Java版本是否符合要求

## 许可证

MIT License 