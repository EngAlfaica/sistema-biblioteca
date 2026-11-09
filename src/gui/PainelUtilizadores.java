package gui;

import gestao.GestorBiblioteca;
import modelo.Utilizador;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Página de Utilizadores: filtro por nome + botão "Registar" em cima,
 * tabela com ícones de editar e remover em cada linha.
 */
public class PainelUtilizadores extends JPanel {

    private final GestorBiblioteca gestor;
    private final JTextField campoPesquisa = new JTextField();
    private TabelaComIcones<Utilizador> tabela;

    public PainelUtilizadores(GestorBiblioteca gestor) {
        this.gestor = gestor;
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(Estilos.BRANCO);

        add(criarBarraSuperior(), BorderLayout.NORTH);
        add(criarTabela(), BorderLayout.CENTER);

        atualizarTabela(gestor.listarTodosOsUtilizadores());
    }

    private JPanel criarBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout(8, 0));
        barra.setBackground(Estilos.BRANCO);

        JButton botaoPesquisar = new JButton("Pesquisar por nome");
        JButton botaoTodos = new JButton("Mostrar todos");
        JButton botaoRegistar = new JButton("+ Registar utilizador");
        Estilos.estilizarBotaoPrimario(botaoRegistar);

        botaoPesquisar.addActionListener(e -> filtrar());
        campoPesquisa.addActionListener(e -> filtrar());
        botaoTodos.addActionListener(e -> {
            campoPesquisa.setText("");
            atualizarTabela(gestor.listarTodosOsUtilizadores());
        });
        botaoRegistar.addActionListener(e -> abrirFormulario(null));

        JPanel esquerda = new JPanel(new BorderLayout(6, 0));
        esquerda.setBackground(Estilos.BRANCO);
        esquerda.add(new JLabel("Pesquisar:"), BorderLayout.WEST);
        esquerda.add(campoPesquisa, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botoes.setBackground(Estilos.BRANCO);
        botoes.add(botaoPesquisar);
        botoes.add(botaoTodos);
        botoes.add(botaoRegistar);

        barra.add(esquerda, BorderLayout.CENTER);
        barra.add(botoes, BorderLayout.EAST);
        return barra;
    }

    private JScrollPane criarTabela() {
        tabela = new TabelaComIcones<>(
                new String[]{"Número", "Nome", "Email", "Telefone"},
                new int[]{100, 240, 240, 130},
                new Icon[]{Icones.editar(Estilos.AZUL_PRIMARIO), Icones.remover(Estilos.VERMELHO_ATRASO)});

        // icone 0 = editar, 1 = remover
        tabela.aoClicar((utilizador, icone) -> {
            if (icone == 0) abrirFormulario(utilizador);
            else removerUtilizador(utilizador);
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(Estilos.BRANCO);
        return scroll;
    }

    /** Mostra só os utilizadores cujo nome contém o texto escrito (sem distinguir maiúsculas). */
    private void filtrar() {
        String texto = campoPesquisa.getText().trim().toLowerCase();
        List<Utilizador> filtrados = gestor.listarTodosOsUtilizadores().stream()
                .filter(u -> u.getNome().toLowerCase().contains(texto))
                .collect(Collectors.toList());
        atualizarTabela(filtrados);
    }

    /** utilizador == null: registo novo; senão: edição desse utilizador. */
    private void abrirFormulario(Utilizador utilizador) {
        boolean edicao = utilizador != null;

        Dialogo.Formulario f = new Dialogo.Formulario(edicao ? "Editar utilizador" : "Registar utilizador", "Guardar");
        JTextField numero = f.campo("Número", edicao ? utilizador.getNumeroUtilizador() : "", !edicao); // identifica o utilizador: não se muda
        JTextField nome = f.campo("Nome", edicao ? utilizador.getNome() : "", true);
        JTextField email = f.campo("Email", edicao ? utilizador.getEmail() : "", true);
        JTextField telefone = f.campo("Telefone", edicao ? utilizador.getTelefone() : "", true);
        if (!f.mostrar(this)) return;

        if (numero.getText().trim().isEmpty() || nome.getText().trim().isEmpty()) {
            aviso("Número e Nome são obrigatórios.");
            return;
        }

        if (edicao) {
            utilizador.setNome(nome.getText().trim());
            utilizador.setEmail(email.getText().trim());
            utilizador.setTelefone(telefone.getText().trim());
        } else {
            boolean ok = gestor.registarUtilizador(new Utilizador(numero.getText().trim(),
                    nome.getText().trim(), email.getText().trim(), telefone.getText().trim()));
            if (!ok) {
                aviso("Já existe um utilizador com esse número.");
                return;
            }
        }
        atualizarTabela(gestor.listarTodosOsUtilizadores());
    }

    private void removerUtilizador(Utilizador utilizador) {
        if (!Dialogo.confirmar(this, "Remover utilizador",
                "Remover o utilizador \"" + utilizador.getNome() + "\"? Esta acção não pode ser desfeita.",
                "Remover", true)) return;

        if (gestor.removerUtilizador(utilizador.getNumeroUtilizador())) {
            atualizarTabela(gestor.listarTodosOsUtilizadores());
        } else {
            aviso("Não foi possível remover o utilizador.");
        }
    }

    private void aviso(String texto) {
        Dialogo.aviso(this, texto);
    }

    /** Usado pela pesquisa global: escreve o texto no campo e filtra a tabela. */
    public void mostrarPesquisa(String texto) {
        campoPesquisa.setText(texto);
        filtrar();
    }

    public void atualizarTabela(List<Utilizador> lista) {
        tabela.carregar(lista, u -> new Object[]{
                u.getNumeroUtilizador(), u.getNome(), u.getEmail(), u.getTelefone()});
    }
}