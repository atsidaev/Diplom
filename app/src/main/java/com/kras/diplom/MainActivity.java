package com.kras.diplom;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


public class MainActivity extends Activity {

    private GoogleApiClient client;
    private SensorManager sensManager;
    private List<Sensor> sensors;
    public LocationManager locManager;
    private SoundPool mSound;
    private AssetManager AM;
    private int Sstart, Sstop;
    private int SoundStream;
    private Button buttonAdd;
    private Button buttonTime;
    private ImageButton buttonRec;
    private TextView tv;
    private Intent intentSetting;
    private Intent intentPoint;
    private Intent intentAbout;
    private NumberFormat nf;
    public int Magnet;
    private String filename;
    private String Text="";
    private int mode = 1;
    private int moderec = 1;
    private boolean rec = false;
    private boolean gps = false;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Map<Integer, Integer> ps = new HashMap<Integer, Integer>();
    private Vector<Point> pointOnMap=new Vector<>();
    private int time = 1000;
    private int t = 0;
    private Marker s,f,lv,pn;
    private Polyline pl;
    private AlertDialog.Builder ad;
    private Context context;
    private GoogleMap map;
    private SharedPreferences sp;
    private Boolean b;


    public GoogleMap.OnMarkerDragListener MarkerLis= new GoogleMap.OnMarkerDragListener() {
        @Override
        public void onMarkerDragStart(Marker marker) {

        }

        @Override
        public void onMarkerDrag(Marker marker) {
            if(pl!=null){
                pl.setVisible(false);
            }

        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
                Log.i("marker", marker.getPosition().latitude + "  " + marker.getPosition().longitude);
                marker.setPosition(marker.getPosition());
            if(mode==3){
                addBorderForGrid();
        }}
    };


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
            gps = true;
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
           // buttonAdd.setText("status");
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

