package pl.hmarchwat.pg;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.Date;
import java.util.EventObject;

public class Controllers {
    @FXML
    private Button button;
    @FXML
    private DatePicker dp;

    @FXML
    //private void date(ActionEvent event){
    //}
    private void action(){
        dp.setValue(LocalDate.of(2022,1,2));
    }
}