package com.example.cs240_fms_2020;

import android.graphics.Color;

public class EventColor extends Color
{
    private float color;

    public EventColor(String eventType)
    {
        constructColor(eventType);
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    public float getColor()
    {
        return color;
    }

    private void constructColor(String eventType)
    {
        color = Math.abs(eventType.hashCode() % 360);
    }
}