    private void clearMap(){
        if (null != map) {
            ps.clear();
            map.clear();
        }
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
            Double l1;
            Double l2;
            Location loc = map.getMyLocation();
            if(loc!=null){
                l1=loc.getLatitude();
                l2=loc.getLongitude();
            }else{
                l1=56.8575;
                l2=60.6125;
            }

            s= map.addMarker( new MarkerOptions()

                    .position(new LatLng(l1, l2))
                    .draggable(true)
                    .anchor(0, 1)
                    .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.start)));
            if(loc!=null){}
        }

        Log.i("start", String.valueOf(s.getPosition().latitude));

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
            Double l1;
            Double l2;
            Location loc = map.getMyLocation();
            if(loc!=null){
                l1=loc.getLatitude();
                l2=loc.getLongitude();
            }else{
                l1=56.8575;
                l2=60.6125;
            }
            f=  map.addMarker(new MarkerOptions()
                    .position(new LatLng(l1, l2))
                    .draggable(true)
                    .anchor((float) 0.1, 1)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.finish)));
        }


    }

    private void addMForGrid() {
        if (null != map) {

            lv = map.addMarker(new MarkerOptions()
                            .position(new LatLng(20, 0))
                            .draggable(true)
                            .anchor(1, 1)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.lv))
            );

            pn = map.addMarker(new MarkerOptions()
                            .position(new LatLng(0, 10))
                            .draggable(true)
                            .anchor(0, 0)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pn))
            );
        }
    }

    private void addBorderForGrid() {

        if (null != map) {

            pl=map.addPolyline(new PolylineOptions()
                    .add(new LatLng(lv.getPosition().latitude, lv.getPosition().longitude)).add(new LatLng(lv.getPosition().latitude, pn.getPosition().longitude))
                    .add(new LatLng(pn.getPosition().latitude, pn.getPosition().longitude)).add(new LatLng(pn.getPosition().latitude, lv.getPosition().longitude))
                    .add(new LatLng(lv.getPosition().latitude, lv.getPosition().longitude))
                    .color(Color.argb(90, 0, 0, 0)).width(2));


        }
        pl.setVisible(true);

    }

    public void nameFile() {
        Text="";
        GregorianCalendar calendar = new GregorianCalendar();
        int dd=calendar.get(Calendar.DAY_OF_MONTH);
        int mm=calendar.get(Calendar.MONTH)+1;
        int yy=calendar.get(Calendar.YEAR);
        int HH=calendar.get(Calendar.HOUR);
        int MM=calendar.get(Calendar.MINUTE);
        int SS=calendar.get(Calendar.SECOND);
        filename=yy+""+mm+""+dd+"_"+HH+""+MM+""+SS+".txt";
        Log.i("filename", filename);
    }


    public int CountPointForInterpolationX(){
        Point p1=new Point(lv.getPosition().latitude,lv.getPosition().longitude);
        Point p2=new Point(pn.getPosition().latitude,pn.getPosition().longitude);
        return (int)((Math.abs(p1.getLongitude()-p2.getLongitude())*100000)/RadiusForInterpolation());

    }

    public int CountPointForInterpolationY(){
        Point p1=new Point(lv.getPosition().latitude,lv.getPosition().longitude);
        Point p2=new Point(pn.getPosition().latitude,pn.getPosition().longitude);
        return (int)((Math.abs(p1.getLatitude()-p2.getLatitude())*100000)/RadiusForInterpolation());
    }

    public int RadiusForInterpolation(){
        Point p1=new Point(lv.getPosition().latitude,lv.getPosition().longitude);
        Point p2=new Point(pn.getPosition().latitude,pn.getPosition().longitude);
        double x=Math.abs((p1.getLongitude() - p2.getLongitude())*100000);
        double y=Math.abs((p1.getLatitude()-p2.getLatitude())*100000);
        Log.i("rx",lv.getPosition().longitude+"");
        Log.i("ry",pn.getPosition().longitude+"");
        if(x>y){
            return (int)(y/20);
        }
        else{
            return (int)(x/20);
        }

    }

    public int RadiusForTimer(){
        Point p1=new Point(s.getPosition().latitude,s.getPosition().longitude);
        Point p2=new Point(f.getPosition().latitude,f.getPosition().longitude);
        int d= (int)(Distanse(p1, p2)*10000);
        return (d/ps.size());
    }

    public void runGPS() {
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
        mTimer.schedule(mTimerTask, 1, time);
    }

    public void StopRecWithGps() {
        mTimer.cancel();
        rec =false;
    }



    public void handleRec() {

        nameFile();
        FileOutputStream fOut = null;
        try {
            FileOutputStream fos = new FileOutputStream(getExternalPath());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        moderec=2;
        buttonRec.setImageURI(Uri.parse("android.resource://com.kras.diplom/" + R.drawable.ok));
        addIconStart();

    }

    public void Conf1(){
        buttonRec.setImageURI(Uri.parse("android.resource://com.kras.diplom/" + R.drawable.stop));
        t = 0;
        moderec=3;
        s.setDraggable(false);
        ps.clear();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                t++;
                ps.put(t, Magnet);
               // Toast.makeText(getApplicationContext(),t+" "+Magnet+" "+ps.size(),Toast.LENGTH_SHORT).show();
                Log.i("run", t+" "+Magnet+" "+ps.size()+" "+ps.get(t));
    }
};
        mTimer.schedule(mTimerTask, 1, time);
    }

    public void Conf2(){
        mTimer.cancel();
        moderec=4;
        buttonRec.setImageURI(Uri.parse("android.resource://com.kras.diplom/" + R.drawable.ok));
        addIconFinish();
    }

    public void StopRec() {
        int dd=RadiusForTimer();
        Log.i("stop"," "+ps.size());
        buttonRec.setImageURI(Uri.parse("android.resource://com.kras.diplom/" + R.drawable.rec));
        moderec=5;

        Point p[] = new Point[ps.size()+1];

        p[1] = new Point(ps.get(1), s.getPosition().latitude, s.getPosition().longitude);
        new Point(ps.get(1), s.getPosition().latitude, s.getPosition().longitude);
        if(ps.size()>1){
            p[ps.size()]=new Point(ps.get(ps.size()),f.getPosition().latitude,f.getPosition().longitude);
        double step1=(p[ps.size()].getLatitude()-p[1].getLatitude())/(ps.size()-1);
        double step2=(p[ps.size()].getLongitude()-p[1].getLongitude())/(ps.size()-1);
            Log.i("stop",step1+" "+step2);
        for(int i=2;i<ps.size();i++) {
            Log.i("stop", i + " " + ps.get(i) + " " + (p[1].getLatitude() + (i-1) * step1) + " " + (p[1].getLongitude() + (i-1) * step2));
            p[i] = new Point(ps.get(i), p[1].getLatitude() + (i-1) * step1, p[1].getLongitude() + (i-1) * step2);
        }

            for(int i=1;i<=ps.size();i++){


            String recPoint = p[i].getLatitude()+" "+ p[i].getLongitude()+" "+p[i].getMagnet();
            saveText(recPoint);
            CircleOnMap(p[i],dd);

        }


    }else {Toast.makeText(getApplicationContext(),"запись велась недостаточно долго",Toast.LENGTH_SHORT).show();}
        writeFile(Text);
    }


    public void CircleOnMap(Point p,int distanse){

        int c;
        int m=p.getMagnet()/10;

        switch (m){
            case -10:c = Color.argb(10,0,0,120);break;
            case -9:c = Color.argb(50, 0, 0, 160);break;
            case -8:c = Color.argb(50, 0, 0, 200);break;
            case -7:c = Color.argb(50, 0, 0, 240);break;
            case -6:c = Color.argb(50, 0, 40, 240);break;
            case -5:c = Color.argb(50, 0, 80, 240);break;
            case -4:c = Color.argb(50, 0, 120, 240);break;
            case -3:c = Color.argb(50, 0, 160, 240);break;
            case -2:c = Color.argb(50, 0, 200, 240);break;
            case -1:c = Color.argb(20, 0, 240, 240);break;
            case 0:c = Color.argb(30, 0, 240, 200);break;
            case 1:c = Color.argb(40, 0, 240, 160);break;
            case 2:c = Color.argb(50, 0, 240, 120);break;
            case 3:c = Color.argb(60, 0, 240, 80);break;
            case 4:c = Color.argb(50, 0, 240, 40);break;
            case 5:c = Color.argb(50, 0, 240, 0);break;
            case 6:c = Color.argb(50, 40, 240, 0);break;
            case 7:c = Color.argb(50, 80, 240, 0);break;
            case 8:c = Color.argb(50, 120, 240, 0);break;
            case 9:c = Color.argb(50, 160, 240, 0);break;
            case 10:c = Color.argb(50, 200, 240, 0);break;
            case 11:c = Color.argb(50, 240, 240, 0);break;
            case 12:c = Color.argb(50, 240, 200, 0);break;
            case 13:c = Color.argb(50, 240, 160, 0);break;
            case 14:c = Color.argb(50, 240, 120, 0);break;
            case 15:c = Color.argb(50, 240, 80, 0);break;
            case 16:c = Color.argb(50, 240, 40, 0);break;
            case 17:c = Color.argb(50, 240, 0, 0);break;
            case 18:c = Color.argb(50, 200, 0, 0);break;
            case 19:c = Color.argb(50, 160, 0, 0);break;
            case 20:c = Color.argb(50, 120, 0, 0);break;
            default:c = Color.argb(50, 0, 0, 0);break;
        }

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(p.getLatitude(), p.getLongitude())).radius(distanse)
                .strokeColor(Color.BLACK)
                .strokeWidth(1)
                .fillColor(c);

        map.addCircle(circleOptions);
        pointOnMap.add(p);
    }

    public void addPoint() {
        Location l = map.getMyLocation();
        int c;
        int m=Magnet/10;

        switch (m){
            case -10:c = Color.argb(10, 0, 0, 120);break;
            case -9:c = Color.argb(50, 0, 0, 160);break;
            case -8:c = Color.argb(50, 0, 0, 200);break;
            case -7:c = Color.argb(50, 0, 0, 240);break;
            case -6:c = Color.argb(50, 0, 40, 240);break;
            case -5:c = Color.argb(50, 0, 80, 240);break;
            case -4:c = Color.argb(50, 0, 120, 240);break;
            case -3:c = Color.argb(50, 0, 160, 240);break;
            case -2:c = Color.argb(50, 0, 200, 240);break;
            case -1:c = Color.argb(50, 0, 240, 240);break;
            case 0:c = Color.argb(50, 0, 240, 200);break;
            case 1:c = Color.argb(50, 0, 240, 160);break;
            case 2:c = Color.argb(50, 0, 240, 120);break;
            case 3:c = Color.argb(50, 0, 240, 80);break;
            case 4:c = Color.argb(50, 0, 240, 40);break;
            case 5:c = Color.argb(50, 0, 240, 0);break;
            case 6:c = Color.argb(50, 40, 240, 0);break;
            case 7:c = Color.argb(50, 80, 240, 0);break;
            case 8:c = Color.argb(50, 120, 240, 0);break;
            case 9:c = Color.argb(50, 160, 240, 0);break;
            case 10:c = Color.argb(50, 200, 240, 0);break;
            case 11:c = Color.argb(50, 240, 240, 0);break;
            case 12:c = Color.argb(50, 240, 200, 0);break;
            case 13:c = Color.argb(50, 240, 160, 0);break;
            case 14:c = Color.argb(50, 240, 120, 0);break;
            case 15:c = Color.argb(50, 240, 80, 0);break;
            case 16:c = Color.argb(50, 240, 40, 0);break;
            case 17:c = Color.argb(50, 240, 0, 0);break;
            case 18:c = Color.argb(50, 200, 0, 0);break;
            case 19:c = Color.argb(50, 160, 0, 0);break;
            case 20:c = Color.argb(50, 120, 0, 0);break;
            default:c = Color.argb(50, 0, 0, 0);break;
        }


        String Point = l.getLatitude()+" "+ l.getLongitude()+" " +Magnet;

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
                .strokeWidth(0);
        Point p=new Point(l.getLatitude(), l.getLongitude());
        pointOnMap.add(p);
        map.addCircle(circleOptions);

    }



    public void Mode1() {
        mode = 1;
        pointOnMap.clear();
        buttonAdd.setText("добавить точку");
        buttonAdd.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
        buttonRec.setVisibility(View.INVISIBLE);
        buttonTime.setVisibility(View.INVISIBLE);
        nameFile();
        lv.setVisible(false);
        pn.setVisible(false);
    }

    public void Mode2() {

        mode = 2;
        buttonAdd.setVisibility(View.INVISIBLE);
        tv.setVisibility(View.VISIBLE);
        buttonRec.setVisibility(View.VISIBLE);
        buttonTime.setVisibility(View.VISIBLE);
        pointOnMap.clear();
        if((lv!=null)&&(pn!=null)){
        lv.setVisible(false);
        pn.setVisible(false);
        }
    }

    public void ModeGrid() {
        mode = 3;

        buttonAdd.setVisibility(View.VISIBLE);
        if(pointOnMap.size()>5){
            buttonAdd.setText("начать интерполяцию");
            buttonAdd.setEnabled(true);
        }
       else{
            buttonAdd.setText("мало точек на карте");
            buttonAdd.setEnabled(false);
        }
        tv.setVisibility(View.INVISIBLE);
        buttonRec.setVisibility(View.INVISIBLE);
        buttonTime.setVisibility(View.INVISIBLE);
        addMForGrid();
        addBorderForGrid();

    }



    public double Distanse(Point p1,Point p2){
        double l1=p1.getLatitude()-p2.getLatitude();
        double l2=p1.getLongitude()-p2.getLongitude();
        return (Math.sqrt(l1*l1+l2*l2))*100000;
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
                handleRec();
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                runGPS();
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

    private Dialog CreateDialogForReadFiles() {

        File dir=getExternalFilesDir(null);
        final String[] files =dir.list();

        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        builder.setTitle("Выбирите файл"); // заголовок для диалога

        builder.setItems(files, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                Log.i("Dialog", files[item]);
                readFile(files[item]);
            }
        });
        builder.setCancelable(true);
        return builder.create();

    }

    private File getExternalPath() {
        File root=getExternalFilesDir(null);

        return(new File(root, filename));
    }

    public void OpenFileDialog(){
        Dialog d=CreateDialogForReadFiles();
        d.show();
    }

    public void readFile(String name) {
        FileInputStream fin = null;
        try {
            File root=getExternalFilesDir(null);
            fin =  new FileInputStream(new File(root, name));
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String (bytes);
            String[] strs=text.split("\n");
            Log.i("read1", strs.length + "");
            for(int i=0;i<strs.length;i++){
                String[] sls=strs[i].split(" ");
                Log.i("read2",strs[0].length()+"");
                int m=Integer.parseInt(sls[2]);
                double l1=Double.parseDouble(sls[0]);
                double l2=Double.parseDouble(sls[1]);
                Point p=new Point(m,l1,l2);
                pointOnMap.add(p);
                CircleOnMap(p, 10000);
            }
        }
        catch(IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{

            try{
                if(fin!=null)
                    fin.close();
            }
            catch(IOException ex){

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void writeFile(String text) {
       //og.i("exist",String.valueOf(getExternalPath().exists()));
        try {
            FileOutputStream fos = new FileOutputStream(getExternalPath());
            //   FileOutputStream fos = openFileOutput(getExternalPath(),MODE_APPEND);

            fos.write(text.getBytes());

            fos.close();
            Log.d("запись в файл", text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveText(String text){
        if(Text!=""){
            Text=Text+"\n"+text;
        }else {
            Text = text ;
        }
    }

    public void StartInterpolation(){
        int countPoint=CountPointForInterpolationX()*CountPointForInterpolationY();
        int countPonMap=pointOnMap.size();
        double rr=RadiusForInterpolation()/100000;
        int r = RadiusForInterpolation();
        Log.i("interpol",countPoint+"");
        Log.i("interpol",CountPointForInterpolationX()+"");
        Log.i("interpol",CountPointForInterpolationY()+"");
        Point pi[]=new Point[countPoint];
        pi[0]=new Point(lv.getPosition().latitude,lv.getPosition().longitude);
        pi[countPoint-1]=new Point(pn.getPosition().latitude,pn.getPosition().longitude);
        int k=1;
        for(int j=0;j<CountPointForInterpolationY()-1;j++){
            for(int i=0;i<CountPointForInterpolationX()-1;i++){
                pi[k]=new Point(pi[0].getLatitude()+j*rr,pi[0].getLongitude()+i*rr);
                double s1=0;
                double s2=0;
                for(int n=0;n<countPonMap;n++){
                    int d= (int) Distanse(pointOnMap.get(n),pi[k]);
                    s1=pointOnMap.get(n).getMagnet()/d*d;
                    s2=1/d*d;
                }

                int m=(int)(s1/s2);
                pi[k].setMagnet(m);
                Log.i("interpol", k + "");
                CircleOnMap(pi[k], r);
                k++;

            }
        }
    }

    public void Rec(View v) {


            if (gps) {
                if(!rec){
                    recWithGPS();
                }
                else {
                    StopRecWithGps();
                }
           } else {
                switch (moderec) {
                    case 1: CreateDialog();break;
                  //  case 1: handleRec();break;
                    case 2: Conf1();break;
                    case 3: Conf2();break;
                    case 4: StopRec();break;
                    case 5: moderec=1;break;
                    default:break;
                }


        }
    }

    public void AddPoint(View v) {
        switch (mode){
            case 1: addPoint();break;
            case 3:   StartInterpolation();break;
            default:break;
        }
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
                       OpenFileDialog();
                        return true;
                    case R.id.menu5:
                        clearMap();
                        return true;
                    case R.id.menu6:
                        startActivity(intentSetting);
                        return true;
                    case R.id.menu7:
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSound = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    private void createOldSoundPool() {
        mSound = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
    }

    private int playSound(int sound) {
        if (sound > 0) {
            SoundStream = mSound.play(sound, 1, 1, 1, 0, 1);
        }
        return SoundStream;
    }

    private int loadSound(String fileName) {
        AssetFileDescriptor afd;
        try {
            afd = AM.openFd(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Не могу загрузить файл " + fileName,
                    Toast.LENGTH_SHORT).show();
            return -1;
        }
        return mSound.load(afd, 1);
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
        map.setOnMarkerDragListener(MarkerLis);
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
    public void onResume(){
        super.onResume();
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        b=sp.getBoolean("magn",false);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // Для устройств до Android 5
            createOldSoundPool();
        } else {
            // Для новых устройств
            createNewSoundPool();
        }

        AM = getAssets();

        // получим идентификаторы
        Sstart = loadSound("cat.ogg");
        Sstop = loadSound("chicken.ogg");
    }

    @Override
    public void onPause(){
        super.onPause();
        sensManager.unregisterListener(listener);
        mSound.release();
        mSound = null;
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

class Point{
    int magnet;
    double longitude;
    double latitude;

    public Point( int m, double lat , double lon ){
        magnet=m;
        longitude=lon;
        latitude=lat;

    }

    public Point( double lat , double lon){
        longitude=lon;
        latitude=lat;

    }

    public Point( int m){
        magnet=m;
    }

    public void setMagnet(int m){
        this.magnet=m;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public int getMagnet(){
        return this.magnet;
    }
}