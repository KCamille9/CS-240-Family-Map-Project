package com.example.cs240_fms_2020.RecyclerUI;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.cs240_fms_2020.Client;
import com.example.cs240_fms_2020.R;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import Model.Event;
import Model.Person;

public class SearchHolder extends RecyclerView.ViewHolder
{

    private View convertView;

    private LinearLayout linearLayout;
    private TextView firstLineTxt;
    private TextView descriptionTxt;
    private ImageView icon;

    public SearchHolder(View itemView)
    {
        super(itemView);
        firstLineTxt = itemView.findViewById(R.id.event_list_description);
        descriptionTxt = itemView.findViewById(R.id.event_list_person);
        icon = itemView.findViewById(R.id.list_item_icon);
        linearLayout = itemView.findViewById(R.id.list_item_event_main_area);
        linearLayout.setClickable(true);
        convertView = itemView;
    }

    public LinearLayout getLinearLayout()
    {
        return linearLayout;
    }

    //Display every found event to screen
    public void bindChosenEvent(Object currObject, Context context)
    {
        final Event event = (Event) currObject;

        showEventDescription(event);

        Drawable eventIcon = new IconDrawable(context,FontAwesomeIcons.fa_map_marker).colorRes(R.color.green).sizeDp(40);
        icon.setImageDrawable(eventIcon);
    }

    private void showEventDescription(Event event)
    {
        String eventDescription = event.getEventType();
        eventDescription += ", " + event.getCity();
        eventDescription += ", " + event.getCountry();
        eventDescription += " " + event.getYear();
        firstLineTxt.setText(eventDescription);

        Client client = Client.init();

        Person currPerson = client.getPeopleMap().get(event.getPersonID());
        String personInfo = currPerson.getFirstName() + " " + currPerson.getLastName();
        descriptionTxt.setText(personInfo);
    }

    //Display every found person to screen
    public void bindChosenPerson(Object currObject, Context context)
    {
        Person currPerson = (Person) currObject;

        showPersonDescription(currPerson, context);
    }

    private void showPersonDescription(Person currPerson, Context context)
    {
        String personInfo = currPerson.getFirstName() + " " + currPerson.getLastName();
        firstLineTxt.setText(personInfo);
        descriptionTxt.setVisibility(View.INVISIBLE);
        if (currPerson.getGender().toLowerCase().equals("m") || currPerson.getGender().toLowerCase().equals("male"))
        {
            icon.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_male).colorRes(R.color.blue).sizeDp(40));
        }
        else
        {
            icon.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_female).colorRes(R.color.pink).sizeDp(40));
        }
    }

}
