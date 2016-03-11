package com.kras.diplom;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

public class PointActivity extends Activity {
    private TextView tv1;
    private TextView tv2;
    private SensorManager sensManager;
    Sensor mAccelerometerSensor;
    Sensor mSensor;
    private NumberFormat nf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);
        tv1=(TextView)findViewById(R.id.tv1);
        tv2=(TextView)findViewById(R.id.tv2);
        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        sensManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensManager.getSensorList(Sensor.TYPE_ALL);
        if(sensors.size() > 0)
        {
            for (Sensor sensor : sensors) {
                switch(sensor.getType())
                {
                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        if(mAccelerometerSensor == null) mAccelerometerSensor = sensor;
                        break;
                    case Sensor.TYPE_ORIENTATION:
                        if(mSensor == null) mSensor = sensor;
                        break;
                    default:
                        break;
                }
            }
    }

}


    @Override
    protected void onResume() {
        super.onResume();
        sensManager.registerListener(listener, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
       sensManager.registerListener(listener, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }
    SensorEventListener listener = new SensorEventListener() {
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float [] values = event.values;
        switch(event.sensor.getType())
        {
            case Sensor.TYPE_LINEAR_ACCELERATION:
            {
                Log.i("y",String.valueOf(nf.format(event.values[1])));
                tv1.setText(String.valueOf(nf.format(event.values[1])));
            }
            break;
            case Sensor.TYPE_ORIENTATION:{
                tv2.setText(String.valueOf(nf.format(event.values[0])));
            }
            break;
        }
    }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sensManager.unregisterListener(listener);

    }
}
