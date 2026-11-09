package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Janelas de diálogo da aplicação, no mesmo estilo do resto:
 *  - Formulario: campos com a legenda por cima, botões "Cancelar" e "Guardar";
 *  - confirmar(): pergunta Sim/Não com botão de acção (vermelho se for destrutiva);
 *  - aviso() e info(): mensagem com um só botão.
 * Substitui o JOptionPane, que tem aspecto antigo e pouco controlo.
 */
public final class Dialogo {

    private Dialogo() {
    }

    // ---------- Formulário ----------

    /** Construção: campo(...) para cada campo, depois mostrar(). Devolve true se o utilizador guardou. */
    public static class Formulario {
        private final String titulo;
        private final String textoConfirmar;
        private final JPanel corpo = new JPanel();
        private JTextField primeiroEditavel;

        public Formulario(String titulo, String textoConfirmar) {
            this.titulo = titulo;
            this.textoConfirmar = textoConfirmar;
            corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
            corpo.setBackground(Estilos.BRANCO);
        }

        /** Acrescenta um campo de texto com legenda por cima. */
        public JTextField campo(String legenda, String valor, boolean editavel) {
            JLabel rotulo = new JLabel(legenda);
            rotulo.setFont(Estilos.FONTE_NORMAL.deriveFont(Font.BOLD, 12f));
            rotulo.setForeground(Estilos.TEXTO_SUAVE);
            rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField campo = new JTextField(valor, 24);
            campo.setEditable(editavel);
            campo.setAlignmentX(Component.LEFT_ALIGNMENT);
            campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, campo.getPreferredSize().height));

            corpo.add(rotulo);
            corpo.add(Box.createVerticalStrut(4));
            corpo.add(campo);
            corpo.add(Box.createVerticalStrut(14));
            if (editavel && primeiroEditavel == null) primeiroEditavel = campo;
            return campo;
        }

        public boolean mostrar(Component pai) {
            return abrir(pai, titulo, corpo, textoConfirmar, "Cancelar", false, primeiroEditavel);
        }
    }

    // ---------- Mensagens ----------

    /** Pergunta de confirmação. perigo = true pinta o botão de vermelho (remover, etc.). */
    public static boolean confirmar(Component pai, String titulo, String mensagem,
                                    String textoConfirmar, boolean perigo) {
        return abrir(pai, titulo, texto(mensagem), textoConfirmar, "Cancelar", perigo, null);
    }

    public static void aviso(Component pai, String mensagem) {
        abrir(pai, "Aviso", texto(mensagem), "Entendi", null, false, null);
    }

    public static void info(Component pai, String mensagem) {
        abrir(pai, "Informação", texto(mensagem), "OK", null, false, null);
    }

    private static JComponent texto(String mensagem) {
        JLabel l = new JLabel("<html><div style='width:300px'>" + mensagem.replace("&", "&amp;")
                .replace("<", "&lt;").replace(">", "&gt;") + "</div></html>");
        l.setFont(Estilos.FONTE_NORMAL.deriveFont(14f));
        l.setForeground(Estilos.TINTA);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        return l;
    }

    // ---------- Janela base (modal) ----------

    private static boolean abrir(Component pai, String titulo, JComponent corpo, String textoOk,
                                 String textoCancelar, boolean perigo, JComponent foco) {
        Window dono = pai == null ? null : SwingUtilities.getWindowAncestor(pai);
        JDialog janela = new JDialog(dono, titulo, Dialog.ModalityType.APPLICATION_MODAL);
        boolean[] resultado = {false};

        JButton ok = new JButton(textoOk);
        Estilos.estilizarBotaoPrimario(ok);
        if (perigo) ok.putClientProperty("perigo", Boolean.TRUE);
        ok.addActionListener(e -> { resultado[0] = true; janela.dispose(); });

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setBackground(Estilos.BRANCO);
        if (textoCancelar != null) {
            JButton cancelar = new JButton(textoCancelar);
            cancelar.setFocusPainted(false);
            cancelar.addActionListener(e -> janela.dispose());
            botoes.add(cancelar);
        }
        botoes.add(ok);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Estilos.BRANCO);
        raiz.setBorder(BorderFactory.createEmptyBorder(22, 24, 18, 24));
        raiz.add(corpo, BorderLayout.CENTER);
        raiz.add(botoes, BorderLayout.SOUTH);

        janela.setContentPane(raiz);
        // Enter = confirmar (atalho próprio; setDefaultButton faria o Nimbus pintar o botão à sua maneira)
        janela.getRootPane().registerKeyboardAction(e -> ok.doClick(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        janela.getRootPane().registerKeyboardAction(e -> janela.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW); // Esc = cancelar
        janela.setResizable(false);
        janela.pack();
        janela.setLocationRelativeTo(dono);
        if (foco != null) SwingUtilities.invokeLater(foco::requestFocusInWindow);
        janela.setVisible(true); // bloqueia até fechar
        return resultado[0];
    }
}