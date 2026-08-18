package com.template.validator;

import java.util.regex.Pattern;
import com.template.util.DialogUtil;

public class PeluciaValidador {

    public boolean validarCampos(String nome, String tipo, String pelagem, String valor, String quantidade) {

        // 1. Validação de Campos Obrigatórios
        if (nome == null || nome.trim().isEmpty()
                || tipo == null || tipo.trim().isEmpty()
                || pelagem == null || pelagem.trim().isEmpty()
                || valor == null || valor.trim().isEmpty()
                || quantidade == null || quantidade.trim().isEmpty()) {

            DialogUtil.showWarning("Preencha todos os campos antes de prosseguir!");
            return false;
        }

        // 2. Validação: O Tipo NÃO pode ser numérico
        if (validarEhNumerico(tipo)) {
            DialogUtil.showWarning("O campo Tipo não pode ser um valor numérico!");
            return false;
        }

        // 3. Validação: Valor e Quantidade DEVEM ser numéricos
        if (!validarEhNumerico(valor)) {
            DialogUtil.showWarning("O campo Valor deve ser um número válido!");
            return false;
        }

        if (!validarEhNumerico(quantidade)) {
            DialogUtil.showWarning("O campo Quantidade deve conter apenas números!");
            return false;
        }

        return true;
    }

    /**
     * Verifica se a string contém apenas números (inteiros ou decimais)
     */
    public boolean validarEhNumerico(String texto) {
        if (texto == null) return false;
        return Pattern.matches("^\\d+(\\.\\d+)?$", texto.trim());
    }
}