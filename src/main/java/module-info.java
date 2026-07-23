module com.example.batallanaval {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.example.batallanaval to javafx.fxml;
    exports com.example.batallanaval;

    opens com.example.batallanaval.view to javafx.graphics, javafx.controls;
    exports com.example.batallanaval.view;

    opens com.example.batallanaval.controller to javafx.graphics, javafx.controls;
    exports com.example.batallanaval.controller;
}