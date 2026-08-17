package com.template.util;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class Clear {

    public static void limparCampos(TextField... campos) {
        for (TextField campo : campos) {
            if (campo != null) {
                campo.clear();
                campo.setStyle("");
            }
        }
    }

    public static void limparCamposETabela(TableView<?> tabela, TextField... campos) {
        limparCampos(campos);

        if (tabela != null) {
            tabela.getSelectionModel().clearSelection();
        }
    }
}
