package com.myfirstapp.myapplicationtest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**

 *
 */
public class Title_screen extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static ArrayList<String> timelineNames = new ArrayList<String>();
    private String title;
    private Button addTimeline;
    private Button openTimeline;
    initiateTimeline mInitiate;

    public interface initiateTimeline {
        public void createNewTimeline(String timelineName);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();

        timelineNames = args.getStringArrayList("timeline_names");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mInitiate = (initiateTimeline) getActivity();
        } catch(ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement initiateTimeline");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.title_screen, parent, false);

        addTimeline = (Button) rootView.findViewById(R.id.new_timeline);
        addTimeline.setOnClickListener(this);

        openTimeline = (Button) rootView.findViewById(R.id.open_timeline);
        openTimeline.setOnClickListener(this);


        return rootView;
    }



    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.new_timeline) {
            final EditText name = (EditText) getView().findViewById(R.id.newTimelineName);
            name.setVisibility(View.VISIBLE);

            Button addTimeline = (Button) v;
            addTimeline.setText("ADD");
            addTimeline.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    boolean initiate;
                    initiate = ((MainActivity)getActivity()).verifyNewTimelineName(name, ((TextView)getView().findViewById(R.id.warningMessage)));

                    if(initiate)
                        ((MainActivity)getActivity()).createNewTimeline(name.getText().toString());
                }
            });

        }
        else if(v.getId() == R.id.open_timeline)
        {
            DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
        }
    }


}

