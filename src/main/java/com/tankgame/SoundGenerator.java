package com.tankgame;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class SoundGenerator {
    public static void main(String[] args) {
        try {
            // 创建sounds目录
            new File("src/main/resources/sounds").mkdirs();
            
            // 生成射击音效
            generateShootSound();
            
            // 生成爆炸音效
            generateExplosionSound();
            
            // 生成关卡完成音效
            generateLevelCompleteSound();
            
            // 生成游戏结束音效
            generateGameOverSound();
            
            System.out.println("音效文件生成完成！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void generateShootSound() throws IOException, LineUnavailableException {
        // 生成一个清脆的射击声
        byte[] data = new byte[44100 / 4]; // 0.25秒的采样
        for (int i = 0; i < data.length; i++) {
            double t = i / 44100.0;
            // 使用多个频率叠加，创造更丰富的声音
            data[i] = (byte) (
                (Math.sin(t * 880) * 0.6 +  // 基础频率
                Math.sin(t * 1760) * 0.3 +   // 高音
                Math.sin(t * 440) * 0.1) *   // 低音
                Math.exp(-t * 8) * 127       // 快速衰减
            );
        }
        
        AudioFormat format = new AudioFormat(44100, 8, 1, true, true);
        AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(data), format, data.length);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File("src/main/resources/sounds/shoot.wav"));
    }
    
    private static void generateExplosionSound() throws IOException, LineUnavailableException {
        // 生成一个更真实的爆炸声
        byte[] data = new byte[44100]; // 1秒的采样
        for (int i = 0; i < data.length; i++) {
            double t = i / 44100.0;
            // 使用多个频率和噪声叠加
            data[i] = (byte) (
                (Math.sin(t * 220) * 0.4 +   // 低频
                Math.sin(t * 440) * 0.3 +     // 中频
                Math.sin(t * 880) * 0.2 +     // 高频
                (Math.random() - 0.5) * 0.1) * // 噪声
                Math.exp(-t * 3) * 127        // 较慢的衰减
            );
        }
        
        AudioFormat format = new AudioFormat(44100, 8, 1, true, true);
        AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(data), format, data.length);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File("src/main/resources/sounds/explosion.wav"));
    }
    
    private static void generateLevelCompleteSound() throws IOException, LineUnavailableException {
        // 生成一个欢快的胜利音效
        byte[] data = new byte[44100 * 2]; // 2秒的采样
        for (int i = 0; i < data.length; i++) {
            double t = i / 44100.0;
            // 使用上升的音阶
            double freq = 440 + t * 440; // 从440Hz上升到880Hz
            data[i] = (byte) (
                (Math.sin(t * freq) * 0.7 +   // 主音
                Math.sin(t * freq * 2) * 0.3) * // 泛音
                Math.exp(-t * 0.5) * 127      // 缓慢衰减
            );
        }
        
        AudioFormat format = new AudioFormat(44100, 8, 1, true, true);
        AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(data), format, data.length);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File("src/main/resources/sounds/level_complete.wav"));
    }
    
    private static void generateGameOverSound() throws IOException, LineUnavailableException {
        // 生成一个低沉的游戏结束音效
        byte[] data = new byte[44100 * 2]; // 2秒的采样
        for (int i = 0; i < data.length; i++) {
            double t = i / 44100.0;
            // 使用下降的音阶
            double freq = 880 - t * 440; // 从880Hz下降到440Hz
            data[i] = (byte) (
                (Math.sin(t * freq) * 0.6 +   // 主音
                Math.sin(t * freq * 0.5) * 0.4) * // 低音
                Math.exp(-t * 0.3) * 127      // 非常缓慢的衰减
            );
        }
        
        AudioFormat format = new AudioFormat(44100, 8, 1, true, true);
        AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(data), format, data.length);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File("src/main/resources/sounds/game_over.wav"));
    }
} 