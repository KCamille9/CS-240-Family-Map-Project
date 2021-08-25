package com.example.cs240_fms_2020.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.cs240_fms_2020.Activities.PersonActivity;
import com.example.cs240_fms_2020.Activities.SearchActivity;
import com.example.cs240_fms_2020.Activities.SettingActivity;
import com.example.cs240_fms_2020.Client;
import com.example.cs240_fms_2020.EventColor;
import com.example.cs240_fms_2020.R;
import com.example.cs240_fms_2020.Settings;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Event;
import Model.Person;


public class MapFragment extends Fragment implements OnMapReadyCallback
{

    private GoogleMap myGoogleMap;
    private SupportMapFragment mapFragment;
    private Map<Marker, Event> markersMap;
    private Map<String, Event> eventsDrawnOnMap;
    private Marker chosenMarker;

    private List<Polyline> lineList;

    private TextView nameTxt;
    private TextView eventTxt;
    private TextView eventYearTxt;

    private ImageView icon;
    private boolean isEvent;

    private Client client = Client.init();

    public MapFragment()
    {}

    public MapFragment(String eventId)
    {
        isEvent = eventId != null;
    }

    View.OnClickListener onClickText = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            textClicked();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(!isEvent);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle)
    {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View v = layoutInflater.inflate(R.layout.fragment_map, viewGroup, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        nameTxt = v.findViewById(R.id.person_name);
        eventTxt = v.findViewById(R.id.event_description);
        eventYearTxt = v.findViewById(R.id.event_year);
        icon = v.findViewById(R.id.map_icon);

        lineList = new ArrayList<>();

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //if just logged in or no marker was clicked
        if (myGoogleMap != null && markersMap != null)
        {
            clearMap();
            Event markedEvent = markersMap.get(chosenMarker);
            putMarkers(myGoogleMap);

            if (chosenMarker == null) {
                if (!markersMap.containsValue(markedEvent)) {
                    removeLines();
                }
            }
            myGoogleMap.setMapType(client.getSettings().getCurrMapType());
        }

        //if marker was clicked before going into another view
        if (chosenMarker != null && markersMap != null)
        {
            drawLines();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                searchClicked();
                return true;
            case R.id.menu_item_settings:
                settingsClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void searchClicked()
    {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
    }

    private void settingsClicked()
    {
        Intent intent = new Intent(getActivity(), SettingActivity.class);
        startActivity(intent);
    }

    private void textClicked()
    {
        Intent intent = new Intent(getActivity(), PersonActivity.class);
        Person person = client.getPeopleMap().get(markersMap.get(chosenMarker).getPersonID());
        client.setChosenPerson(person);
        startActivity(intent);
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        putMarkers(googleMap);
    }

    private void putMarkers(GoogleMap googleMap)
    {
        chosenMarker = null;
        markersMap = new HashMap<>();

        //Get all events from curr user info and assign it a color
        //Events num might change depending on user settings
        Map<String, EventColor> allMapColors = client.getEventColorsMap();
        eventsDrawnOnMap = client.getCreatedEventsInMap();

        myGoogleMap = googleMap;
        myGoogleMap.setMapType(Client.init().getSettings().getCurrMapType());

        myGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker)
            {
                markerClicked(marker);
                return true;
            }
        });

        //Set every found event as a marker in map
        for (Event currEvent : eventsDrawnOnMap.values())
        {
            LatLng currentPosition = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
            EventColor eventColor = allMapColors.get(currEvent.getEventType().toLowerCase());

            //Create marker and assign color depending on event type
            Marker marker = myGoogleMap.addMarker(new MarkerOptions().position(currentPosition)
                    .icon(BitmapDescriptorFactory.defaultMarker(eventColor.getColor()))
                    .title(currEvent.getEventType()));
            markersMap.put(marker, currEvent);

            if (client.getChosenEvent() == currEvent)
            {
                chosenMarker = marker;
            }
        }

        //Center camera on clicked marker
        if (chosenMarker != null && isEvent)
        {
            myGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(chosenMarker.getPosition()));
            markerClicked(chosenMarker);
        }
    }



    private void markerClicked(Marker marker)
    {
        //Display info of event and person in the section below
        Event currEvent = markersMap.get(marker);
        Person currPerson = client.getPeopleMap().get(currEvent.getPersonID());
        String newName = currPerson.getFirstName() + " " + currPerson.getLastName();
        String eventInfo = currEvent.getEventType() + ": " + currEvent.getCity() + ", " + currEvent.getCountry();
        String yearInfo = "(" + currEvent.getYear() + ")";

        nameTxt.setText(newName);
        nameTxt.setVisibility(View.VISIBLE);
        nameTxt.setOnClickListener(onClickText);

        eventTxt.setText(eventInfo);
        eventTxt.setVisibility(View.VISIBLE);
        eventTxt.setOnClickListener(onClickText);

        eventYearTxt.setText(yearInfo);
        eventYearTxt.setVisibility(View.VISIBLE);
        eventYearTxt.setOnClickListener(onClickText);

        if (currPerson.getGender().toLowerCase().equals("m")){
            icon.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).colorRes(R.color.blue).sizeDp(40));
        }
        else {
            icon.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).colorRes(R.color.pink).sizeDp(40));
        }
        icon.setVisibility(View.VISIBLE);
        icon.setOnClickListener(onClickText);

        chosenMarker = marker;
        client.setChosenEvent(currEvent);
        drawLines();
    }


    private void drawLines()
    {
        Settings settings = Client.init().getSettings();

        removeLines();

        if (settings.isStoryLines()){
            startStoryLines();
        }
        if (settings.isSpouseLines()){
            startSpouseLines();
        }
        if (settings.isFamilyLines()){
            startFamilyLines();
        }
    }

    private void removeLines()
    {
        for (Polyline currLine : lineList) {
            currLine.remove();
        }
        lineList = new ArrayList<Polyline>();
    }

    private void startStoryLines() {
        Client client = Client.init();
        Event currEvent = markersMap.get(chosenMarker);
        Person currPerson = client.getPeopleMap().get(currEvent.getPersonID());
        List<Event> eventsList = client.getPeopleEventsMap().get(currPerson.getPersonID());
        eventsList = client.sortEventsByYear(eventsList);

        if (!client.getSettings().containsEventType(currEvent.getEventType())) {
            return;
        }

        firstStoryLine(eventsList);
    }

    private void firstStoryLine(List<Event> events)
    {
        int i = 0;
        while (i < events.size() - 1) {
            if (client.getCreatedEventsInMap().containsValue(events.get(i))) {
                Event event = events.get(i);
                i++;

                secondStoryLine(event, events, i);
            }
            else {
                i++;
            }
        }
    }

    private void secondStoryLine(Event eventOne, List<Event> events, int index)
    {
        while (index < events.size()) {

            if (client.getCreatedEventsInMap().containsValue(events.get(index))) {
                Event eventTwo = events.get(index);

                Polyline newLine = myGoogleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(eventOne.getLatitude(), eventOne.getLongitude()),
                                new LatLng(eventTwo.getLatitude(), eventTwo.getLongitude()))
                        .color(client.getSettings().getStoryColor()));
                lineList.add(newLine);

                return;
            }
            index++;
        }
    }

    private void startSpouseLines()
    {
        Event currEvent = markersMap.get(chosenMarker);
        Person currPerson = client.getPeopleMap().get(currEvent.getPersonID());
        List<Event> eventsList = client.getPeopleEventsMap().get(currPerson.getSpouseID());

        //if spouse found
        if(eventsList != null)
        {
            eventsList = client.sortEventsByYear(eventsList);
            Settings settings = client.getSettings();

            if (settings.containsEventType(currEvent.getEventType())) {
                for (int i = 0; i < eventsList.size(); i++) {
                    if (client.getCreatedEventsInMap().containsValue(eventsList.get(i))) {
                        Event correctEvent = eventsList.get(i);

                        Polyline newLine = myGoogleMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(correctEvent.getLatitude(), correctEvent.getLongitude()),
                                        new LatLng(currEvent.getLatitude(), currEvent.getLongitude()))
                                .color(client.getSettings().getSpouseColor()));
                        lineList.add(newLine);

                        return;
                    }
                }
            }
        }
    }

    private void startFamilyLines()
    {
        Event currEvent = markersMap.get(chosenMarker);
        Person currPerson = client.getPeopleMap().get(currEvent.getPersonID());

        familyLineHelper(currPerson, currEvent, 10);
    }

    private void familyLineHelper(Person person, Event event, int generation)
    {
        //Do the father ancestry
        if (person.getFatherID() != null) {
            familyLineHelperFather(person, event, generation);
        }

        //Do the mother ancestry
        if (person.getMotherID() != null){
            familyLineHelperMother(person, event, generation);
        }
    }

    private void familyLineHelperFather(Person person, Event event, int generation)
    {
        List<Event> eventsList = client.getPeopleEventsMap().get(person.getFatherID());
        eventsList = client.sortEventsByYear(eventsList);

        for (int i = 0; i < eventsList.size(); i++) {
            if (eventsDrawnOnMap.containsValue(eventsList.get(i))) {
                Event correctEvent = eventsList.get(i);

                Polyline newLine = myGoogleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(event.getLatitude(), event.getLongitude()),
                                new LatLng(correctEvent.getLatitude(), correctEvent.getLongitude()))
                        .color(client.getSettings().getFamilyColor())
                        .width(generation));
                lineList.add(newLine);

                Person father = client.getPeopleMap().get(person.getFatherID());
                familyLineHelper(father, correctEvent, generation / 2);
                return;
            }
        }

    }

    private void familyLineHelperMother(Person person, Event event, int generation)
    {
        List<Event> eventsList = client.getPeopleEventsMap().get(person.getMotherID());
        eventsList = client.sortEventsByYear(eventsList);

        for (int i = 0; i < eventsList.size(); i++) {
            if (eventsDrawnOnMap.containsValue(eventsList.get(i))) {
                Event correctEvent = eventsList.get(i);

                Polyline newLine = myGoogleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(event.getLatitude(), event.getLongitude()),
                                new LatLng(correctEvent.getLatitude(), correctEvent.getLongitude()))
                        .color(client.getSettings().getFamilyColor())
                        .width(generation));
                lineList.add(newLine);

                Person mother = client.getPeopleMap().get(person.getMotherID());
                familyLineHelper(mother, correctEvent, generation / 2);
                return;
            }
        }
    }

    private void clearMap()
    {
        for (Marker currMarker: markersMap.keySet()) {
            currMarker.remove();
        }
    }

}


