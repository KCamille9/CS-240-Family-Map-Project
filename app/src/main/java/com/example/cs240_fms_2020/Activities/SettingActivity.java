package com.example.cs240_fms_2020.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cs240_fms_2020.Client;
import com.example.cs240_fms_2020.R;
import com.example.cs240_fms_2020.Settings;
import com.example.cs240_fms_2020.Interfaces.StreamContext;


public class SettingActivity extends AppCompatActivity implements StreamContext
{

    private Settings currSettings;
    private Client client = Client.init();


    //From fragment settings elements
    private Context here = this;

    private RelativeLayout logOutView;
    private Switch lifeLinesSwitch;
    private Switch familyLinesSwitch;
    private Switch spouseLinesSwitch;
    private Switch fatherSideLinesSwitch;
    private Switch motherSideLinesSwitch;
    private Switch femaleEventLinesSwitch;
    private Switch maleEventLinesSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);
        //bck btn
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init and update
        initSettingsElements();
        saveUserPreferences();

        //event listeners for switches
        switchesStatusControl();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }


    public void initSettingsElements()
    {
        logOutView = (RelativeLayout) findViewById(R.id.logOutView);
        lifeLinesSwitch = (Switch) findViewById(R.id.life_lines_switch);
        familyLinesSwitch = (Switch) findViewById(R.id.family_lines_switch);
        spouseLinesSwitch = (Switch) findViewById(R.id.spouse_lines_switch);
        fatherSideLinesSwitch = (Switch) findViewById(R.id.father_lines_switch);
        motherSideLinesSwitch = (Switch) findViewById(R.id.mother_lines_switch);
        femaleEventLinesSwitch = (Switch) findViewById(R.id.female_events_lines_switch);
        maleEventLinesSwitch = (Switch) findViewById(R.id.male_events_lines_switch);


        logOutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 0);
            }
        });
    }

    public void saveUserPreferences()
    {
        lifeLinesSwitch.setChecked(client.getSettings().isStoryLines());
        familyLinesSwitch.setChecked(client.getSettings().isFamilyLines());
        spouseLinesSwitch.setChecked(client.getSettings().isSpouseLines());
        fatherSideLinesSwitch.setChecked(client.getSettings().isFatherSideEvents());
        motherSideLinesSwitch.setChecked(client.getSettings().isMotherSideEvents());
        femaleEventLinesSwitch.setChecked(client.getSettings().isFemaleEvents());
        maleEventLinesSwitch.setChecked(client.getSettings().isMaleEvents());

    }

    public void switchesStatusControl()
    {

        lifeLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                client.getSettings().setStoryLines(isChecked);
            }
        });

        familyLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                client.getSettings().setFamilyLines(isChecked);
            }
        });

        spouseLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                client.getSettings().setSpouseLines(isChecked);
            }
        });

        fatherSideLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                client.getSettings().setFatherSideEvents(isChecked);
            }
        });

        motherSideLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                client.getSettings().setMotherSideEvents(isChecked);
            }
        });

        femaleEventLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                client.getSettings().setFemaleEvents(isChecked);
            }
        });

        maleEventLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                client.getSettings().setMaleEvents(isChecked);
            }
        });

    }

    @Override
    public void onExecuteCompleteData(String message) {

    }

}

