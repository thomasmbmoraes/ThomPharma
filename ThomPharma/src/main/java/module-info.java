/**
 * Módulo principal do sistema ThomPharma.
 */
module thompharma {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires org.postgresql.jdbc;
    requires jbcrypt;

    requires com.zaxxer.hikari;
    requires org.slf4j;

    opens thompharma to javafx.fxml, org.junit.platform.commons;
    opens thompharma.telas to javafx.fxml;
    opens thompharma.modelo to javafx.base;
    opens thompharma.dao to org.junit.platform.commons;
    opens thompharma.service to org.junit.platform.commons;

    exports thompharma;
    exports thompharma.telas;
    exports thompharma.modelo;
    exports thompharma.dao;
    exports thompharma.service;
}
