package com.example.cs240_fms_2020;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;


public class Settings
{

    private boolean storyLines;
    private boolean familyLines;
    private boolean spouseLines;
    private boolean motherSideEvents;
    private boolean fatherSideEvents;
    private boolean femaleEvents;
    private boolean maleEvents;
    private List<String> allEvents;
    private List<String> displayedEvents;

    private int storyColor;
    private int familyColor;
    private int spouseColor;
    private int currMapType;


    public Settings()
    {
        storyLines = true;
        familyLines = true;
        spouseLines = true;
        motherSideEvents = true;
        fatherSideEvents = true;
        femaleEvents = true;
        maleEvents = true;

        allEvents = new ArrayList<>(Client.init().getEventTypes());
        displayedEvents = new ArrayList<>(Client.init().getEventTypes());

        //Get colors from resources
        storyColor = Color.BLUE;
        familyColor = Color.GREEN;
        spouseColor = Color.MAGENTA;
        currMapType = GoogleMap.MAP_TYPE_NORMAL;

    }

    public boolean containsEventType(String eventType)
    {
        eventType = eventType.toLowerCase();
        for (String event: displayedEvents) {
            if (event.toLowerCase().equals(eventType)){
                return true;
            }
        }
        return false;
    }

    public boolean isStoryLines()
    {
        return storyLines;
    }

    public void setStoryLines(boolean storyLines)
    {
        this.storyLines = storyLines;
    }

    public boolean isFamilyLines()
    {
        return familyLines;
    }

    public void setFamilyLines(boolean familyLines)
    {
        this.familyLines = familyLines;
    }

    public boolean isSpouseLines()
    {
        return spouseLines;
    }

    public void setSpouseLines(boolean spouseLines)
    {
        this.spouseLines = spouseLines;
    }

    public int getStoryColor()
    {
        return storyColor;
    }

    public int getFamilyColor()
    {
        return familyColor;
    }

    public int getSpouseColor()
    {
        return spouseColor;
    }

    public int getCurrMapType()
    {
        return currMapType;
    }

    public void setCurrMapType(int currMapType)
    {
        this.currMapType = currMapType;
    }

    public boolean isMotherSideEvents() {
        return motherSideEvents;
    }

    public void setMotherSideEvents(boolean motherSideEvents) {
        this.motherSideEvents = motherSideEvents;
    }

    public boolean isFatherSideEvents() {
        return fatherSideEvents;
    }

    public void setFatherSideEvents(boolean fatherSideEvents) {
        this.fatherSideEvents = fatherSideEvents;
    }

    public boolean isFemaleEvents() {
        return femaleEvents;
    }

    public void setFemaleEvents(boolean femaleEvents) {
        this.femaleEvents = femaleEvents;
    }

    public boolean isMaleEvents() {
        return maleEvents;
    }

    public void setMaleEvents(boolean maleEvents) {
        this.maleEvents = maleEvents;
    }



}
