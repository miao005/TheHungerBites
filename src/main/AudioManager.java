package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class AudioManager {
    private Clip clip;
    private float currentVolume = 0.5f; // remembered across clip changes

    public void playMusic(String filePath) {
        try {
            URL url = getClass().getResource(filePath);
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audio);
            setClipVolume(clip, currentVolume); // use saved volume, not hardcoded
            if (filePath.contains("gameover")) {
                clip.start();
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
            setClipVolume(sfx, 0.8f);
            sfx.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVolume(float volume) {
        currentVolume = volume; // remember it
        if (clip != null) setClipVolume(clip, volume);
    }

    private void setClipVolume(Clip c, float volume) {
        FloatControl gain = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
        gain.setValue(20f * (float) Math.log10(Math.max(volume, 0.0001f)));
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    public void stopMusic()  { if (clip != null) clip.stop(); }
    public void pauseMusic() { if (clip != null) clip.stop(); }
    public void resumeMusic(){ if (clip != null) clip.start(); }
}