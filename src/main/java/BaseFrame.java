import javax.swing.*;
import java.awt.*;

public class BaseFrame extends JFrame {

    // Variabile statica condivisa tra tutte le finestre
    public static boolean isFullscreen = false;

    public BaseFrame(String title) {
        super(title);

        // Default size (se non in fullscreen)
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Metodo per applicare o togliere fullscreen
    public void setFullscreen(boolean fullscreen) {
        isFullscreen = fullscreen;
        dispose(); // necessario per cambiare il decorato
        setUndecorated(fullscreen);
        setVisible(true);
        if (fullscreen) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setSize(1400, 800);
            setLocationRelativeTo(null);
        }
    }
}
