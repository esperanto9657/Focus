package jp.ac.titech.itpro.sdl.focus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.media.RingtoneManager;
import android.media.Ringtone;
import android.media.MediaPlayer;
import android.media.AudioAttributes;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    private Ringtone onStopAlert;
    private Ringtone moveAlert;
    private SensorManager manager;
    private Sensor sensor;
    private static int delay = SensorManager.SENSOR_DELAY_NORMAL;
    private int accuracy;
    private float prev_x = 100;
    private float prev_y = 100;
    private float prev_z = 100;
    private long startTime;
    private TextView outputView;
    private EditText inputName;
    private String name = "";
    private final static String KEY_NAME = "MainActivity.name";
    private LocalDateTime endAlert;
    private static final float alertThreshold = 1;
    private static final float stopThreshold = 0.1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (manager == null) {
            finish();
            return;
        }
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor == null) {
            finish();
        }
        manager.registerListener(this, sensor, delay);
        moveAlert = RingtoneManager.getRingtone(this, uri);
        if (savedInstanceState != null) {
            name = savedInstanceState.getString(KEY_NAME);
        }

        outputView = findViewById(R.id.output_view);
        inputName = findViewById(R.id.input_name);
        endAlert = LocalDateTime.now();
        Button okButton = findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = inputName.getText().toString().trim();
                endAlert = LocalDateTime.now().plusMinutes(Long.parseLong(name));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(onStopAlert != null){
            onStopAlert.stop();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(endAlert.isAfter(LocalDateTime.now())){
            onStopAlert = RingtoneManager.getRingtone(this, uri);
            onStopAlert.play();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(prev_x == 100 && prev_y == 100 && prev_z == 100){
            prev_x = event.values[0];
            prev_y = event.values[1];
            prev_z = event.values[2];
        }
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if((Math.abs(prev_x - x) > alertThreshold || Math.abs(prev_y - y) > alertThreshold || Math.abs(prev_z - z) > alertThreshold) && !moveAlert.isPlaying() && endAlert.isAfter(LocalDateTime.now())){
            if(endAlert.isAfter(LocalDateTime.now())){
                Log.d("end", endAlert.format(DateTimeFormatter.ISO_LOCAL_TIME));
            }
            Log.d("end", endAlert.format(DateTimeFormatter.ISO_LOCAL_TIME));
            Log.d("current", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
            moveAlert.play();
            startTime = 0;
            Log.d("play", "play_move");
        }

        if(Math.abs(prev_x - x) < stopThreshold && Math.abs(prev_y - y) < stopThreshold && Math.abs(prev_z - z) < stopThreshold){
            if(startTime == 0){
                startTime = event.timestamp;
            }
            long currentTime = event.timestamp;
            if(currentTime > startTime + 1E+10 && moveAlert.isPlaying()){
                moveAlert.stop();
                Log.d("stop", "stop_move");
            }
        }

        prev_x = x;
        prev_y = y;
        prev_z = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_NAME, name);
    }
}