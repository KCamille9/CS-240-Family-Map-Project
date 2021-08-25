package com.example.cs240_fms_2020.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.cs240_fms_2020.Fragments.MapFragment;
import com.example.cs240_fms_2020.Fragments.RegisterLoginFragment;
import com.example.cs240_fms_2020.Interfaces.LoginWatcher;
import com.example.cs240_fms_2020.R;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

public class MainActivity extends AppCompatActivity implements LoginWatcher {

    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Add iconify library for icons across classes
        Iconify.with(new FontAwesomeModule());

        setContentView(R.layout.main_activity_view);
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        //start map fragment
        if ((getIntent().getExtras() != null))
        {

            Fragment mapFragment = new MapFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.add(R.id.fragment_container, mapFragment).commit();
        }
        //Start main fragment
        else if (fragment == null)
        {
            fragment = new RegisterLoginFragment();
            ((RegisterLoginFragment) fragment).setLoginWatcher(this);
            fragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    public void loginComplete()
    {
        //Start map fragment
        Fragment mapFragment = new MapFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, mapFragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }
}