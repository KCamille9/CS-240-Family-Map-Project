package com.example.cs240_fms_2020;


import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import RequestResult.EventResult;
import RequestResult.LoginRequest;
import RequestResult.LoginResult;
import RequestResult.PersonResult;
import RequestResult.RegisterRequest;
import RequestResult.RegisterResult;

public class ServerProxy
{

    private static ServerProxy serverProxy;

    public static ServerProxy init()
    {
        if (serverProxy == null)
        {
            serverProxy = new ServerProxy();
        }
        return serverProxy;
    }


    public LoginResult login(String serverHost, String serverPort, LoginRequest loginRequest)
    {
        Gson gson = new Gson();
        try
        {
            String path = "http://" + serverHost + ":" + serverPort + "/user/login";
            URL url = new URL(path);

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);

            http.addRequestProperty("Accept", "application/json");

            http.connect();

            String requestInfo = gson.toJson(loginRequest);
            OutputStream body = http.getOutputStream();
            writeString(requestInfo, body);

            //Was a successful response
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK)
            {

                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                LoginResult loginResult = gson.fromJson(respData, LoginResult.class);
                return loginResult;
            }
            else {
                LoginResult loginResultErr = new LoginResult();
                loginResultErr.setMessage("Error: " + http.getResponseMessage());
                return loginResultErr;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            LoginResult loginResultErr = new LoginResult();
            loginResultErr.setMessage("Error: " + e.getMessage());
            return loginResultErr;
        }
    }

    public RegisterResult register(String serverHost, String serverPort, RegisterRequest regReq)
    {
        Gson gson = new Gson();
        try
        {
            String path = "http://" + serverHost + ":" + serverPort + "/user/register";
            URL url = new URL(path);

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");

            http.connect();

            String requestInfo = gson.toJson(regReq);
            OutputStream body = http.getOutputStream();
            writeString(requestInfo, body);

            //Was a successful response
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                RegisterResult registerResult = gson.fromJson(respData, RegisterResult.class);
                return registerResult;
            }
            else {
                RegisterResult registerResultErr = new RegisterResult();
                registerResultErr.setMessage("Error: " + http.getResponseMessage());
                return registerResultErr;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            RegisterResult registerResultErr = new RegisterResult();
            registerResultErr.setMessage("Error: " + e.getMessage());
            return registerResultErr;
        }
    }

    public PersonResult getPeople(String serverHost, String serverPort, String authToken)
    {
        Gson gson = new Gson();
        try
        {
            String path = "http://" + serverHost + ":" + serverPort + "/person";
            URL url = new URL(path);

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");

            http.connect();

            //Was a successful response
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                PersonResult peopleResult = gson.fromJson(respData, PersonResult.class);
                peopleResult.setSuccess(true);
                return peopleResult;
            }
            else {
                PersonResult peopleResult = new PersonResult();
                peopleResult.setSuccess(false);
                peopleResult.setMessage(http.getResponseMessage());
                return peopleResult;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public EventResult getEvents(String serverHost, String serverPort, String authToken)
    {
        Gson gson = new Gson();
        try
        {
            String path = "http://" + serverHost + ":" + serverPort + "/event";
            URL url = new URL(path);

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");

            http.connect();

            //Was a successful response
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                EventResult eventsResult = gson.fromJson(respData, EventResult.class);
                eventsResult.setSuccess(true);
                return eventsResult;
            }
            else
                {
                EventResult eventsResult = new EventResult();
                    eventsResult.setSuccess(false);
                    eventsResult.setMessage(http.getResponseMessage());
                return eventsResult;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String readString(InputStream is) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private static void writeString(String str, OutputStream os) throws IOException
    {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
