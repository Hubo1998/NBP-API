module pl.hmarchwat.pg {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires json.simple;


    opens pl.hmarchwat.pg to javafx.fxml;
    exports pl.hmarchwat.pg;
}