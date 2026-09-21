package com.template;

import com.template.validator.IPeluciaValidator;
import com.template.validator.PeluciaValidador;
import com.template.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        IPeluciaValidator peluciasValidator = new PeluciaValidador();

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("main.fxml"));

        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == MainController.class) {
                return new MainController(peluciasValidator);
            }
            try {
                return controllerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Scene scene = new Scene(loader.load(), 600, 400);

        stage.setTitle("Hello");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}