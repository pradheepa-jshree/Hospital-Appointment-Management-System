package com.medibook.hospital.appointmentinterface.patient;

import com.medibook.hospital.appointmentinterface.dao.AppointmentDAO;
import com.medibook.hospital.appointmentinterface.dao.DoctorDAO;
import com.medibook.hospital.appointmentinterface.dao.PatientDAO;
import com.medibook.hospital.appointmentinterface.model.Doctor;
import com.medibook.hospital.appointmentinterface.model.Patient;
import com.medibook.hospital.appointmentinterface.patient.view.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class PatientPortalMainView implements AppointmentViewListener {

    private final BorderPane mainLayout;
    private final int loggedInPatientId;
    private final Patient loggedInPatient;

    // --- NEW FIELD to hold the logout action ---
    private final Runnable logoutAction;

    private Button activeButton;
    private Button dashboardBtn;
    private Button appointmentsBtn;
    private Button profileBtn;

    /**
     * UPDATED CONSTRUCTOR: Now accepts a Runnable 'logoutAction' from the LoginApp.
     */
    public PatientPortalMainView(int patientId, Runnable logoutAction) {
        this.loggedInPatientId = patientId;
        this.logoutAction = logoutAction; // Store the provided action

        PatientDAO patientDAO = new PatientDAO();
        this.loggedInPatient = patientDAO.getPatientById(patientId);
        this.mainLayout = new BorderPane();
        initializePortal();
    }

    private void initializePortal() {
        mainLayout.getStyleClass().add("root-pane");
        Node sideNav = createSideNavigationBar();
        mainLayout.setLeft(sideNav);
        showDashboard();
    }

    private void showDashboard() {
        Runnable bookAppointmentAction = () -> {
            PatientDAO dao = new PatientDAO();
            if (dao.isProfileComplete(loggedInPatientId)) {
                startNewBookingFlow();
            } else {
                showAlert("Profile Incomplete", "Please complete your profile (Date of Birth and Phone Number) before booking an appointment.");
                showMyProfile();
            }
        };

        Runnable viewAppointmentsAction = this::showMyAppointments;
        Runnable viewProfileAction = this::showMyProfile;

        Node dashboardNode = new PatientDashboardView().getView(
                loggedInPatient.getFullName(),
                bookAppointmentAction,
                viewAppointmentsAction,
                viewProfileAction
        );
        switchView(dashboardNode, dashboardBtn);
    }

    private void showMyAppointments() {
        MyAppointmentsView appointmentsView = new MyAppointmentsView(loggedInPatientId, this);
        switchView(appointmentsView.getView(), appointmentsBtn);
    }

    private void showMyProfile() {
        switchView(new MyProfileView().getView(loggedInPatientId), profileBtn);
    }

    private void startNewBookingFlow() {
        Consumer<Doctor> onDoctorSelected = doctor -> {
            DoctorScheduleSelectionView scheduleView = new DoctorScheduleSelectionView(doctor);
            final Consumer<DoctorScheduleSelectionView.AppointmentSelection>[] onSlotSelected = new Consumer[1];
            Runnable onGoBack = () -> switchView(scheduleView.getView(onSlotSelected[0]), null);

            onSlotSelected[0] = selection -> {
                Runnable onBookingConfirmed = this::showMyAppointments;
                AppointmentConfirmationView confirmationView = new AppointmentConfirmationView(selection, loggedInPatientId);
                switchView(confirmationView.getView(onBookingConfirmed, onGoBack), null);
            };
            switchView(scheduleView.getView(onSlotSelected[0]), null);
        };
        switchView(new DoctorSearchView().getView(onDoctorSelected), null);
    }

    @Override
    public void onRescheduleRequested(int doctorId, int oldAppointmentId) {
        DoctorDAO doctorDAO = new DoctorDAO();
        Doctor doctor = doctorDAO.getDoctorById(doctorId);

        if (doctor == null) {
            showAlert("Error", "Could not find the requested doctor's details.");
            return;
        }

        DoctorScheduleSelectionView scheduleView = new DoctorScheduleSelectionView(doctor);
        final Consumer<DoctorScheduleSelectionView.AppointmentSelection>[] onSlotSelected = new Consumer[1];
        Runnable onGoBack = () -> switchView(scheduleView.getView(onSlotSelected[0]), null);

        onSlotSelected[0] = selection -> {
            Runnable onBookingConfirmed = () -> {
                AppointmentDAO appointmentDAO = new AppointmentDAO();
                boolean success = appointmentDAO.cancelAppointment(oldAppointmentId);
                if (success) {
                    showAlert("Success", "Your appointment has been successfully rescheduled.");
                } else {
                    showAlert("Warning", "Your new appointment was booked, but we could not cancel the old one. Please contact support.");
                }
                showMyAppointments();
            };
            AppointmentConfirmationView confirmationView = new AppointmentConfirmationView(selection, loggedInPatientId);
            switchView(confirmationView.getView(onBookingConfirmed, onGoBack), null);
        };
        switchView(scheduleView.getView(onSlotSelected[0]), null);
    }

    private Node createSideNavigationBar() {
        VBox sideNav = new VBox();
        sideNav.getStyleClass().add("side-navigation-bar");
        VBox topNavButtons = new VBox(10);

        dashboardBtn = createNavButton(FontAwesomeIcon.HOME);
        appointmentsBtn = createNavButton(FontAwesomeIcon.CALENDAR_CHECK_ALT);
        profileBtn = createNavButton(FontAwesomeIcon.USER);
        topNavButtons.getChildren().addAll(dashboardBtn, appointmentsBtn, profileBtn);

        dashboardBtn.setOnAction(e -> showDashboard());
        appointmentsBtn.setOnAction(e -> showMyAppointments());
        profileBtn.setOnAction(e -> showMyProfile());

        VBox bottomNavButtons = new VBox(10);
        Button logoutBtn = createNavButton(FontAwesomeIcon.SIGN_OUT);
        bottomNavButtons.getChildren().add(logoutBtn);

        logoutBtn.setOnAction(e -> handleLogout());

        Pane spacer = new Pane();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sideNav.getChildren().addAll(topNavButtons, spacer, bottomNavButtons);
        return sideNav;
    }

    /**
     * UPDATED LOGOUT LOGIC: Now calls the logoutAction to return to the login screen.
     */
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to log out?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                System.out.println("Logging out... Returning to login screen.");
                // Execute the action provided by LoginApp
                logoutAction.run();
            }
        });
    }

    // --- (NO CHANGES NEEDED FOR THE HELPER METHODS BELOW) ---

    private void switchView(Node newView, Button clickedButton) {
        newView.getStyleClass().add("content-pane");
        mainLayout.setCenter(newView);
        if (clickedButton != null) {
            setActiveButton(clickedButton);
        }
    }

    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("nav-button-active");
        }
        button.getStyleClass().add("nav-button-active");
        activeButton = button;
    }

    private Button createNavButton(FontAwesomeIcon iconName) {
        FontAwesomeIconView icon = new FontAwesomeIconView(iconName);
        icon.getStyleClass().add("glyph-icon");
        Button button = new Button();
        button.setGraphic(icon);
        button.getStyleClass().add("nav-button");
        return button;
    }

    public Node getPortalView() {
        return mainLayout;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}