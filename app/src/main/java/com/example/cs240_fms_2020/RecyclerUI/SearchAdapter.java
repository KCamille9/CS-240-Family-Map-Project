package com.example.cs240_fms_2020.RecyclerUI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cs240_fms_2020.Activities.EventActivity;
import com.example.cs240_fms_2020.Activities.PersonActivity;
import com.example.cs240_fms_2020.Client;
import com.example.cs240_fms_2020.R;

import java.util.List;

import Model.Event;
import Model.Person;


public class SearchAdapter extends RecyclerView.Adapter<SearchHolder>
{

    private List<Object> objectList;
    private Context context;
    private LayoutInflater inflater;

    public SearchAdapter(List<Object> objectList, Context context)
    {
        this.context = context;
        this.objectList = objectList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.list_item_event, parent, false);
        return new SearchHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchHolder holder, int position)
    {
        final Object currObject = objectList.get(position);
        if (currObject instanceof Person)
        {
            //Start Person Activity
            holder.getLinearLayout().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    personClicked((Person) currObject);
                }
            });
            //Bind every found event and display it using holder
            holder.bindChosenPerson(currObject, context);
        }
        else{
            //Start Event Activity
            holder.getLinearLayout().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    eventClicked((Event) currObject);
                }
            });
            //Bind every found person and display it using holder
            holder.bindChosenEvent(currObject, context);
        }
    }

    @Override
    public int getItemCount()
    {
        return objectList.size();
    }

    private void eventClicked(Event event)
    {
        Intent intent = new Intent(context, EventActivity.class);
        intent.putExtra("Event", "Event");
        //Update client chosen event
        Client.init().setChosenEvent(event);
        context.startActivity(intent);
    }

    private void personClicked(Person person)
    {
        Intent intent = new Intent(context, PersonActivity.class);
        //Update client chosen person
        Client.init().setChosenPerson(person);
        context.startActivity(intent);
    }
}
