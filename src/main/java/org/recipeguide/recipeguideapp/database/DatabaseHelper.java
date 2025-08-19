package org.recipeguide.recipeguideapp.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.recipeguide.recipeguideapp.Recipe;

public class DatabaseHelper {
    private static Connection connection;
    public static ObservableList<Recipe> getAllRecipes() {
        ObservableList<Recipe> recipes = FXCollections.observableArrayList();

        String query = """
            SELECT r.recipe_id, r.recipe_name, r.category, r.preparation_time, r.instructions, r.image_url,\s
                   SUM(ri.ingredient_quantity * i.unit_price) AS total_cost,
                   SUM(CASE WHEN ri.ingredient_quantity > i.total_quantity THEN (ri.ingredient_quantity - i.total_quantity) * i.unit_price ELSE 0 END) AS missing_cost
            FROM recipes r
            LEFT JOIN recipe_ingredient ri ON r.recipe_id = ri.recipe_id
            LEFT JOIN ingredients i ON ri.ingredient_id = i.ingredient_id
            GROUP BY r.recipe_id, r.recipe_name, r.category, r.preparation_time, r.instructions, r.image_url;
        """;

        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("recipe_id"));
                recipe.setName(rs.getString("recipe_name"));
                recipe.setCategory(rs.getString("category"));
                recipe.setPreparationTime(rs.getInt("preparation_time"));
                recipe.setInstructions(rs.getString("instructions"));
                recipe.setImageUrl(rs.getString("image_url"));
                recipe.setTotalCost(rs.getDouble("total_cost"));
                recipe.setMissingCost(rs.getDouble("missing_cost"));

                recipes.add(recipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recipes;
    }

