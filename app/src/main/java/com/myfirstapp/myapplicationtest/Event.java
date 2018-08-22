package com.myfirstapp.myapplicationtest;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by KUMUD on 7/22/2016.
 */
public class Event implements Comparable<Event> {

    private int id;
    private String eventOverview;
    private String eventDetails;
    private int eventDate;
    private String timelineName;

    public Event()
    {

    }

    public Event(String eventOverview, String eventDetails, int eventDate, String timelineName)
    {
        this.eventOverview = eventOverview;
        this.eventDetails = eventDetails;
        this.eventDate = eventDate;
        this.timelineName = timelineName;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public void setOverview(String eventOverview){
        this.eventOverview = eventOverview;
    }

    public String getOverview() {
        return this.eventOverview;
    }

    public void setDetailed(String eventDetails){
        this.eventDetails = eventDetails;
    }

    public String getDetailed()
    {
        return this.eventDetails;
    }

    public void setEventDate(int eventDate)
    {
        this.eventDate = eventDate;
    }

    public int getEventDate()
    {
        return this.eventDate;
    }

    public void setTimelineName(String timelineName) { this.timelineName = timelineName;}

    public String getTimelineName() { return this.timelineName;}


    @Override
    public int compareTo(Event compareEventDate) {
        if(this.eventDate > compareEventDate.eventDate)
        {
            return 1;
        }
        else
        {
            return -1;
        }

    }





}
