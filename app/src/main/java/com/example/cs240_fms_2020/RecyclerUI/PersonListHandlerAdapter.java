package com.example.cs240_fms_2020.RecyclerUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.cs240_fms_2020.Client;
import com.example.cs240_fms_2020.R;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.List;

import Model.Event;
import Model.Person;


public class PersonListHandlerAdapter extends BaseExpandableListAdapter
{

    private Context context;
    private List<String> groups;
    private List<Event> eventsList;
    private List<Person> peopleList;
    private Person chosenPerson;

    private TextView firstLineTxt;
    private TextView secondLineTxt;
    private ImageView listIcon;

    private Client client = Client.init();

    public PersonListHandlerAdapter(Context context, List<String> groups,
                                    List<Event> eventsList, List<Person> peopleList,
                                    Person chosenPerson) {
        this.context = context;
        this.groups = groups;
        this.eventsList = eventsList;
        this.peopleList = peopleList;
        this.chosenPerson = chosenPerson;
    }

    @Override
    public int getGroupCount()
    {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        if (groupPosition == 0){
            return eventsList.size();
        }
        else{
            return peopleList.size();
        }
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        if (groupPosition == 0){
            return eventsList;
        }
        else{
            return peopleList;
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        if(groupPosition == 0){
            return eventsList.get(childPosition);
        }
        else{
            return peopleList.get(childPosition);
        }
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        String groupTitle = groups.get(groupPosition);

        if (convertView == null)
        {
            //Use the list_header_event view
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_header_event, null);
        }

        TextView header = convertView.findViewById(R.id.event_header);
        header.setText(groupTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            //Use the list_item_event view
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_event, null);
        }

        firstLineTxt = convertView.findViewById(R.id.event_list_description);
        secondLineTxt = convertView.findViewById(R.id.event_list_person);
        listIcon = convertView.findViewById(R.id.list_item_icon);

        //Set the event group as the first one
        if (groupPosition == 0)
        {
            Event currEvent = (Event) getChild(groupPosition, childPosition);

            listIcon.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_map_marker).colorRes(R.color.green));
            refresh(currEvent, null);

        }
        //Set the relatives group
        else
            {
            Person currPerson = (Person) getChild(groupPosition, childPosition);

            if (currPerson.getGender().toLowerCase().equals("m")){
                listIcon.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_male).colorRes(R.color.blue).sizeDp(40));
            }
            else {
                listIcon.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_female).colorRes(R.color.pink).sizeDp(40));
            }

            refresh(null, currPerson);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }


    private void refresh(Event event, Person person)
    {
        if (person == null)
        {
            String eventDescription = event.getEventType();
            eventDescription += ", " + event.getCity();
            eventDescription += ", " + event.getCountry();
            eventDescription += " " + event.getYear();

            firstLineTxt.setText(eventDescription);

            Person currPerson = client.getPeopleMap().get(event.getPersonID());
            String personInfo = currPerson.getFirstName() + " " + currPerson.getLastName();

            secondLineTxt.setText(personInfo);
        }
        else {
            String personDescription = person.getFirstName() + " " + person.getLastName();
            String relationshipDescription = findRelationshipType(person);

            firstLineTxt.setText(personDescription);
            secondLineTxt.setText(relationshipDescription);
        }
    }

    private String findRelationshipType(Person persons)
    {
        if (chosenPerson.getSpouseID().equals(persons.getPersonID())) {
            return "Spouse";
        }

        if (persons.getFatherID() != null && persons.getMotherID() != null) {
            if (persons.getFatherID().equals(chosenPerson.getPersonID()) ||
                    persons.getMotherID().equals(chosenPerson.getPersonID())) {
                return "Child";
            }
        }

        if (chosenPerson.getMotherID() != null && chosenPerson.getMotherID() != null) {
            if (chosenPerson.getFatherID().equals(persons.getPersonID())) {
                return "Father";
            }
            else if (chosenPerson.getMotherID().equals(persons.getPersonID())) {
                return "Mother";
            }
        }
        return "Error";
    }

}
