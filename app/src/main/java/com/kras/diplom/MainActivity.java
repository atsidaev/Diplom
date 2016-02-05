package com.kras.diplom;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    private SensorManager sensManager;
    private List<Sensor> sensors;
    public LocationManager locManager;
    private Button buttonAdd;
    private Button buttonTime;
    private ImageButton buttonRec;
    private TextView tv;
    private Intent intentSetting;
    private Intent intentPoint;
    private Intent intentAbout;
    private NumberFormat nf;
    private int Magnet;
    private String filename;
    private int mode = 1;
    private boolean rec = false;
    private boolean gps = false;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int time = 1000;
    AlertDialog.Builder ad;
    Context context;
    GoogleMap map;

    private LocationListener locListaner = new LocationListener() {

        public void onLocationChanged(Location argLocation) {
            buttonAdd.setText("Добавить точку");
            printLocation(argLocation);
            CameraPosition camera = new CameraPosition.Builder()
                    .target(new LatLng(argLocation.getLatitude(), argLocation.getLongitude()))
                    .zoom(5)
                    .bearing(45)
                    .tilt(20)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(camera);
            map.animateCamera(cameraUpdate);

        }

        @Override
        public void onProviderDisabled(String arg0) {

            buttonAdd.setText("Включите GPS");
            buttonAdd.setEnabled(false);
            gps = false;
            // tv.setText("disabled");
        }

        @Override
        public void onProviderEnabled(String arg0) {

            buttonAdd.setText("идет поиск");
            buttonAdd.setEnabled(false);
            gps = true;
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            buttonAdd.setText("status");
        }
    };

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            if (accuracy == sensManager.SENSOR_STATUS_ACCURACY_LOW) {

            }
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Magnet = (int) sensorEvent.values[0];
            tv.setText(String.valueOf(nf.format(sensorEvent.values[0])));
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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
                Location loc = map.getMyLocation();

                CameraPosition camera = new CameraPosition.Builder()
                        .target(new LatLng(20, 20))
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
                .center(new LatLng(56.8691986, 60.65636187)).radius(5)
                .fillColor(Color.YELLOW).strokeColor(Color.DKGRAY)
                .strokeWidth(5);

        map.addCircle(circleOptions);


        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.ic_cast_dark))
                .position(new LatLng(0, 0), 500000f, 500000f);
        map.addGroundOverlay(newarkMap);
    }

    private void addIconStart() {
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


        /** Make sure that the map has been initialised **/
        /** Make sure that the map has been initialised **/
        if (null != map) {
            Location loc = map.getMyLocation();
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                    .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_cast_dark)));
        }
    }

    private void addIconFinish() {
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


        /** Make sure that the map has been initialised **/
        /** Make sure that the map has been initialised **/
        if (null != map) {
            Location loc = map.getMyLocation();
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                    .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_cast_dark)));
        }
    }

    private void addMarker() {

        if (null != map) {
            map.addMarker(new MarkerOptions()
                            .position(new LatLng(0, 0))
                            .title("Marker")
                            .draggable(true)
            );
        }
    }

    private void addBorderForGrid() {

        if (null != map) {
            map.addMarker(new MarkerOptions()
                            .position(new LatLng(0, 0))
                            .title("Marker")
                            .draggable(true)
            );
        }
    }

    public void nameFile() {
        GregorianCalendar calendar = new GregorianCalendar();
        int dd=calendar.get(Calendar.DAY_OF_MONTH);
        int mm=calendar.get(Calendar.MONTH)+1;
        int yy=calendar.get(Calendar.YEAR);
        int HH=calendar.get(Calendar.HOUR);
        int MM=calendar.get(Calendar.MINUTE);
        int SS=calendar.get(Calendar.SECOND);
        filename=yy+mm+dd+"_"+HH+MM+SS+".txt";
    }

    public void StopRecWithGps() {

    }

    public void StopRec() {

    }



    public void runGPS() {

    }

    public void recWithGPS() {
        rec = true;
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                addPoint();
            }
        };
        mTimer.schedule(mTimerTask, time);
    }

    public void handleRec() {
        rec = true;
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                addPoint();
            }
        };
        mTimer.schedule(mTimerTask, time);
    }

    public void addPoint() {
        Location l = map.getMyLocation();
        int c;
        int m=Magnet/10;

        switch (m){
            case -10:c = Color.rgb(0,0,120);break;
            case -9:c = Color.rgb(0,0,160);break;
            case -8:c = Color.rgb(0,0,200);break;
            case -7:c = Color.rgb(0,0, 240);break;
            case -6:c = Color.rgb(0,40, 240);break;
            case -5:c = Color.rgb(0,80,240 );break;
            case -4:c = Color.rgb(0,120,240 );break;
            case -3:c = Color.rgb(0,160,240);break;
            case -2:c = Color.rgb(0,200,240 );break;
            case -1:c = Color.rgb(0,240,240 );break;
            case 0:c = Color.rgb(0,240,200 );break;
            case 1:c = Color.rgb(0,240,160);break;
            case 2:c = Color.rgb(0,240,120);break;
            case 3:c = Color.rgb(0,240,80);break;
            case 4:c = Color.rgb(0,240,40);break;
            case 5:c = Color.rgb(0,240,0 );break;
            case 6:c = Color.rgb(40,240,0);break;
            case 7:c = Color.rgb(80,240,0);break;
            case 8:c = Color.rgb(120,240,0);break;
            case 9:c = Color.rgb(160,240,0);break;
            case 10:c = Color.rgb(200,240,0);break;
            case 11:c = Color.rgb(240,240,0);break;
            case 12:c = Color.rgb(240,200,0);break;
            case 13:c = Color.rgb(240,160,0);break;
            case 14:c = Color.rgb(240,120,0);break;
            case 15:c = Color.rgb(240,80,0);break;
            case 16:c = Color.rgb(240,40,0);break;
            case 17:c = Color.rgb(240,0,0);break;
            case 18:c = Color.rgb(200,0,0);break;
            case 19:c = Color.rgb( 160,0, 0);break;
            case 20:c = Color.rgb( 120,0, 0);break;
            default:c = Color.rgb(0, 0, 0);break;
        }


        String Point = l.getLatitude()+"\t "+ l.getLongitude()+"\t " +Magnet;

        FileOutputStream fOut = null;


        try {
            fOut = openFileOutput(filename, MODE_APPEND);

            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(Point);
            osw.flush();
            osw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(l.getLatitude(), l.getLongitude())).radius(1)
                .fillColor(c).strokeColor(Color.DKGRAY)
                .strokeWidth(1);

        map.addCircle(circleOptions);
    }



    public void Mode1() {
        mode = 1;

        buttonAdd.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
        buttonRec.setVisibility(View.INVISIBLE);
        buttonTime.setVisibility(View.INVISIBLE);
        nameFile();

    }

    public void Mode2() {
        mode = 2;
        buttonAdd.setVisibility(View.INVISIBLE);
        tv.setVisibility(View.VISIBLE);
        buttonRec.setVisibility(View.VISIBLE);
        buttonTime.setVisibility(View.VISIBLE);


    }

    public void ModeGrid() {
        mode = 3;
        buttonAdd.setVisibility(View.INVISIBLE);
        tv.setVisibility(View.INVISIBLE);
        buttonRec.setVisibility(View.INVISIBLE);
        buttonTime.setVisibility(View.INVISIBLE);
    }



    public void CreateDialog() {
        context = MainActivity.this;
        String title = "У вас выключен gps";
        String message = "продолжить с ручной привязкой";
        String button1String = "да";
        String button2String = "включить gps";

        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение

        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(context, "Вы сделали правильный выбор",
                        Toast.LENGTH_LONG).show();
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(context, "Возможно вы правы", Toast.LENGTH_LONG)
                        .show();
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(context, "Вы ничего не выбрали",
                        Toast.LENGTH_LONG).show();
            }
        });
        ad.show();
    }


    public void Rec(View v) {
        if(rec){
        if (!gps) {
            CreateDialog();
        } else {
            recWithGPS();
        }
    }else{
            if(gps){
                StopRecWithGps();

            }else
                StopRec();
        }
    }

    public void AddPoint(View v) {
        addPoint();
    }



    public void showPopupMenu(View v) {

        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.menu);
        switch (mode) {
            case 1:
                popupMenu.getMenu().findItem(R.id.menu1).setVisible(false);
                popupMenu.getMenu().findItem(R.id.menu2).setVisible(true);
                popupMenu.getMenu().findItem(R.id.menu3).setVisible(true);
                break;
            case 2:
                popupMenu.getMenu().findItem(R.id.menu1).setVisible(true);
                popupMenu.getMenu().findItem(R.id.menu2).setVisible(false);
                popupMenu.getMenu().findItem(R.id.menu3).setVisible(true);
                break;
            case 3:
                popupMenu.getMenu().findItem(R.id.menu1).setVisible(true);
                popupMenu.getMenu().findItem(R.id.menu2).setVisible(true);
                popupMenu.getMenu().findItem(R.id.menu3).setVisible(false);
                break;

            default:
                break;
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu1:
                        Mode1();
                        return true;
                    case R.id.menu2:
                        Mode2();
                        return true;
                    case R.id.menu3:
                        ModeGrid();
                        return true;
                    case R.id.menu4:
                        startActivity(intentPoint);
                        return true;
                    case R.id.menu5:
                        startActivity(intentSetting);
                        return true;
                    case R.id.menu6:
                        startActivity(intentAbout);
                        return true;
                    default:
                        return false;
                }
            }
        });


        popupMenu.show();
    }

    public void showMenuTime(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.menutime);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu1:
                        buttonTime.setText("1 сек");
                        time = 1000;
                        return true;
                    case R.id.menu2:
                        buttonTime.setText("5 сек");
                        time = 5000;
                        return true;
                    case R.id.menu3:
                        buttonTime.setText("10 сек");
                        time = 10000;
                        return true;
                    default:
                        return false;
                }
            }
        });


        popupMenu.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        buttonAdd = (Button) findViewById(R.id.badd);
        buttonRec = (ImageButton) findViewById(R.id.ButtonRec);
        buttonTime = (Button) findViewById(R.id.buttontime);


        createMapView();
        Mode2();
        // addMarker();
        //  init();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        intentSetting = new Intent(this, Setting.class);
        intentPoint = new Intent(this, PointActivity.class);
        intentAbout = new Intent(this, AboutActivity.class);

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

        sensManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors = sensManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);

        if (sensors.size() != 0) {
            sensManager.registerListener(listener,
                    sensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        } else {

        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.kras.diplom/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.kras.diplom/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
