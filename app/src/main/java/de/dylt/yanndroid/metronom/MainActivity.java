package de.dylt.yanndroid.metronom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Locale;
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
    static SoundPool soundPool;
    static HashMap<Integer, Integer> soundModes = new HashMap<>();
    int firstSound;
    int othersSound;

    int beatcounter;
    static SharedPreferences sharedPreferences;

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedPreferences = getSharedPreferences("settings", Activity.MODE_PRIVATE);

        setLanguage();

        menuexpanded = true;
        fab = findViewById(R.id.fab);
        itemscard = findViewById(R.id.itemscard);
        play = findViewById(R.id.playswitch);
        settings = findViewById(R.id.settingsswitch);


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        beatcounter = 0;
        counter = findViewById(R.id.counter);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        soundModes.put(1, soundPool.load(getBaseContext(), R.raw.mode_1_first, 1));
        soundModes.put(2, soundPool.load(getBaseContext(), R.raw.mode_1_others, 1));
        soundModes.put(3, soundPool.load(getBaseContext(), R.raw.mode_2_first, 1));
        soundModes.put(4, soundPool.load(getBaseContext(), R.raw.mode_2_others, 1));

        timer = new Timer();


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

                    int first_beat = sharedPreferences.getInt("first_beat", 150);
                    int other_beat = sharedPreferences.getInt("other_beat", 75);


                    if (sharedPreferences.getBoolean("sound_mode_1", true)) {
                        firstSound = soundModes.get(1);
                        othersSound = soundModes.get(2);
                    } else {
                        firstSound = soundModes.get(3);
                        othersSound = soundModes.get(4);
                    }


                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    beatcounter++;
                                    if (beatcounter == beat_picker1 + 1) {
                                        beatcounter = 1;
                                    }
                                    counter.setText(String.valueOf(beatcounter));


                                    if (beatcounter == 1) {
                                        if (options.get("sound")) {
                                            soundPool.play(firstSound, 1, 1, 0, 0, 1);
                                        }

                                        if (options.get("vibration")) {
                                            vibrator.vibrate(first_beat);
                                        }
                                    } else {
                                        if (options.get("sound")) {
                                            soundPool.play(othersSound, 1, 1, 0, 0, 1);
                                        }

                                        if (options.get("vibration")) {
                                            vibrator.vibrate(other_beat);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        soundPool.release();
        soundPool = null;
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

    public void setLanguage() {
        switch (sharedPreferences.getInt("language_spinner", 0)) {
            case 0:
                setLocale(MainActivity.this, "");
                return;
            case 1:
                setLocale(MainActivity.this, "en");
                return;
            case 2:
                setLocale(MainActivity.this, "de");
                return;
            case 3:
                setLocale(MainActivity.this, "fr");
                return;
        }

    }

    public static void playSounds() {
        if (sharedPreferences.getBoolean("sound_mode_1", true)) {
            soundPool.play(soundModes.get(1), 1, 1, 0, 0, 1);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    soundPool.play(soundModes.get(2), 1, 1, 0, 0, 1);
                }
            }, 500);
        } else {
            soundPool.play(soundModes.get(3), 1, 1, 0, 0, 1);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    soundPool.play(soundModes.get(4), 1, 1, 0, 0, 1);
                }
            }, 500);
        }
    }
}