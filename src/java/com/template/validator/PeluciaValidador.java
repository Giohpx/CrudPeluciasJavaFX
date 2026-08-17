package com.template.validator;

import java.util.regex.Pattern;
import com.template.util.DialogUtil;
import java.util.regex.Pattern;

public class PeluciaValidador {

    public boolean validarCampos(String nome_pelucia, String email_pelucia,
                                 String senha_pelucia, String login_pelucia) {

        if (nome_pelucia.isEmpty() || email_pelucia.isEmpty()
                || login_pelucia.isEmpty() || senha_pelucia.isEmpty()) {

            DialogUtil.showError("Preencha todos os campos antes de prosseguir!");
            return false;
        }

        if (!validarEmail(email_pelucia)) {
            DialogUtil.showError("Digite um e-mail válido (exemplo@dominio.com)!");
            return false;
        }

        if (!validarSenha(senha_pelucia)) {
            DialogUtil.showError("A senha deve possuir no mínimo 6 caracteres, contendo letras e números.");
            return false;
        }

        if (!validarNome(nome_pelucia)) {
            DialogUtil.showError("O nome deve conter entre 3 e 50 letras.");
            return false;
        }

        if (!validarLogin(login_pelucia)) {
            DialogUtil.showError("O login deve conter entre 4 e 20 caracteres (letras, números ou _).");
            return false;
        }

        return true;
    }

    public boolean validarEmail(String email_pelucia) {
        return Pattern.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$", email_pelucia);
    }

    public boolean validarSenha(String senha_pelucia) {
        return Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$", senha_pelucia);
    }

    public boolean validarNome(String nome_pelucia) {
        return Pattern.matches("^[a-zA-ZÀ-ÿ\\s]{3,50}$", nome_pelucia);
    }

    public boolean validarLogin(String login_pelucia) {
        return Pattern.matches("^[a-zA-Z0-9_]{4,20}$", login_pelucia);
    }
}