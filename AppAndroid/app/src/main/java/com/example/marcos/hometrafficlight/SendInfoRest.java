package com.example.marcos.hometrafficlight;

import android.content.Context;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Marcos on 28/10/2017.
 */

public class SendInfoRest extends AsyncTask<URL, Integer, Long> {

    private final String TAG = "SendInfoRest";
    private Context context;

    public SendInfoRest(Context _context){
        context = _context;
    }

    protected Long doInBackground(URL... urls) {

        long aLong = 1;

        Log.d(TAG, "Run async task");
        URL endpoint;

        AdvertisingIdClient.Info idInfo = null;

        try {
            idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String advertId = idInfo.getId();

        Log.d(TAG, "Dispositivo: " + advertId);

        BatteryManager bm = (BatteryManager) context.getSystemService(context.BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        Log.d(TAG, "Bateria actual: " + batLevel);

        try {
            //LOCAL: endpoint = new URL("http://10.0.2.2:8080/HomeTrafficLight/rest/moviles/" + advertId);
            endpoint = new URL("http://158.49.245.82:8081/HomeTrafficLight/rest/moviles/" + advertId);

            JSONObject json = new JSONObject();
            json.put("dispositivo", advertId);
            json.put("bateria", batLevel);
            String requestBody = json.toString();

            Log.d(TAG, "requestBody: " + requestBody);

            // Create connection
            try {
                HttpURLConnection myConnection =
                        (HttpURLConnection) endpoint.openConnection();

                myConnection.setRequestProperty("Accept", "application/json");
                myConnection.setRequestMethod("GET");

                if (myConnection.getResponseCode() == 200) {
                    Log.d(TAG, "Respuesta 200");

                    //LOCAL: endpoint = new URL("http://10.0.2.2:8080/HomeTrafficLight/rest/moviles/" + advertId);
                    endpoint = new URL("http://158.49.245.82:8081/HomeTrafficLight/rest/moviles/" + advertId);

                    HttpURLConnection putConnection =
                            (HttpURLConnection) endpoint.openConnection();

                    putConnection.setRequestProperty("Accept", "application/json");
                    putConnection.setRequestProperty("Content-Type", "application/json");

                    putConnection.setRequestMethod("PUT");
                    putConnection.setDoOutput(true);

                    DataOutputStream dStream = new DataOutputStream(putConnection.getOutputStream());
                    dStream.writeBytes(requestBody);
                    dStream.flush();
                    dStream.close();

                    if (putConnection.getResponseCode() == 204) {
                        Log.d(TAG, "Dispositivo actualizado en el REST");
                    } else {
                        Log.d(TAG, "Error en PUT: " + putConnection.getResponseCode());
                    }

                    putConnection.disconnect();

                    myConnection.disconnect();

                } else if (myConnection.getResponseCode() == 400) {
                    Log.d(TAG, "Respuesta 400");

                    //LOCAL endpoint = new URL("http://10.0.2.2:8080/HomeTrafficLight/rest/moviles");
                    endpoint = new URL("http://158.49.245.82:8081/HomeTrafficLight/rest/moviles");

                    HttpURLConnection postConnection =
                            (HttpURLConnection) endpoint.openConnection();

                    postConnection.setRequestProperty("Accept", "application/json");
                    postConnection.setRequestProperty("Content-Type", "application/json");

                    postConnection.setRequestMethod("POST");
                    postConnection.setDoOutput(true);

                    DataOutputStream dStream = new DataOutputStream(postConnection.getOutputStream());
                    dStream.writeBytes(requestBody);
                    dStream.flush();
                    dStream.close();

                    if (postConnection.getResponseCode() == 201) {
                        Log.d(TAG, "Dispositivo registrado en el REST");
                    } else {
                        Log.d(TAG, "Error en POST: " + postConnection.getResponseCode());
                    }

                    postConnection.disconnect();
                    myConnection.disconnect();

                } else {
                    // TODO: Error handling code goes here
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return aLong;
    }
}