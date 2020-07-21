package jp.ac.titech.itpro.sdl.focus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.media.RingtoneManager;
import android.media.Ringtone;
import android.media.MediaPlayer;
import android.media.AudioAttributes;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ringtone != null){
            ringtone.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        alert(this);
    }

    private void alert(Context context){
        ringtone = RingtoneManager.getRingtone(context, uri);
        /*try{
            player.setDataSource(context, uri); // 音声を設定
            player.setAudioAttributes(
                    new AudioAttributes
                            .Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()); // アラームのボリュームで再生
            player.setLooping(true); // ループ再生を設定
            player.prepare(); // 音声を読み込み
        }
        catch (IOException e){
            Log.d("IO", e.getMessage());
        }*/
        Log.d("play", "play");
        ringtone.play(); // 再生
    }
}