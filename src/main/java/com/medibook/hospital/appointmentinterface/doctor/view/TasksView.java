package com.medibook.hospital.appointmentinterface.doctor.view;

import com.medibook.hospital.appointmentinterface.dao.TaskDAO;
import com.medibook.hospital.appointmentinterface.model.Task;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

public class TasksView {

    private TableView<Task> table;
    private final TaskDAO taskDAO;

    public TasksView() {
        this.taskDAO = new TaskDAO();
    }

    public Node getView(int doctorId) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(10));
        Label title = new Label("My Tasks");
        title.getStyleClass().add("page-title");

        table = new TableView<>();
        setupTableColumns();
        loadTasks(doctorId);

        view.getChildren().addAll(title, table);
        return view;
    }

    private void setupTableColumns() {
        TableColumn<Task, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        descriptionCol.setPrefWidth(500);

        TableColumn<Task, LocalDate> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty());

        TableColumn<Task, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        TableColumn<Task, Void> completeCol = createCompleteColumn();

        table.getColumns().addAll(descriptionCol, dueDateCol, statusCol, completeCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * UPDATED: Creates the "Complete" column with a CheckBox that can be toggled.
     */
    private TableColumn<Task, Void> createCompleteColumn() {
        TableColumn<Task, Void> col = new TableColumn<>("Complete");

        col.setCellFactory(param -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();
            {
                // This listener now handles both checking and unchecking
                checkBox.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());

                    // Determine the new status based on the checkbox state
                    String newStatus = checkBox.isSelected() ? "Completed" : "Pending";

                    // Call the DAO to update the database
                    boolean success = taskDAO.updateTaskStatus(task.getId(), newStatus);

                    if (success) {
                        // If the DB update succeeds, update the UI model
                        task.statusProperty().set(newStatus);
                        getTableView().refresh();
                    } else {
                        // If the DB update fails, revert the checkbox to its previous state
                        checkBox.setSelected(!checkBox.isSelected());
                        showAlert("Error", "Failed to update task status.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Task task = getTableView().getItems().get(getIndex());
                    // The checkbox is now always enabled
                    checkBox.setDisable(false);
                    // Set the initial checked state based on the task's status
                    checkBox.setSelected("Completed".equalsIgnoreCase(task.getStatus()));
                    setGraphic(checkBox);
                }
            }
        });
        return col;
    }

    private void loadTasks(int doctorId) {
        List<Task> taskList = taskDAO.getTasksForDoctor(doctorId);
        table.setItems(FXCollections.observableArrayList(taskList));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}