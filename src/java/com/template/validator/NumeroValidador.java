package com.template.validator;

public class NumeroValidador implements Validador<String> {
    private final String nomeCampo;
    private final String valor;

    public NumeroValidador(String nomeCampo, String valor) {
        this.nomeCampo = nomeCampo;
        this.valor = valor;
    }

    @Override
    public boolean validar(String valorAtual) {
        if (this.valor == null || this.valor.trim().isEmpty()) {
            return false;
        }
        return this.valor.trim().matches("^\\d+(\\.\\d+)?$");
    }

    @Override
    public String getMensagemErro() {
        return "O campo " + nomeCampo + " deve conter apenas valores numéricos.";
    }

    @Override
    public String getValor() {
        return valor;
    }
}
