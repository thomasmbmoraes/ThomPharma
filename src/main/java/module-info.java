/**
 * modulo principal do sistema thompharma
 * declara as dependencias e pacotes exportados
 */
module thompharma {
    // dependencias do javafx para interface grafica
    requires javafx.controls;
    requires javafx.fxml;

    // dependencias para conexao com banco de dados postgresql
    requires java.sql;
    requires org.postgresql.jdbc;
    requires jbcrypt;

    // permite que o javafx acesse as classes dos pacotes abaixo
    opens thompharma to javafx.fxml;
    opens thompharma.telas to javafx.fxml;
    opens thompharma.modelo to javafx.base;

    // exporta os pacotes para uso entre modulos
    exports thompharma;
    exports thompharma.telas;
    exports thompharma.modelo;
}