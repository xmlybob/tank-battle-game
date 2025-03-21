package com.tankgame;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Tank {
    private int x, y;
    private Direction direction;
    private double angle;
    private int speed = 5;
    private boolean isPlayer;
    private static final int SIZE = 40;
    private static final int trackHeight = 8;
    private static final int wheelSize = 6;
    private static final int turretSize = 30;
    private static final int cannonLength = 30;
    private static final int cannonWidth = 6;
    private static final int dotSize = 4;
    private int health;  // 坦克血量
    private int maxHealth;  // 最大血量
    
    public enum Direction {
        UP(Math.PI * 1.5), DOWN(Math.PI * 0.5), LEFT(Math.PI), RIGHT(0);
        
        private final double angle;
        
        Direction(double angle) {
            this.angle = angle;
        }
        
        public double getAngle() {
            return angle;
        }
    }
    
    public Tank(int x, int y, Direction direction, boolean isPlayer) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.angle = direction.getAngle();
        this.isPlayer = isPlayer;
        // 设置血量
        if (isPlayer) {
            this.maxHealth = 5;  // 玩家5点血
        } else {
            this.maxHealth = 3;  // 敌人3点血
        }
        this.health = this.maxHealth;
    }
    
    public void setDirection(Direction direction) {
        this.direction = direction;
        this.angle = direction.getAngle();
    }
    
    public void setAngle(double angle) {
        this.angle = angle;
        double minDiff = Double.MAX_VALUE;
        for (Direction dir : Direction.values()) {
            double diff = Math.abs(normalizeAngle(angle - dir.getAngle()));
            if (diff < minDiff) {
                minDiff = diff;
                this.direction = dir;
            }
        }
    }
    
    private double normalizeAngle(double angle) {
        while (angle < 0) angle += Math.PI * 2;
        while (angle >= Math.PI * 2) angle -= Math.PI * 2;
        return angle;
    }
    
    public void move() {
        x += Math.cos(angle) * speed;
        y += Math.sin(angle) * speed;
        
        x = Math.max(SIZE/2, Math.min(TankGame.GAME_WIDTH - SIZE/2, x));
        y = Math.max(SIZE/2, Math.min(TankGame.GAME_HEIGHT - SIZE/2, y));
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();
        
        g2d.translate(x, y);
        g2d.rotate(angle);
        
        g.setColor(isPlayer ? Color.GREEN : Color.RED);
        
        g.fillRect(-SIZE/2, -SIZE/2, SIZE, trackHeight);
        g.fillRect(-SIZE/2, SIZE/2 - trackHeight, SIZE, trackHeight);
        
        g.setColor(Color.DARK_GRAY);
        for (int i = -2; i <= 2; i++) {
            g.fillOval(i * (SIZE/4) - wheelSize/2, -SIZE/2, wheelSize, wheelSize);
            g.fillOval(i * (SIZE/4) - wheelSize/2, SIZE/2 - wheelSize, wheelSize, wheelSize);
        }
        
        g.setColor(isPlayer ? Color.GREEN.darker() : Color.RED.darker());
        g.fillOval(-turretSize/2, -turretSize/2, turretSize, turretSize);
        
        g.fillRect(-cannonWidth/2, -cannonLength/2, cannonWidth, cannonLength);
        
        g.setColor(Color.WHITE);
        g.fillOval(-dotSize/2, -turretSize/2 + dotSize, dotSize, dotSize);
        
        g2d.setTransform(oldTransform);
        
        // 绘制血条
        drawHealthBar(g);
    }
    
    private void drawHealthBar(Graphics g) {
        int barWidth = SIZE;
        int barHeight = 4;
        
        // 绘制血条背景
        g.setColor(Color.GRAY);
        g.fillRect(x - barWidth/2, y - SIZE/2 - 10, barWidth, barHeight);
        
        // 绘制当前血量
        g.setColor(isPlayer ? Color.GREEN : Color.RED);
        int currentWidth = (int)((float)health / maxHealth * barWidth);
        g.fillRect(x - barWidth/2, y - SIZE/2 - 10, currentWidth, barHeight);
    }
    
    public Bullet fire() {
        int bulletX = x + (int)(Math.cos(angle) * cannonLength);
        int bulletY = y + (int)(Math.sin(angle) * cannonLength);
        return new Bullet(bulletX, bulletY, angle, isPlayer);
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x - SIZE/2, y - SIZE/2, SIZE, SIZE);
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public double getAngle() {
        return angle;
    }
    
    public boolean hit() {
        health--;
        return health <= 0;
    }
    
    public void takeDamage(int damage) {
        health -= damage;
    }
    
    public int getHealth() {
        return health;
    }
    
    public boolean isPlayer() {
        return isPlayer;
    }
} 