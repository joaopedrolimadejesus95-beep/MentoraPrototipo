package org.example;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Usuario {
    private int idUsuario;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    private int numero;


    

    //Set
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public void setNumero(Integer numero){
        this.numero = numero;

    }
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    //get
    public int getIdUsuario() {
        return idUsuario;
    }
   public String getNome() {
        return nome;
    }
    public String getEmail() {
        return email;

    }
    public String getSenha() {
        return senha;
    }
    public int getNumero() {
        return numero;
    }
}
