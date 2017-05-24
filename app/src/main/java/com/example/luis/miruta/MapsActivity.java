package com.example.luis.miruta;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import javax.net.ssl.HttpsURLConnection;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Spinner spinner;
    TextView texto;
    String rutaL;


    //arreglo de prueba
    String FRutas[] = {"Red","Blue","White","Yellow","Black", "Green","Purple","Orange","Grey"};


    List<String> lista_rutas = new ArrayList<>();// listado e rutas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        texto = (TextView)findViewById(R.id.text1) ;
        // seleccionar el spinner
        spinner = (Spinner) findViewById(R.id.spinner3);


        new ConsultarDatos().execute("http://10.0.2.2/gpsmovil/consultarRutas.php");


    }

    //funcion par acargar spinner en el metodo asynctask
    public void cargarlistado(){

        //texto.setText(rutaL);
        String[] rutas= rutaL.split(",");
        // agregar elemento del array a spinner


        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, rutas);

        spinner.setAdapter(adaptador);



    }


    //metodo asincron para consultar Rutas
    private class ConsultarDatos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.

            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            JSONArray ruta =null;


            try {
                ruta = new JSONArray(result);
                StringBuilder stringBuilder = new StringBuilder();
                // int ca= ruta.length();
               // texto.setText(ruta.getString(2)+ca);



               //inicializamos la lista donde almacenaremos los objetos Fruta

                String carac1, carac2 ,carac = "";
                for (int i = 0; i < ruta.length(); i++) {
                    lista_rutas.add(ruta.getString(i)); //creamos un objeto ruta y lo insertamos en la lista

                }
                for (int i = 0;i < lista_rutas.size(); i++){

                    carac = lista_rutas.get(i);
                    carac1 = carac.substring(0, carac.length()-2);// eliminar 2 ultimos caracteres
                    carac2 = carac1.substring(2);// elimniar 2 primero caraceteres
                    stringBuilder.append(carac2);
                    if (i == lista_rutas.size()-1 ){

                    }

                    else {
                        stringBuilder.append(",");

                    }
                }


                String diaArray[] = ruta.getString(1).split(",");

                String finalString = stringBuilder.toString();
               // texto.setText(String.valueOf(lista_rutas.size()));
               // texto.setText(finalString);
                    rutaL = finalString;

                cargarlistado();

                //texto.setText(rutaL);





            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    //url par adecargar
    private String downloadUrl(String myurl) throws IOException {
        myurl = myurl.replace(" ","%20");
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.


        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("respuesta", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
