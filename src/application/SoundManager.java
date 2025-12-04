package application;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager {

    private static SoundManager instance;
    private MediaPlayer musicPlayer;
    
    // Default volumes (0.0 to 1.0)
    private double musicVolume = 0.5;
    private double sfxVolume = 0.5;

    private SoundManager() {
        // Private constructor for Singleton
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

       public void playMusic(String resourcePath) {
        try {
            // Stop old music if playing
            if (musicPlayer != null) {
                musicPlayer.stop();
                musicPlayer.dispose();
            }

            URL resource = getClass().getResource(resourcePath);
            if (resource == null) {
                System.out.println("Music file not found: " + resourcePath);
                return;
            }

            Media media = new Media(resource.toString());
            musicPlayer = new MediaPlayer(media);
            musicPlayer.setVolume(musicVolume);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop forever
            musicPlayer.play();
            
        } catch (Exception e) {
            System.out.println("Error playing music: " + e.getMessage());
        }
    }

    public void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    public void setMusicVolume(double volume) {
        this.musicVolume = volume;
        if (musicPlayer != null) {
            musicPlayer.setVolume(volume);
        }
    }
    
    public double getMusicVolume() { return musicVolume; }

    public void playSFX(String resourcePath) {
        try {
            URL resource = getClass().getResource(resourcePath);
            if (resource == null) {
                System.out.println("SFX file not found: " + resourcePath);
                return;
            }

            AudioClip clip = new AudioClip(resource.toString());
            clip.setVolume(sfxVolume);
            clip.play();

        } catch (Exception e) {
            System.out.println("Error playing SFX: " + e.getMessage());
        }
    }

    public void setSFXVolume(double volume) {
        this.sfxVolume = volume;
    }
    
    public double getSFXVolume() { return sfxVolume; }
}