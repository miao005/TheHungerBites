package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class AudioManager {
    private Clip clip;

    public void playMusic(String filePath) {
        try {
            URL url = getClass().getResource(filePath);
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audio);
            setVolume(0.5f); // change this to adjust music volume (0.0 - 1.0)
            if (filePath.contains("gameover")) {
                clip.start(); // plays once, no loop
            } else {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSFX(String filePath) {
        try {
            URL url = getClass().getResource(filePath);
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            Clip sfx = AudioSystem.getClip();
            sfx.open(audio);
            setClipVolume(sfx, 0.8f); // change this to adjust SFX volume (0.0 - 1.0)
            sfx.start(); // plays once, no loop, won't interrupt music
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVolume(float volume) {
        if (clip != null) setClipVolume(clip, volume);
    }

    private void setClipVolume(Clip c, float volume) {
        FloatControl gain = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
        gain.setValue(20f * (float) Math.log10(Math.max(volume, 0.0001f)));
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    public void stopMusic() {
        if (clip != null) clip.stop();
    }

    public void pauseMusic() {
        if (clip != null) clip.stop();
    }

    public void resumeMusic() {
        if (clip != null) clip.start();
    }
}