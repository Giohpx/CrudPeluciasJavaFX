package com.template.controller;

import com.template.validator.IPeluciaValidador;
import javafx.util.Callback;

public class ControllerFactory implements Callback<Class<?>, Object> {
    private final IPeluciaValidador peluciaValidator;

    public ControllerFactory(IPeluciaValidador peluciaValidator) {
        this.peluciaValidator = peluciaValidator;
    }

    @Override
    public Object call(Class<?> controllerClass) {
        if (controllerClass == MainController.class) {
            return new MainController(peluciaValidator);
        }

        try {
            return controllerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar controller: "
                    + controllerClass.getName(), e);
        }
    }
}
