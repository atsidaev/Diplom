package com.kras.diplom;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;


public class MainActivity extends Activity {

    private SensorManager sensManager;
    private List<Sensor> sensors;
    public LocationManager locManager;
    private TextView tv;

    GoogleMap map;

    private LocationListener locListaner = new LocationListener() {

        public void onLocationChanged(Location argLocation) {
            printLocation(argLocation);
        }

        @Override
        public void onProviderDisabled(String arg0) {
            printLocation(null);
        }

        @Override
        public void onProviderEnabled(String arg0) {
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        }
    };

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            tv.setText(String.valueOf(sensorEvent.values[0]));
        }
    };

    private void printLocation(Location loc) {
        if (loc != null) {
            tv.setText("Longitude:\t" + loc.getLongitude() +
                    "\nLatitude:\t" + loc.getLatitude());
        } else {
             tv.setText("Location unavailable");
        }
    }

    private void createMapView() {
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try {
            if (null == map) {
                map = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.mapView)).getMap();
                //map.getUiSettings().isCompassEnabled();
                map.getUiSettings().setCompassEnabled(true);
                map.getUiSettings().setAllGesturesEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);

                /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                if (null == map) {
                    Toast.makeText(getApplicationContext(),
                            "Error creating map", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException exception) {
            Log.e("mapApp", exception.toString());
        }
    }

    private void init() {


        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(0, 0)).radius(500000)
                .fillColor(Color.YELLOW).strokeColor(Color.DKGRAY)
                .strokeWidth(5);

        map.addCircle(circleOptions);

        CircleOptions circleOptions2 = new CircleOptions()
                .center(new LatLng(0, 20)).radius(500000)
                .fillColor(Color.RED).strokeColor(Color.DKGRAY)
                .strokeWidth(5);

        map.addCircle(circleOptions2);


        CircleOptions circleOptions3 = new CircleOptions()
                .center(new LatLng(0, -5)).radius(500000)
                .fillColor(Color.BLUE).strokeColor(Color.DKGRAY)
                .strokeWidth(5);

        map.addCircle(circleOptions3);
    }
    private void addMarker() {

        /** Make sure that the map has been initialised **/
        if (null != map) {
            map.addMarker(new MarkerOptions()
                            .position(new LatLng(0, 0))
                            .title("Marker")
                            .draggable(true)
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=(TextView)findViewById(R.id.tv);
        createMapView();

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListaner);
        Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        printLocation(loc);
       // addMarker();
       // init();
    }

    @Override
    public void onStart() {
        super.onStart();

        sensManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors = sensManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);

        if (sensors.size() != 0) {
            sensManager.registerListener(listener,
                    sensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        } else {

        }
    }



}
