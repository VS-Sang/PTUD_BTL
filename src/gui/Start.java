package gui;

import javax.swing.SwingUtilities;

public class Start {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
