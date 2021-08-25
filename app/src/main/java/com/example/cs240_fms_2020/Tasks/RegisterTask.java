package com.example.cs240_fms_2020.Tasks;


import android.os.AsyncTask;


import com.example.cs240_fms_2020.ServerProxy;
import com.example.cs240_fms_2020.Interfaces.StreamContext;

import RequestResult.RegisterRequest;
import RequestResult.RegisterResult;


public class RegisterTask extends AsyncTask<RegisterRequest, RegisterResult, RegisterResult> implements StreamContext {

    private String serverHost;
    private String ip;
    private RegisterContext context;


    public interface RegisterContext {
        void onExecuteComplete(String message);
    }

    public RegisterTask(String serverHost, String ip, RegisterContext context)
    {
        this.serverHost = serverHost;
        this.ip = ip;
        this.context = context;
    }

    @Override
    protected RegisterResult doInBackground(RegisterRequest... registerRequests)
    {
        return processRegisterRequest(registerRequests[0]);
    }

    private RegisterResult processRegisterRequest(RegisterRequest registerRequest)
    {
        ServerProxy serverProxy = ServerProxy.init();
        RegisterResult registerResult = serverProxy.register(serverHost, ip, registerRequest);
        return registerResult;
    }

    @Override
    protected void onPostExecute(RegisterResult registerResult)
    {
        if (registerResult.getMessage() == null)
        {
            StreamTask streamTask = new StreamTask(serverHost, ip, this);
            String authToken = registerResult.getAuthToken();
            streamTask.execute(authToken);
        }
        else
            {
            context.onExecuteComplete(registerResult.getMessage());
        }
    }


    @Override
    public void onExecuteCompleteData(String message)
    {
        context.onExecuteComplete(message);
    }
}
