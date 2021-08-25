package com.example.cs240_fms_2020.Tasks;


import android.os.AsyncTask;


import com.example.cs240_fms_2020.Client;
import com.example.cs240_fms_2020.Interfaces.LoginContext;
import com.example.cs240_fms_2020.ServerProxy;
import com.example.cs240_fms_2020.Interfaces.StreamContext;

import RequestResult.LoginRequest;
import RequestResult.LoginResult;

public class LoginTask extends AsyncTask<LoginRequest, LoginResult, LoginResult> implements StreamContext
{
    private String serverHost;
    private String ip;
    private LoginContext context;

    public LoginTask(String serverHost, String ip, LoginContext context)
    {
        this.serverHost = serverHost;
        this.ip = ip;
        this.context = context;
    }

    @Override
    protected LoginResult doInBackground(LoginRequest... loginRequests)
    {
        return processLoginRequest(loginRequests[0]);
    }

    private LoginResult processLoginRequest(LoginRequest loginRequest)
    {
        ServerProxy serverProxy = ServerProxy.init();
        LoginResult loginResult = serverProxy.login(serverHost, ip, loginRequest);
        return loginResult;
    }

    @Override
    protected void onPostExecute(LoginResult loginResult)
    {
        if (loginResult.getMessage() == null)
        {
            //Start instance of Client, initialize it, and then execute login
            Client client = Client.init();

            client.setServerHost(serverHost);
            client.setIp(ip);
            String authToken = loginResult.getAuthToken();
            client.setAuthToken(authToken);

            //Use StreamTask to do the handling of login
            StreamTask streamTask = new StreamTask(serverHost, ip, this);
            streamTask.execute(authToken);
        }
        else
            {
            context.onExecuteComplete(loginResult.getMessage());
        }
    }

    @Override
    public void onExecuteCompleteData(String message)
    {
        context.onExecuteComplete(message);
    }


}






