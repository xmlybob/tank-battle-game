# 贡献指南

感谢你对坦克大战游戏项目的关注！我们欢迎任何形式的贡献，包括但不限于：

- 提交 Bug 报告
- 提出新功能建议
- 提交代码改进
- 改进文档
- 添加测试用例

## 如何贡献

1. Fork 本仓库
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的改动 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启一个 Pull Request

## 开发环境设置

1. 确保你已安装 Java 11 或更高版本
2. 安装 Maven 3.6 或更高版本
3. 克隆仓库：
   ```bash
   git clone https://github.com/xmlybob/tank-battle-game.git
   cd tank-battle-game
   ```
4. 构建项目：
   ```bash
   mvn clean package
   ```
5. 运行游戏：
   ```bash
   java -jar target/tank-game-1.0-SNAPSHOT.jar
   ```

## 代码规范

- 使用 Java 代码规范
- 确保代码经过测试
- 添加适当的注释
- 保持代码简洁清晰

## 提交信息规范

提交信息应该清晰描述改动内容，格式如下：

```
<type>(<scope>): <subject>

<body>

<footer>
```

类型（type）：
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式（不影响代码运行的变动）
- refactor: 重构
- test: 增加测试
- chore: 构建过程或辅助工具的变动

## 问题反馈

如果你发现任何问题或有改进建议，请：

1. 查看是否已有相关 issue
2. 如果没有，创建新的 issue
3. 详细描述问题或建议
4. 添加相关的标签

## 许可证

通过提交代码，你同意你的代码将使用与项目相同的 MIT 许可证。 