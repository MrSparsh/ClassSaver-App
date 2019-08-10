package com.example.comp.timetableapp;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

public class SettingActivity extends AppCompatActivity {
    Switch aSwitch1,aSwitch2;
    AudioManager audioManager;
    public void onSwitchClick(View view)
    {
        if(aSwitch1.isChecked())
        {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
        else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }
    public void onSwitchClick2(View view)
    {
        if(aSwitch2.isChecked())
        {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
        else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        aSwitch1=(Switch)findViewById(R.id.switch1);
        aSwitch2=(Switch)findViewById(R.id.switch2);
        audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
    }

}
