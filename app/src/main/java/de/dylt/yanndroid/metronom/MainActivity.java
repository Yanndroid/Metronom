package de.dylt.yanndroid.metronom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    MaterialCardView itemscard;
    boolean menuexpanded;

    CheckBox play;
    CheckBox settings;

    TextView counter;


    Timer timer;
    Vibrator vibrator;

    int beatcounter;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("settings", Activity.MODE_PRIVATE);

        menuexpanded = true;
        fab = findViewById(R.id.fab);
        itemscard = findViewById(R.id.itemscard);
        play = findViewById(R.id.playswitch);
        settings = findViewById(R.id.settingsswitch);


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        beatcounter = 0;
        counter = findViewById(R.id.counter);


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settings.isChecked()) {
                    settings.setChecked(false);
                    //settings click action

                    SettingsDialog bottomSheetDialog = SettingsDialog.newInstance();
                    bottomSheetDialog.show(getSupportFragmentManager(), "tag");


                } else {
                    //stop click action
                    play.setChecked(false);
                    timer.cancel();
                    beatcounter = 0;
                    counter.setText(String.valueOf(beatcounter));
                }
            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (play.isChecked()) {
                    //play click action
                    settings.setChecked(true);

                    timer = new Timer();


                    int beat_picker1 = sharedPreferences.getInt("beat_picker1", 4);
                    int beat_picker2 = sharedPreferences.getInt("beat_picker2", 4);
                    int tempo = sharedPreferences.getInt("tempo", 60);

                    HashMap<String, Boolean> options = new HashMap<String, Boolean>();
                    options.put("vibration", sharedPreferences.getBoolean("vib_switch", false));
                    options.put("sound", sharedPreferences.getBoolean("sound_switch", false));


                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (options.get("sound")) {
                                        final MediaPlayer mp = MediaPlayer.create(getBaseContext(), R.raw.beat);
                                        mp.start();
                                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            public void onCompletion(MediaPlayer mp) {
                                                mp.release();
                                            }

                                            ;
                                        });
                                    }

                                    beatcounter++;
                                    if (beatcounter == beat_picker1 + 1) {
                                        beatcounter = 1;
                                    }
                                    counter.setText(String.valueOf(beatcounter));


                                    if (options.get("vibration")) {
                                        if (beatcounter == beat_picker1) {
                                            vibrator.vibrate((long) 150);
                                        } else {
                                            vibrator.vibrate((long) 75);
                                        }
                                    }
                                }
                            });
                        }
                    }, 0, (long) ((long) (((double) 60 / tempo) * 1000) / ((double) beat_picker1 / beat_picker2)));


                } else {
                    //pause click action
                    timer.cancel();
                }
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuexpanded) {
                    itemscard.setVisibility(View.GONE);
                    fab.animate().rotation(-135).setDuration(300);
                } else {
                    itemscard.setVisibility(View.VISIBLE);
                    fab.animate().rotation(0).setDuration(300);
                }
                menuexpanded = !menuexpanded;
            }
        });

        setDarkMode();

    }

    public void setDarkMode() {
        switch (sharedPreferences.getInt("theme_spinner", 0)) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                return;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                return;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                return;
        }
    }
}