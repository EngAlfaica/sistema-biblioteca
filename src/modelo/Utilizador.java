package modelo;

/**
 * Representa um utilizador (leitor) da biblioteca.
 */
public class Utilizador {

    private String numeroUtilizador; // identificador único (ex: número de cartão)
    private String nome;
    private String email;
    private String telefone;

    public Utilizador(String numeroUtilizador, String nome, String email, String telefone) {
        this.numeroUtilizador = numeroUtilizador;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    public String getNumeroUtilizador() {
        return numeroUtilizador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s", numeroUtilizador, nome, email, telefone);
    }
}