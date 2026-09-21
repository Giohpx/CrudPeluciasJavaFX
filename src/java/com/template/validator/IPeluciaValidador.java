package com.template.validator;

public interface IPeluciaValidador {
    boolean validarPelucia(
            String nome,
            String tipo,
            String pelagem,
            String valor,
            String quantidade);

    String getMensagemErro();
}
