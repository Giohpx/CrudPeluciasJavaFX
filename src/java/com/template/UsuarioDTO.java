package com.template;

public class UsuarioDTO {

    private int id;
    private String nome;
    private String tipo;
    private int qtde_pelucias;
    private String pelagem;
    private double valor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getQtde_pelucias() {
        return qtde_pelucias;
    }

    public void setQtde_pelucias(int qtde_pelucias) {
        this.qtde_pelucias = qtde_pelucias;
    }

    public String getPelagem() {
        return pelagem;
    }

    public void setPelagem(String pelagem) {
        this.pelagem = pelagem;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}