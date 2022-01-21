module pl.hmarchwat.pg {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens pl.hmarchwat.pg to javafx.fxml;
    exports pl.hmarchwat.pg;
}