package org.recipeguide.recipeguideapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.recipeguide.recipeguideapp.database.DatabaseHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class HelloController {
    public TextField searchField;
    public ImageView logoImageView;
    public GridPane recipeGrid;
    public ComboBox categoryFilter;
    public ScrollPane scrollPane;
    public GridPane resultsGrid;
    public ComboBox costFilter;
    public ComboBox ingredientCountFilter;
    public Button searchByRecipeButton;
    public Button searchByIngredientButton;
    public Button filterButton;
    public ComboBox prepTimeCombo;
    public ComboBox costCombo;
    public Button sortButton;
    public ScrollPane resultsScrollPane;
    public Label resultsLabel;
    private ImageView createSafeImageView(String imagePath, double width, double height) {
        ImageView imageView = new ImageView();
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);

        try {
            if (imagePath != null && getClass().getResource(imagePath) != null) {
                String fullPath = getClass().getResource(imagePath).toExternalForm();
                Image image = new Image(fullPath);
                imageView.setImage(image);
            } else {
                if (imagePath != null) {
                    System.out.println("Görsel bulunamadı: " + imagePath);
                }
            }
        } catch (Exception e) {
            System.out.println("Görsel yüklenirken hata oluştu: " + imagePath + " - " + e.getMessage());
        }

        return imageView;
    }
    private void showPlaceholderMessage() {
        resultsGrid.getChildren().clear();

        VBox placeholderContainer = new VBox(30);
        placeholderContainer.setAlignment(Pos.CENTER);

        VBox iconContainer = new VBox(5);
        iconContainer.setAlignment(Pos.CENTER);

        Label searchIcon = new Label("🔍");
        searchIcon.setStyle(
                "-fx-font-size: 48px;" +
                        "-fx-text-fill: #ADB5BD;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(173,181,189,0.3), 5, 0, 0, 1);"
        );

        iconContainer.getChildren().addAll(searchIcon);

        Label mainMessage = new Label("🍴 Lezzetli Tarifler Sizi Bekliyor!");
        mainMessage.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #FF6347;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);"
        );

        Label subMessage = new Label("Tarif adı ile arama yapın veya elinizdeki malzemeleri girin");
        subMessage.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #666666;" +
                        "-fx-font-style: italic;"
        );

        placeholderContainer.getChildren().addAll(
                iconContainer,
                mainMessage,
                subMessage
        );

        resultsGrid.add(placeholderContainer, 13, 0, 3, 1);
    }


    private void showEmptyResultsMessage() {
        resultsGrid.getChildren().clear();

        VBox emptyContainer = new VBox(15);
        emptyContainer.setAlignment(Pos.CENTER);
        emptyContainer.setPadding(new Insets(50));
        emptyContainer.setStyle("-fx-background-color: #FFF3CD;");

        Label emptyIcon = new Label("📭");
        emptyIcon.setStyle("-fx-font-size: 36px;");

        Label emptyMessage = new Label("Sonuç Bulunamadı");
        emptyMessage.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #856404;"
        );

        Label emptySubMessage = new Label("Farklı arama terimleri deneyebilirsiniz");
        emptySubMessage.setStyle(
                "-fx-font-size: 12px; " +
                        "-fx-text-fill: #856404;"
        );

        emptyContainer.getChildren().addAll(emptyIcon, emptyMessage, emptySubMessage);
        resultsGrid.add(emptyContainer, 0, 0, 3, 1);
    }

    @FXML
    public void initialize() {
        try {
            InputStream logoStream = getClass().getResourceAsStream("/org/recipeguide/recipeguideapp/images/logo.png");
            if (logoStream != null) {
                Image logo = new Image(logoStream);
                logoImageView.setImage(logo);
            } else {
                System.out.println("Logo dosyası bulunamadı, boş bırakılıyor.");
            }
        } catch (Exception e) {
            System.out.println("Logo yüklenirken hata oluştu, boş bırakılıyor: " + e.getMessage());
        }

        showPlaceholderMessage();

        recipeGrid.getChildren().clear();
        ObservableList<Recipe> recipes = DatabaseHelper.getAllRecipes();
        int row = 0;
        int column = 0;

        for (Recipe recipe : recipes) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #DDDDDD; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
            card.setPrefWidth(200);

            double epsilon = 0.00001;

            if (recipe.getMissingCost() > epsilon) {
                card.setStyle("-fx-background-color: #FFCCCC;");
            } else {
                card.setStyle("-fx-background-color: #CCFFCC;");
            }

            ImageView imageView = createSafeImageView(recipe.getImageUrl(), 180, 120);

            Label nameLabel = new Label(recipe.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333333;");

            Label prepTimeLabel = new Label("Prep Time: " + recipe.getPreparationTime() + " dakika");
            prepTimeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777;");

            Label costLabel = new Label("Cost: " + String.format("%.2f", recipe.getTotalCost()) + " TL");
            costLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777;");

            if (recipe.getMissingCost() > epsilon) {
                costLabel.setText("Missing Cost: " + String.format("%.2f", recipe.getMissingCost()) + " TL");
            }

            Button updateButton = new Button("Update");
            updateButton.setOnAction(e -> onUpdateRecipe(recipe));
            updateButton.setStyle(
                    "-fx-background-color: #FF6347; " +
                            "-fx-text-fill: white; " +
                            "-fx-border-color: #FF6347; " +
                            "-fx-border-radius: 16px; " +
                            "-fx-background-radius: 16px; " +
                            "-fx-padding: 3 7;"
            );

            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> onDeleteRecipe(recipe));
            deleteButton.setStyle(
                    "-fx-background-color: #FF6347; " +
                            "-fx-text-fill: white; " +
                            "-fx-border-color: #FF6347; " +
                            "-fx-border-radius: 16px; " +
                            "-fx-background-radius: 16px; " +
                            "-fx-padding: 3 7;"
            );

            HBox actionButtons = new HBox(5);
            actionButtons.setAlignment(Pos.CENTER);
            actionButtons.getChildren().addAll(updateButton, deleteButton);

            card.getChildren().addAll(imageView, nameLabel, prepTimeLabel, costLabel, actionButtons);
            card.setOnMouseClicked(e -> showRecipeDetails(recipe));

            recipeGrid.add(card, column, row);

            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }

        prepTimeCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                costCombo.setDisable(true);
            } else {
                costCombo.setDisable(false);
            }
        });

        costCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                prepTimeCombo.setDisable(true);
            } else {
                prepTimeCombo.setDisable(false);
            }
        });
    }

    @FXML
    private void showRecipeDetails(Recipe recipe) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/recipeguide/recipeguideapp/recipe_details.fxml"));
            Parent root = loader.load();

            RecipeDetailsController controller = loader.getController();
            controller.setRecipe(recipe);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(recipe.getName());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddRecipe(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/recipeguide/recipeguideapp/add_recipe.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Recipe");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onUpdateRecipe(Recipe recipe) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/recipeguide/recipeguideapp/update_recipe.fxml"));
            AnchorPane updateRecipeView = loader.load();
            UpdateRecipe controller = loader.getController();
            controller.setRecipeData(recipe);

            Stage stage = new Stage();
            stage.setTitle("Update Recipe");
            stage.setScene(new Scene(updateRecipeView));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteRecipe(Recipe recipe) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Bu tarifi silmek istediğinize emin misiniz?");
        alert.setContentText(recipe.getName());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DatabaseHelper.deleteRecipe(recipe.getId());
                DatabaseHelper.deleteRecipeAndIngredients(recipe.getId());
                refreshRecipeGrid();
            }
        });
    }

    private void refreshRecipeGrid() {
        recipeGrid.getChildren().clear();
        ObservableList<Recipe> recipes = DatabaseHelper.getAllRecipes();
        int row = 0;
        int column = 0;

        for (Recipe recipe : recipes) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #DDDDDD; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
            card.setPrefWidth(200);

            double epsilon = 0.00001;

            if (recipe.getMissingCost() > epsilon) {
                card.setStyle("-fx-background-color: #FFCCCC;");
            } else {
                card.setStyle("-fx-background-color: #CCFFCC;");
            }

            ImageView imageView = createSafeImageView(recipe.getImageUrl(), 180, 120);

            Label nameLabel = new Label(recipe.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333333;");

            Label prepTimeLabel = new Label("Prep Time: " + recipe.getPreparationTime() + " dakika");
            prepTimeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777;");

            Label costLabel = new Label("Cost: " + String.format("%.2f", recipe.getTotalCost()) + " TL");
            costLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777;");

            if (recipe.getMissingCost() > epsilon) {
                costLabel.setText("Missing Cost: " + String.format("%.2f", recipe.getMissingCost()) + " TL");
            }

            Button updateButton = new Button("Update");
            updateButton.setOnAction(e -> onUpdateRecipe(recipe));
            updateButton.setStyle(
                    "-fx-background-color: #FF6347; " +
                            "-fx-text-fill: white; " +
                            "-fx-border-color: #FF6347; " +
                            "-fx-border-radius: 16px; " +
                            "-fx-background-radius: 16px; " +
                            "-fx-padding: 3 7;"
            );

            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> onDeleteRecipe(recipe));
            deleteButton.setStyle(
                    "-fx-background-color: #FF6347; " +
                            "-fx-text-fill: white; " +
                            "-fx-border-color: #FF6347; " +
                            "-fx-border-radius: 16px; " +
                            "-fx-background-radius: 16px; " +
                            "-fx-padding: 3 7;"
            );

            HBox actionButtons = new HBox(5);
            actionButtons.setAlignment(Pos.CENTER);
            actionButtons.getChildren().addAll(updateButton, deleteButton);

            card.getChildren().addAll(imageView, nameLabel, prepTimeLabel, costLabel, actionButtons);
            card.setOnMouseClicked(e -> showRecipeDetails(recipe));

            recipeGrid.add(card, column, row);

            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    @FXML
    public void onCriteriaSelected() {

    }

    @FXML
    public void onSearchByRecipe() {
        String searchText = searchField.getText().toLowerCase();
        ObservableList<Recipe> allRecipes = DatabaseHelper.getAllRecipes();
        ObservableList<Recipe> filteredRecipes = FXCollections.observableArrayList();

        for (Recipe recipe : allRecipes) {
            if (recipe.getName().toLowerCase().contains(searchText)) {
                filteredRecipes.add(recipe);
            }
        }

        displayRecipes(filteredRecipes, false);
    }

    @FXML
    public void onSearchByIngredient() {
        String ingredientText = searchField.getText().toLowerCase();
        ObservableList<Recipe> allRecipes = DatabaseHelper.getAllRecipes();
        Set<Recipe> filteredRecipes = new HashSet<>();

        List<String> userIngredients = Arrays.asList(ingredientText.split(","));
        userIngredients.replaceAll(String::trim);

        for (Recipe recipe : allRecipes) {
            double matchPercentage = DatabaseHelper.calculateIngredientMatchPercentage(recipe.getName(), userIngredients);
            recipe.setMatchPercentage(matchPercentage);
        }

        List<Recipe> sortedRecipes = allRecipes.stream()
                .filter(recipe -> recipe.getMatchPercentage() > 0)
                .sorted(Comparator.comparingDouble(Recipe::getMatchPercentage).reversed())
                .collect(Collectors.toList());

        displayRecipes(FXCollections.observableArrayList(sortedRecipes), true);
    }

    private void displayRecipes(ObservableList<Recipe> recipes, boolean showMatchPercentage) {
        resultsGrid.getChildren().clear();

        if (recipes.isEmpty()) {
            showEmptyResultsMessage();
            return;
        }

        int row = 0;
        int column = 0;

        for (Recipe recipe : recipes) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #DDDDDD; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
            card.setPrefWidth(200);

            double epsilon = 0.00001;

            if (recipe.getMissingCost() > epsilon) {
                card.setStyle("-fx-background-color: #FFCCCC;");
            } else {
                card.setStyle("-fx-background-color: #CCFFCC;");
            }

            ImageView imageView = createSafeImageView(recipe.getImageUrl(), 180, 120);

            Label nameLabel = new Label(recipe.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333333;");

            Label prepTimeLabel = new Label("Prep Time: " + recipe.getPreparationTime() + " dakika");
            prepTimeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777;");

            Label costLabel = new Label("Cost: " + String.format("%.2f", recipe.getTotalCost()) + " TL");
            costLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777;");

            if (recipe.getMissingCost() > epsilon) {
                costLabel.setText("Missing Cost: " + String.format("%.2f", recipe.getMissingCost()) + " TL");
            }

            card.getChildren().addAll(imageView, nameLabel, prepTimeLabel, costLabel);

            if (showMatchPercentage) {
                Label matchPercentageLabel = new Label("Match Percentage: " + String.format("%.2f", recipe.getMatchPercentage()) + "%");
                matchPercentageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777;");
                card.getChildren().add(matchPercentageLabel);
            }

            Button updateButton = new Button("Update");
            updateButton.setOnAction(e -> onUpdateRecipe(recipe));
            updateButton.setStyle(
                    "-fx-background-color: #FF6347; " +
                            "-fx-text-fill: white; " +
                            "-fx-border-color: #FF6347; " +
                            "-fx-border-radius: 16px; " +
                            "-fx-background-radius: 16px; " +
                            "-fx-padding: 3 7;"
            );

            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> onDeleteRecipe(recipe));
            deleteButton.setStyle(
                    "-fx-background-color: #FF6347; " +
                            "-fx-text-fill: white; " +
                            "-fx-border-color: #FF6347; " +
                            "-fx-border-radius: 16px; " +
                            "-fx-background-radius: 16px; " +
                            "-fx-padding: 3 7;"
            );

            HBox actionButtons = new HBox(5);
            actionButtons.setAlignment(Pos.CENTER);
            actionButtons.getChildren().addAll(updateButton, deleteButton);

            card.getChildren().add(actionButtons);
            card.setOnMouseClicked(e -> showRecipeDetails(recipe));

            resultsGrid.add(card, column, row);

            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    @FXML
    public void onFilter() {
        String selectedCategory = (String) categoryFilter.getValue();
        String selectedCostRange = (String) costFilter.getValue();
        String selectedIngredientCount = (String) ingredientCountFilter.getValue();

        Double minCost = null, maxCost = null;
        if ("Düşük (0-200 TL)".equals(selectedCostRange)) {
            minCost = 0.0;
            maxCost = 200.0;
        } else if ("Orta (200-500 TL)".equals(selectedCostRange)) {
            minCost = 200.0;
            maxCost = 500.0;
        } else if ("Yüksek (500-1000 TL)".equals(selectedCostRange)) {
            minCost = 500.0;
            maxCost = 1000.0;
        }

        Integer minIngredients = null, maxIngredients = null;
        if ("Az (1-3)".equals(selectedIngredientCount)) {
            minIngredients = 1;
            maxIngredients = 3;
        } else if ("Orta (4-6)".equals(selectedIngredientCount)) {
            minIngredients = 4;
            maxIngredients = 6;
        } else if ("Çok (7+)".equals(selectedIngredientCount)) {
            minIngredients = 7;
            maxIngredients = null;
        }

        ObservableList<Recipe> filteredRecipes = DatabaseHelper.getFilteredRecipes(selectedCategory, minCost, maxCost, minIngredients, maxIngredients);
        displayRecipes(filteredRecipes, false);
    }

    @FXML
    public void onSort() {
        String selectedPrepTime = (String) prepTimeCombo.getValue();
        String selectedCost = (String) costCombo.getValue();

        ObservableList<Recipe> allRecipes = DatabaseHelper.getAllRecipes();

        if (selectedPrepTime != null) {
            if (selectedPrepTime.equals("En hızlıdan yavaşa")) {
                allRecipes = FXCollections.observableArrayList(
                        allRecipes.stream()
                                .sorted((r1, r2) -> Integer.compare(r1.getPreparationTime(), r2.getPreparationTime()))
                                .toList()
                );
            } else if (selectedPrepTime.equals("En yavaştan hızlıya")) {
                allRecipes = FXCollections.observableArrayList(
                        allRecipes.stream()
                                .sorted((r1, r2) -> Integer.compare(r2.getPreparationTime(), r1.getPreparationTime()))
                                .toList()
                );
            }
        }

        if (selectedCost != null) {
            if (selectedCost.equals("Artan")) {
                allRecipes = FXCollections.observableArrayList(
                        allRecipes.stream()
                                .sorted((r1, r2) -> Double.compare(r1.getTotalCost(), r2.getTotalCost()))
                                .toList()
                );
            } else if (selectedCost.equals("Azalan")) {
                allRecipes = FXCollections.observableArrayList(
                        allRecipes.stream()
                                .sorted((r1, r2) -> Double.compare(r2.getTotalCost(), r1.getTotalCost()))
                                .toList()
                );
            }
        }

        displayRecipes(allRecipes, false);

        prepTimeCombo.getSelectionModel().clearSelection();
        costCombo.getSelectionModel().clearSelection();
    }
}