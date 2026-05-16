module farmap {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens farmap to javafx.fxml;
    opens farmap.telas to javafx.fxml;
    opens farmap.modelo to javafx.base;
    exports farmap;
    exports farmap.telas;
    exports farmap.modelo;
}