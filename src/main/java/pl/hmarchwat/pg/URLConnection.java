package pl.hmarchwat.pg;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class URLConnection {
    private final URL URL;
    private final Integer responseCode;

    public URLConnection(URL URL) throws IOException {
        this.URL =URL;
        this.responseCode=getConnection(URL);
    }

    public URL getURL() {
        return URL;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public Integer getConnection(URL url) throws IOException {
        //nawiązywanie połączenia ze stroną
        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection.getResponseCode();
    }

    public StringBuilder scanURL() throws IOException {
        //skanowanie tekstu wczytanego ze strony
        StringBuilder URLtext= new StringBuilder();
        Scanner scanner=new Scanner(getURL().openStream());
        while (scanner.hasNext()){
            URLtext.append(scanner.nextLine());
        }
        scanner.close();
        return URLtext;
    }

    public JSONObject getJSONObject() throws IOException, ParseException {
        StringBuilder textFromSite=scanURL();
        //przetwarzanie wczytanego tekstu ze strony jako obiekt JSON
        JSONParser parser=new JSONParser();
        return (JSONObject) parser.parse(String.valueOf(textFromSite));
    }
}
