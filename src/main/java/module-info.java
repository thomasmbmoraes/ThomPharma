module farmap {
    requires javafx.controls;
    requires javafx.fxml;

    opens farmap to javafx.fxml;
    exports farmap;
}
