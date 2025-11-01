package com.medibook.hospital.appointmentinterface.doctor.view;

import com.medibook.hospital.appointmentinterface.model.Doctor;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import java.util.List;

public class ComposeMessageDialog extends Dialog<Pair<Doctor, Pair<String, String>>> {

    public ComposeMessageDialog(List<Doctor> allDoctors) {
        setTitle("Compose New Message");
        setHeaderText("Fill in the details to send a new message.");

        // Setup Buttons
        ButtonType sendButtonType = new ButtonType("Send", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(sendButtonType, ButtonType.CANCEL);

        // Create UI Components
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Doctor> toComboBox = new ComboBox<>();
        toComboBox.getItems().addAll(allDoctors);
        toComboBox.setPromptText("Select a recipient...");

        // This makes the dropdown show the doctor's full name instead of the object reference
        toComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Doctor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFullName());
            }
        });
        toComboBox.setButtonCell(toComboBox.getCellFactory().call(null));

        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject");
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter your message here...");
        messageArea.setWrapText(true);

        grid.add(new Label("To:"), 0, 0);
        grid.add(toComboBox, 1, 0);
        grid.add(new Label("Subject:"), 0, 1);
        grid.add(subjectField, 1, 1);
        grid.add(new Label("Message:"), 0, 2);
        grid.add(messageArea, 1, 2);

        getDialogPane().setContent(grid);

        // Convert the result to a Pair when the send button is clicked
        setResultConverter(dialogButton -> {
            if (dialogButton == sendButtonType) {
                Doctor recipient = toComboBox.getValue();
                String subject = subjectField.getText();
                String message = messageArea.getText();
                // We use a nested Pair to return all three values
                return new Pair<>(recipient, new Pair<>(subject, message));
            }
            return null;
        });
    }
}