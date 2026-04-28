package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.IOException;
import java.net.URL;

public class AudioManager {
    private Clip clip;

    public void playMusic(String filePath){
        try {
            URL url = getClass().getResource(filePath);
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audio);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // loops forever
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopMusic(){
        if(clip != null)
            clip.stop();
    }

    public void pauseMusic(){
        if(clip != null)
            clip.stop();
    }

    public void resumeMusic(){
        if(clip != null)
            clip.start();
    }
}