package com.kras.diplom;


import android.Manifest;
import android.annotation.TargetApi;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.os.Handler;

import java.util.logging.LogRecord;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private GoogleApiClient client;
    private SensorManager sensManager;
    // private List<Sensor> sensors;
    public LocationManager locManager;
    public Sensor stepSensor;
    public Sensor orintationSensor;
    public Sensor magnSensor;
    private SoundPool mSound;
    private AssetManager AM;
    private int Sstart, Sstop;
    private int SoundStream;
    private FloatingActionButton fab;
    private Intent intentSetting;
    private Intent intentPoint;
    private Intent intentAbout;
    private NumberFormat nf;
    public int Magnet, k;
    private String filename;
    private String Text = "";
    private int mode = 1, rfi;
    private int moderec = 1;
    private boolean rec = false, gps = false, step = false;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Map<Integer, Integer> ps = new HashMap<Integer, Integer>();
    private Vector<Point> pointOnMap = new Vector<>();
    private Vector<Point> pfi;

    private Location loc;
    private int uA;
    private Handler h;
    private double Lstep = 1, colstep;
    private int t = 0;
    public Marker s, f, lv, pn;
    private Polyline pl;
    private AlertDialog.Builder ad;
    private Context context;
    private GoogleMap map;
    private SharedPreferences sp;
    private Boolean b;
    private Point PA, PP;
    private int time = 1000;
    int countPoint, cx, cy;
    private Vector<Point> pointfi = new Vector<>();
    Double ll1, ll2;
    ProgressBar pbCount;
    private TextView tv;


    public GoogleMap.OnMarkerDragListener MarkerLis = new GoogleMap.OnMarkerDragListener() {
        @Override
        public void onMarkerDragStart(Marker marker) {

        }

        @Override
        public void onMarkerDrag(Marker marker) {
            if (pl != null) {
                pl.setVisible(false);
            }

        }

        @Override
        public void onMarkerDragEnd(Marker marker) {

            marker.setPosition(marker.getPosition());
            if (mode == 3) {
                addBorderForGrid();
            }
        }
    };

    private LocationListener locListaner = new LocationListener() {

        public void onLocationChanged(Location argLocation) {
            // buttonAdd.setText("Добавить точку");
            // fab.setImageResource(R.drawable.ic_plusone_tall_off_client);
            printLocation(argLocation);

          /*  CameraPosition camera = new CameraPosition.Builder()
                    .target(new LatLng(argLocation.getLatitude(), argLocation.getLongitude()))
                    .zoom(5)
                    .bearing(45)
                    .tilt(20)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(camera);
          //  map.animateCamera(cameraUpdate);*/

        }

        @Override
        public void onProviderDisabled(String arg0) {

            //buttonAdd.setText("Включите GPS");
            // buttonAdd.setEnabled(false);
            gps = false;
            // tv.setText("disabled");
        }

        @Override
        public void onProviderEnabled(String arg0) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            loc = locManager.getLastKnownLocation(arg0);
            // buttonAdd.setText("идет поиск");
            //buttonAdd.setEnabled(false);
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
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            switch (event.sensor.getType()) {
                case Sensor.TYPE_STEP_COUNTER: {
                    colstep = Double.parseDouble(String.valueOf(event.values[0]));
                }
                break;
                case Sensor.TYPE_ORIENTATION: {

                    uA = Integer.parseInt(nf.format(event.values[0]));
                }
                break;

                case Sensor.TYPE_MAGNETIC_FIELD: {
                    Magnet = (int) event.values[2];
                    if(b)  tv.setText(String.valueOf(nf.format(event.values[0]))+" "+String.valueOf(nf.format(event.values[1]))+" "+String.valueOf(nf.format(event.values[2])));
                    else
                    tv.setText(String.valueOf(nf.format(event.values[2])));
                }
                break;
            }
        }
    };


    private void printLocation(Location loc) {
        if (loc != null) {
            //  tv.setText("Longitude:\t" + loc.getLongitude() +"\nLatitude:\t" + loc.getLatitude());
        } else {
            // tv.setText("Location unavailable");
        }
    }

    public void runGPS() {
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    private void createMapView() {

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



                if(map.getMyLocation()!=null)
               loc = map.getMyLocation();

                CameraPosition camera = new CameraPosition.Builder()
                        .target(new LatLng(56.84, 60.65))
                        .zoom(10)
                        .bearing(45)
                        .tilt(20)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(camera);
                map.animateCamera(cameraUpdate);

                if (null == map) {
                    Toast.makeText(getApplicationContext(),
                            "Error creating map", Toast.LENGTH_SHORT).show();
                }

            }
        } catch (NullPointerException exception) {
            Log.e("mapApp", exception.toString());
        }
    }

    private void clearMap() {
        if (null != map) {
            pointOnMap.clear();
            map.clear();
        }
    }

    private void MarkerOnMap(Point p) {
        int c;
        int m = p.getMagnet() / 10;

        switch (m) {
            case -10:
                c = R.drawable.color1;
                break;
            case -9:
                c = R.drawable.color1;
                break;
            case -8:
                c = R.drawable.color2;
                break;
            case -7:
                c = R.drawable.color2;
                break;
            case -6:
                c = R.drawable.color3;
                break;
            case -5:
                c = R.drawable.color3;
                break;
            case -4:
                c = R.drawable.color4;

                break;
            case -3:
                c = R.drawable.color4;
                break;
            case -2:
                c = R.drawable.color5;
                break;
            case -1:
                c = R.drawable.color5;
                break;
            case 0:
                c = R.drawable.color6;
                break;
            case 1:
                c = R.drawable.color7;
                break;
            case 2:
                c = R.drawable.color8;
                break;
            case 3:
                c = R.drawable.color9;
                break;
            case 4:
                c = R.drawable.color10;
                break;
            case 5:
                c = R.drawable.color10;
                break;
            case 6:
                c = R.drawable.color11;
                break;
            case 7:
                c = R.drawable.color12;
                break;
            case 8:
                c = R.drawable.color13;
                break;
            case 9:
                c = R.drawable.color13;
                break;
            case 10:
                c = R.drawable.color14;
                break;
            case 11:
                c = R.drawable.color14;
                break;
            case 12:
                c = R.drawable.color15;
                break;
            case 13:
                c = R.drawable.color15;
                break;
            case 14:
                c = R.drawable.color15;
                break;
            case 15:
                c = R.drawable.color16;
                break;
            case 16:
                c = R.drawable.color16;
                break;
            case 17:
                c = R.drawable.color16;
                break;
            case 18:
                c = R.drawable.red1;
                break;
            case 19:
                c = R.drawable.red1;
                break;
            case 20:
                c = R.drawable.red1;
                break;
            default:
                c = R.drawable.color0;
                break;
        }

        pointOnMap.add(p);

        if (null != map) {


            map.addMarker(new MarkerOptions()

                    .position(new LatLng(p.getLatitude(), p.getLongitude()))
                    .draggable(false)
                    .anchor((float) 0.5, (float) 0.5)
                    .alpha((float) 0.5)
                    .title(p.getMagnet() + "")
                    .icon(
                            BitmapDescriptorFactory.fromResource(c)));

        }
    }

    private void addIconStart() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (null != map) {
            Double l1;
            Double l2;
            Location loc = map.getMyLocation();
            if (loc != null) {
                l1 = loc.getLatitude();
                l2 = loc.getLongitude();
            } else {
                l1 = 56.8575;
                l2 = 60.6125;
            }

            s = map.addMarker(new MarkerOptions()

                    .position(new LatLng(l1, l2))
                    .draggable(true)
                    .anchor((float) 0, 1)
                    .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.start)));

        }



    }

    private void addIconFinish() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }


        if (null != map) {
            Double l1;
            Double l2;
            Location loc = map.getMyLocation();
            if (loc != null) {
                l1 = loc.getLatitude();
                l2 = loc.getLongitude();
            } else {
                l1 = 56.8575;
                l2 = 60.6125;
            }
            f = map.addMarker(new MarkerOptions()
                    .position(new LatLng(l1, l2))
                    .draggable(true)
                    .anchor((float) 0.2, 1)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.finish)));
        }


    }

    private void addMForGrid() {
        if (null != map) {
            double lv1=pointOnMap.get(0).getLatitude();
            double lv2=pointOnMap.get(0).getLongitude();

            double pn1=pointOnMap.get(pointOnMap.size()-1).getLatitude();
            double pn2=pointOnMap.get(pointOnMap.size()-1).getLongitude();


            for(int i=0;i<pointOnMap.size(); i++){

                if(pointOnMap.get(i).getLatitude()>lv1) {
                    lv1 = pointOnMap.get(i).getLatitude();
                }
                if(pointOnMap.get(i).getLongitude()<lv2) {
                    lv2 = pointOnMap.get(i).getLongitude();
                }
                if(pointOnMap.get(i).getLatitude()<pn1) {
                    pn1 = pointOnMap.get(i).getLatitude();
                }
                if(pointOnMap.get(i).getLongitude()>pn2) {
                    pn2 = pointOnMap.get(i).getLongitude();
                }


            }
            lv = map.addMarker(new MarkerOptions()
                            .position(new LatLng(lv1, lv2))
                            .draggable(true)
                            .anchor(1, 1)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.lv))
            );

            pn = map.addMarker(new MarkerOptions()
                            .position(new LatLng(pn1, pn2))
                            .draggable(true)
                            .anchor(0, 0)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pn))
            );
        }
    }

    private void addBorderForGrid() {
        if((lv!=null)&&(pn!=null)&&(pl!=null)){

            pl.setVisible(false);

        }
        if (null != map) {

            pl = map.addPolyline(new PolylineOptions()
                    .add(new LatLng(lv.getPosition().latitude, lv.getPosition().longitude)).add(new LatLng(lv.getPosition().latitude, pn.getPosition().longitude))
                    .add(new LatLng(pn.getPosition().latitude, pn.getPosition().longitude)).add(new LatLng(pn.getPosition().latitude, lv.getPosition().longitude))
                    .add(new LatLng(lv.getPosition().latitude, lv.getPosition().longitude))
                    .color(Color.argb(90, 0, 0, 0)).width(2));


        }
        pl.setVisible(true);

    }


    public void StartInterpolation1() {

        pfi = new Vector<>();
        //  int c = CountPointForInterpolationX() * CountPointForInterpolationY();
        rfi = RadiusForInterpolation();

        for (int i = 0; i < pointOnMap.size(); i++) {
            if ((pointOnMap.get(i).getLatitude() < lv.getPosition().latitude) && (pointOnMap.get(i).getLatitude() > pn.getPosition().latitude) && (pointOnMap.get(i).getLongitude() > lv.getPosition().longitude) && (pointOnMap.get(i).getLongitude() < pn.getPosition().longitude)) {
                pfi.add(pointOnMap.get(i));
            }
        }
        if (pfi.size() > 1) {
            int k1, k2;
            if (pn.getPosition().longitude - lv.getPosition().longitude > 0) {
                k2 = 1;
            } else {
                k2 = -1;
            }
            if (lv.getPosition().latitude - pn.getPosition().latitude > 0) {
                k1 = 1;
            } else {
                k1 = -1;
            }
            if ((k1 == -1) || (k2 == -1)) {
                Snackbar.make(this.getCurrentFocus(), "расположите указатели правильно", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else {
                playSound(Sstart);

                Snackbar.make(this.getCurrentFocus(), "идет интерполяция", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                countPoint = CountPointForInterpolationX() * CountPointForInterpolationY();
                ll1 = lv.getPosition().latitude;
                ll2 = lv.getPosition().longitude;
                cy = CountPointForInterpolationY();
                cx = CountPointForInterpolationX();

                new Thread(new Runnable() {
                    public void run() {
                        double rr = ((double) (rfi) / 100000);
                        int countPonO = pfi.size();
                        Point pi[] = new Point[countPoint];
                        Point p1 = new Point(ll1, ll2);

                        k = 0;
                        for (int j = 0; j < cy; j++) {
                            for (int i = 0; i < cx; i++) {


                                pi[k] = new Point((p1.getLatitude() - j * rr) - rr / 2, (p1.getLongitude() + i * 2 * rr) + rr);
                                double s1 = 0;
                                double s2 = 0;
                                for (int n = 0; n < countPonO; n++) {
                                    int d = Distanse(pfi.get(n), pi[k]);
                                    s1 = pfi.get(n).getMagnet() / d * d;
                                    s2 = 1 / d * d;
                                }

                                int m = (int) (s1 / s2);
                                pi[k].setMagnet(m);
                                pointfi.add(pi[k]);
                                k++;

                            }
                        }
                        h.sendEmptyMessage(1);
                        FileInterpol(cx,cy,rr, pointfi);
                    }
                }).start();
                //mt = new Interpolation();
                // mt.execute();


            }
        } else {
            Snackbar.make(this.getCurrentFocus(), "Мало точек в области", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }


    public void FileInterpol(int xx, int yy, double rr, Vector<Point> vp){
        Text = "";
        GregorianCalendar calendar = new GregorianCalendar();
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        int mm = calendar.get(Calendar.MONTH) + 1;
        int yyy = calendar.get(Calendar.YEAR);
        int HH = calendar.get(Calendar.HOUR);
        int MM = calendar.get(Calendar.MINUTE);
        int SS = calendar.get(Calendar.SECOND);
        filename = "grd"+yyy + "" + mm + "" + dd + "_" + HH + "" + MM + "" + SS + ".grd";

        try {
            FileOutputStream file=new FileOutputStream(getExternalPath());

            ByteBuffer buf= ByteBuffer.allocate(100+xx*yy*12);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.putLong(0x42525344);
            buf.putLong(4);
            buf.putLong(1);
            buf.putLong(0x44495247);
            buf.putLong(72);
            buf.putLong(yy);
            buf.putLong(xx);
            buf.putDouble(0.0);
            buf.putDouble(0.0);
            buf.putDouble(rr);
            buf.putDouble(rr);
            buf.putDouble(-300.0);
            buf.putDouble(300.0);
            buf.putDouble(0.0);
            buf.putDouble(300.0);
            buf.putLong(0x41544144);
            buf.putLong(xx*yy*8);
            for(int i=0;i<xx;i++){
                for(int j=0;j<yy;j++){
                    buf.putDouble(vp.get((yy-j)*i).getMagnet());
                }
            }

            file.write(buf.array());
            file.flush();
            file.close();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void StartInterpolation2() {
        Vector<Point> pfi = new Vector<>();
        for (int i = 0; i < pointOnMap.size(); i++) {
            if ((pointOnMap.get(i).getLatitude() < lv.getPosition().latitude) && (pointOnMap.get(i).getLatitude() > pn.getPosition().latitude) && (pointOnMap.get(i).getLongitude() > lv.getPosition().longitude) && (pointOnMap.get(i).getLongitude() < pn.getPosition().longitude)) {
                pfi.add(pointOnMap.get(i));
            }
        }
        int countPoint = CountPointForInterpolationX() * CountPointForInterpolationY();
        int countPonO = pfi.size();
        int r = RadiusForInterpolation();
        double rr = ((double) (r) / 100000);

        Point pi[] = new Point[countPoint];
        Point p1 = new Point(lv.getPosition().latitude, lv.getPosition().longitude);
        int k = 0;
        for (int j = 0; j < CountPointForInterpolationY(); j++) {
            for (int i = 0; i < CountPointForInterpolationX(); i++) {
                pi[k] = new Point((p1.getLatitude() - j * rr) - rr / 2, (p1.getLongitude() + i * rr) + rr / 2);
                int[] m = new int[countPonO];
                for (int n = 0; n < countPonO; n++) {
                    m[n] = pfi.get(n).getMagnet();
                }
                for (int n = 0; n < countPonO; n++) {
                    m[n] = pfi.get(n).getMagnet();
                }


                //     int mm=(int)(s1/s2);
                //   pi[k].setMagnet(m);

                CircleOnMap(pi[k], r / 2);
                k++;

            }
        }
    }


    public void recWithGPS() {
        rec = true;
        mTimer = new Timer();
        if (map.getMyLocation() != null) {
            mTimerTask = new TimerTask() {

                @Override
                public void run() {


                    Location l = map.getMyLocation();
                    Point p = new Point(Magnet, l.getLatitude(), l.getLongitude());
                    addPoint(p);
                }


            };
            mTimer.schedule(mTimerTask, 1, time);
        } else {
            Snackbar.make(this.getCurrentFocus(), "местоположение не найдено", Snackbar.LENGTH_SHORT);
        }
    }

    public void StopRecWithGps() {
        mTimer.cancel();
        rec = false;
    }


    public void recWithStep() {
        nameFile();
        Text = "";
        FileOutputStream fOut = null;
        try {
            FileOutputStream fos = new FileOutputStream(getExternalPath());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clearMap();
        moderec = 6;
        fab.setImageURI(Uri.parse("android.resource://com.kras.diplom/" + R.drawable.ok));
        addIconStart();
    }

    public void StartRecStep() {
        s.setDraggable(false);
        playSound(Sstart);
        PP = new Point(Magnet, uA, colstep);
        mTimer = new Timer();

        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                PA = new Point(Magnet, uA, colstep);
                double s = (PA.getStep() - PP.getStep()) * Lstep / 100000;
                final Point pp = new Point(PA.getMagnet(), (PP.getLatitude() + s * Math.cos(PP.getOrientation())), (PP.getLongitude() + s * Math.sin(PP.getOrientation())));
              /*  runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        MarkerOnMap(pp);
                    }
                });*/

                pointOnMap.add(PA);
                PP = PA;
            }
        };
        mTimer.schedule(mTimerTask, time, time);
        fab.setImageURI(Uri.parse("android.resource://com.kras.diplom/" + R.drawable.stop));
        moderec = 7;

    }

    public void FinishRecStep() {
        playSound(Sstop);
        mTimer.cancel();
        moderec = 1;
        fab.setImageURI(Uri.parse("android.resource://com.kras.diplom/" + R.drawable.rec));
        int n = pointOnMap.size();
        Point pp[] = new Point[n];
        pp[0] = new Point(pointOnMap.get(0).getMagnet(), s.getPosition().latitude, s.getPosition().longitude);

        for (int i = 1; i < n; i++) {

            double s = (pointOnMap.get(i).getStep() - pointOnMap.get(i - 1).getStep()) * Lstep / 100000;
            pp[i] = new Point(pointOnMap.get(i).getMagnet(), (pp[i - 1].getLatitude() + s * Math.cos(pointOnMap.get(i - 1).getOrientation())), (pp[i - 1].getLongitude() + s * Math.sin(pointOnMap.get(i - 1).getOrientation())));
            MarkerOnMap(pp[i]);
        }


        for (int j = 0; j < n; j++) {
            addPoint(pp[j]);
        }
        writeFile(Text);
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
        moderec = 2;
        fab.setImageURI(Uri.parse("android.resource://com.kras.diplom/" + R.drawable.ok));
        addIconStart();

    }

    public void Conf1() {
        playSound(Sstart);
        fab.setImageURI(Uri.parse("android.resource://com.kras.diplom/" + R.drawable.stop));
        t = 0;
        moderec = 3;
        s.setDraggable(false);
        ps.clear();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                t++;
                ps.put(t, Magnet);

            }
        };
        mTimer.schedule(mTimerTask, 1, time);
    }

    public void Conf2() {
        mTimer.cancel();
        moderec = 4;
        fab.setImageURI(Uri.parse("android.resource://com.kras.diplom/" + R.drawable.ok));
        addIconFinish();
    }

    public void StopRec() {
        playSound(Sstop);
        // int dd=RadiusForTimer();

        fab.setImageURI(Uri.parse("android.resource://com.kras.diplom/" + R.drawable.rec));
        moderec = 5;

        Point p[] = new Point[ps.size() + 1];

        p[1] = new Point(ps.get(1), s.getPosition().latitude, s.getPosition().longitude);
        new Point(ps.get(1), s.getPosition().latitude, s.getPosition().longitude);
        if (ps.size() > 1) {
            p[ps.size()] = new Point(ps.get(ps.size()), f.getPosition().latitude, f.getPosition().longitude);
            double step1 = (p[ps.size()].getLatitude() - p[1].getLatitude()) / (ps.size() - 1);
            double step2 = (p[ps.size()].getLongitude() - p[1].getLongitude()) / (ps.size() - 1);

            for (int i = 2; i < ps.size(); i++) {

                p[i] = new Point(ps.get(i), p[1].getLatitude() + (i - 1) * step1, p[1].getLongitude() + (i - 1) * step2);
            }

            for (int i = 1; i <= ps.size(); i++) {
                String recPoint = p[i].getLatitude() + " " + p[i].getLongitude() + " " + p[i].getMagnet();
                saveText(recPoint);
                //CircleOnMap(p[i],dd);
                MarkerOnMap(p[i]);
            }

        } else {
            Toast.makeText(getApplicationContext(), "запись велась недостаточно долго", Toast.LENGTH_SHORT).show();
        }
        writeFile(Text);
    }

    public void CircleOnMap(Point p, int distanse) {

        int c;
        int m = p.getMagnet() / 10;

        switch (m) {
            case -10:
                c = Color.argb(10, 0, 0, 120);
                break;
            case -9:
                c = Color.argb(50, 0, 0, 160);
                break;
            case -8:
                c = Color.argb(50, 0, 0, 200);
                break;
            case -7:
                c = Color.argb(50, 0, 0, 240);
                break;
            case -6:
                c = Color.argb(50, 0, 40, 240);
                break;
            case -5:
                c = Color.argb(50, 0, 80, 240);
                break;
            case -4:
                c = Color.argb(50, 0, 120, 240);
                break;
            case -3:
                c = Color.argb(50, 0, 160, 240);
                break;
            case -2:
                c = Color.argb(50, 0, 200, 240);
                break;
            case -1:
                c = Color.argb(20, 0, 240, 240);
                break;
            case 0:
                c = Color.argb(30, 0, 240, 200);
                break;
            case 1:
                c = Color.argb(40, 0, 240, 160);
                break;
            case 2:
                c = Color.argb(50, 0, 240, 120);
                break;
            case 3:
                c = Color.argb(60, 0, 240, 80);
                break;
            case 4:
                c = Color.argb(50, 0, 240, 40);
                break;
            case 5:
                c = Color.argb(50, 0, 240, 0);
                break;
            case 6:
                c = Color.argb(50, 40, 240, 0);
                break;
            case 7:
                c = Color.argb(50, 80, 240, 0);
                break;
            case 8:
                c = Color.argb(50, 120, 240, 0);
                break;
            case 9:
                c = Color.argb(50, 160, 240, 0);
                break;
            case 10:
                c = Color.argb(50, 200, 240, 0);
                break;
            case 11:
                c = Color.argb(50, 240, 240, 0);
                break;
            case 12:
                c = Color.argb(50, 240, 200, 0);
                break;
            case 13:
                c = Color.argb(50, 240, 160, 0);
                break;
            case 14:
                c = Color.argb(50, 240, 120, 0);
                break;
            case 15:
                c = Color.argb(50, 240, 80, 0);
                break;
            case 16:
                c = Color.argb(50, 240, 40, 0);
                break;
            case 17:
                c = Color.argb(50, 240, 0, 0);
                break;
            case 18:
                c = Color.argb(50, 200, 0, 0);
                break;
            case 19:
                c = Color.argb(50, 160, 0, 0);
                break;
            case 20:
                c = Color.argb(50, 120, 0, 0);
                break;
            default:
                c = Color.argb(50, 0, 0, 0);
                break;
        }

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(p.getLatitude(), p.getLongitude())).radius(distanse)
                .strokeColor(Color.BLACK)
                .strokeWidth(1)
                .fillColor(c);

        map.addCircle(circleOptions);
        pointOnMap.add(p);
    }

    public void addPoint(Point pp) {


        String Point = pp.getLatitude() + " " + pp.getLongitude() + " " + pp.getMagnet();
        saveText(Point);
        //CircleOnMap(pp, 2);
        MarkerOnMap(pp);

    }

    public int Distanse(Point p1, Point p2) {
        double l1 = p1.getLatitude() - p2.getLatitude();
        double l2 = p1.getLongitude() - p2.getLongitude();
        int d=(int) ((Math.sqrt(l1 * l1 + l2 * l2)) * 100000);
        if(d>0) return d;
        else return 1;
    }

    public int CountPointForInterpolationX() {
        Point p1 = new Point(lv.getPosition().latitude, lv.getPosition().longitude);
        Point p2 = new Point(pn.getPosition().latitude, pn.getPosition().longitude);
        return (int) ((Math.abs(p1.getLongitude() - p2.getLongitude()) * 100000) / (2 * RadiusForInterpolation()));

    }

    public int CountPointForInterpolationY() {
        Point p1 = new Point(lv.getPosition().latitude, lv.getPosition().longitude);
        Point p2 = new Point(pn.getPosition().latitude, pn.getPosition().longitude);

        return (int) ((Math.abs(p1.getLatitude() - p2.getLatitude()) * 100000) / RadiusForInterpolation());
    }

    public int RadiusForInterpolation() {
        Point p1 = new Point(lv.getPosition().latitude, lv.getPosition().longitude);
        Point p2 = new Point(pn.getPosition().latitude, pn.getPosition().longitude);
        double x = Math.abs((p1.getLongitude() - p2.getLongitude()) * 100000);
        double y = Math.abs((p1.getLatitude() - p2.getLatitude()) * 100000);

        if (x > y) {
            return (int) (y / 20);
        } else {
            return (int) (x / 20);
        }

    }


    public void Mode1() {
        clearMap();
        fab.setEnabled(true);
        mode = 1;
        pointOnMap.clear();
        // buttonAdd.setText("добавить точку");
        nameFile();
        if ((lv != null) || (pn != null)) {
            lv.setVisible(false);
            pn.setVisible(false);
        }
    }

    public void Mode2() {
        clearMap();
        mode = 2;
        fab.setEnabled(true);
        pointOnMap.clear();
        if (pl != null) pl.setVisible(false);
        if ((lv != null) && (pn != null)) {
            lv.setVisible(false);
            pn.setVisible(false);
        }
    }

    public void ModeGrid() {
        if (mode==3){ Snackbar.make(this.getCurrentFocus(), "Режим интерполяции уже включен", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();}
        else{

        if (pointOnMap.size() > 2) {
            mode = 3;
            // buttonAdd.setText("начать интерполяцию");
            fab.setEnabled(true);
            addMForGrid();
            addBorderForGrid();
        } else {

            Snackbar.make(this.getCurrentFocus(), "мало точек на карте", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }}


    }

    public void CreateDialog() {
        context = MainActivity.this;
        String title = "У вас выключен gps. Как продолжить";
        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);  // заголовок
        ad.setPositiveButton("по линии", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                handleRec();
            }
        });
        ad.setNeutralButton("акселерометр", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                recWithStep();
            }
        });
        ad.setNegativeButton("gps", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                runGPS();
            }
        });
        ad.setCancelable(true);
        ad.show();
    }

    private Dialog CreateDialogForReadFiles() {

        File dir = getExternalFilesDir(null);
        final String[] files ;
        String[] ff = dir.list();
        String[] st=new String[dir.list().length];
        int k=0;
        for(String f : ff){
            boolean found = f.startsWith("2");
            Log.i("list", f);
            if(found){
                Log.i("listt", f);
                st[k]=f;
                k++;
            }
        }

        files=new String[k];
        for(int i=0;i<k;i++){
            files[i]=st[i];
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        File root = getExternalFilesDir(null);
        return (new File(root, filename));
    }

    public void OpenFileDialog() {
        Dialog d = CreateDialogForReadFiles();
        d.show();
    }

    public void readFile(String name) {
        FileInputStream fin = null;
        try {
            File root = getExternalFilesDir(null);
            fin = new FileInputStream(new File(root, name));
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String(bytes);
            String[] strs = text.split("\n");
            Log.i("read1", strs.length + "");
            for (int i = 0; i < strs.length; i++) {
                String[] sls = strs[i].split(" ");
                Log.i("read2", strs[0].length() + "");
                int m = Integer.parseInt(sls[2]);
                double l1 = Double.parseDouble(sls[0]);
                double l2 = Double.parseDouble(sls[1]);
                Point p = new Point(m, l1, l2);
                pointOnMap.add(p);
                MarkerOnMap(p);
                //CircleOnMap(p, 10000);
            }
        } catch (IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {

            try {
                if (fin != null)
                    fin.close();
            } catch (IOException ex) {

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void writeFile(String text) {
        try {
            FileOutputStream fos = new FileOutputStream(getExternalPath());
            fos.write(text.getBytes());
            fos.close();
            Log.d("запись в файл", text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveText(String text) {
        if (Text != "") {
            Text = Text + "\n" + text;
        } else {
            Text = text;
        }
    }

    public void nameFile() {
        Text = "";
        GregorianCalendar calendar = new GregorianCalendar();
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        int mm = calendar.get(Calendar.MONTH) + 1;
        int yy = calendar.get(Calendar.YEAR);
        int HH = calendar.get(Calendar.HOUR);
        int MM = calendar.get(Calendar.MINUTE);
        int SS = calendar.get(Calendar.SECOND);
        filename = yy + "" + mm + "" + dd + "_" + HH + "" + MM + "" + SS + ".txt";

    }


    public void Rec() {

        switch (mode) {
            case 2:
                if (gps) {
                    if (!rec) {
                        recWithGPS();
                    } else {
                        StopRecWithGps();
                    }
                } else {
                    switch (moderec) {
                        case 1:
                            CreateDialog();
                            break;
                        case 2:
                            Conf1();
                            break;
                        case 3:
                            Conf2();
                            break;
                        case 4:
                            StopRec();
                            break;
                        case 5:
                            moderec = 1;
                            break;
                        case 6:
                            StartRecStep();
                            break;
                        case 7:
                            FinishRecStep();
                            break;
                        default:
                            break;
                    }


                }
                break;
            case 1:
                if (map.getMyLocation() != null) {
                    Location l = map.getMyLocation();

                    Point p = new Point(Magnet, l.getLatitude(), l.getLongitude());
                    addPoint(p);
                    break;
                } else {
                    Snackbar.make(this.getCurrentFocus(), "идет поиск местоположения", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    break;
                }

            case 3:
                StartInterpolation1();
                break;
            default:
                break;
        }
    }


    public void showPopupMenu(View v) {

        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.menu);
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
                    case R.id.menu8:
                        startActivity(intentPoint);
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

        h = new Handler() {

            public void handleMessage(android.os.Message msg) {
                if (msg.what == 1) {
                    for (int i = 0; i < pointfi.size(); i++) {
                        CircleOnMap(pointfi.get(i), rfi / 2);

                    }
                    pointfi.clear();
                }

            }

            ;
        };
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        tv = (TextView) findViewById(R.id.m);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rec();
                //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //  .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Mode2();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        intentSetting = new Intent(this, Setting.class);
        intentPoint = new Intent(this, PointActivity.class);
        intentAbout = new Intent(this, AboutActivity.class);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListaner);
        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListaner);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        sensManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensManager.getSensorList(Sensor.TYPE_ALL);
        if (sensors.size() > 0) {
            for (Sensor sensor : sensors) {
                switch (sensor.getType()) {
                    case Sensor.TYPE_STEP_COUNTER:
                        if (stepSensor == null) stepSensor = sensor;
                        break;
                    case Sensor.TYPE_ORIENTATION:
                        if (orintationSensor == null) orintationSensor = sensor;
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        if (magnSensor == null) magnSensor = sensor;
                        break;
                    default:
                        break;
                }
            }
        }

        sensManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors = sensManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);

        if (sensors.size() != 0) {
            sensManager.registerListener(listener,
                    sensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        } else {

        }

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.kras.diplom/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
        createMapView();
        map.setOnMarkerDragListener(MarkerLis);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensManager.registerListener(listener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensManager.registerListener(listener, orintationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensManager.registerListener(listener, magnSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        b = sp.getBoolean("magn", false);
        time = Integer.parseInt(sp.getString("time", "1")) * 1000;
        Lstep = Double.parseDouble(sp.getString("step","1"));
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // Для устройств до Android 5
            createOldSoundPool();
        } else {
            // Для новых устройств
            createNewSoundPool();
        }
        AM = getAssets();
        Sstart = loadSound("1.ogg");
        Sstop = loadSound("2.ogg");
    }

    @Override
    public void onPause() {
        super.onPause();
        sensManager.unregisterListener(listener);
        mSound.release();
        mSound = null;
        sensManager.unregisterListener(listener);
    }

    @Override
    public void onStop() {
        super.onStop();
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            openQuitDialog();
        }
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu1:
                Mode1();
                break;
            case R.id.menu2:
                Mode2();
                break;
            case R.id.menu3:
                ModeGrid();
                break;
            case R.id.menu4:
                OpenFileDialog();
                break;
            case R.id.menu5:
                clearMap();
                break;
            case R.id.menu6:
                startActivity(intentSetting);
                break;
            case R.id.menu7:
                startActivity(intentAbout);
                break;
            case R.id.menu8:
                startActivity(intentPoint);
                break;

            default:
                break;
        }/*
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
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                return;
            }
        });

        quitDialog.show();
    }


}

class Point{
    int magnet;
    double step;
    double longitude;
    double latitude;
    double orientation;

    public Point( int m, double lat , double lon ){
        magnet=m;
        longitude=lon;
        latitude=lat;

    }

    public Point( double lat , double lon){
        longitude=lon;
        latitude=lat;

    }

    public Point(int m, int u,double s){
        double uA=u*3.1415/180;
        magnet=m;
        orientation=uA;
        step=s;
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

    public double getStep(){
        return this.step;
    }

    public double getOrientation() {
        return this.orientation;
    }
}

