package pl.hmarchwat.pg;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Controllers {
    @FXML
    private ListView<String> listViewUSD= new ListView<>();
    @FXML
    private DatePicker datePicker;

    @FXML
    private void dateChange() {
        Date today= Calendar.getInstance().getTime();
        ObservableList<String> data= FXCollections.observableArrayList();
        try {
            String URLadress="http://api.nbp.pl/api/exchangerates/rates/a/usd/2016-04-04/?format=json";
            URL url=new URL(URLadress);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            //sprawdzanie połączenia kod 200=OK
            int responsecode=connection.getResponseCode();
            if(responsecode!=200){
                throw new RuntimeException("Błąd HTTP:"+responsecode);
            }else {
                StringBuilder URLtext= new StringBuilder();
                Scanner scanner=new Scanner(url.openStream());
                while (scanner.hasNext()){
                    URLtext.append(scanner.nextLine());
                }
                scanner.close();
                //przetwarzanie wczytanego tekstu ze strony jako obiekt JSON
                JSONParser parser=new JSONParser();
                JSONObject object= (JSONObject) parser.parse(String.valueOf(URLtext));
                JSONArray array= (JSONArray) object.get("rates");
                for (int i=0;i<array.size();i++){
                    JSONObject individualObject= (JSONObject) array.get(i);
                    //przetwarzamy wszystkie istniejące obiekty w tabeli rates.
                    data.add(individualObject.get("effectiveDate").toString()+"   "+individualObject.get("mid"));
                    listViewUSD.setItems(data);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }
}