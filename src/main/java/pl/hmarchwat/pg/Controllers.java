package pl.hmarchwat.pg;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;

import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class Controllers implements Initializable {
    @FXML
    private Label message;
    @FXML
    private Label infoLabel;
    @FXML
    private DatePicker dateSelection;
    @FXML
    private TableView<ExchangeRate> rateTable = new TableView<>();
    @FXML
    private final LocalDate today= LocalDate.now();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        infoLabel.setText("Notowania kursu dla waluty:");
        //Tworzenie kolumn
        TableColumn<ExchangeRate,?> dateColumn=new TableColumn<>("Data");
        dateColumn.setMinWidth(85);
        //przypisanie pól obiektu do kolumn tabeli
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("exchangeDate"));

        TableColumn<ExchangeRate,?> bidColumn=new TableColumn<>("Kupno");
        bidColumn.setMinWidth(120);
        bidColumn.setCellValueFactory(new PropertyValueFactory<>("bid"));

        TableColumn<ExchangeRate,?> askColumn=new TableColumn<>("Sprzedaż");
        askColumn.setMinWidth(120);
        askColumn.setCellValueFactory(new PropertyValueFactory<>("ask"));

        //dodanie kolumn do tabeli
        rateTable.getColumns().add(0,dateColumn);
        rateTable.getColumns().add(1,bidColumn);
        rateTable.getColumns().add(2,askColumn);
    }

    public void dateChange() throws IOException, ParseException {
        LocalDate chosenDate= dateSelection.getValue();
        if(chosenDate.toEpochDay()>=today.toEpochDay()){
            setMessage("Wybierz datę z przeszłości.");
        }else {
            String stringUrlAdress="http://api.nbp.pl/api/exchangerates/rates/c/usd/"+chosenDate+"/"+today+"/?format=json";
            URLConnection nbpusdURL=new URLConnection(new URL(stringUrlAdress));
            //sprawdzanie połączenia kod 200=OK
            if(nbpusdURL.getResponseCode()==200){
                setMessage("");
                JSONObject jsonObject=nbpusdURL.getJSONObject();
                infoLabel.setText("Notowania kursu dla waluty: "+ jsonObject.get("currency"));
                generateColumns(jsonObject);
            }else if(nbpusdURL.getResponseCode()==400){
                //błąd 400 = przekroczony limit 93 dni
                setMessage("Spróbuj wybrać późniejszą datę");
            }else{
                throw new RuntimeException("Błąd HTTP:"+nbpusdURL.getResponseCode());
            }
        }
    }

    public void generateColumns(JSONObject object){
        JSONArray jsonArray=(JSONArray) object.get("rates");
        ObservableList<ExchangeRate> individualObjectsList= FXCollections.observableArrayList();
        //dla pierwszej iteracji brak różnicy wartości.
        Double bid0=0.0;
        Double ask0=0.0;
        DecimalFormat df=new DecimalFormat("#.####");
        for (int i=0;i<jsonArray.size();i++) {
            //przetwarzamy wszystkie istniejące obiekty w tabeli rates.
            JSONObject individualObject = (JSONObject) jsonArray.get(i);
            String exchangeDate= (String) individualObject.get("effectiveDate");
            Double bid1 = (Double) individualObject.get("bid");
            Double ask1 = (Double) individualObject.get("ask");
            Double biddiff = bid1 - bid0;
            Double askdiff = ask1 - ask0;
            bid0 = bid1;
            ask0 = ask1;
            //tworzymy stringi które, będą wpisywane razem z różnicą wartości notowań do obiektów.
            String bidString;
            String askString;
            if (i != 0) {
                if (biddiff > 0) {bidString = bid1 + " (+" + df.format(biddiff) + ")";} else {bidString = bid1 + " (" + df.format(biddiff) + ")";}
                if (askdiff > 0) {askString = ask1 + " (+" + df.format(askdiff) + ")";} else {askString = ask1 + " (" + df.format(askdiff) + ")";}
            } else {
                bidString = String.valueOf(bid1);
                askString = String.valueOf(ask1);
            }
            individualObjectsList.add(new ExchangeRate(exchangeDate,bidString,askString));
        }
        rateTable.setItems(individualObjectsList);
    }
    public void setMessage(String text){
        message.setText(text);
    }
}