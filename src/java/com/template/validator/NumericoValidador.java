package com.template.validator;

import java.util.regex.Pattern;

public class NumericoValidador implements Validator <String> {

    private final String nomeCampo;
    private final String valor;

    public NumericoValidador(String nomeCampo, String valor) {
        this.nomeCampo = nomeCampo;
        this.valor = valor;
    }

    @Override
    public boolean validar() {
        if (valor == null || valor.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches("^\\d+(\\.\\d+)?$", valor.trim());
    }

    @Override
    public String getMensagemErro() {
        return "O campo " + nomeCampo + " deve conter um número válido!";
    }
}