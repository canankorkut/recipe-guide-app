package org.recipeguide.recipeguideapp;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.recipeguide.recipeguideapp.database.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddRecipe {
    public TextField unitPriceField;
    public TextField ingredientNameField;
    public VBox ingredientsVBox;
    public TextField ingredientCountField;
    @FXML
    private TextField recipeNameField;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TextField preparationTimeField;
    @FXML
    private TextArea instructionsArea;
    @FXML
    private TextField ingredientQuantityField;

    @FXML
    private ComboBox<String> ingredientUnitComboBox;

    @FXML
    private ImageView recipeImageView;

    @FXML
    private String imageUrl;

    private List<HBox> ingredientInputBoxes = new ArrayList<>();

    @FXML
    public void initialize() {
        loadIngredients();
    }

    private void loadIngredients() {

    }

    @FXML
    private void onUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Recipe Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            recipeImageView.setImage(image);

            imageUrl = selectedFile.toURI().toString();

            String recipeName = recipeNameField.getText();
            String category = categoryComboBox.getValue();
            String preparationTimeStr = preparationTimeField.getText();
            String instructions = instructionsArea.getText();

            if (recipeName != null && !recipeName.isEmpty() && category != null && preparationTimeStr != null && !preparationTimeStr.isEmpty()) {
                int preparationTime;
                try {
                    preparationTime = Integer.parseInt(preparationTimeStr);
                    DatabaseHelper.saveRecipeImage(selectedFile, recipeName, category, preparationTime, instructions);
                } catch (NumberFormatException e) {
                    showAlert("Error", "Preparation time must be a valid number.");
                }
            } else {
                showAlert("Error", "Please enter a recipe name, select a category, and enter preparation time before uploading an image.");
            }
        }
    }

    @FXML
    private void onAddIngredient() {
        String newIngredientName = ingredientNameField.getText();
        String quantityStr = ingredientQuantityField.getText();
        String unit = ingredientUnitComboBox.getValue();
        String unitPriceStr = unitPriceField.getText();

        double quantity;
        double unitPrice;
        try {
            quantity = Double.parseDouble(quantityStr);
            unitPrice = Double.parseDouble(unitPriceStr);
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid quantity or unit price. Please use a valid number.");
            return;
        }

        DatabaseHelper.saveIngredient(newIngredientName, quantity, unit, unitPrice);
    }

    @FXML
    private void onCreateIngredients() {
        ingredientsVBox.getChildren().clear();
        ingredientInputBoxes.clear();

        String countStr = ingredientCountField.getText();
        int count;
        try {
            count = Integer.parseInt(countStr);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number for the ingredient count.");
            return;
        }

        ObservableList<String> ingredients = DatabaseHelper.getIngredients();

        for (int i = 0; i < count; i++) {
            HBox ingredientBox = new HBox(10);

            ComboBox<String> ingredientComboBox = new ComboBox<>(ingredients);
            ingredientComboBox.setPromptText("Select ingredient");
            ingredientComboBox.setPrefWidth(200);

            TextField ingredientQuantityField = new TextField();
            ingredientQuantityField.setPromptText("Quantity");
            ingredientQuantityField.setPrefWidth(80);

            ingredientBox.getChildren().addAll(ingredientComboBox, ingredientQuantityField);
            ingredientsVBox.getChildren().add(ingredientBox);
            ingredientInputBoxes.add(ingredientBox);
        }
    }

    @FXML
    private void onSaveRecipe() {
        String recipeName = recipeNameField.getText();
        String category = categoryComboBox.getValue();
        String preparationTimeStr = preparationTimeField.getText();
        String instructions = instructionsArea.getText();

        if (recipeName.isEmpty() || category == null || preparationTimeStr.isEmpty() || instructions.isEmpty() || imageUrl == null) {
            showAlert("Error", "All fields must be filled out.");
            return;
        }

        int preparationTime;
        try {
            preparationTime = Integer.parseInt(preparationTimeStr);
        } catch (NumberFormatException e) {
            showAlert("Error", "Preparation time must be a valid number.");
            return;
        }

        DatabaseHelper.saveRecipe(recipeName, category, preparationTime, instructions, imageUrl);

        for (HBox ingredientBox : ingredientInputBoxes) {
            ComboBox<String> ingredientComboBox = (ComboBox<String>) ingredientBox.getChildren().get(0);
            TextField ingredientQuantityField = (TextField) ingredientBox.getChildren().get(1);

            String ingredientName = ingredientComboBox.getValue();
            String quantityStr = ingredientQuantityField.getText();

            if (ingredientName != null && !quantityStr.isEmpty()) {
                double quantity = Double.parseDouble(quantityStr);
                DatabaseHelper.saveRecipeIngredient(recipeName, ingredientName, quantity);
            } else {
                System.out.println("Geçersiz malzeme veya miktar: " + ingredientName + ", " + quantityStr);
            }
        }

        showAlert("Success", "Tarif başarıyla eklendi.");
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) recipeNameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
