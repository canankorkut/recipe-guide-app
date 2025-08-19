module org.recipeguide.recipeguideapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens org.recipeguide.recipeguideapp to javafx.fxml;
    exports org.recipeguide.recipeguideapp;
    exports org.recipeguide.recipeguideapp.database;
    opens org.recipeguide.recipeguideapp.database to javafx.fxml;
}