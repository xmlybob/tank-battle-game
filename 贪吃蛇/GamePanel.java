import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.LinkedList;
import java.util.Queue;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 50;  // 加快速度
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean autoPlay = true;  // 自动模式
    Timer timer;
    Random random;
    private boolean[][] visited;
    private char[][] path;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        // 初始化蛇的位置
        for(int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("分数: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("分数: " + applesEaten))/2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        // 检查是否还有空间放置食物
        boolean hasSpace = false;
        boolean[][] occupied = new boolean[SCREEN_WIDTH/UNIT_SIZE][SCREEN_HEIGHT/UNIT_SIZE];
        
        // 标记蛇身占据的位置
        for(int i = 0; i < bodyParts; i++) {
            int gridX = x[i] / UNIT_SIZE;
            int gridY = y[i] / UNIT_SIZE;
            if(gridX >= 0 && gridX < SCREEN_WIDTH/UNIT_SIZE && 
               gridY >= 0 && gridY < SCREEN_HEIGHT/UNIT_SIZE) {
                occupied[gridX][gridY] = true;
            }
        }
        
        // 检查是否还有空位
        for(int i = 0; i < SCREEN_WIDTH/UNIT_SIZE; i++) {
            for(int j = 0; j < SCREEN_HEIGHT/UNIT_SIZE; j++) {
                if(!occupied[i][j]) {
                    hasSpace = true;
                    break;
                }
            }
            if(hasSpace) break;
        }
        
        if(!hasSpace) {
            running = false;
            return;
        }

        // 生成新食物
        do {
            appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
            appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
        } while(isAppleOnSnake());
    }

    private boolean isAppleOnSnake() {
        for(int i = 0; i < bodyParts; i++) {
            if(x[i] == appleX && y[i] == appleY) {
                return true;
            }
        }
        return false;
    }

    public void autoMove() {
        // 使用简单的寻路算法
        int headX = x[0];
        int headY = y[0];
        
        // 决定下一步移动方向
        if(headX < appleX && canMove('R')) {
            direction = 'R';
        } else if(headX > appleX && canMove('L')) {
            direction = 'L';
        } else if(headY > appleY && canMove('U')) {
            direction = 'U';
        } else if(headY < appleY && canMove('D')) {
            direction = 'D';
        } else {
            // 如果不能直接移动到食物，选择一个安全的方向
            if(canMove('R')) direction = 'R';
            else if(canMove('D')) direction = 'D';
            else if(canMove('L')) direction = 'L';
            else if(canMove('U')) direction = 'U';
        }
    }

    private boolean canMove(char dir) {
        int nextX = x[0];
        int nextY = y[0];
        
        switch(dir) {
            case 'U': nextY -= UNIT_SIZE; break;
            case 'D': nextY += UNIT_SIZE; break;
            case 'L': nextX -= UNIT_SIZE; break;
            case 'R': nextX += UNIT_SIZE; break;
        }
        
        // 检查是否会撞墙
        if(nextX < 0 || nextX >= SCREEN_WIDTH || 
           nextY < 0 || nextY >= SCREEN_HEIGHT) {
            return false;
        }
        
        // 检查是否会撞到自己
        for(int i = 0; i < bodyParts; i++) {
            if(nextX == x[i] && nextY == y[i]) {
                return false;
            }
        }
        
        return true;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch(direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // 检查是否撞到自己
        for(int i = bodyParts; i > 0; i--) {
            if((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        // 检查是否撞到左边界
        if(x[0] < 0) {
            running = false;
        }
        // 检查是否撞到右边界
        if(x[0] > SCREEN_WIDTH - UNIT_SIZE) {
            running = false;
        }
        // 检查是否撞到上边界
        if(y[0] < 0) {
            running = false;
        }
        // 检查是否撞到下边界
        if(y[0] > SCREEN_HEIGHT - UNIT_SIZE) {
            running = false;
        }

        if(!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        // 显示分数
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("分数: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("分数: " + applesEaten))/2, g.getFont().getSize());

        // 游戏结束文本
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("游戏结束", (SCREEN_WIDTH - metrics2.stringWidth("游戏结束"))/2, SCREEN_HEIGHT/2);
        
        // 添加重新开始提示
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("按空格键重新开始", (SCREEN_WIDTH - metrics3.stringWidth("按空格键重新开始"))/2, SCREEN_HEIGHT/2 + 100);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            if(autoPlay) {
                autoMove();
            }
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if(!running) {
                        startGame();
                    }
                    break;
                case KeyEvent.VK_A:  // 按A键切换自动/手动模式
                    autoPlay = !autoPlay;
                    break;
            }
        }
    }
} 