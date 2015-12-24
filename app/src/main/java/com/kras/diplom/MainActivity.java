package com.kras.diplom;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.NumberFormat;
import java.util.List;


public class MainActivity extends Activity {

    private SensorManager sensManager;
    private List<Sensor> sensors;
    public LocationManager locManager;
    private Button buttonAdd;
    private TextView tv;
    private Intent intentSetting;
    private Intent intentPoint;
    private Intent intentAbout;
    NumberFormat nf;
    int Magnet;

    GoogleMap map;

    private LocationListener locListaner = new LocationListener() {

        public void onLocationChanged(Location argLocation) {

            printLocation(argLocation);

        }

        @Override
        public void onProviderDisabled(String arg0) {
           buttonAdd.setText("Включите GPS");
            buttonAdd.setEnabled(false);
           // tv.setText("disabled");
        }

        @Override
        public void onProviderEnabled(String arg0){

            buttonAdd.setText("идет поиск");
            buttonAdd.setEnabled(false);
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            tv.setText("status");
        }
    };

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
             Magnet=(int)sensorEvent.values[0];
             tv.setText(String.valueOf(nf.format(sensorEvent.values[0])));
        }
    };

    private void printLocation(Location loc) {
        if (loc != null) {
          //  tv.setText("Longitude:\t" + loc.getLongitude() +"\nLatitude:\t" + loc.getLatitude());
        } else {
           // tv.setText("Location unavailable");
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
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                CameraPosition camera = new CameraPosition.Builder()
                        .target(new LatLng(loc.getLatitude(), loc.getLongitude()))
                        .zoom(5)
                        .bearing(45)
                        .tilt(20)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(camera);
                map.animateCamera(cameraUpdate);
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
                .center(new LatLng(56.8691986,60.65636187)).radius(5)
                .fillColor(Color.YELLOW).strokeColor(Color.DKGRAY)
                .strokeWidth(5);

        map.addCircle(circleOptions);

        CircleOptions circleOptions2 = new CircleOptions()
                .center(new LatLng(0, 20)).radius(500000)
                .fillColor(Color.RED).strokeColor(Color.DKGRAY)
                .strokeWidth(5);

   //     map.addCircle(circleOptions2);


        CircleOptions circleOptions3 = new CircleOptions()
                .center(new LatLng(0, -5)).radius(500000)
                .fillColor(Color.BLUE).strokeColor(Color.DKGRAY)
                .strokeWidth(5);

      //  map.addCircle(circleOptions3);


        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.ic_cast_dark))
                .position(new LatLng(0, 0), 500000f, 500000f);
        map.addGroundOverlay(newarkMap);
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
        tv = (TextView) findViewById(R.id.tv);
        buttonAdd=(Button) findViewById(R.id.badd);
        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListaner);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        createMapView();
       // addMarker();
      //  init();
    }

    @Override
    public void onStart() {
        super.onStart();
        intentSetting = new Intent(this,Setting.class );
        intentPoint = new Intent(this,PointActivity.class );
        intentAbout = new Intent(this,AboutActivity.class );
        sensManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors = sensManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);

        if (sensors.size() != 0) {
            sensManager.registerListener(listener,
                    sensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        } else {



        }
    }

    public void Add(View v){
        Location l= map.getMyLocation();
        int c;
        if(Magnet<0){
            c=Color.rgb(0,0,-10*Magnet);
        }
        else{
            c=Color.rgb(10*Magnet,0,0);
        }
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(l.getLatitude(),l.getLongitude())).radius(5)
                .fillColor(c).strokeColor(Color.DKGRAY)
                .strokeWidth(5);

        map.addCircle(circleOptions);
    }

    public void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.menu);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.menu1:
                                startActivity(intentPoint);
                                return true;
                            case R.id.menu2:
                                startActivity(intentSetting);
                                return true;
                            case R.id.menu3:
                                startActivity(intentAbout);
                                return true;
                            default:
                                return false;
                        }
                    }
                });


        popupMenu.show();
    }


}
