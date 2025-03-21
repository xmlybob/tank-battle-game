package com.tankgame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;

public class Boss {
    private int x, y;
    private double angle = 0;
    private int health;
    private int speed;
    private BossType type;
    private long lastMoveTime;
    private static final int SIZE = 80;
    private int bodyLength = 0; // 龙的身体长度
    private boolean movingRight = true; // 移动方向
    private double floatOffset = 0; // 用于鬼的飘动效果
    private int maxHealth;
    private int width = 120;  // 增大Boss尺寸
    private int height = 120;
    private int movePattern = 0;
    private int animationFrame = 0;
    
    public enum BossType {
        DRAGON(8, Color.RED, 8, "龙王"),      // 红色龙王，速度快，血量高
        GHOST(4, Color.CYAN, 5, "幽灵"),      // 青色幽灵，速度中等
        DEMON(6, Color.MAGENTA, 6, "恶魔");   // 紫色恶魔，速度和血量平衡
        
        private final int speed;
        private final Color color;
        private final int health;
        private final String displayName;
        
        BossType(int speed, Color color, int health, String displayName) {
            this.speed = speed;
            this.color = color;
            this.health = health;
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    public Boss(int x, int y, BossType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.speed = type.speed;
        this.maxHealth = type.health;
        this.health = maxHealth;
        this.lastMoveTime = System.currentTimeMillis();
        this.bodyLength = 0;
        this.movingRight = true;
        this.floatOffset = 0;
    }
    
    public void move() {
        animationFrame = (animationFrame + 1) % 30;
        
        switch (type) {
            case DRAGON:
                // 龙王：快速左右移动并俯冲
                if (movePattern == 0) {
                    x += speed * 2;
                    if (x >= TankGame.GAME_WIDTH - width) {
                        movePattern = 1;
                    }
                } else {
                    x -= speed * 2;
                    if (x <= 0) {
                        movePattern = 0;
                    }
                }
                // 每隔一段时间进行俯冲攻击
                if (animationFrame == 0) {
                    y += height/2;
                    if (y >= TankGame.GAME_HEIGHT - height) {
                        y = 50;
                    }
                }
                bodyLength = Math.min(bodyLength + 1, SIZE * 2); // 逐渐伸长身体
                break;
                
            case GHOST:
                // 幽灵：飘忽不定的移动
                angle += 0.05;
                x += Math.cos(angle) * speed;
                y += Math.sin(angle) * speed * 0.5;
                // 确保不会飞出屏幕
                x = Math.max(0, Math.min(TankGame.GAME_WIDTH - width, x));
                y = Math.max(50, Math.min(TankGame.GAME_HEIGHT / 2, y));
                break;
                
            case DEMON:
                // 恶魔：十字形移动
                if (movePattern == 0) {
                    x += speed;
                    y += speed;
                    if (x >= TankGame.GAME_WIDTH - width || y >= TankGame.GAME_HEIGHT - height) {
                        movePattern = 1;
                    }
                } else {
                    x -= speed;
                    y -= speed;
                    if (x <= 0 || y <= 50) {
                        movePattern = 0;
                    }
                }
                break;
        }
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();
        
        switch (type) {
            case DRAGON:
                drawDragon(g2d);
                break;
            case GHOST:
                drawGhost(g2d);
                break;
            case DEMON:
                drawDemon(g2d);
                break;
        }
        
        g2d.setTransform(oldTransform);
        
        // 绘制血条和名字
        drawHealthBar(g);
    }
    
    private void drawDragon(Graphics2D g) {
        // 绘制龙王
        int[] xPoints = new int[7];
        int[] yPoints = new int[7];
        
        // 身体
        g.setColor(type.color);
        g.fillRect(x + width/4, y, width/2, height/2);
        
        // 头部
        g.fillOval(x, y - height/4, width/2, height/2);
        
        // 翅膀
        g.setColor(type.color.darker());
        // 左翅膀
        xPoints[0] = x; yPoints[0] = y + height/4;
        xPoints[1] = x - width/3; yPoints[1] = y;
        xPoints[2] = x - width/4; yPoints[2] = y + height/2;
        g.fillPolygon(xPoints, yPoints, 3);
        
        // 右翅膀
        xPoints[0] = x + width; yPoints[0] = y + height/4;
        xPoints[1] = x + width + width/3; yPoints[1] = y;
        xPoints[2] = x + width + width/4; yPoints[2] = y + height/2;
        g.fillPolygon(xPoints, yPoints, 3);
        
        // 眼睛
        g.setColor(Color.YELLOW);
        g.fillOval(x + width/8, y - height/8, width/10, height/10);
    }
    
    private void drawGhost(Graphics2D g) {
        // 绘制幽灵
        // 主体
        g.setColor(type.color);
        g.fillOval(x, y, width, height/2);
        
        // 飘动的下半身
        int waveOffset = (int)(Math.sin(animationFrame * 0.2) * 10);
        for (int i = 0; i < 3; i++) {
            g.fillOval(x + width/3 * i + waveOffset, y + height/2, 
                      width/3, height/3);
        }
        
        // 眼睛
        g.setColor(Color.WHITE);
        g.fillOval(x + width/4, y + height/4, width/6, height/6);
        g.fillOval(x + width*2/3, y + height/4, width/6, height/6);
        
        // 瞳孔
        g.setColor(Color.BLACK);
        g.fillOval(x + width/4 + width/12, y + height/4 + height/12, 
                  width/12, height/12);
        g.fillOval(x + width*2/3 + width/12, y + height/4 + height/12, 
                  width/12, height/12);
    }
    
    private void drawDemon(Graphics2D g) {
        // 绘制恶魔
        // 身体
        g.setColor(type.color);
        g.fillOval(x, y, width, height);
        
        // 角
        int[] xHorns = {x + width/4, x + width/2, x + width*3/4};
        int[] yHorns = {y, y - height/3, y};
        g.fillPolygon(xHorns, yHorns, 3);
        
        // 眼睛
        g.setColor(Color.RED);
        g.fillOval(x + width/4, y + height/3, width/6, height/6);
        g.fillOval(x + width*2/3, y + height/3, width/6, height/6);
        
        // 嘴
        g.setColor(Color.BLACK);
        g.drawArc(x + width/4, y + height/2, width/2, height/4, 0, -180);
    }
    
    private void drawHealthBar(Graphics g) {
        int barWidth = width;
        int barHeight = 6;
        int barY = y - 20;
        
        // 血条背景
        g.setColor(Color.GRAY);
        g.fillRect(x, barY, barWidth, barHeight);
        
        // 当前血量
        g.setColor(type.color);
        int currentWidth = (int)((float)health / maxHealth * barWidth);
        g.fillRect(x, barY, currentWidth, barHeight);
        
        // 显示Boss名称和血量
        String healthText = type.toString() + " HP:" + health;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(healthText)) / 2;
        g.drawString(healthText, textX, barY - 5);
    }
    
    public boolean hit() {
        health--;
        return health <= 0;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
} 