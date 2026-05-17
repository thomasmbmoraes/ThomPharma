/**
 * modulo principal do sistema farmap
 * declara as dependencias e pacotes exportados
 */
module farmap {
    // dependencias do javafx para interface grafica
    requires javafx.controls;
    requires javafx.fxml;

    // dependencias para conexao com banco de dados postgresql
    requires java.sql;
    requires org.postgresql.jdbc;

    // permite que o javafx acesse as classes dos pacotes abaixo
    // necessario para carregar os controllers dos arquivos fxml
    opens farmap to javafx.fxml;
    opens farmap.telas to javafx.fxml;

    // permite que o javafx acesse os modelos para exibir nas tabelas
    opens farmap.modelo to javafx.base;

    // exporta os pacotes para uso entre modulos
    exports farmap;
    exports farmap.telas;
    exports farmap.modelo;
}