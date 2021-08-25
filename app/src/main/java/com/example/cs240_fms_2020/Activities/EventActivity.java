package com.example.cs240_fms_2020.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cs240_fms_2020.Fragments.MapFragment;
import com.example.cs240_fms_2020.R;


public class EventActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String key = "Event";
        String arguments = getIntent().getExtras().getString(key);

        FragmentManager fm = getSupportFragmentManager();
        Fragment mapFragment = new MapFragment(arguments);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        //Start map fragment
        fragmentTransaction.add(R.id.map_fragment, mapFragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

    //Back btn
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
