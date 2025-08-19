package org.recipeguide.recipeguideapp;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.recipeguide.recipeguideapp.database.DatabaseHelper;

import java.util.Objects;

public class RecipeDetailsController {
    public Text ingredientsText;
    @FXML
    private ImageView recipeImage;
    @FXML
    private Label nameLabel;
    @FXML
    private Label prepTimeLabel;
    @FXML
    private Label costLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Text instructionsText;

    @FXML
    public void setRecipe(Recipe recipe) {
        nameLabel.setText(recipe.getName());
        prepTimeLabel.setText("Prep Time: " + recipe.getPreparationTime() + " dakika");
        costLabel.setText("Cost: " + String.format("%.2f", recipe.getTotalCost()) + " TL");
        categoryLabel.setText("Category: " + recipe.getCategory());
        instructionsText.setText("Instructions: " + recipe.getInstructions());
        setIngredients(recipe.getName());


        String imagePath = Objects.requireNonNull(getClass().getResource(recipe.getImageUrl())).toExternalForm();
        recipeImage.setImage(new Image(imagePath));
    }

    private void setIngredients(String recipeName) {
        ObservableList<String> ingredients = DatabaseHelper.getRecipeIngredients(recipeName);

        if (ingredients.isEmpty()) {
            ingredientsText.setText("No ingredient information found.");
            return;
        }

        StringBuilder ingredientsBuilder = new StringBuilder("Ingredients:\n");

        for (String ingredient : ingredients) {
            ingredientsBuilder.append("• ").append(ingredient).append("\n");
        }

        ingredientsText.setText(ingredientsBuilder.toString());
    }
}
