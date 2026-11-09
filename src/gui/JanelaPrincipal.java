package gui;

import gestao.GestorBiblioteca;
import persistencia.PersistenciaDados;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Janela principal: menu lateral claro à esquerda, cabeçalho de página em cima
 * e a página actual por baixo (CardLayout). O menu e o cabeçalho são neutros:
 * a cor da marca só aparece no item activo e nas acções.
 * Carrega os dados ao abrir e grava-os automaticamente ao fechar.
 */
public class JanelaPrincipal extends JFrame {

    private final CardLayout cartas = new CardLayout();
    private final JPanel areaConteudo = new JPanel(cartas);
    private final JLabel tituloPagina = new JLabel();
    private final List<BotaoMenu> botoesMenu = new ArrayList<>();
    /** Título de cada página (para o cabeçalho), pelo nome interno. */
    private final java.util.Map<String, String> titulos = new java.util.HashMap<>();

    private PainelInicio painelInicio;
    private PainelLivros painelLivros;
    private PainelUtilizadores painelUtilizadores;
    private PainelEmprestimos painelEmprestimos;
    private PainelRelatorios painelRelatorios;
    private GestorBiblioteca gestor;
    private PaletaPesquisa paleta;

    public JanelaPrincipal() {
        super("Sistema de Gestão de Biblioteca");

        titulos.put("inicio", "Início");
        titulos.put("livros", "Livros");
        titulos.put("utilizadores", "Utilizadores");
        titulos.put("emprestimos", "Empréstimos");
        titulos.put("relatorios", "Relatórios");

        gestor = new GestorBiblioteca();
        try {
            PersistenciaDados.carregarTudo(gestor);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível carregar os dados guardados: " + ex.getMessage(),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
        }

        painelInicio = new PainelInicio(gestor, pagina -> ir(pagina, titulos.get(pagina)));
        painelLivros = new PainelLivros(gestor);
        painelUtilizadores = new PainelUtilizadores(gestor);
        painelEmprestimos = new PainelEmprestimos(gestor);
        painelRelatorios = new PainelRelatorios(gestor);
        areaConteudo.add(painelInicio, "inicio");
        areaConteudo.add(painelLivros, "livros");
        areaConteudo.add(painelUtilizadores, "utilizadores");
        areaConteudo.add(painelEmprestimos, "emprestimos");
        areaConteudo.add(painelRelatorios, "relatorios");
        areaConteudo.setBackground(Estilos.BRANCO);

        JPanel direita = new JPanel(new BorderLayout());
        direita.setBackground(Estilos.BRANCO);
        direita.add(criarCabecalho(), BorderLayout.NORTH);
        direita.add(areaConteudo, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(criarMenuLateral(), BorderLayout.WEST);
        add(direita, BorderLayout.CENTER);

        // Pesquisa global: Ctrl+K em qualquer sítio da janela
        paleta = new PaletaPesquisa(this, gestor, (pagina, texto) -> {
            ir(pagina, titulos.get(pagina));
            if (pagina.equals("livros")) painelLivros.mostrarPesquisa(texto);
            else painelUtilizadores.mostrarPesquisa(texto);
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_DOWN_MASK), "pesquisaGlobal");
        getRootPane().getActionMap().put("pesquisaGlobal", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { paleta.abrir(); }
        });

        ir("inicio", "Início");

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    PersistenciaDados.guardarTudo(gestor);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(JanelaPrincipal.this,
                            "Não foi possível guardar os dados: " + ex.getMessage(),
                            "Erro ao guardar", JOptionPane.ERROR_MESSAGE);
                }
                dispose();
                System.exit(0);
            }
        });

        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 520));
        setLocationRelativeTo(null);
    }

    // ---------- Menu lateral ----------

    private JPanel criarMenuLateral() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(Estilos.FUNDO_MENU);
        menu.setPreferredSize(new Dimension(224, 0));
        // traço fino do lado direito a separar o menu do conteúdo
        menu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Estilos.LINHA_FINA));

        menu.add(criarMarca());
        menu.add(criarItem("Início", "inicio", Icones.inicio(Estilos.TEXTO_SUAVE), Icones.inicio(Estilos.AZUL_PRIMARIO)));
        menu.add(criarItem("Livros", "livros", Icones.livros(Estilos.TEXTO_SUAVE), Icones.livros(Estilos.AZUL_PRIMARIO)));
        menu.add(criarItem("Utilizadores", "utilizadores", Icones.utilizadores(Estilos.TEXTO_SUAVE), Icones.utilizadores(Estilos.AZUL_PRIMARIO)));
        menu.add(criarItem("Empréstimos", "emprestimos", Icones.emprestimos(Estilos.TEXTO_SUAVE), Icones.emprestimos(Estilos.AZUL_PRIMARIO)));
        menu.add(criarItem("Relatórios", "relatorios", Icones.relatorios(Estilos.TEXTO_SUAVE), Icones.relatorios(Estilos.AZUL_PRIMARIO)));

        menu.add(Box.createVerticalGlue()); // empurra o "Sair" para o fundo

        BotaoMenu sair = new BotaoMenu("Sair", null, null);
        sair.addActionListener(e -> {
            for (java.awt.event.WindowListener l : getWindowListeners()) {
                l.windowClosing(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }
        });
        menu.add(sair);
        menu.add(Box.createVerticalStrut(14));
        return menu;
    }

    /** Nome da aplicação com um pequeno quadrado azul de marca (a única mancha de cor do menu). */
    private JPanel criarMarca() {
        JPanel marca = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        marca.setOpaque(false);
        marca.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 10));
        marca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComponent quadrado = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Estilos.AZUL_PRIMARIO);
                g2.fillRoundRect(0, 0, 22, 22, 6, 6);
                g2.setColor(Estilos.BRANCO);
                g2.fillRoundRect(5, 5, 3, 12, 1, 1);
                g2.fillRoundRect(10, 5, 3, 12, 1, 1);
                g2.fillRoundRect(15, 7, 3, 10, 1, 1);
                g2.dispose();
            }
        };
        quadrado.setPreferredSize(new Dimension(22, 22));

        JLabel nome = new JLabel("  Biblioteca");
        nome.setFont(Estilos.FONTE_SUBTITULO.deriveFont(16f));
        nome.setForeground(Estilos.TINTA);

        marca.add(quadrado);
        marca.add(nome);
        return marca;
    }

    private BotaoMenu criarItem(String texto, String nomePagina, Icon normal, Icon activo) {
        BotaoMenu botao = new BotaoMenu(texto, normal, activo);
        botao.addActionListener(e -> ir(nomePagina, texto));
        botao.setNomePagina(nomePagina);
        botoesMenu.add(botao);
        return botao;
    }

    /** Mostra a página pedida (recarregando os dados dela) e marca o botão correspondente como activo. */
    private void ir(String nomePagina, String titulo) {
        switch (nomePagina) {
            case "inicio": painelInicio.atualizar(); break;
            case "livros": painelLivros.atualizarTabela(gestor.listarTodosOsLivros()); break;
            case "utilizadores": painelUtilizadores.atualizarTabela(gestor.listarTodosOsUtilizadores()); break;
            case "emprestimos": painelEmprestimos.atualizarTabela(); break;
            case "relatorios": painelRelatorios.atualizar(); break;
            default: break;
        }
        cartas.show(areaConteudo, nomePagina);
        tituloPagina.setText(titulo);
        for (BotaoMenu b : botoesMenu) {
            b.setActivo(nomePagina.equals(b.getNomePagina()));
        }
    }

    // ---------- Cabeçalho da página ----------

    private JPanel criarCabecalho() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilos.BRANCO);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Estilos.LINHA_FINA),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));

        tituloPagina.setFont(Estilos.FONTE_TITULO);
        tituloPagina.setForeground(Estilos.TINTA);
        barra.add(tituloPagina, BorderLayout.WEST);

        // Utilizador à direita: círculo com a inicial + nome em cinzento
        JPanel utilizador = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        utilizador.setOpaque(false);
        JComponent avatar = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(Estilos.ITEM_ACTIVO);
                g2.fillOval(0, 0, 28, 28);
                g2.setColor(Estilos.AZUL_PRIMARIO);
                g2.setFont(Estilos.FONTE_SUBTITULO);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("B", (28 - fm.stringWidth("B")) / 2, (28 - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(28, 28));
        JButton botaoPesquisa = new JButton("Pesquisar   Ctrl+K");
        botaoPesquisa.setFont(Estilos.FONTE_NORMAL);
        botaoPesquisa.setForeground(Estilos.TEXTO_SUAVE);
        botaoPesquisa.setFocusable(false);
        botaoPesquisa.addActionListener(e -> paleta.abrir());
        utilizador.add(botaoPesquisa);
        utilizador.add(Box.createHorizontalStrut(10));
        JLabel nome = new JLabel("Bibliotecário");
        nome.setFont(Estilos.FONTE_NORMAL);
        nome.setForeground(Estilos.TEXTO_SUAVE);
        utilizador.add(nome);
        utilizador.add(avatar);
        barra.add(utilizador, BorderLayout.EAST);
        return barra;
    }

    // ---------- Item do menu (desenhado à mão para ignorar o Nimbus) ----------

    private static class BotaoMenu extends JButton {
        private final Icon iconeNormal;
        private final Icon iconeActivo;
        private boolean activo = false;
        private boolean rato = false;
        private String nomePagina;

        BotaoMenu(String texto, Icon iconeNormal, Icon iconeActivo) {
            super(texto);
            this.iconeNormal = iconeNormal;
            this.iconeActivo = iconeActivo;
            setIcon(iconeNormal);
            setIconTextGap(12);
            setFont(Estilos.FONTE_NORMAL.deriveFont(Font.PLAIN, 14f));
            setForeground(new Color(0x37, 0x41, 0x51));
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 10));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setPreferredSize(new Dimension(224, 42));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { rato = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { rato = false; repaint(); }
            });
        }

        void setActivo(boolean a) {
            activo = a;
            if (iconeNormal != null) setIcon(a ? iconeActivo : iconeNormal);
            setForeground(a ? Estilos.AZUL_PRIMARIO : new Color(0x37, 0x41, 0x51));
            setFont(getFont().deriveFont(a ? Font.BOLD : Font.PLAIN));
            repaint();
        }

        void setNomePagina(String n) { nomePagina = n; }
        String getNomePagina() { return nomePagina; }

        @Override
        protected void paintComponent(Graphics g) {
            if (activo || rato) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(activo ? Estilos.ITEM_ACTIVO : Estilos.ITEM_HOVER);
                g2.fillRoundRect(10, 2, getWidth() - 20, getHeight() - 4, 10, 10); // pílula com margem
                g2.dispose();
            }
            super.paintComponent(g); // desenha ícone e texto por cima
        }
    }
}