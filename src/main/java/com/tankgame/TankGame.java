package com.tankgame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TankGame extends JFrame {
    private static final String VERSION_FILE = "/VERSION";
    private static final String ICON_FILE = "/images/icon.png";
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    private GamePanel gamePanel;
    private SoundManager soundManager;
    private Properties properties;

    public TankGame() {
        // 加载版本信息
        String version = "1.0.0";
        try (InputStream is = getClass().getResourceAsStream(VERSION_FILE)) {
            if (is != null) {
                version = new String(is.readAllBytes()).trim();
            }
        } catch (IOException e) {
            System.err.println("无法加载版本信息: " + e.getMessage());
        }

        // 设置窗口标题
        setTitle("坦克大战 v" + version);
        setSize(GAME_WIDTH, GAME_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 加载图标
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(ICON_FILE));
            if (icon.getImage() != null) {
                setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            System.err.println("无法加载图标: " + e.getMessage());
        }

        // 初始化游戏面板
        gamePanel = new GamePanel();
        add(gamePanel);

        // 初始化音效管理器
        soundManager = new SoundManager();
        gamePanel.setSoundManager(soundManager);

        // 加载配置
        loadConfig();

        // 设置窗口位置
        setLocationRelativeTo(null);
    }

    private void loadConfig() {
        properties = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            if (is != null) {
                properties.load(is);
            }
        } catch (IOException e) {
            System.err.println("无法加载配置文件: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("无法设置系统外观: " + e.getMessage());
        }

        // 创建并显示游戏窗口
        EventQueue.invokeLater(() -> {
            TankGame game = new TankGame();
            game.setVisible(true);
        });
    }
} 