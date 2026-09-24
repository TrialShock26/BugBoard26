package frontend.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowStateListener;

public class BaseFrame extends JFrame {

    // Stato "massimizzato" condiviso tra tutte le finestre dell'applicazione:
    // viene aggiornato automaticamente quando l'utente massimizza/ripristina
    // la finestra, e riapplicato ad ogni nuova schermata aperta dalla sidebar,
    // cosi' non serve reimpostarlo manualmente ogni volta che si cambia pagina.
    private static boolean maximized = false;

    public BaseFrame(String title) {
        super(title);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Dimensione di default (usata se la finestra non e' massimizzata)
        setSize(1400, 800);
        setLocationRelativeTo(null);

        // Riapplica lo stato massimizzato ereditato dalla schermata precedente
        if (maximized) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        // Tiene traccia di eventuali cambi di stato fatti manualmente dall'utente
        // (doppio click sulla barra del titolo, pulsante massimizza/ripristina, ecc.)
        WindowStateListener listener = e ->
                maximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
        addWindowStateListener(listener);
    }


    protected void navigateTo(JFrame next) {
        for (WindowStateListener l : getWindowStateListeners()) {
            removeWindowStateListener(l);
        }
        next.setVisible(true);
        dispose();
    }
}

