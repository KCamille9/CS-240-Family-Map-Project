package com.example.cs240_fms_2020.Tasks;


import android.os.AsyncTask;


import com.example.cs240_fms_2020.Client;
import com.example.cs240_fms_2020.ServerProxy;
import com.example.cs240_fms_2020.Interfaces.StreamContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Event;
import Model.Person;
import RequestResult.EventResult;
import RequestResult.PersonResult;


public class StreamTask extends AsyncTask<String, Boolean, Boolean> {

    private String serverHost;
    private String ip;
    private StreamContext context;
    private Client client = Client.init();


    public StreamTask(String server, String ip, StreamContext c)
    {
        serverHost = server;
        this.ip = ip;
        context = c;
    }

    @Override
    protected Boolean doInBackground(String... authToken)
    {
        ServerProxy serverProxy = ServerProxy.init();
        PersonResult allPersonResults = serverProxy.getPeople(serverHost, ip, authToken[0]);
        EventResult allEventResults = serverProxy.getEvents(serverHost, ip, authToken[0]);

        Boolean bool = sendAllDataToClient(allPersonResults, allEventResults);
        return bool;
    }

    @Override
    protected void onPostExecute(Boolean bool) {
        if (bool){
            Person user = client.getUsers();
            String message = "Welcome!";
            context.onExecuteCompleteData(message);
            client.setUpData();
        }
        else {
            context.onExecuteCompleteData("Error");
        }
    }

    private boolean sendAllDataToClient(PersonResult allPersonResults, EventResult allEventResults)
    {
        //Retrieve both people and events and set it to client instance
        return (setUpEvents(allEventResults) && setUpPeople(allPersonResults));
    }

    private boolean setUpPeople(PersonResult personResult)
    {
        if (personResult.isSuccess()){
            Map<String, Person> personsMap = new HashMap<String, Person>();

            List<Person> personArray = Arrays.asList(personResult.getData());
            client.setUsers(personArray.get(0));


            for(int i = 0; i < personArray.size(); i++){
                String personID = personArray.get(i).getPersonID();
                personsMap.put(personID, personArray.get(i));
            }

            client.setPeopleMap(personsMap);
            return true;
        }
        return false;
    }

    private boolean setUpEvents(EventResult eventResult)
    {
        if (eventResult.isSuccess())
        {
            Map<String, Event> eventsMap = new HashMap<String, Event>();
            ArrayList<Event> eventsArr = eventResult.getData();

            for(int i = 0; i < eventsArr.size(); i++){
                String eventID = eventsArr.get(i).getEventID();
                eventsMap.put(eventID, eventsArr.get(i));
            }

            client.setEventsMap(eventsMap);
            return true;
        }
        return false;
    }

}
