package com.hswgt.android.clip2gether;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPRequest {

    private final String USER_AGENT = "Mozilla/5.0";

    private String url, result;

    /**
     * Der Konstruktor initialisiert ein neues HTTPRequest Objekt, es wird die @param url alS String übergeben, welche Adresse per POST oder GET angefragt werden soll.
     *
     * @param url
     */
    public HTTPRequest(String url) {
        this.url = url;
    }

    /**
     * Führt einen GET Request auf das Objekt durch ( bzw. die URL )
     * @throws IOException
     */
    public void getRequest() throws IOException {

        // neues URL Object erzeugen
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional defaultwert ist GET
        con.setRequestMethod("GET");

        // request header hinzufügen
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        result = response.toString();
    }

    /**
     * Führt einen POST Request auf das Objekt durch ( bzw. die URL ), es wird eine Liste an URL Parametern @param urlParameters extra angegeben.
     *
     * Beispiele
     * "username=exampleuser&password=examplepassword&commkey=ASdno124KA123ASD230ASDm0"
     *
     * @param urlParameters
     * @throws IOException
     */
    public void postRequest(String urlParameters) throws IOException {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // reuqest header hinzufügen
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        result = response.toString();

    }

    /**
     * Das gespeiche Ergebniss des vorgegangenen Requestes als String zurückgeben
     * @return
     */
    public String getResult() {
        return result;
    }
}
