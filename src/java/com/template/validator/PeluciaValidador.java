package com.template.validator;

import java.util.ArrayList;
import java.util.List;

public class PeluciaValidador implements IPeluciaValidador {

    private String mensagemErro = "";

    @Override
    public boolean validarPelucia(
            String nome,
            String tipo,
            String pelagem,
            String valor,
            String quantidade) {

        List<Validador<String>> validadores = new ArrayList<>();

        validadores.add(new CamposObrigatoriosValidador("Nome", nome));
        validadores.add(new CamposObrigatoriosValidador("Tipo", tipo));
        validadores.add(new CamposObrigatoriosValidador("Pelagem", pelagem));
        validadores.add(new CamposObrigatoriosValidador("Valor", valor));
        validadores.add(new CamposObrigatoriosValidador("Quantidade", quantidade));
        validadores.add(new NumericoValidador("Valor", valor));
        validadores.add(new NumeroValidador("Quantidade", quantidade));

        for (Validador<String> validador : validadores) {

            if (!validador.validar(validador.getValor())) {

                mensagemErro = validador.getMensagemErro();

                return false;
            }
        }

        mensagemErro = "";

        return true;
    }

    @Override
    public String getMensagemErro() {
        return mensagemErro;
    }
}
