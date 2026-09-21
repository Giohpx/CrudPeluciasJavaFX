package com.template;

import com.template.controller.ControllerFactory;
import com.template.validator.IPeluciaValidador;
import com.template.validator.PeluciaValidador;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        IPeluciaValidador peluciaValidator = new PeluciaValidador();
        ControllerFactory controllerFactory =
                new ControllerFactory(peluciaValidator);

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("main.fxml"));
        loader.setControllerFactory(controllerFactory);

        Scene scene = new Scene(loader.load(), 600, 463);

        stage.setTitle("CRUD Pelucias");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
