package com.medibook.hospital.appointmentinterface.doctor.view;

import com.medibook.hospital.appointmentinterface.dao.DoctorDAO;
import com.medibook.hospital.appointmentinterface.dao.MessageDAO;
import com.medibook.hospital.appointmentinterface.model.Doctor;
import com.medibook.hospital.appointmentinterface.model.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.List;

public class SecureMessagingView {

    private TableView<Message> table;
    private final MessageDAO messageDAO;
    private final DoctorDAO doctorDAO;
    private final Doctor loggedInDoctor;

    /**
     * The constructor is refactored to accept the currently logged-in Doctor.
     * This is necessary to know who is sending messages and whose inbox to load.
     */
    public SecureMessagingView(Doctor loggedInDoctor) {
        this.loggedInDoctor = loggedInDoctor;
        this.messageDAO = new MessageDAO();
        this.doctorDAO = new DoctorDAO();
    }

    public Node getView() {
        BorderPane view = new BorderPane();

        // Header
        HBox header = createHeader();
        view.setTop(header);
        BorderPane.setMargin(header, new Insets(0, 0, 20, 0));

        // Message Table
        this.table = createMessageTable();
        view.setCenter(table);

        // Load the dynamic data for the logged-in doctor
        loadMessages();

        return view;
    }

    private void loadMessages() {
        List<Message> messageList = messageDAO.getMessagesForDoctor(loggedInDoctor.getId());
        ObservableList<Message> observableMessages = FXCollections.observableArrayList(messageList);
        table.setItems(observableMessages);
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Secure Messaging");
        title.getStyleClass().add("page-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button composeBtn = new Button("Compose New Message");
        composeBtn.getStyleClass().add("action-button");

        // Set the action to the fully implemented handler method
        composeBtn.setOnAction(e -> handleComposeMessage());

        header.getChildren().addAll(title, spacer, composeBtn);
        return header;
    }

    private TableView<Message> createMessageTable() {
        TableView<Message> tv = new TableView<>();
        TableColumn<Message, String> fromCol = new TableColumn<>("From");
        fromCol.setCellValueFactory(cellData -> cellData.getValue().fromProperty());
        fromCol.setPrefWidth(200);

        TableColumn<Message, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
        subjectCol.setPrefWidth(500);

        TableColumn<Message, String> timeCol = new TableColumn<>("Received");
        timeCol.setCellValueFactory(cellData -> cellData.getValue().timestampProperty());
        timeCol.setPrefWidth(200);

        tv.getColumns().addAll(fromCol, subjectCol, timeCol);

        // Add action for double-clicking a row to read the message
        tv.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tv.getSelectionModel().getSelectedItem() != null) {
                handleReadMessage(tv.getSelectionModel().getSelectedItem());
            }
        });

        return tv;
    }

    /**
     * FULLY IMPLEMENTED: Handles opening the compose dialog and sending the message.
     */
    private void handleComposeMessage() {
        // Get all doctors to use as potential recipients
        List<Doctor> allDoctors = doctorDAO.getAllDoctors();
        // Prevent a doctor from sending a message to themselves
        allDoctors.removeIf(doctor -> doctor.getId() == loggedInDoctor.getId());

        // Create and show the custom dialog
        ComposeMessageDialog dialog = new ComposeMessageDialog(allDoctors);
        dialog.showAndWait().ifPresent(result -> {
            Doctor recipient = result.getKey();
            String subject = result.getValue().getKey();
            String messageBody = result.getValue().getValue();

            // Validate the user's input before sending
            if (recipient != null && subject != null && !subject.trim().isEmpty() && messageBody != null && !messageBody.trim().isEmpty()) {
                boolean success = messageDAO.sendMessage(
                        recipient.getId(),
                        loggedInDoctor.getFullName(),
                        subject,
                        messageBody
                );

                if (success) {
                    showAlert("Success", "Message sent successfully.");
                    // After sending, refresh the message list (important for UX)
                    loadMessages();
                } else {
                    showAlert("Error", "Failed to send the message. Please try again.");
                }
            } else {
                // This handles cases where the user clicks "Send" with empty fields
                showAlert("Invalid Input", "Please select a recipient and fill out all fields.");
            }
        });
    }

    /**
     * FULLY IMPLEMENTED: Handles reading a message when a row is double-clicked.
     */
    private void handleReadMessage(Message message) {
        // Fetch the full message body from the database using its ID
        String fullMessageBody = messageDAO.getMessageBody(message.getId());

        Alert messageDialog = new Alert(Alert.AlertType.INFORMATION);
        messageDialog.setTitle("Read Message");
        messageDialog.setHeaderText("From: " + message.getFrom() + "\nSubject: " + message.getSubject());

        // Use a TextArea to display the message content for better formatting and scrolling
        TextArea messageContent = new TextArea(fullMessageBody != null ? fullMessageBody : "Message content could not be loaded.");
        messageContent.setWrapText(true);
        messageContent.setEditable(false);
        messageContent.setPrefHeight(200);

        // Make the dialog resizable and set its content
        messageDialog.setResizable(true);
        messageDialog.getDialogPane().setContent(messageContent);
        messageDialog.showAndWait();
    }

    /**
     * A helper method to easily show information alerts.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}