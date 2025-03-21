package com.tankgame;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class SoundManager {
    private static final String CONFIG_FILE = "/config.properties";
    private static final String SOUND_DIR = "/sounds/";
    private static final String[] SOUND_FILES = {
        "shoot.wav",
        "explosion.wav",
        "hit_tank.wav",
        "hit_boss.wav",
        "powerup.wav",
        "gameover.wav",
        "levelup.wav",
        "boss_spawn.wav"
    };

    private Clip[] clips;
    private boolean soundEnabled;
    private float volume;
    private Properties config;

    public SoundManager() {
        clips = new Clip[SOUND_FILES.length];
        loadConfig();
        loadSounds();
    }

    private void loadConfig() {
        config = new Properties();
        try (InputStream is = getClass().getResourceAsStream(CONFIG_FILE)) {
            if (is != null) {
                config.load(is);
                soundEnabled = Boolean.parseBoolean(config.getProperty("sound.enabled", "true"));
                volume = Float.parseFloat(config.getProperty("sound.volume", "0.8"));
            }
        } catch (IOException e) {
            System.err.println("无法加载音效配置: " + e.getMessage());
            soundEnabled = true;
            volume = 0.8f;
        }
    }

    private void loadSounds() {
        for (int i = 0; i < SOUND_FILES.length; i++) {
            String soundFile = SOUND_DIR + SOUND_FILES[i];
            try {
                URL soundURL = getClass().getResource(soundFile);
                if (soundURL != null) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
                    clips[i] = AudioSystem.getClip();
                    clips[i].open(audioIn);
                    
                    // 设置音量
                    if (clips[i] instanceof FloatControl) {
                        FloatControl gainControl = (FloatControl) clips[i].getControl(FloatControl.Type.MASTER_GAIN);
                        float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                        gainControl.setValue(dB);
                    }
                } else {
                    System.err.println("警告：找不到音效文件: " + soundFile);
                }
            } catch (Exception e) {
                System.err.println("警告：无法加载音效文件 " + soundFile + ": " + e.getMessage());
            }
        }
    }

    public void playShoot() {
        if (soundEnabled) playSound(0);
    }

    public void playExplosion() {
        if (soundEnabled) playSound(1);
    }

    public void playHitTank() {
        if (soundEnabled) playSound(2);
    }

    public void playHitBoss() {
        if (soundEnabled) playSound(3);
    }

    public void playPowerUp() {
        if (soundEnabled) playSound(4);
    }

    public void playGameOver() {
        if (soundEnabled) playSound(5);
    }

    public void playLevelUp() {
        if (soundEnabled) playSound(6);
    }

    public void playBossSpawn() {
        if (soundEnabled) playSound(7);
    }

    private void playSound(int index) {
        if (clips[index] != null) {
            clips[index].setFramePosition(0);
            clips[index].start();
        }
    }

    public void toggleSound() {
        soundEnabled = !soundEnabled;
        if (!soundEnabled) {
            stopAllSounds();
        }
    }

    private void stopAllSounds() {
        for (Clip clip : clips) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
                clip.setFramePosition(0);
            }
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }
} 