package gui;

import gestao.GestorBiblioteca;
import modelo.Livro;
import modelo.Utilizador;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Pesquisa global (Ctrl+K): uma janelinha flutuante com um campo de texto.
 * À medida que se escreve, mostra livros (título, autor, ISBN) e utilizadores
 * (nome, número). Setas ↑ ↓ escolhem, Enter abre, Esc fecha.
 * Ao escolher, pede à janela principal para ir à página certa já com a pesquisa feita.
 */
public class PaletaPesquisa extends JDialog {

    /** Um resultado: o que se vê e para onde leva. */
    private static class Resultado {
        final String tipo;      // "Livro" ou "Utilizador"
        final String titulo;
        final String detalhe;
        final String pagina;    // "livros" ou "utilizadores"
        final String texto;     // o que se escreve na pesquisa da página
        Resultado(String tipo, String titulo, String detalhe, String pagina, String texto) {
            this.tipo = tipo; this.titulo = titulo; this.detalhe = detalhe;
            this.pagina = pagina; this.texto = texto;
        }
    }

    private final GestorBiblioteca gestor;
    private final BiConsumer<String, String> aoEscolher; // (página, texto da pesquisa)
    private final JTextField campo = new JTextField();
    private final DefaultListModel<Resultado> modelo = new DefaultListModel<>();
    private final JList<Resultado> lista = new JList<>(modelo);
    private final JLabel dica = new JLabel();

    public PaletaPesquisa(Frame dono, GestorBiblioteca gestor, BiConsumer<String, String> aoEscolher) {
        super(dono, false);
        this.gestor = gestor;
        this.aoEscolher = aoEscolher;
        setUndecorated(true);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Estilos.BRANCO);
        raiz.setBorder(BorderFactory.createLineBorder(new Color(0xC9, 0xCF, 0xD9)));

        campo.setFont(Estilos.FONTE_NORMAL.deriveFont(16f));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Estilos.LINHA_FINA),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        campo.putClientProperty("JTextField.placeholderText", "Pesquisar livros e utilizadores…");

        lista.setCellRenderer(new RenderResultado());
        lista.setFixedCellHeight(46);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        dica.setFont(Estilos.FONTE_NORMAL);
        dica.setForeground(Estilos.TEXTO_SUAVE);
        dica.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(560, 46 * 6));

        JPanel centro = new JPanel(new CardLayout());
        centro.add(scroll, "lista");
        centro.add(dica, "dica");
        centro.setName("centro");

        raiz.add(campo, BorderLayout.NORTH);
        raiz.add(centro, BorderLayout.CENTER);
        setContentPane(raiz);
        setSize(560, 62 + 46 * 6);

        campo.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { pesquisar(centro); }
            public void removeUpdate(DocumentEvent e) { pesquisar(centro); }
            public void changedUpdate(DocumentEvent e) { pesquisar(centro); }
        });

        // Teclado: ↑ ↓ mexem na lista (o foco fica no campo), Enter abre, Esc fecha
        campo.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                int n = modelo.size();
                if (e.getKeyCode() == KeyEvent.VK_DOWN && n > 0) {
                    lista.setSelectedIndex(Math.min(n - 1, lista.getSelectedIndex() + 1));
                    lista.ensureIndexIsVisible(lista.getSelectedIndex());
                } else if (e.getKeyCode() == KeyEvent.VK_UP && n > 0) {
                    lista.setSelectedIndex(Math.max(0, lista.getSelectedIndex() - 1));
                    lista.ensureIndexIsVisible(lista.getSelectedIndex());
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    escolher();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    setVisible(false);
                }
            }
        });
        lista.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int i = lista.locationToIndex(e.getPoint());
                if (i >= 0) { lista.setSelectedIndex(i); escolher(); }
            }
        });
        // Fecha ao clicar fora da janelinha
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowLostFocus(WindowEvent e) { setVisible(false); }
        });
    }

    /** Abre centrada na parte de cima da janela principal, com o campo vazio. */
    public void abrir() {
        campo.setText("");
        modelo.clear();
        mostrar((JPanel) getContentPane().getComponent(1), false, "Escreva para pesquisar livros e utilizadores.");
        Window dono = getOwner();
        setLocation(dono.getX() + (dono.getWidth() - getWidth()) / 2, dono.getY() + 90);
        setVisible(true);
        campo.requestFocusInWindow();
    }

    private void pesquisar(JPanel centro) {
        String texto = campo.getText().trim();
        modelo.clear();
        if (texto.isEmpty()) {
            mostrar(centro, false, "Escreva para pesquisar livros e utilizadores.");
            return;
        }
        String alvo = texto.toLowerCase();
        List<Resultado> resultados = new ArrayList<>();

        for (Livro l : gestor.pesquisar(texto, "")) {
            resultados.add(new Resultado("Livro", l.getTitulo(),
                    l.getAutor() + "  ·  ISBN " + l.getIsbn(), "livros", texto));
        }
        for (Utilizador u : gestor.listarTodosOsUtilizadores()) {
            if (u.getNome().toLowerCase().contains(alvo) || u.getNumeroUtilizador().toLowerCase().contains(alvo)) {
                resultados.add(new Resultado("Utilizador", u.getNome(),
                        "Nº " + u.getNumeroUtilizador(), "utilizadores", texto));
            }
        }
        for (int i = 0; i < Math.min(resultados.size(), 20); i++) modelo.addElement(resultados.get(i));

        if (modelo.isEmpty()) {
            mostrar(centro, false, "Nada encontrado para \"" + texto + "\".");
        } else {
            mostrar(centro, true, null);
            lista.setSelectedIndex(0);
        }
    }

    private void mostrar(JPanel centro, boolean temLista, String mensagem) {
        if (mensagem != null) dica.setText(mensagem);
        ((CardLayout) centro.getLayout()).show(centro, temLista ? "lista" : "dica");
    }

    private void escolher() {
        Resultado r = lista.getSelectedValue();
        if (r == null) return;
        setVisible(false);
        aoEscolher.accept(r.pagina, r.texto);
    }

    /** Desenha cada resultado: etiqueta do tipo à esquerda, título e detalhe ao lado. */
    private static class RenderResultado extends JPanel implements ListCellRenderer<Resultado> {
        private final JLabel tipo = new JLabel();
        private final JLabel texto = new JLabel();

        RenderResultado() {
            setLayout(new BorderLayout(14, 0));
            setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
            tipo.setFont(Estilos.FONTE_NORMAL.deriveFont(Font.BOLD, 11f));
            tipo.setForeground(Estilos.TEXTO_SUAVE);
            tipo.setPreferredSize(new Dimension(70, 20));
            texto.setFont(Estilos.FONTE_NORMAL);
            add(tipo, BorderLayout.WEST);
            add(texto, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Resultado> l, Resultado r, int i,
                                                      boolean sel, boolean foco) {
            tipo.setText(r.tipo.toUpperCase());
            texto.setText("<html><b>" + esc(r.titulo) + "</b><br><span style='color:#6B7280'>"
                    + esc(r.detalhe) + "</span></html>");
            setBackground(sel ? Estilos.ITEM_ACTIVO : Estilos.BRANCO);
            texto.setForeground(Estilos.TINTA);
            return this;
        }

        private static String esc(String s) {
            return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }
}