package game;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Kezeli a zenéket, hangeffekteket,
 * le tudja játszani, és leállítani a zenét
 */
public class Sounds {
    private static Clip clip;
    public static void playMusic (final String url) {

        try {
            clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    MainClass.class.getResourceAsStream("/sounds/" + url));
            clip.open(inputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);


        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void playSound(final String url){
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    MainClass.class.getResourceAsStream("/sounds/" + url));
            clip.open(inputStream);
            clip.start();


        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void stopMusic(){
        clip.stop ();
    }
}
