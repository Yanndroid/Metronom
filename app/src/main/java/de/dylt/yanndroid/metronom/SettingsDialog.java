package de.dylt.yanndroid.metronom;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.HashMap;

import static de.dylt.yanndroid.metronom.R.string.need_restart;

public class SettingsDialog extends BottomSheetDialogFragment {

    boolean seeking = false;
    Vibrator vibrator;


    public static SettingsDialog newInstance() {
        SettingsDialog fragment = new SettingsDialog();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.settings_dialog, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));


        initSettings(dialog);


    }


    public void initSettings(Dialog dialog) {
        SharedPreferences sharedPreferences;
        sharedPreferences = getContext().getSharedPreferences("settings", Activity.MODE_PRIVATE);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);


        /** language_spinner */
        ArrayList<String> language_options = new ArrayList<>();
        language_options.add(getString(R.string.system));
        language_options.add(getString(R.string.english));
        language_options.add(getString(R.string.german));
        language_options.add(getString(R.string.french));

        Spinner language_spinner = dialog.findViewById(R.id.language_spinner);
        language_spinner.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_layout, language_options));
        ((ArrayAdapter) language_spinner.getAdapter()).notifyDataSetChanged();

        language_spinner.setSelection(sharedPreferences.getInt("language_spinner", 0));
        final int[] language_spinner_selection = {sharedPreferences.getInt("language_spinner", 0)};

        language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (language_spinner_selection[0] != position) {
                    restartapp();
                }
                sharedPreferences.edit().putInt("language_spinner", position).commit();
                language_spinner_selection[0] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /** Beat_picker */
        NumberPicker beatpicker1 = dialog.findViewById(R.id.beatpicker1);
        beatpicker1.setMinValue(1);
        beatpicker1.setMaxValue(32);
        beatpicker1.setValue(sharedPreferences.getInt("beat_picker1", 4));
        beatpicker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                sharedPreferences.edit().putInt("beat_picker1", newVal).commit();

            }
        });


        NumberPicker beatpicker2 = dialog.findViewById(R.id.beatpicker2);
        beatpicker2.setMinValue(1);
        beatpicker2.setMaxValue(32);
        beatpicker2.setValue(sharedPreferences.getInt("beat_picker2", 4));
        beatpicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                sharedPreferences.edit().putInt("beat_picker2", newVal).commit();
            }
        });


        /** Tempo_bar */
        SeekBar tempo_bar = dialog.findViewById(R.id.tempo_bar);
        EditText tempo_text = dialog.findViewById(R.id.tempo_text);

        tempo_bar.setMax(300);
        tempo_bar.setMin(20);


        tempo_bar.setProgress(sharedPreferences.getInt("tempo", 60));
        tempo_text.setText(String.valueOf(sharedPreferences.getInt("tempo", 60)));

        tempo_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seeking) {
                    sharedPreferences.edit().putInt("tempo", progress).commit();
                    tempo_text.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seeking = true;
                tempo_text.setEnabled(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seeking = false;
                tempo_text.setEnabled(true);
            }
        });

        tempo_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() != 0) {
                    if (19 < Integer.parseInt(s.toString()) && Integer.parseInt(s.toString()) < 301) {
                        sharedPreferences.edit().putInt("tempo", Integer.parseInt(s.toString())).commit();
                        tempo_bar.setProgress(Integer.parseInt(s.toString()));
                    } else {
                        if (Integer.parseInt(s.toString()) > 300) {
                            tempo_text.setText(String.valueOf(300));
                        } else {
                            tempo_text.setError(getString(R.string.tempo_error));
                        }

                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        /** Theme_spinner */
        ArrayList<String> theme_options = new ArrayList<>();
        theme_options.add(getString(R.string.system));
        theme_options.add(getString(R.string.always));
        theme_options.add(getString(R.string.never));

        Spinner theme_spinner = dialog.findViewById(R.id.theme_spinner);
        theme_spinner.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_layout, theme_options));
        ((ArrayAdapter) theme_spinner.getAdapter()).notifyDataSetChanged();

        theme_spinner.setSelection(sharedPreferences.getInt("theme_spinner", 0));
        final int[] theme_spinner_selection = {sharedPreferences.getInt("theme_spinner", 0)};

        theme_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sharedPreferences.edit().putInt("theme_spinner", position).commit();
                theme_spinner_selection[0] = position;
                switch (position) {
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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        /** switches */

        LinearLayout vib_expand = dialog.findViewById(R.id.vib_expand);
        HashMap<Boolean, Integer> visibility = new HashMap<>();
        visibility.put(true, View.VISIBLE);
        visibility.put(false, View.GONE);

        SwitchMaterial vib_switch = dialog.findViewById(R.id.vib_switch);
        vib_switch.setChecked(sharedPreferences.getBoolean("vib_switch", false));

        vib_expand.setVisibility(visibility.get(sharedPreferences.getBoolean("vib_switch", false)));

        vib_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("vib_switch", isChecked).commit();
                vib_expand.setVisibility(visibility.get(isChecked));
            }
        });

        SwitchMaterial sound_switch = dialog.findViewById(R.id.sound_switch);
        sound_switch.setChecked(sharedPreferences.getBoolean("sound_switch", false));
        sound_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("sound_switch", isChecked).commit();
            }
        });


        /** Download */
        View download = dialog.findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse("https://github.com/Yanndroid/Metronom/raw/master/app/release/app-release.apk");
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle("Metronome Apk");
                //request.setDescription("Downloading");
                request.setVisibleInDownloadsUi(true);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Metronome.apk");
                downloadManager.enqueue(request);

            }
        });


        /** Vibration_menu */
        SeekBar first_beat_bar = dialog.findViewById(R.id.first_beat_bar);

        first_beat_bar.setMax(300);
        first_beat_bar.setMin(30);

        first_beat_bar.setProgress(sharedPreferences.getInt("first_beat", 150));
        first_beat_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sharedPreferences.edit().putInt("first_beat", progress).commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                vibrator.vibrate(seekBar.getProgress());
            }
        });

        SeekBar other_beat_bar = dialog.findViewById(R.id.other_beat_bar);

        other_beat_bar.setMax(300);
        other_beat_bar.setMin(30);

        other_beat_bar.setProgress(sharedPreferences.getInt("other_beat", 75));
        other_beat_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sharedPreferences.edit().putInt("other_beat", progress).commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                vibrator.vibrate(seekBar.getProgress());
            }
        });


    }


    public void restartapp() {
        Snackbar.make(getDialog().findViewById(R.id.language_spinner), need_restart, Snackbar.LENGTH_LONG).setAction(R.string.ok, new Snackbarbutton()).show();
    }

    public class Snackbarbutton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            getActivity().finishAffinity();
        }
    }

}