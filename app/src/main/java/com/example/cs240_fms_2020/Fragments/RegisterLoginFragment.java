package com.example.cs240_fms_2020.Fragments;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


import com.example.cs240_fms_2020.Interfaces.LoginContext;
import com.example.cs240_fms_2020.Interfaces.LoginWatcher;
import com.example.cs240_fms_2020.R;
import com.example.cs240_fms_2020.Tasks.LoginTask;
import com.example.cs240_fms_2020.Tasks.RegisterTask;

import RequestResult.LoginRequest;
import RequestResult.RegisterRequest;


public class RegisterLoginFragment extends Fragment implements LoginContext, RegisterTask.RegisterContext
{

    private LoginWatcher loginWatcher;
    private TextWatcher watcher;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    private EditText serverHostTxt;
    private EditText ipTxt;
    private EditText userNameTxt;
    private EditText passwordTxt;
    private EditText firstNameTxt;
    private EditText lastNameTxt;
    private EditText emailTxt;

    private Button maleBtn;
    private Button femaleBtn;

    private Button loginBtn;
    private Button registerBtn;

    //class that will be used to enable and disable register and login buttons
    // depending on fields completed
    private class Enabler implements TextWatcher
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            evaluate();
        }

        @Override
        public void afterTextChanged(Editable s) {}

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        registerRequest = new RegisterRequest();
        loginRequest = new LoginRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        watcher = new Enabler();

        initElementsAndListeners(v);

        return v;
    }



    private void initElementsAndListeners(View view)
    {

        ipTxt = view.findViewById(R.id.portNumberInput);
        ipTxt.addTextChangedListener(watcher);

        serverHostTxt = view.findViewById(R.id.serverHostInput);
        serverHostTxt.addTextChangedListener(watcher);

        userNameTxt = view.findViewById(R.id.userNameInput);
        userNameTxt.addTextChangedListener(watcher);

        passwordTxt = view.findViewById(R.id.passwordInput);
        passwordTxt.addTextChangedListener(watcher);

        firstNameTxt = view.findViewById(R.id.firstNameInput);
        firstNameTxt.addTextChangedListener(watcher);

        lastNameTxt = view.findViewById(R.id.lastNameInput);
        lastNameTxt.addTextChangedListener(watcher);

        emailTxt = view.findViewById(R.id.emailInput);
        emailTxt.addTextChangedListener(watcher);

        maleBtn = view.findViewById(R.id.maleButton);
        maleBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                registerRequest.setGender("m");
                evaluate();
            }
        });

        femaleBtn = view.findViewById(R.id.femaleButton);
        femaleBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                registerRequest.setGender("f");
                evaluate();
            }
        });

        loginBtn = view.findViewById(R.id.loginButton);
        registerBtn = view.findViewById(R.id.registerButton);
        evaluate();

        loginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                loginRequest.setUserName(userNameTxt.getText().toString());
                loginRequest.setPassword(passwordTxt.getText().toString());
                LoginTask loginTask = new LoginTask(serverHostTxt.getText().toString(),
                        ipTxt.getText().toString(),
                        RegisterLoginFragment.this);

                loginTask.execute(loginRequest);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                registerRequest.setServerHost(serverHostTxt.getText().toString());
                registerRequest.setServerPort(ipTxt.getText().toString());
                registerRequest.setUserName(userNameTxt.getText().toString());
                registerRequest.setEmail(emailTxt.getText().toString());
                registerRequest.setFirstName(firstNameTxt.getText().toString());
                registerRequest.setLastName(lastNameTxt.getText().toString());
                registerRequest.setPassword(passwordTxt.getText().toString());

                RegisterTask regTask = new RegisterTask(serverHostTxt.getText().toString(),
                        ipTxt.getText().toString(),
                        RegisterLoginFragment.this);

                regTask.execute(registerRequest);
            }
        });

    }



    @Override
    public void onExecuteComplete(String message)
    {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        loginWatcher.loginComplete();
    }

    //Check if all fields completed for register or login
    public void evaluate()
    {

        registerValidation();
        loginValidation();
    }

    private void loginValidation()
    {
        if (validateLoginButton()){
            loginBtn.setEnabled(false);
        }
        else {
            loginBtn.setEnabled(true);
        }
    }

    private void registerValidation()
    {
        if (validateRegisterButton()){
            registerBtn.setEnabled(false);
        }
        else {
            registerBtn.setEnabled(true);
        }
    }

    //Use TextUtils to check if field are empty or filled
    private boolean validateRegisterButton()
    {
     return TextUtils.isEmpty(serverHostTxt.getText()) ||
             TextUtils.isEmpty(ipTxt.getText()) ||
             TextUtils.isEmpty(userNameTxt.getText()) ||
             TextUtils.isEmpty(passwordTxt.getText()) ||
             TextUtils.isEmpty(emailTxt.getText()) ||
             TextUtils.isEmpty(firstNameTxt.getText()) ||
             TextUtils.isEmpty(lastNameTxt.getText()) ||
             registerRequest.getGender() == null;
    }

    //Use TextUtils to check if field are empty or filled
    private boolean validateLoginButton()
    {
        return TextUtils.isEmpty(serverHostTxt.getText()) ||
                TextUtils.isEmpty(ipTxt.getText()) ||
                TextUtils.isEmpty(userNameTxt.getText()) ||
                TextUtils.isEmpty(passwordTxt.getText());
    }

    public void setLoginWatcher(LoginWatcher logListen)
    {
        loginWatcher = logListen;
    }


}
