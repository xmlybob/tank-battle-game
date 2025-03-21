package com.tankgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Iterator;

public class GamePanel extends JPanel implements KeyListener, MouseMotionListener, MouseListener {
    private Tank playerTank;
    private List<Tank> enemyTanks;
    private List<Bullet> bullets;
    private List<Boss> bosses;
    private long lastBossSpawnTime;
    private static final long BOSS_SPAWN_INTERVAL = 10000; // 改为每10秒检查一次
    private static double BOSS_SPAWN_CHANCE = 0.6; // 提高出现概率到60%
    private static final double BOSS_DEATH_CHANCE = 0.2; // 20%概率击杀Boss
    private static double ENEMY_RESPAWN_CHANCE = 0.3; // 30%概率重生敌人
    private boolean gameOver;
    private int currentLevel = 1;
    private static final int MAX_LEVEL = 3;
    private boolean levelCompleted = false;
    private SoundManager soundManager;
    private Point mousePosition = new Point();
    private int currentScore = 0;  // 当前得分
    private int totalPossibleScore = 0;  // 关卡总分
    private int bulletsFired = 0;  // 发射的炮弹数量
    private int bulletsHit = 0;   // 命中的炮弹数量
    
    // 记录按键状态
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private boolean spacePressed = false; // 添加空格键状态
    private long lastShootTime = 0; // 上次发射时间
    private static final long SHOOT_COOLDOWN = 100; // 发射冷却时间从200毫秒减少到100毫秒

