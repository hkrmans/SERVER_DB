module com.example.server_db_admin {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.server_db_admin to javafx.fxml;
    exports com.example.server_db_admin;
    exports com.example.server_db_admin.Controllers;
    opens com.example.server_db_admin.Controllers to javafx.fxml;
}