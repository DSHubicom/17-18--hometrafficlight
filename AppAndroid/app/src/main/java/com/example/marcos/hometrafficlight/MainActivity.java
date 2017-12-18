package com.example.marcos.hometrafficlight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");
        Log.d(TAG, "Creando thread por primera vez");

        final PeriodicalPost periodicalPost = new PeriodicalPost();

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    periodicalPost.setPeriodicalRestCheck(getApplicationContext());
                } else {
                    periodicalPost.cancelPeriodicalRestCheck(getApplicationContext());
                }
            }
        });

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }
}