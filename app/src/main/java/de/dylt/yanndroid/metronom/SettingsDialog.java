package de.dylt.yanndroid.metronom;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Locale;

public class SettingsDialog extends BottomSheetDialogFragment {

    boolean seeking = false;


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

        /** language_spinner */
        ArrayList<String> language_options = new ArrayList<>();
        language_options.add("System");
        language_options.add("English");
        language_options.add("German");
        language_options.add("French");

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
                /*switch (position) {
                    case 0:
                        setLocale(SettingsActivity.this, "");
                        return;
                    case 1:
                        setLocale(SettingsActivity.this, "en");
                        return;
                    case 2:
                        setLocale(SettingsActivity.this, "de");
                        return;
                    case 3:
                        setLocale(SettingsActivity.this, "fr");
                        return;
                }*/
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
                            tempo_text.setError("20 to 300 only");
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
        theme_options.add("System");
        theme_options.add("Always");
        theme_options.add("Never");

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

        SwitchMaterial vib_switch = dialog.findViewById(R.id.vib_switch);
        vib_switch.setChecked(sharedPreferences.getBoolean("vib_switch", false));
        vib_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("vib_switch", isChecked).commit();
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


    }

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public void restartapp() {
        Snackbar.make(getDialog().findViewById(R.id.language_spinner), "Need to restart to apply language", Snackbar.LENGTH_LONG).setAction("Ok", new Snackbarbutton()).show();
    }

    public class Snackbarbutton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            getActivity().finishAffinity();
        }
    }

}