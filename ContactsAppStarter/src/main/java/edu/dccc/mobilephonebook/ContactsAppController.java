package edu.dccc.mobilephonebook;

import edu.dccc.store.CSVReaderWriter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.TreeSet;

public class ContactsAppController {

    @FXML private Button addButton;
    @FXML private TextField nameField, phoneField, searchField;
    @FXML private ToggleButton directionToggle;
    @FXML private CheckBox singleSearchCheck;
    @FXML private Label statusLabel, resultsLabel;
    @FXML private ListView<Contact> contactListView;

    private final ArrayList<Contact> bridgeList = new ArrayList<>();
    private final DoublyLinkedList<Contact> storage = new DoublyLinkedList<>();
    private final ObservableList<Contact> displayList = FXCollections.observableArrayList();

    private final String FILE_NAME = "contacts.csv";
    private CSVReaderWriter<Contact> csvReaderWriter;

    @FXML
    public void initialize() {
        csvReaderWriter = new CSVReaderWriter<>(FILE_NAME, bridgeList, Contact.class);
        contactListView.setItems(displayList);

        csvReaderWriter.loadFromCSV(true);

        storage.clear();
        for (Contact c : bridgeList) {
            if (!storage.contains(c)) {
                storage.add(c);
            }
        }

        setupListeners();

        contactListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Contact selected = contactListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    handleEditPopup(selected);
                }
            }
        });

        contactListView.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null) clearStatus();
        });

        nameField.setOnKeyTyped(e -> clearStatus());
        phoneField.setOnKeyTyped(e -> clearStatus());
        searchField.setOnKeyTyped(e -> clearStatus());

        validateInput();
        updateUI();
        resetStatus();
    }

    @FXML
    private void onClearSearch() {
        searchField.clear();
        singleSearchCheck.setSelected(false);
        updateUI();
        searchField.requestFocus();
    }

    @FXML
    private void onSingleSearchToggled() {
        performSearch(searchField.getText());
    }

    @FXML
    private void onSearchKeyReleased() {
        performSearch(searchField.getText());
    }

    private void performSearch(String query) {
        contactListView.getSelectionModel().clearSelection();

        if (query == null || query.isEmpty()) {
            TreeSet<Contact> sortedResults = new TreeSet<>();
            for (Contact c : storage) {
                sortedResults.add(c);
            }
            displayList.setAll(sortedResults);
            resultsLabel.setText("Total Contacts: " + storage.size());
            resetStatus();
            return;
        }

        displayList.clear();
        int iterations = 0;
        TreeSet<Contact> results = new TreeSet<>();

        String lowerQuery = query.toLowerCase().trim();
        String searchDigits = query.replaceAll("[^0-9]", "");

        Iterable<Contact> path = directionToggle.isSelected() ? storage.backwards() : storage;

        for (Contact c : path) {
            iterations++;

            String contactDigits = c.getPhone().replaceAll("[^0-9]", "");
            boolean nameMatch = c.getName().toLowerCase().contains(lowerQuery);
            boolean phoneMatch = !searchDigits.isEmpty() && contactDigits.contains(searchDigits);

            if (lowerQuery.isEmpty() || nameMatch || phoneMatch) {
                results.add(c);

                if (singleSearchCheck.isSelected() && !lowerQuery.isEmpty()) {
                    break;
                }
            }
        }

        //"No results found" message
        if (results.isEmpty() && !lowerQuery.isEmpty()) {
            resultsLabel.setText("No results found.");
            showTimedStatus("⚠ No results found for your search.", "#FBBF24"); // Flashes an orange warning in the status bar
        } else {
            resultsLabel.setText(String.format("Found: %d | Iterations: %d", results.size(), iterations));
        }

        displayList.setAll(results);
    }

    @FXML
    protected void onAddButtonClick() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        String digits = phone.replaceAll("[^0-9]", "");
        if (!phone.startsWith("+") && digits.length() == 10) {
            phone = digits.substring(0, 3) + "-" + digits.substring(3, 6) + "-" + digits.substring(6);
        }

        Contact newContact = new Contact(name, phone);
        storage.add(newContact);
        bridgeList.add(newContact);

        nameField.clear();
        phoneField.clear();
        updateUI();
        validateInput();
        showTimedStatus("Successfully added: " + name, "#60A5FA");
    }

    @FXML
    protected void onDeleteButtonClick() {
        Contact selected = contactListView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            String deletedName = selected.getName();
            storage.remove(selected);
            bridgeList.remove(selected);
            updateUI();
            showTimedStatus("🗑 Deleted: " + deletedName, "#F87171");
        } else {
            showTimedStatus("⚠ Please select a contact to delete first.", "#FBBF24");
        }
    }

    private void handleEditPopup(Contact contact) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Contact");
        dialog.setHeaderText("Updating: " + contact.getName());

        TextField nameEdit = new TextField(contact.getName());
        TextField phoneEdit = new TextField(contact.getPhone());

        VBox content = new VBox(10, new Label("Name:"), nameEdit, new Label("Phone:"), phoneEdit);
        content.setPadding(new javafx.geometry.Insets(20));
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        javafx.scene.Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);

        Runnable validate = () -> {
            boolean nameBlank = nameEdit.getText().trim().isEmpty();
            boolean phoneBlank = phoneEdit.getText().trim().isEmpty();
            okButton.setDisable(nameBlank || phoneBlank);
        };

        nameEdit.textProperty().addListener((obs, old, val) -> validate.run());
        phoneEdit.textProperty().addListener((obs, old, val) -> validate.run());
        validate.run();

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                contact.setName(nameEdit.getText().trim());
                contact.setPhone(phoneEdit.getText().trim());
                contact.setLastModified(java.time.LocalDateTime.now());

                updateUI();
                showTimedStatus("✔ Updated: " + contact.getName(), "#60A5FA");
            }
        });
    }

    @FXML
    public void onExitButtonClick() {
        csvReaderWriter.saveToCSVSorted("Name,Phone,Timestamp");
        Platform.exit();
    }

    private void updateUI() { performSearch(searchField.getText()); }

    private void clearStatus() {
        statusLabel.setText("");
    }

    private void resetStatus() {
        statusLabel.setText("System Ready | Total Contacts: " + storage.size());
        statusLabel.setStyle("-fx-text-fill: #94A3B8;");
    }

    private void showTimedStatus(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        pause.setOnFinished(e -> resetStatus());
        pause.play();
    }

    private void validateInput() {
        boolean disable = nameField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty();
        addButton.setDisable(disable);
    }

    private void setupListeners() {
        searchField.textProperty().addListener((obs, old, newVal) -> performSearch(newVal));
        directionToggle.selectedProperty().addListener((obs, old, val) -> performSearch(searchField.getText()));
        nameField.textProperty().addListener((obs, old, val) -> validateInput());
        phoneField.textProperty().addListener((obs, old, val) -> validateInput());
    }
}