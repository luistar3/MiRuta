package com.example.luis.miruta;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.JsonReader;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.util.jar.Manifest;



import javax.net.ssl.HttpsURLConnection;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnPolylineClickListener ,OnMapReadyCallback ,GoogleMap.OnMapClickListener,GoogleMap.OnMapLongClickListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private Marker marcador; // marcador par nuestra ubicaccion
    Spinner spinner;// lista para rutas
    TextView texto;
    String rutaL;
    double lat = 0.0, lng = 0.0;






    LocationManager locationManager;
    String provider;
    private final int MY_PERMISSIONS_REQUEST_CODE=1;
    Location location;
    Boolean isPermissionGranted=false;

    //arreglo de prueba
    //String FRutas[] = {"Red","Blue","White","Yellow","Black", "Green","Purple","Orange","Grey"};


    List<String> lista_rutas = new ArrayList<>();// listado e rutas




            @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





        //texto = (TextView)findViewById(R.id.text1) ;
        // seleccionar el spinner
        spinner = (Spinner) findViewById(R.id.spinner3);
        new ConsultarDatos().execute("http://10.0.2.2:8080/gpsmovil/consultarRutas.php");


    }



    //funcion par acargar spinner en el metodo asynctask
    public void cargarlistado() {

        //texto.setText(rutaL);
        String[] rutas = rutaL.split(",");
        // agregar elemento del array a spinner
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, rutas);
        spinner.setAdapter(adaptador);


    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(51.5, -0.1), new LatLng(40.7, -74.0))
                .width(5)
                .color(Color.RED));
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

            JSONArray ruta = null;


            try {
                ruta = new JSONArray(result);
                StringBuilder stringBuilder = new StringBuilder();
                // int ca= ruta.length();
                // texto.setText(ruta.getString(2)+ca);
                //inicializamos la lista donde almacenaremos los objetos Fruta
                String carac1, carac2, carac = "";
                for (int i = 0; i < ruta.length(); i++) {
                    lista_rutas.add(ruta.getString(i)); //creamos un objeto ruta y lo insertamos en la lista
                }
                for (int i = 0; i < lista_rutas.size(); i++) {
                    carac = lista_rutas.get(i);
                    carac1 = carac.substring(0, carac.length() - 2);// eliminar 2 ultimos caracteres
                    carac2 = carac1.substring(2);// elimniar 2 primero caraceteres
                    stringBuilder.append(carac2);
                    if (i == lista_rutas.size() - 1) {
                    } else {
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
        myurl = myurl.replace(" ", "%20");
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

        miUbiacion();

       //  Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        Polyline line = mMap.addPolyline(new PolylineOptions()
//                .add(new LatLng(51.5, -0.1), new LatLng(40.7, -74.0))
//                .width(30)
//                .color(Color.RED));

//        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
//                .clickable(true)
//                .add(
//                        new LatLng(-35.016, 143.321),
//                        new LatLng(-34.747, 145.592),
//                        new LatLng(-34.364, 147.891),
//                        new LatLng(-33.501, 150.217),
//                        new LatLng(-32.306, 149.248),
//                        new LatLng(-32.491, 147.309))
//                .width(20).color(Color.RED));



        ArrayList<LatLng> coordList = new ArrayList<LatLng>();

// Adding points to ArrayList
        coordList.add(new LatLng(-18.00064985805495, -70.23515902584228));
        coordList.add(new LatLng(-18.003710939035983, -70.23301325863036));
        coordList.add(new LatLng(-18.005996511511366,-70.23627482479247));
        coordList.add(new LatLng(-18.00750660563893,-70.23923598354492));
        coordList.add(new LatLng(-18.00505779783065,-70.24129592006835));
        coordList.add(new LatLng(-18.002404884317595,-70.237991438562));
        coordList.add(new LatLng(-18.001017190578647,-70.23614607875976));
        coordList.add(new LatLng(-18.000813117048846,-70.23490153377685));
// etc...

// Find map fragment. This line work only with support library


        PolylineOptions polylineOptions = new PolylineOptions();

// Create polyline options with existing LatLng ArrayList
        polylineOptions.addAll(coordList);
        polylineOptions
                .width(25)
                .color(Color.RED);

// Adding multiple points in map using polyline and arraylist
        mMap.addPolyline(polylineOptions);


    }

    public void agregarMarcodor(double lat, double lng) {
        LatLng coodernadas = new LatLng(lat, lng);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coodernadas, 100);
        if (marcador != null) marcador.remove();

        marcador = mMap.addMarker(new MarkerOptions() // agregar marcador al mapa
                .position(coodernadas)
                .title("MI Ubicacion")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
        mMap.animateCamera(miUbicacion);

    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            agregarMarcodor(lat, lng);
        }

    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
        actualizarUbicacion(location);
        }


        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void miUbiacion() {



//        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
//            return;
//
//        }
//        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0,locationListener);

        if (ActivityCompat.checkSelfPermission(getApplicationContext()
                , android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                &&ActivityCompat.checkSelfPermission(getApplicationContext()
                ,android.Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else {
            if (!mMap.isMyLocationEnabled())
                mMap.setMyLocationEnabled(true);

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (myLocation == null){

                Criteria criteria1 = new Criteria();
                criteria1.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = lm.getBestProvider(criteria1,true);

                myLocation = lm.getLastKnownLocation(provider);



            }
            if (myLocation != null){
                LatLng userLocation = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation,14),100,null);


            }

        }

       LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
       Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
     actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,0,locationListener);

    }




}
