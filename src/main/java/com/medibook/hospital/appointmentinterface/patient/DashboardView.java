// Create this new file: DashboardView.java
package com.medibook.hospital.appointmentinterface.patient;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardView {
    public Node getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        view.getStyleClass().add("content-area");

        Label title = new Label("Dashboard");
        title.getStyleClass().add("page-title");

        Label welcomeMessage = new Label("Welcome back, Patient!");

        view.getChildren().addAll(title, welcomeMessage);
        return view;
    }
}