    public static void saveRecipe(String recipeName, String category, int preparationTime, String instructions, String imageUrl) {
        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres")) {
            // Aynı tarif ismine sahip bir tarif olup olmadığını kontrol et
            String checkQuery = "SELECT COUNT(*) FROM recipes WHERE recipe_name = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, recipeName);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }

            String query = "INSERT INTO recipes (recipe_name, category, preparation_time, instructions, image_url) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, recipeName);
            stmt.setString(2, category);
            stmt.setInt(3, preparationTime);
            stmt.setString(4, instructions);
            stmt.setString(5, imageUrl);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<String> getIngredients() {
        ObservableList<String> ingredients = FXCollections.observableArrayList();
        String query = "SELECT ingredient_name FROM ingredients";
        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres");
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ingredients.add(rs.getString("ingredient_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    public static void saveIngredient(String ingredientName, double quantity, String unit, double unitPrice) {
        String query = "INSERT INTO ingredients (ingredient_name, total_quantity, ingredient_unit, unit_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres");
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, ingredientName);
            pstmt.setDouble(2, quantity);
            pstmt.setString(3, unit);
            pstmt.setDouble(4, unitPrice);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveRecipeImage(File imageFile, String recipeName, String category, int preparationTime, String instructions) {
        String imageDirectory = "src/main/resources/org/recipeguide/recipeguideapp/images";

        File dir = new File(imageDirectory);

        try {
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (created) {
                    System.out.println("Dizin başarıyla oluşturuldu: " + imageDirectory);
                } else {
                    System.out.println("Dizin oluşturulurken bir hata oluştu: " + imageDirectory);
                }
            } else {
                //System.out.println("Dizin zaten mevcut: " + imageDirectory);
            }
        } catch (Exception e) {
            System.out.println("Hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }

        File destinationFile = new File(dir, imageFile.getName());

        try (FileInputStream fis = new FileInputStream(imageFile)) {
            Files.copy(fis, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/org/recipeguide/recipeguideapp/images/" + destinationFile.getName();
            String sql = "INSERT INTO recipes (recipe_name, category, preparation_time, instructions, image_url) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres");
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, recipeName);
                pstmt.setString(2, category);
                pstmt.setInt(3, preparationTime);
                pstmt.setString(4, instructions);
                pstmt.setString(5, imageUrl);
                pstmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveRecipeIngredient(String recipeName, String ingredientName, double quantity) {
        String getRecipeIdQuery = "SELECT recipe_id FROM recipes WHERE recipe_name = ?";
        String getIngredientIdQuery = "SELECT ingredient_id FROM ingredients WHERE ingredient_name = ?";
        String insertQuery = "INSERT INTO recipe_ingredient (recipe_id, ingredient_id, ingredient_quantity) VALUES (?, ?, ?)";

        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres")) {
            PreparedStatement getRecipeIdStmt = conn.prepareStatement(getRecipeIdQuery);
            getRecipeIdStmt.setString(1, recipeName);
            ResultSet recipeRs = getRecipeIdStmt.executeQuery();
            int recipeId = -1;
            if (recipeRs.next()) {
                recipeId = recipeRs.getInt("recipe_id");
            }

            PreparedStatement getIngredientIdStmt = conn.prepareStatement(getIngredientIdQuery);
            getIngredientIdStmt.setString(1, ingredientName);
            ResultSet ingredientRs = getIngredientIdStmt.executeQuery();
            int ingredientId = -1;
            if (ingredientRs.next()) {
                ingredientId = ingredientRs.getInt("ingredient_id");
            }

            if (recipeId != -1 && ingredientId != -1) {
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, recipeId);
                insertStmt.setInt(2, ingredientId);
                insertStmt.setDouble(3, quantity);
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateRecipe(int recipeId, String recipeName, String category, int preparationTime, String instructions, String imageUrl) {
        String query = "UPDATE recipes SET recipe_name = ?, category = ?, preparation_time = ?, instructions = ?, image_url = ? WHERE recipe_id = ?";

        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres");
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, recipeName);
            pstmt.setString(2, category);
            pstmt.setInt(3, preparationTime);
            pstmt.setString(4, instructions);
            pstmt.setString(5, imageUrl);
            pstmt.setInt(6, recipeId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Recipe getRecipeById(int recipeId) {
        Recipe recipe = null;
        String query = "SELECT * FROM recipes WHERE recipe_id = ?";

        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres");
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, recipeId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                recipe = new Recipe();
                recipe.setId(resultSet.getInt("recipe_id"));
                recipe.setName(resultSet.getString("recipe_name"));
                recipe.setCategory(resultSet.getString("category"));
                recipe.setPreparationTime(resultSet.getInt("preparation_time"));
                recipe.setInstructions(resultSet.getString("instructions"));
                recipe.setImageUrl(resultSet.getString("image_url"));
            } else {
                //System.out.println("No recipe found with ID: " + recipeId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipe;
    }

    public static void updateRecipeIngredient(String recipeName, String ingredientName, double quantity) {
        String getRecipeIdQuery = "SELECT recipe_id FROM recipes WHERE recipe_name = ?";
        String getIngredientIdQuery = "SELECT ingredient_id FROM ingredients WHERE ingredient_name = ?";
        String getIngredientInRecipeQuery = "SELECT ingredient_id FROM recipe_ingredient WHERE recipe_id = ? AND ingredient_id = ?";
        String updateQuery = "UPDATE recipe_ingredient SET ingredient_quantity = ? WHERE recipe_id = ? AND ingredient_id = ?";
        String insertQuery = "INSERT INTO recipe_ingredient (recipe_id, ingredient_id, ingredient_quantity) VALUES (?, ?, ?)";

        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres")) {
            PreparedStatement getRecipeIdStmt = conn.prepareStatement(getRecipeIdQuery);
            getRecipeIdStmt.setString(1, recipeName);
            ResultSet recipeRs = getRecipeIdStmt.executeQuery();
            int recipeId = -1;
            if (recipeRs.next()) {
                recipeId = recipeRs.getInt("recipe_id");
            }

            PreparedStatement getIngredientIdStmt = conn.prepareStatement(getIngredientIdQuery);
            getIngredientIdStmt.setString(1, ingredientName);
            ResultSet ingredientRs = getIngredientIdStmt.executeQuery();
            int ingredientId = -1;
            if (ingredientRs.next()) {
                ingredientId = ingredientRs.getInt("ingredient_id");
            }

            if (recipeId != -1) {
                PreparedStatement getIngredientInRecipeStmt = conn.prepareStatement(getIngredientInRecipeQuery);
                getIngredientInRecipeStmt.setInt(1, recipeId);
                getIngredientInRecipeStmt.setInt(2, ingredientId);
                ResultSet ingredientInRecipeRs = getIngredientInRecipeStmt.executeQuery();

                if (ingredientInRecipeRs.next()) {
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setDouble(1, quantity);
                    updateStmt.setInt(2, recipeId);
                    updateStmt.setInt(3, ingredientId);
                    updateStmt.executeUpdate();
                } else {
                    PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                    insertStmt.setInt(1, recipeId);
                    insertStmt.setInt(2, ingredientId);
                    insertStmt.setDouble(3, quantity);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getIngredientIdByName(String ingredientName) {
        String sql = "SELECT ingredient_id FROM ingredients WHERE ingredient_name = ?";
        int ingredientId = -1;

        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ingredientName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ingredientId = rs.getInt("ingredient_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ingredientId;
    }


    public static String getIngredientQuantity(String recipeName, String ingredient) {
        String getRecipeIdQuery = "SELECT recipe_id FROM recipes WHERE recipe_name = ?";
        String getIngredientQuantityQuery = """
        SELECT ri.ingredient_quantity\s
        FROM recipe_ingredient ri
        JOIN ingredients i ON ri.ingredient_id = i.ingredient_id
        WHERE ri.recipe_id = ? AND i.ingredient_name = ?
    """;
        String quantity = "";

        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres")) {
            PreparedStatement getRecipeIdStmt = conn.prepareStatement(getRecipeIdQuery);
            getRecipeIdStmt.setString(1, recipeName);
            ResultSet recipeRs = getRecipeIdStmt.executeQuery();
            int recipeId = -1;
            if (recipeRs.next()) {
                recipeId = recipeRs.getInt("recipe_id");
            }

            if (recipeId != -1) {
                PreparedStatement getQuantityStmt = conn.prepareStatement(getIngredientQuantityQuery);
                getQuantityStmt.setInt(1, recipeId);
                getQuantityStmt.setString(2, ingredient);
                ResultSet quantityRs = getQuantityStmt.executeQuery();
                if (quantityRs.next()) {
                    quantity = quantityRs.getString("ingredient_quantity");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quantity;
    }

    public static ObservableList<String> getRecipeIngredients(String recipeName) {
        ObservableList<String> ingredients = FXCollections.observableArrayList();
        String getRecipeIdQuery = "SELECT recipe_id FROM recipes WHERE recipe_name = ?";
        String getIngredientsQuery = """
        SELECT i.ingredient_name, ri.ingredient_quantity, i.ingredient_unit\s
        FROM recipe_ingredient ri
        JOIN ingredients i ON ri.ingredient_id = i.ingredient_id
        WHERE ri.recipe_id = ?
    """;

        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres")) {
            PreparedStatement getRecipeIdStmt = conn.prepareStatement(getRecipeIdQuery);
            getRecipeIdStmt.setString(1, recipeName);
            ResultSet recipeRs = getRecipeIdStmt.executeQuery();
            int recipeId = -1;
            if (recipeRs.next()) {
                recipeId = recipeRs.getInt("recipe_id");
            }
            if (recipeId != -1) {
                PreparedStatement getIngredientsStmt = conn.prepareStatement(getIngredientsQuery);
                getIngredientsStmt.setInt(1, recipeId);
                ResultSet ingredientsRs = getIngredientsStmt.executeQuery();
                while (ingredientsRs.next()) {
                    String name = ingredientsRs.getString("ingredient_name");
                    double quantity = ingredientsRs.getDouble("ingredient_quantity");
                    String unit = ingredientsRs.getString("ingredient_unit");

                    String formattedQuantity;
                    if (quantity == Math.floor(quantity)) {
                        formattedQuantity = String.valueOf((int) quantity);
                    } else {
                        formattedQuantity = String.format("%.2f", quantity);
                    }

                    String ingredientInfo = name + " - " + formattedQuantity + " " + unit;
                    ingredients.add(ingredientInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ingredients;
    }

    public static void deleteRecipe(int recipeId) {
        String sql = "DELETE FROM recipes WHERE recipe_id = ?";
        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteRecipeAndIngredients(int recipeId) {
        String deleteIngredientsSql = "DELETE FROM recipe_ingredient WHERE recipe_id = ?";

        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres")) {

            try (PreparedStatement pstmt = conn.prepareStatement(deleteIngredientsSql)) {
                pstmt.setInt(1, recipeId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error executing deleteIngredientsSql: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static double calculateIngredientMatchPercentage(String recipeName, List<String> ingredients) {
        ObservableList<String> recipeIngredients = getRecipeIngredients(recipeName);
        int matchingIngredients = 0;

        for (String ingredient : ingredients) {
            for (String recipeIngredient : recipeIngredients) {
                String recipeIngredientName = recipeIngredient.split(" - ")[0].trim().toLowerCase();

                if (recipeIngredientName.equals(ingredient.trim().toLowerCase())) {
                    matchingIngredients++;
                    break;
                }
            }
        }

        return ((double) matchingIngredients / ingredients.size()) * 100;
    }

    public static ObservableList<Recipe> getFilteredRecipes(String category, Double minCost, Double maxCost, Integer minIngredients, Integer maxIngredients) {
        ObservableList<Recipe> recipes = FXCollections.observableArrayList();
        StringBuilder query = new StringBuilder("""
        SELECT r.recipe_id, r.recipe_name, r.category, r.preparation_time, r.instructions, r.image_url,
               SUM(ri.ingredient_quantity * i.unit_price) AS total_cost
        FROM recipes r
        LEFT JOIN recipe_ingredient ri ON r.recipe_id = ri.recipe_id
        LEFT JOIN ingredients i ON ri.ingredient_id = i.ingredient_id
        WHERE 1=1
    """);

        if (category != null && !category.isEmpty()) {
            query.append(" AND r.category = '").append(category).append("'");
        }

        if (minIngredients != null) {
            query.append(" AND (SELECT COUNT(*) FROM recipe_ingredient WHERE recipe_id = r.recipe_id) >= ").append(minIngredients);
        }
        if (maxIngredients != null) {
            query.append(" AND (SELECT COUNT(*) FROM recipe_ingredient WHERE recipe_id = r.recipe_id) <= ").append(maxIngredients);
        }

        query.append(" GROUP BY r.recipe_id, r.recipe_name, r.category, r.preparation_time, r.instructions, r.image_url");

        if (minCost != null && maxCost != null) {
            query.append(" HAVING SUM(ri.ingredient_quantity * i.unit_price) BETWEEN ")
                    .append(minCost).append(" AND ").append(maxCost);
        } else if (minCost != null) {
            query.append(" HAVING SUM(ri.ingredient_quantity * i.unit_price) >= ").append(minCost);
        } else if (maxCost != null) {
            query.append(" HAVING SUM(ri.ingredient_quantity * i.unit_price) <= ").append(maxCost);
        }

        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query.toString())) {

            while (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("recipe_id"));
                recipe.setName(rs.getString("recipe_name"));
                recipe.setCategory(rs.getString("category"));
                recipe.setPreparationTime(rs.getInt("preparation_time"));
                recipe.setInstructions(rs.getString("instructions"));
                recipe.setImageUrl(rs.getString("image_url"));
                recipe.setTotalCost(rs.getDouble("total_cost"));

                recipes.add(recipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recipes;
    }

}
