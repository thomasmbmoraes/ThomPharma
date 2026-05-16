module farmap {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens farmap to javafx.fxml;
    opens farmap.telas to javafx.fxml;
    exports farmap;
    exports farmap.telas;
}