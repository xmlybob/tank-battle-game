package com.tankgame;

import java.awt.*;

public class Bullet {
    private int x, y;
    private double angle;
    private static final int SPEED = 15;
    private static final int SIZE = 8;
    private boolean fromPlayer;
    private int bounceCount = 0;  // 反射次数计数
    private static final int MAX_BOUNCES = 3;  // 最大反射次数
    
    public Bullet(int x, int y, double angle, boolean fromPlayer) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.fromPlayer = fromPlayer;
    }
    
    public void move() {
        int nextX = x + (int)(Math.cos(angle) * SPEED);
        int nextY = y + (int)(Math.sin(angle) * SPEED);
        
        boolean bounced = false;
        
        // 检查是否会碰到边界
        if (nextX <= 0 || nextX >= TankGame.GAME_WIDTH) {
            if (bounceCount < MAX_BOUNCES) {
                angle = Math.PI - angle;  // 水平反射
                bounceCount++;
                bounced = true;
            }
        }
        if (nextY <= 0 || nextY >= TankGame.GAME_HEIGHT) {
            if (bounceCount < MAX_BOUNCES) {
                angle = -angle;  // 垂直反射
                bounceCount++;
                bounced = true;
            }
        }
        
        // 如果发生了反射且还没有超过最大反射次数，重新计算下一个位置
        if (bounced) {
            nextX = x + (int)(Math.cos(angle) * SPEED);
            nextY = y + (int)(Math.sin(angle) * SPEED);
        }
        
        x = nextX;
        y = nextY;
    }
    
    public void draw(Graphics g) {
        g.setColor(fromPlayer ? Color.YELLOW : Color.ORANGE);
        g.fillOval(x - SIZE/2, y - SIZE/2, SIZE, SIZE);
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x - SIZE/2, y - SIZE/2, SIZE, SIZE);
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public boolean isFromPlayer() {
        return fromPlayer;
    }
    
    public boolean shouldRemove() {
        return bounceCount >= MAX_BOUNCES && 
               (x <= 0 || x >= TankGame.GAME_WIDTH || y <= 0 || y >= TankGame.GAME_HEIGHT);
    }
} 