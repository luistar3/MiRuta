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
import android.view.View;
import android.widget.AdapterView;
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
import org.json.JSONObject;

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
    public GoogleMap mMap;
    private Marker marcador; // marcador par nuestra ubicaccion
    private Marker mar; // marcadores de vehiculos
    List<Double> listLatitud= new ArrayList<>();
    List<Double> listLongitud= new ArrayList<>();
    ArrayList<LatLng> listaCoordenadas;
    List<Double> listLat= new ArrayList<>();
    List<Double> listLng= new ArrayList<>();
    PolylineOptions polylineOptions;
    Polyline polylineFinal;

    TratamientoCoordenadas tra = new TratamientoCoordenadas();
    Spinner spinner;// lista para rutas
    TextView texto;
    String rutaL;
    double lat = 0.0, lng = 0.0;

    String[] rutas;
    String[] rutaNom;
    String[] rutaId;
    int co=0;
    Double Flatitud;
    Double Flogitud;

    List<Marker> markers = new ArrayList<Marker>();



    LocationManager locationManager;
    String provider;
    private final int MY_PERMISSIONS_REQUEST_CODE=1;
    Location location;
    Boolean isPermissionGranted=false;

    //arreglo de prueba
    //String FRutas[] = {"Red","Blue","White","Yellow","Black", "Green","Purple","Orange","Grey"};


    List<String> lista_rutas = new ArrayList<>();// listado e rutas


        private View popup=null;

    String PosicionAc="";


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
        new ConsultarDatos().execute("http://10.0.2.2:80/gpsmovil/consultarRutas.php");



                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        //mMap.clear();
                        //eliminar_Marker();
                        listaCoordenadas = new ArrayList<LatLng>();
                        polylineOptions = new PolylineOptions();
                        limpiaArray();
                        agregarMarcodora();
                        co=co+1;
                        if (polylineFinal==null){
                        }
                        else {
                            polylineFinal.remove();
                        }
                        PosicionAc = String.valueOf(rutaId[position]);
                       new ConsultarTrasadorutas().execute("http://10.0.2.2:80/gpsmovil/consultarTrasadoRutas.php?id="+rutaId[position]+"");
                        new ConsultarPuntosVehiculos().execute("http://10.0.2.2:80/gpsmovil/consultarPuntosRuta.php?id="+rutaId[position]+"");

                        //new ConsultarTrasadorutas().execute("http://10.0.2.2:8080/gpsmovil/consultarTrasadoRutas.php?id=10945988182");

                       // Toast.makeText(getBaseContext(),"caracteres- "+rutaId[position].length(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });


    }

    //metodo asincro.. para consultar los marcadores de los vehiculos
    private class ConsultarPuntosVehiculos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        @Override
        protected void onPostExecute(String result) {

            eliminar_Marker();
            ArrayList<String> puntosa = new ArrayList<>();
           // Toast.makeText(getBaseContext(),"lati"+Flatitud+"long"+Flogitud,Toast.LENGTH_LONG).show();

            puntosa=tra.tratarPuntos(result,Flatitud,Flogitud);

        for (int i = 0; i < puntosa.size(); i++) {

                String[] d = puntosa.get(i).split(",");
              Marker mir= mMap.addMarker(new MarkerOptions()
                        .title("Placa: "+d[0])

                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.van))
                        .position(new LatLng(Double.parseDouble(d[1]), Double.parseDouble(d[2])))
                      .snippet("Dist:"+d[3]+"\n Tiempo: "+d[4]+"\n Velocidad: "+d[5])



                );
                mir.showInfoWindow();
                markers.add(mir);

            }


        }


    }
    public void eliminar_Marker(){

        for (Marker mir : markers){
            mir.remove();

        }
        markers.clear();
    }

    //funcion par acargar spinner en el metodo asynctask
    public void cargarlistado() {

        //texto.setText(rutaL);
         rutas = rutaL.split(",");
        String NomRuta="";
        String IdRuta="";

        String com="";
        boolean valor=true;
        //Toast.makeText(getBaseContext(),"catidad - "+rutas.length, Toast.LENGTH_LONG).show();

        for (int u=0; u < rutas.length;u++){

            com+=rutas[u];
            if (valor==true){
                NomRuta+= rutas[u];
                valor=false;
            }
            else {
                IdRuta+=rutas[u];
                valor=true;

            }

        }
        IdRuta = IdRuta.substring(1);// elimniar 2 primero caraceteres
        rutaNom=NomRuta.split("\"");
        rutaId=IdRuta.split("\"");

        //Toast.makeText(getBaseContext(),"texto - "+IdRuta, Toast.LENGTH_LONG).show();


        // agregar elemento del array a spinner
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, rutaNom);
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


    //metodo asincron para consultar Lineas para Trasar Rutas
    private class ConsultarTrasadorutas extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        @Override
        protected void onPostExecute(String result) {


            JSONArray rutatrasado ;
            try {

                rutatrasado = new JSONArray(result);

                corrlat(rutatrasado );
                colocarPolylineas();
               // Toast.makeText(getBaseContext(),"coordenadas- "+result, Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    void limpiaArray(){


         listLat.clear();
        listLng.clear();
        listaCoordenadas.clear();



    }
    public void colocarPolylineas(){

        for (int i = 0; i < listLat.size(); i++) {

            listaCoordenadas.add(new LatLng(listLat.get(i),listLng.get(i)));
        }
        // etc...

// Find map fragment. This line work only with support library
//      Toast.makeText(getBaseContext(),">"+ listaCoordenadas.size(), Toast.LENGTH_SHORT).show();

// Create polyline options with existing LatLng ArrayList
        polylineOptions.addAll(listaCoordenadas);
        polylineOptions
                .width(25)
                .color(Color.BLUE)
                .geodesic(true);

// Adding multiple points in map using polyline and arraylist
       // mMap.addPolyline(polylineOptions);

        polylineFinal = mMap.addPolyline (polylineOptions);

    }
    void  corrlat (JSONArray re){

        List<String> lista_LatLong = new ArrayList<>();


        lista_LatLong.clear();
        listLat.clear();
        listLng.clear();
        String ll="";
        try {
            for (int i = 0; i < re.length(); i++) {
                lista_LatLong.add(re.getString(i)); //creamos un objeto ruta y lo insertamos en la lista
            }
           for (int i = 0; i < lista_LatLong.size(); i++) {

                ll= lista_LatLong.get(i).replace("[","");
            ll= ll.replace("]","");
            ll= ll.replace("\"","");

                String [] lat= ll.split(",");
              //  Toast.makeText(getBaseContext(),">"+lat[0], Toast.LENGTH_SHORT).show();
                String a = lat[0];
                String b = lat[1];

            listLat.add(Double.parseDouble(a));
               listLng.add(Double.parseDouble(b));
           }
          //  Toast.makeText(getBaseContext(),">"+listLat.get(1), Toast.LENGTH_SHORT).show();



        }catch (Exception io){


        }

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


        int len = 10000;

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

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                 Flatitud=marker.getPosition().latitude;
                Flogitud=marker.getPosition().longitude;
                agregarMarcodora();
                new ConsultarPuntosVehiculos().execute("http://10.0.2.2:80/gpsmovil/consultarPuntosRuta.php?id="+PosicionAc+"");


            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

        });

    }
    public void agregarMarcodora() {

        LatLng coodernadas = new LatLng(Flatitud, Flogitud);
       // CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coodernadas, 100);
        if (marcador != null) marcador.remove();

        marcador = mMap.addMarker(new MarkerOptions() // agregar marcador al mapa
                .position(coodernadas)
                .title("MI Ubicacion")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.boy))
                .draggable(true))
        ;
        }


    public void agregarMarcodor(double lat, double lng) {
        Flatitud=lat;
        Flogitud=lng;
        LatLng coodernadas = new LatLng(lat, lng);
       // CameraUpdate miUbicacion = CameraUpdateFactory.newLatLng(coodernadas);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coodernadas,15);
        if (marcador != null) marcador.remove();

        marcador = mMap.addMarker(new MarkerOptions() // agregar marcador al mapa
                .position(coodernadas)
                .title("MI Ubicacion")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.boy)).draggable(true));
        mMap.animateCamera(miUbicacion);

    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            agregarMarcodor(lat, lng);
        }
        new ConsultarPuntosVehiculos().execute("http://10.0.2.2:80/gpsmovil/consultarPuntosRuta.php?id="+PosicionAc+"");

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
