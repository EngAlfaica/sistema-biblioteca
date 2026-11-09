import gui.Estilos;
import gui.JanelaLogin;
import javax.swing.*;

/**
 * Ponto de entrada da aplicação.
 */
public class Main {
    public static void main(String[] args) {
        // Garante que a interface é criada na thread correcta do Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            Estilos.aplicarTema();
            JanelaLogin login = new JanelaLogin();
            login.setVisible(true);
        });
    }
}