package com.myfirstapp.myapplicationtest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.icu.text.DisplayContext;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by KUMUD on 10/18/2016.
 */

public class EventInfoDisplay extends Fragment {

    private TextView title;
    private TextView summary;
    private int color;
    private TextView year;
    private int startYear;
    private int endYear;
    private LinearLayout l;
    private Button edit;
    private Button delete;
    private Timeline parent;
    private Event event;
    public static int RESULT_DELETE_EVENT = 3;

    public EventInfoDisplay() {
    }

    public void setEvent(Event e) {
        event = e;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_info_display, container, false);

        verticalTimelineDiagram v = (verticalTimelineDiagram) rootView.findViewById(R.id.verticalTimeline);
        v.setUp(startYear, endYear, event.getEventDate(), color);

        title = (TextView) rootView.findViewById(R.id.eventTitle);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Bold.ttf");
        title.setTypeface(typeface);

        year = (TextView) rootView.findViewById(R.id.eventYear);
        year.setTypeface(typeface);
        year.setTextSize(40f);

        summary = (TextView) rootView.findViewById(R.id.eventSummary);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        summary.setTypeface(typeface);

        l = (LinearLayout) rootView.findViewById(R.id.infoBox);
        edit = (Button) rootView.findViewById(R.id.editButton);
        delete = (Button) rootView.findViewById(R.id.deleteButton);

        return rootView;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();

        color = args.getInt("event_color");
        startYear = args.getInt("start_year");
        endYear = args.getInt("end_year");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        title.setText(event.getOverview());
        ((GradientDrawable) title.getBackground().mutate()).setStroke(5, color);

        summary.setText(event.getDetailed());
        ((GradientDrawable) summary.getBackground().mutate()).setStroke(5, color);

        year.setText(Integer.toString(event.getEventDate()));
        ((GradientDrawable) year.getBackground().mutate()).setStroke(5, color);

        LayerDrawable layerDrawable = (LayerDrawable) l.getBackground();
        final GradientDrawable shape = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.gradientDrawable);
        shape.setStroke(5, color);

        ((GradientDrawable) edit.getBackground().mutate()).setStroke(5, color);
        ((GradientDrawable) delete.getBackground().mutate()).setStroke(5, color);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Do you really want to delete this event?")
                        .setMessage("This action is not reversible");


                builder.setPositiveButton("Delete Event", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.putExtra("event_databaseID", event.getID());
                        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_DELETE_EVENT, intent);
                        getFragmentManager().popBackStackImmediate();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }
        });
    }

}
