package pl.hmarchwat.pg;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;

import java.util.Scanner;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Controllers {
    @FXML
    private Label message;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TableView<ExchangeRate> tableView= new TableView<>();
    private final LocalDate today= LocalDate.now();

    @FXML
    private void dateChange() throws IOException, ParseException {
        LocalDate chosenDate=datePicker.getValue();
        if(chosenDate.toEpochDay()>=today.toEpochDay()){
            message.setText("Wybierz datę z przeszłości.");
        }else {
            String stringUrlAdress="http://api.nbp.pl/api/exchangerates/rates/c/usd/"+chosenDate+"/"+today+"/?format=json";
            URL url=new URL(stringUrlAdress);
            Integer responseCode=getConnection(url);
            //sprawdzanie połączenia kod 200=OK
            if(responseCode==200){
                StringBuilder textFromSite=scanURL(url);
                JSONObject jsonObject=getJSONObject(textFromSite.toString());
                JSONArray jsonArray=(JSONArray) jsonObject.get("rates");
                generateColumns(jsonArray);
            }else if(responseCode==400){
                message.setText("Spróbuj wybrać późniejszą datę");
            }else{
                throw new RuntimeException("Błąd HTTP:"+responseCode);
            }
        }
    }
    public Integer getConnection(URL url) throws IOException {
        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection.getResponseCode();
    }
    public StringBuilder scanURL(URL url) throws IOException {
        StringBuilder URLtext= new StringBuilder();
        Scanner scanner=new Scanner(url.openStream());
        while (scanner.hasNext()){
            URLtext.append(scanner.nextLine());
        }
        scanner.close();
        return URLtext;
    }
    public JSONObject getJSONObject(String URLtext) throws ParseException {
        //przetwarzanie wczytanego tekstu ze strony jako obiekt JSON
        JSONParser parser=new JSONParser();
        return (JSONObject) parser.parse(URLtext);
    }
    public void generateColumns(JSONArray array){

        TableColumn<ExchangeRate,?> dateColumn=new TableColumn<>("Data");
        dateColumn.setMinWidth(100);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("exchangeDate"));

        TableColumn<ExchangeRate,?> bidColumn=new TableColumn<>("Kupno");
        bidColumn.setMinWidth(100);
        bidColumn.setCellValueFactory(new PropertyValueFactory<>("bid"));

        TableColumn<ExchangeRate,?> askColumn=new TableColumn<>("Sprzedaż");
        askColumn.setMinWidth(100);
        askColumn.setCellValueFactory(new PropertyValueFactory<>("ask"));

        tableView.getColumns().set(0,dateColumn);
        tableView.getColumns().set(1,bidColumn);
        tableView.getColumns().set(2,askColumn);

        ObservableList<ExchangeRate> individualObjectsList= FXCollections.observableArrayList();

        //dla pierwszej iteracji brak różnicy wartości.
        Double bid0=0.0;
        Double ask0=0.0;
        DecimalFormat df=new DecimalFormat("#.####");
        for (int i=0;i<array.size();i++) {
            //przetwarzamy wszystkie istniejące obiekty w tabeli rates.
            JSONObject individualObject = (JSONObject) array.get(i);
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
        tableView.setItems(individualObjectsList);
    }
}