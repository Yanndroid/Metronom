package de.dylt.yanndroid.metronom;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    MaterialCardView itemscard;
    Boolean menuexpanded;

    CheckBox play;
    CheckBox settings;

    TextView counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuexpanded = true;


        fab = findViewById(R.id.fab);
        itemscard = findViewById(R.id.itemscard);
        play = findViewById(R.id.playswitch);
        settings = findViewById(R.id.settingsswitch);
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


                }
            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (play.isChecked()) {
                    //play click action
                    settings.setChecked(true);


                } else {
                    //pause click action

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


    }


}