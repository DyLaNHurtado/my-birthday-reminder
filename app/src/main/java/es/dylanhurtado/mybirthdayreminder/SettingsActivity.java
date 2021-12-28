package es.dylanhurtado.mybirthdayreminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {


    private TextView aboutUs;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch notifications;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch animations;
    private RadioGroup animationsOptions;
    private RadioButton slow;
    private RadioButton normal;
    private RadioButton fast;
    private AboutUsDialog birthdayOptionsDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // To navigation button of toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        this.aboutUs = findViewById(R.id.textView_AboutUs);
        this.notifications = findViewById(R.id.switchNotifications);
        this.animations = findViewById(R.id.switchAnimations);
        this.animationsOptions = findViewById(R.id.radioGroupAnimation);
        this.slow = findViewById(R.id.radioButtonSlow);
        this.normal = findViewById(R.id.radioButtonNormal);
        this.fast = findViewById(R.id.radioButtonFast);
        animations.setChecked(getPreferences().get(1));
        notifications.setChecked(getPreferences().get(0));
        slow.setChecked(getPreferences().get(2));
        normal.setChecked(getPreferences().get(3));
        fast.setChecked(getPreferences().get(4));

        animations.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                animationsOptions.setVisibility(View.VISIBLE);
            } else {
                animationsOptions.setVisibility(View.INVISIBLE);
            }
        });
        animations.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                animationsOptions.setVisibility(View.VISIBLE);
            } else {
                animationsOptions.setVisibility(View.INVISIBLE);
            }
        });

        aboutUs.setOnClickListener(view -> {
            birthdayOptionsDialog = new AboutUsDialog();

            birthdayOptionsDialog.show(getSupportFragmentManager(), "aboutUsDialog");
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        setPreferences();
    }

    @Override
    protected void onStop() {
        super.onStop();
        setPreferences();
    }

    private void setPreferences() {
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("notifications", notifications.isChecked());
        editor.putBoolean("animations", animations.isChecked());
        editor.putBoolean("animationOptionSlow", slow.isChecked());
        editor.putBoolean("animationOptionNormal", normal.isChecked());
        editor.putBoolean("animationOptionFast", fast.isChecked());
        editor.apply();
        finish();
    }

    private List<Boolean> getPreferences() {
        SharedPreferences preferences = this.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        List<Boolean> prefs = new ArrayList<>();
        prefs.add(preferences.getBoolean("notifications", true));
        prefs.add(preferences.getBoolean("animations", true));
        prefs.add(preferences.getBoolean("animationOptionSlow", true));
        prefs.add(preferences.getBoolean("animationOptionNormal", true));
        prefs.add(preferences.getBoolean("animationOptionFast", true));

        return prefs;
    }
}