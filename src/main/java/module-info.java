module com.example.batallanaval {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.junit.jupiter.api;

    exports com.example.batallanaval;

    opens com.example.batallanaval to javafx.fxml;

    opens test to org.junit.platform.commons;
}