package com.template.validator;

import java.util.ArrayList;
import java.util.List;
import static com.template.util.DialogUtil.showWarning;

public class PeluciaValidador implements IPeluciaValidator {

    @Override
    public boolean validarPelucia(String nome, String tipo, String pelagem, String valor, String quantidade) {
        List<Validator<String>> validadores = new ArrayList<>();

        validadores.add(new CampoObrigatorioValidador("Nome", nome));
        validadores.add(new CampoObrigatorioValidador("Tipo", tipo));
        validadores.add(new CampoObrigatorioValidador("Pelagem", pelagem));
        validadores.add(new CampoObrigatorioValidador("Valor", valor));
        validadores.add(new CampoObrigatorioValidador("Quantidade", quantidade));

        validadores.add(new NumericoValidador("Valor", valor));
        validadores.add(new NumericoValidador("Quantidade", quantidade));

        for (Validator<String> validador : validadores) {
            if (!validador.validar(validador.getValor())) {
                showWarning(validador.getMensagemErro());
                return false;
            }
        }

        return true;
    }
}