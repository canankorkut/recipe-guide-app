package org.recipeguide.recipeguideapp;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.recipeguide.recipeguideapp.database.DatabaseHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UpdateRecipe {

    public TextField preparationTimeField;
    public TextField recipeNameField;
    public ComboBox<String> categoryComboBox;
    public TextArea instructionsArea;
    public ImageView recipeImageView;
    public TextField ingredientNameField;
    public TextField ingredientQuantityField;
    public ComboBox ingredientUnitComboBox;
    public TextField unitPriceField;
    public TextField ingredientCountField;
    private int recipeId;

    @FXML
    private String imageUrl;

    @FXML
    private VBox ingredientsVBox;
    private List<HBox> ingredientInputBoxes = new ArrayList<>();

    @FXML
    public void initialize() {
    }

    public void setRecipeData(Recipe recipe) {
        this.recipeId = recipe.getId();
        System.out.println("Recipe id: " + recipeId);
        recipeNameField.setText(recipe.getName());
        categoryComboBox.setValue(recipe.getCategory());
        preparationTimeField.setText(String.valueOf(recipe.getPreparationTime()));
        instructionsArea.setText(recipe.getInstructions());

        ObservableList<String> ingredients = DatabaseHelper.getRecipeIngredients(recipe.getName());
        System.out.println("Ingredients: " + ingredients);
        ingredientCountField.setText(String.valueOf(ingredients.size()));

        ingredientsVBox.getChildren().clear();
        ingredientInputBoxes.clear();

        for (String ingredientWithQuantity : ingredients) {
            String[] parts = ingredientWithQuantity.split(" - ");
            if (parts.length != 2) continue;

            String ingredientName = parts[0];
            String quantity = parts[1];

            HBox ingredientBox = new HBox(10);

            ComboBox<String> ingredientComboBox = new ComboBox<>(DatabaseHelper.getIngredients());
            ingredientComboBox.setValue(ingredientName);
            ingredientComboBox.setPrefWidth(200);

            TextField ingredientQuantityField = new TextField();
            ingredientQuantityField.setPromptText("Quantity");
            ingredientQuantityField.setText(quantity);
            ingredientQuantityField.setPrefWidth(80);

            ingredientBox.getChildren().addAll(ingredientComboBox, ingredientQuantityField);
            ingredientsVBox.getChildren().add(ingredientBox);
            ingredientInputBoxes.add(ingredientBox);
        }
    }

    public void onUpdateRecipe() {
        String recipeName = recipeNameField.getText();
        String category = categoryComboBox.getValue();
        int preparationTime = Integer.parseInt(preparationTimeField.getText());
        String instructions = instructionsArea.getText();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = DatabaseHelper.getRecipeById(recipeId).getImageUrl();
        }

        DatabaseHelper.updateRecipe(recipeId, recipeName, category, preparationTime, instructions, imageUrl);

        for (HBox ingredientBox : ingredientInputBoxes) {
            ComboBox<String> ingredientComboBox = (ComboBox<String>) ingredientBox.getChildren().get(0);
            TextField ingredientQuantityField = (TextField) ingredientBox.getChildren().get(1);

            String ingredientName = ingredientComboBox.getValue();
            String quantityStr = ingredientQuantityField.getText();

            if (ingredientName != null && !quantityStr.isEmpty()) {
                double quantity = Double.parseDouble(quantityStr);
                int ingredientId = DatabaseHelper.getIngredientIdByName(ingredientComboBox.getValue());
                DatabaseHelper.updateRecipeIngredient(recipeName, ingredientName, quantity);
            } else {
                System.out.println("Geçersiz malzeme veya miktar: " + ingredientName + ", " + quantityStr);
            }
        }
        showSuccessAlert("Success", "Tarif başarıyla güncellendi.");
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
                    showAlert("Preparation time must be a valid number.");
                }
            } else {
                showAlert("Please enter a recipe name, select a category, and enter preparation time before uploading an image.");
            }
        }
    }

    @FXML
    private void onAddIngredient() {
        String newIngredientName = ingredientNameField.getText();
        String quantityStr = ingredientQuantityField.getText();
        String unit = (String) ingredientUnitComboBox.getValue();
        String unitPriceStr = unitPriceField.getText();

        double quantity;
        double unitPrice;
        try {
            quantity = Double.parseDouble(quantityStr);
            unitPrice = Double.parseDouble(unitPriceStr);
        } catch (NumberFormatException e) {
            showAlert("Invalid quantity or unit price. Please use a valid number.");
            return;
        }

        DatabaseHelper.saveIngredient(newIngredientName, quantity, unit, unitPrice);
    }

    @FXML
    private void onCreateIngredients() {
        String countStr = ingredientCountField.getText();
        int count;
        try {
            count = Integer.parseInt(countStr);
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid number for the ingredient count.");
            return;
        }

        ObservableList<String> ingredients = DatabaseHelper.getIngredients();

        int currentIngredientCount = ingredientInputBoxes.size();

        for (int i = currentIngredientCount; i < count; i++) {
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

        ingredientCountField.setText(String.valueOf(count));
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) recipeNameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
