package studiokita;

import studiokita.view.LoginForm;
import javax.swing.*;
import java.awt.*;

/**
 * ============================================================
 *  StudioApp — Entry Point Aplikasi Studio Kita (Premium UAS)
 * ============================================================
 */
public class StudioApp {

    public static void main(String[] args) {
        // High-quality rendering settings
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Setup Look and Feel (Using System L&F as base)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Note: Using cross-platform Look & Feel.");
        }

        // Start Application
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
