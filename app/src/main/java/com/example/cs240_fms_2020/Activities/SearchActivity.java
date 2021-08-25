package com.example.cs240_fms_2020.Activities;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cs240_fms_2020.Client;
import com.example.cs240_fms_2020.R;
import com.example.cs240_fms_2020.RecyclerUI.SearchAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Model.Event;
import Model.Person;


public class SearchActivity extends AppCompatActivity
{

    private EditText searchTextField;
    private Button searchBtn;
    private String searchInput;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter searchAdapter;

    private Client client = Client.init();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initElements();
    }

    private void initElements()
    {
        searchTextField = findViewById(R.id.search_text);
        searchBtn = findViewById(R.id.search_button);
        recyclerView = findViewById(R.id.search_list_result);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initListeners();
    }

    private void initListeners()
    {
        searchTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                searchInput = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s)
            {}
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (searchInput != null){
                    updateUI();
                }
            }
        });
    }


    //Back btn
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Refresh UI with found items
    private void updateUI()
    {
        List<Object> objectList = new ArrayList<>();

        searchInPeople(objectList);
        searchInEvents(objectList);

        //If what the user typed in the search bar was found in events or in people
        if (objectList.size() != 0) {
            searchAdapter = new SearchAdapter(objectList, this);
            recyclerView.setAdapter(searchAdapter);
        }
    }

    private void searchInPeople(List<Object> objects)
    {
        Map<String, Person> people = client.getPeopleMap();
        Map<String, Person> availablePeople = people;
        getPersonsList(availablePeople, objects);
    }

    private void searchInEvents(List<Object> objectList)
    {
        Map<String, Event> events = client.getCreatedEventsInMap();
        Map<String, Event> availableEvents = events;
        getEventsList(availableEvents, objectList);
    }

    private void getPersonsList(Map<String, Person> people, List<Object> objects)
    {
        //Find user keywords in people attributes
        for (Person person: people.values())
        {
            if (person.getFirstName().toLowerCase().contains(searchInput.toLowerCase()))
            {
                objects.add(person);
            }
            else if (person.getLastName().toLowerCase().contains(searchInput.toLowerCase()))
            {
                objects.add(person);
            }
        }
    }

    private void getEventsList(Map<String, Event> clientEvents, List<Object> objects)
    {
        for (Event event: clientEvents.values())
        {
            //Find user keywords in event attributes
            if (event.getEventType().toLowerCase().contains(searchInput.toLowerCase()))
            {
                objects.add(event);
            }
            else if (event.getCountry().toLowerCase().contains(searchInput.toLowerCase()))
            {
                objects.add(event);
            }
            else if (event.getCity().toLowerCase().contains(searchInput.toLowerCase()))
            {
                objects.add(event);
            }
//            else if (event.getYear() == searchInput)
//            {
//                objectList.add(event);
//            }
//            else if(event.getLatitude() == searchInput)
//            {
//                objectList.add(event);
//            }
//            else if(event.getLongitude() == searchInput)
//            {
//                objectList.add(event);
//            }

            //TO DO: ADD LAT AND LONG HERE
        }
    }
}
