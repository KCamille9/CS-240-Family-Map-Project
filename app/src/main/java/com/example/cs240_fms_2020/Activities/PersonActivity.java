package com.example.cs240_fms_2020.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.cs240_fms_2020.Client;
import com.example.cs240_fms_2020.R;
import com.example.cs240_fms_2020.RecyclerUI.PersonListHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

import Model.Event;
import Model.Person;

public class PersonActivity extends AppCompatActivity
{

    private Person chosenPerson;

    private TextView firstNameTxt;
    private TextView lastNameTxt;
    private TextView genderTxt;

    private ExpandableListView listView;
    private ExpandableListAdapter expandableListAdapter;

    private Client client = Client.init();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_activity_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        chosenPerson = client.getChosenPerson();

        initElements();
        updateUI();
    }

    private void initElements()
    {
        firstNameTxt = findViewById(R.id.person_first_name);
        lastNameTxt = findViewById(R.id.person_last_name);
        genderTxt = findViewById(R.id.person_gender);
        listView = findViewById(R.id.person_activity_expandable);

        firstNameTxt.setText(chosenPerson.getFirstName());
        lastNameTxt.setText(chosenPerson.getLastName());
        genderTxt.setText(chosenPerson.getGender().toUpperCase());

        initListeners();
    }

    private void initListeners()
    {
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                if (groupPosition == 0){
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    intent.putExtra("Event", "Event");
                    client.setChosenEvent((Event) expandableListAdapter.getChild(groupPosition, childPosition));
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    client.setChosenPerson((Person) expandableListAdapter.getChild(groupPosition, childPosition));
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void updateUI()
    {
        List<Person> relativesArrayList = new ArrayList<>(client.findRelatives(chosenPerson.getPersonID()));
        List<Event> eventsArrayList = new ArrayList<>(client.getPeopleEventsMap().get(chosenPerson.getPersonID()));
        eventsArrayList = client.sortEventsByYear(eventsArrayList);

        //Groups needed for PersonAdapter
        List<String> groups = new ArrayList<>();
        groups.add("Events");
        groups.add("Relatives");

        //Get events and relatives based on chosenPerson
        eventsArrayList = retrieveEvents(eventsArrayList);
        relativesArrayList = retrievePeople(relativesArrayList);

        expandableListAdapter = new PersonListHandlerAdapter(this, groups, eventsArrayList, relativesArrayList, chosenPerson);
        listView.setAdapter(expandableListAdapter);
    }

    //Back btn
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    private List<Event> retrieveEvents(List<Event> eventsList)
    {
        List<Event> filteredEvents = new ArrayList<>();

        //Find events created in map and add it to filter list
        for (Event currEvent: eventsList) {
            if (client.getCreatedEventsInMap().containsValue(currEvent)){
                filteredEvents.add(currEvent);
            }
        }
        return filteredEvents;
    }

    private List<Person> retrievePeople(List<Person> personsList)
    {
        List<Person> filteredPeople = new ArrayList<>();

        //Find people created in map and add it to filter list
        for (Person person: personsList) {
            if (client.isPersonTypeInSettings(person)){
                filteredPeople.add(person);
            }
        }
        return filteredPeople;
    }
}
