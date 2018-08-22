package com.myfirstapp.myapplicationtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by KUMUD on 7/22/2016.
 */
public class EventInfoStorage extends SQLiteOpenHelper
{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "eventInfoStorage.db";
    private static String TABLE_TIMELINE = "timelineInfo";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_OVERVIEW = "eventoverview";
    public static final String COLUMN_DESCRIPTION = "eventdescription";
    public static final String COLUMN_DATE = "eventdate";
    public static final String COLUMN_TIMELINE_NAME = "timelinename";
    private Context context;

    private static String CREATE_TABLE_TIMELINE = "CREATE TABLE " + TABLE_TIMELINE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_OVERVIEW + " TEXT," + COLUMN_DESCRIPTION + " TEXT," + COLUMN_DATE + " INTEGER," + COLUMN_TIMELINE_NAME + " TEXT" + ")";

    public EventInfoStorage(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TIMELINE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addEvent(Event event) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_OVERVIEW, event.getOverview());
        values.put(COLUMN_DESCRIPTION, event.getDetailed());
        values.put(COLUMN_DATE, event.getEventDate());
        values.put(COLUMN_TIMELINE_NAME, event.getTimelineName());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_TIMELINE, null, values);
        db.close();
    }

    public Event[] findEvents(String timelineName)
    {
        String query = "Select * FROM " + TABLE_TIMELINE + " WHERE " + COLUMN_TIMELINE_NAME + " = \"" + timelineName + "\"" + " ORDER BY " + COLUMN_DATE + " ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Event[] timelineEvents = new Event[cursor.getCount()];
        int i = 0;

        while (cursor.moveToNext()) {
            Event event = new Event();
            event.setID(Integer.parseInt(cursor.getString(0)));
            event.setOverview(cursor.getString(1));
            event.setDetailed(cursor.getString(2));
            event.setEventDate(cursor.getInt(3));
            event.setTimelineName(cursor.getString(4));
            timelineEvents[i] = event;
            i++;
        }

        cursor.close();

        db.close();
        return timelineEvents;
    }

    public void deleteEvent(int id) {

        String[] val = new String[] { String.valueOf(id)};
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TIMELINE, COLUMN_ID + " = ?", val);

        db.close();
    }

}