    // 游戏参数
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;
    private static final int PLAYER_SPEED = 5;
    private static final int BULLET_SPEED = 10;
    private static final int ENEMY_SPAWN_INTERVAL = 2000;  // 2秒
    private static final int MAX_ENEMIES = 5;
    private static final int MAX_BOSSES = 1;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        
        initGame();
        startGameLoop();
    }

    public void setSoundManager(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    private void initGame() {
        playerTank = new Tank(TankGame.GAME_WIDTH / 2, TankGame.GAME_HEIGHT - 100, Tank.Direction.UP, true);
        enemyTanks = new CopyOnWriteArrayList<>();
        bullets = new CopyOnWriteArrayList<>();
        bosses = new CopyOnWriteArrayList<>();
        lastBossSpawnTime = System.currentTimeMillis();
        gameOver = false;
        levelCompleted = false;
        currentScore = 0;
        totalPossibleScore = 0;
        initLevel(currentLevel);
    }

    private void initLevel(int level) {
        enemyTanks.clear();
        bullets.clear();
        
        // 根据关卡设置不同数量和位置的敌方坦克
        switch (level) {
            case 1:
                // 第一关：4个敌人，从四个方向各出现一个
                spawnEnemyTank(50, 50, Tank.Direction.DOWN); // 上方
                spawnEnemyTank(50, TankGame.GAME_HEIGHT - 150, Tank.Direction.UP); // 下方
                spawnEnemyTank(50, TankGame.GAME_HEIGHT / 2, Tank.Direction.RIGHT); // 左方
                spawnEnemyTank(TankGame.GAME_WIDTH - 100, TankGame.GAME_HEIGHT / 2, Tank.Direction.LEFT); // 右方
                break;
            case 2:
                // 第二关：8个敌人，每个方向2个
                spawnEnemyTank(50, 50, Tank.Direction.DOWN);
                spawnEnemyTank(TankGame.GAME_WIDTH - 100, 50, Tank.Direction.DOWN);
                spawnEnemyTank(50, TankGame.GAME_HEIGHT - 150, Tank.Direction.UP);
                spawnEnemyTank(TankGame.GAME_WIDTH - 100, TankGame.GAME_HEIGHT - 150, Tank.Direction.UP);
                spawnEnemyTank(50, 150, Tank.Direction.RIGHT);
                spawnEnemyTank(50, TankGame.GAME_HEIGHT - 250, Tank.Direction.RIGHT);
                spawnEnemyTank(TankGame.GAME_WIDTH - 100, 150, Tank.Direction.LEFT);
                spawnEnemyTank(TankGame.GAME_WIDTH - 100, TankGame.GAME_HEIGHT - 250, Tank.Direction.LEFT);
                break;
            case 3:
                // 第三关：12个敌人，每个方向3个
                for (int i = 0; i < 3; i++) {
                    // 上方
                    spawnEnemyTank(100 + i * 200, 50, Tank.Direction.DOWN);
                    // 下方
                    spawnEnemyTank(100 + i * 200, TankGame.GAME_HEIGHT - 150, Tank.Direction.UP);
                    // 左方
                    spawnEnemyTank(50, 100 + i * 150, Tank.Direction.RIGHT);
                    // 右方
                    spawnEnemyTank(TankGame.GAME_WIDTH - 100, 100 + i * 150, Tank.Direction.LEFT);
                }
                break;
        }
    }

    private void spawnEnemyTank(int x, int y, Tank.Direction direction) {
        Tank enemy = new Tank(x, y, direction, false);
        enemy.setSpeed(3 + currentLevel);
        enemyTanks.add(enemy);
        totalPossibleScore += (currentLevel == 1 ? 100 : (currentLevel == 2 ? 150 : 200));
    }

    private void spawnNewEnemy() {
        // 随机选择出现位置（上、下、左、右四个方向）
        int position = (int)(Math.random() * 4);
        int x, y;
        Tank.Direction direction;
        
        switch (position) {
            case 0: // 上方
                x = (int)(Math.random() * (TankGame.GAME_WIDTH - 100) + 50);
                y = 50;
                direction = Tank.Direction.DOWN;
                break;
            case 1: // 下方
                x = (int)(Math.random() * (TankGame.GAME_WIDTH - 100) + 50);
                y = TankGame.GAME_HEIGHT - 150;
                direction = Tank.Direction.UP;
                break;
            case 2: // 左方
                x = 50;
                y = (int)(Math.random() * (TankGame.GAME_HEIGHT - 200) + 100);
                direction = Tank.Direction.RIGHT;
                break;
            default: // 右方
                x = TankGame.GAME_WIDTH - 100;
                y = (int)(Math.random() * (TankGame.GAME_HEIGHT - 200) + 100);
                direction = Tank.Direction.LEFT;
                break;
        }
        
        spawnEnemyTank(x, y, direction);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition = e.getPoint();
        if (playerTank != null && !gameOver && !levelCompleted) {
            updatePlayerAngle();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mousePosition = e.getPoint();
        if (playerTank != null && !gameOver && !levelCompleted) {
            updatePlayerAngle();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // 移除鼠标点击发射子弹的功能
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    private void updatePlayerAngle() {
        // 计算鼠标位置相对于玩家坦克的角度
        double dx = mousePosition.x - playerTank.getX();
        double dy = mousePosition.y - playerTank.getY();
        double angle = Math.atan2(dy, dx);
        playerTank.setAngle(angle);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                // 重新开始游戏
                currentLevel = 1;
                gameOver = false;
                levelCompleted = false;
                initGame();
            }
            return;
        }
        
        if (levelCompleted) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER && currentLevel < MAX_LEVEL) {
                currentLevel++;
                initLevel(currentLevel);
                levelCompleted = false;
                // 重置玩家坦克位置
                playerTank = new Tank(TankGame.GAME_WIDTH / 2, TankGame.GAME_HEIGHT - 100, Tank.Direction.UP, true);
                updatePlayerAngle(); // 更新角度以对准鼠标
            }
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_M && soundManager != null) {
            soundManager.toggleSound();
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                upPressed = true;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                downPressed = true;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                spacePressed = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                upPressed = false;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                downPressed = false;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
            case KeyEvent.VK_SPACE:
                spacePressed = false;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    private void updatePlayerMovement() {
        if (!gameOver && !levelCompleted) {
            double moveAngle = 0;
            boolean moving = false;
            
            if (upPressed) {
                if (rightPressed) moveAngle = Math.PI * 1.75;
                else if (leftPressed) moveAngle = Math.PI * 1.25;
                else moveAngle = Math.PI * 1.5;
                moving = true;
            }
            else if (downPressed) {
                if (rightPressed) moveAngle = Math.PI * 0.25;
                else if (leftPressed) moveAngle = Math.PI * 0.75;
                else moveAngle = Math.PI * 0.5;
                moving = true;
            }
            else if (leftPressed) {
                moveAngle = Math.PI;
                moving = true;
            }
            else if (rightPressed) {
                moveAngle = 0;
                moving = true;
            }
            
            if (moving) {
                double oldX = playerTank.getX();
                double oldY = playerTank.getY();
                playerTank.setAngle(moveAngle);
                playerTank.move();
                // 移动后立即恢复瞄准角度
                updatePlayerAngle();
            }
        }
    }

    private void startGameLoop() {
        new Thread(() -> {
            while (true) {  // 改为永远运行
                if (!gameOver) {  // 只在游戏未结束时更新游戏状态
                    updatePlayerMovement();
                    updateGame();
                }
                repaint();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateGame() {
        // 检查是否需要发射子弹
        if (spacePressed && !gameOver && !levelCompleted) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShootTime >= SHOOT_COOLDOWN) {
                bullets.add(playerTank.fire());
                if (soundManager != null) soundManager.playShoot();
                lastShootTime = currentTime;
                bulletsFired++;
            }
        }

        // 更新所有子弹的位置
        List<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bullet.move();
            if (bullet.shouldRemove()) {
                bulletsToRemove.add(bullet);
                continue;
            }

            Rectangle bulletRect = bullet.getBounds();
            boolean bulletHit = false;

            // 检查子弹与敌方坦克的碰撞
            for (Tank enemyTank : enemyTanks) {
                if (bulletRect.intersects(enemyTank.getBounds())) {
                    bulletsToRemove.add(bullet);
                    enemyTank.takeDamage(1);
                    if (enemyTank.getHealth() <= 0) {
                        enemyTanks.remove(enemyTank);
                        currentScore += (currentLevel == 1 ? 100 : (currentLevel == 2 ? 150 : 200));
                        if (soundManager != null) soundManager.playExplosion();
                    } else {
                        if (soundManager != null) soundManager.playHitTank();
                    }
                    bulletHit = true;
                    break;
                }
            }

            if (!bulletHit) {
                // 检查子弹与Boss的碰撞
                for (Boss boss : bosses) {
                    if (bulletRect.intersects(boss.getBounds())) {
                        bulletsToRemove.add(bullet);
                        if (boss.hit()) {
                            bosses.remove(boss);
                            currentScore += 500;
                            if (soundManager != null) soundManager.playExplosion();
                        } else {
                            if (soundManager != null) soundManager.playHitBoss();
                        }
                        break;
                    }
                }
            }
        }
        
        // 移除需要删除的子弹
        bullets.removeAll(bulletsToRemove);

        // 检查是否需要生成新的Boss
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBossSpawnTime > BOSS_SPAWN_INTERVAL && !gameOver && !levelCompleted) {
            if (Math.random() < BOSS_SPAWN_CHANCE) {
                spawnBoss();
                lastBossSpawnTime = currentTime;
                totalPossibleScore += 500;
            }
        }

        // 检查是否需要重生敌人
        if (Math.random() < ENEMY_RESPAWN_CHANCE && enemyTanks.size() < 5 && !gameOver && !levelCompleted) {
            spawnNewEnemy();
        }

        // 更新Boss移动
        for (Boss boss : bosses) {
            boss.move();
        }

        // Check if level is completed
        if (enemyTanks.isEmpty() && !levelCompleted) {
            if (currentLevel == MAX_LEVEL) {
                gameOver = true;
                if (soundManager != null) soundManager.playGameOver();
            } else {
                levelCompleted = true;
                if (soundManager != null) soundManager.playLevelUp();
            }
        }

        // Update enemy tanks movement
        for (Tank enemyTank : enemyTanks) {
            // Randomly change direction (10% chance)
            if (Math.random() < 0.1) {
                Tank.Direction[] directions = Tank.Direction.values();
                enemyTank.setDirection(directions[(int)(Math.random() * directions.length)]);
            }
            
            // Move enemy tank
            enemyTank.move();
            
            // Check collision with player tank
            if (enemyTank.getBounds().intersects(playerTank.getBounds())) {
                if (playerTank.hit()) {
                    gameOver = true;
                    if (soundManager != null) soundManager.playGameOver();
                }
            }
            
            // Check collision with other enemy tanks and reverse direction if needed
            for (Tank otherTank : enemyTanks) {
                if (otherTank != enemyTank && enemyTank.getBounds().intersects(otherTank.getBounds())) {
                    // Reverse direction
                    switch (enemyTank.getDirection()) {
                        case UP: enemyTank.setDirection(Tank.Direction.DOWN); break;
                        case DOWN: enemyTank.setDirection(Tank.Direction.UP); break;
                        case LEFT: enemyTank.setDirection(Tank.Direction.RIGHT); break;
                        case RIGHT: enemyTank.setDirection(Tank.Direction.LEFT); break;
                    }
                    enemyTank.move(); // Move away from collision
                    break;
                }
            }
        }

        // 检查Boss与玩家的碰撞
        for (Boss boss : bosses) {
            if (boss.getBounds().intersects(playerTank.getBounds())) {
                gameOver = true;
                if (soundManager != null) soundManager.playGameOver();
                break;
            }
        }

        // 在updateGame方法中修改关卡升级部分
        if (currentScore >= currentLevel * 1000) {
            currentLevel++;
            currentScore = 0;
            if (soundManager != null) soundManager.playLevelUp();
            // 增加难度
            ENEMY_RESPAWN_CHANCE += 0.1;
            BOSS_SPAWN_CHANCE += 0.05;
        }
    }

    private void spawnBoss() {
        // 随机选择Boss类型
        Boss.BossType[] types = Boss.BossType.values();
        Boss.BossType type = types[(int)(Math.random() * types.length)];
        
        // 在屏幕上方随机位置生成Boss
        int x = (int)(Math.random() * (TankGame.GAME_WIDTH - 100) + 50);
        int y = 50;
        
        bosses.add(new Boss(x, y, type));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw player tank
        playerTank.draw(g);
        
        // Draw enemy tanks
        for (Tank tank : enemyTanks) {
            tank.draw(g);
        }
        
        // Draw bosses
        for (Boss boss : bosses) {
            boss.draw(g);
        }
        
        // Draw bullets
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }

        // 在屏幕底部绘制分数和炮弹信息
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        
        // 显示分数
        String scoreText = String.format("当前得分: %d / %d", currentScore, totalPossibleScore);
        FontMetrics metrics = g.getFontMetrics();
        int scoreX = (TankGame.GAME_WIDTH - metrics.stringWidth(scoreText)) / 2;
        int scoreY = TankGame.GAME_HEIGHT - 50;
        g.drawString(scoreText, scoreX, scoreY);
        
        // 显示炮弹统计
        String bulletStats = String.format("发射: %d  命中: %d  命中率: %.1f%%", 
            bulletsFired, 
            bulletsHit,
            bulletsFired > 0 ? (bulletsHit * 100.0 / bulletsFired) : 0.0);
        int bulletStatsX = (TankGame.GAME_WIDTH - metrics.stringWidth(bulletStats)) / 2;
        g.drawString(bulletStats, bulletStatsX, scoreY + 25);

        // Draw game instructions and level info
        if (!gameOver && !levelCompleted) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString("操作说明：", 10, 20);
            g.drawString("WASD或方向键：移动坦克", 10, 40);
            g.drawString("鼠标移动：瞄准", 10, 60);
            g.drawString("空格键：发射子弹", 10, 80);
            g.drawString("M键：开关音效", 10, 100);
            g.drawString("当前关卡：" + currentLevel, 10, 120);
            g.drawString("剩余敌人：" + enemyTanks.size(), TankGame.GAME_WIDTH - 150, 20);
            g.drawString("Boss数量：" + bosses.size(), TankGame.GAME_WIDTH - 150, 40);
            g.drawString("音效状态：" + (soundManager != null && soundManager.isSoundEnabled() ? "开启" : "关闭"), TankGame.GAME_WIDTH - 150, 60);
        }

        // Draw level completed message
        if (levelCompleted) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String levelCompleteText = "第 " + currentLevel + " 关完成！";
            metrics = g.getFontMetrics();
            int x = (TankGame.GAME_WIDTH - metrics.stringWidth(levelCompleteText)) / 2;
            int y = TankGame.GAME_HEIGHT / 2;
            g.drawString(levelCompleteText, x, y);
            
            if (currentLevel < MAX_LEVEL) {
                g.setFont(new Font("Arial", Font.PLAIN, 24));
                String nextLevelText = "按回车键进入下一关";
                metrics = g.getFontMetrics();
                x = (TankGame.GAME_WIDTH - metrics.stringWidth(nextLevelText)) / 2;
                g.drawString(nextLevelText, x, y + 50);
            }
        }

        // Draw game over message
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String gameOverText = enemyTanks.isEmpty() ? "恭喜通关！" : "Game Over!";
            metrics = g.getFontMetrics();
            int x = (TankGame.GAME_WIDTH - metrics.stringWidth(gameOverText)) / 2;
            int y = TankGame.GAME_HEIGHT / 2;
            g.drawString(gameOverText, x, y);
            
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            String restartText = "按回车键重新开始游戏";
            metrics = g.getFontMetrics();
            x = (TankGame.GAME_WIDTH - metrics.stringWidth(restartText)) / 2;
            g.drawString(restartText, x, y + 50);
            
            // 在游戏结束时显示最终得分
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String finalScoreText = String.format("最终得分: %d / %d", currentScore, totalPossibleScore);
            metrics = g.getFontMetrics();
            x = (TankGame.GAME_WIDTH - metrics.stringWidth(finalScoreText)) / 2;
            g.drawString(finalScoreText, x, y + 100);
        }
    }
} 