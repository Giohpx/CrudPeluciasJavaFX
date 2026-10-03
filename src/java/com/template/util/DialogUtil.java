package com.template.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;

public class DialogUtil {

    public static void showError(String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void showError(Label label, String mensagem) {
        showMessage(label, mensagem, "#e74c3c");
    }

    public static void showInfo(String mensagem) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void showInfo(Label label, String mensagem) {
        showMessage(label, mensagem, "#2ecc71");
    }

    public static void showWarning(String mensagem) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Atenção");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void showWarning(Label label, String mensagem) {
        showMessage(label, mensagem, "#f1c40f");
    }

    public static void clearMessage(Label label) {
        if (label != null) {
            label.setText("");
            label.setStyle("");
        }
    }

    private static void showMessage(
            Label label,
            String mensagem,
            String cor) {

        if (label == null) {
            return;
        }

        label.setText(mensagem);
        label.setWrapText(true);
        label.setStyle(
                "-fx-text-fill: " + cor + ";" +
                        "-fx-font-weight: bold;"
        );
    }
}
