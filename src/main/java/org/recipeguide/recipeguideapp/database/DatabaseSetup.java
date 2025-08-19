package org.recipeguide.recipeguideapp.database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {
    public static void createTables() {
        String createRecipesTable = "CREATE TABLE IF NOT EXISTS recipes ("
                + "recipe_id SERIAL PRIMARY KEY, "
                + "recipe_name VARCHAR(255) NOT NULL, "
                + "category VARCHAR(50) NOT NULL, "
                + "preparation_time INT NOT NULL, "
                + "instructions TEXT NOT NULL, "
                + "image_url TEXT NOT NULL);";

        String createIngredientsTable = "CREATE TABLE IF NOT EXISTS ingredients ("
                + "ingredient_id SERIAL PRIMARY KEY, "
                + "ingredient_name VARCHAR(255) NOT NULL, "
                + "total_quantity DECIMAL(10, 2) NOT NULL, "
                + "ingredient_unit VARCHAR(50) NOT NULL, "
                + "unit_price DECIMAL(10, 2) NOT NULL);";

        String createRecipeIngredientTable = "CREATE TABLE IF NOT EXISTS recipe_ingredient ("
                + "recipe_id INT REFERENCES recipes(recipe_id) ON DELETE CASCADE, "
                + "ingredient_id INT REFERENCES ingredients(ingredient_id) ON DELETE CASCADE, "
                + "ingredient_quantity FLOAT NOT NULL, "
                + "PRIMARY KEY (recipe_id, ingredient_id));";

        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres");
            Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(createRecipesTable);
            stmt.executeUpdate(createIngredientsTable);
            stmt.executeUpdate(createRecipeIngredientTable);

        } catch (SQLException e) {
            System.out.println("Table creation error: " + e.getMessage());
        }

    }

//   public static void dropTable(String tableName) {
//        String dropTableQuery = "DROP TABLE IF EXISTS " + tableName + " CASCADE;";
//
//        try (Connection conn = new DatabaseConnection().getConnection("recipe_guide", "postgres", "postgres");
//             Statement stmt = conn.createStatement()) {
//
//            stmt.executeUpdate(dropTableQuery);
//           System.out.println(tableName + " table dropped successfully.");
//       } catch (SQLException e) {
//            System.out.println("Error dropping table: " + e.getMessage());
//        }
//   }
}
