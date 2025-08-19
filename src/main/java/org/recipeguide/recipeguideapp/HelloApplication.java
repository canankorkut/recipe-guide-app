package org.recipeguide.recipeguideapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.recipeguide.recipeguideapp.database.DatabaseConnection;
import org.recipeguide.recipeguideapp.database.DatabaseSetup;

import java.io.IOException;
import java.sql.Connection;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DatabaseConnection db = new DatabaseConnection();
        Connection conn =  db.getConnection("recipe_guide","postgres", "postgres");

        if (conn != null) {
            DatabaseSetup.createTables();
        } else {
            System.out.println("Failed to establish database connection.");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 870);
        stage.setTitle("Recipe Rover");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}