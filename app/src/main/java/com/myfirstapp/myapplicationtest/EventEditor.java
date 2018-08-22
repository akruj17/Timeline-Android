package com.myfirstapp.myapplicationtest;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by KUMUD on 7/22/2016.
 */
public class EventEditor extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    private Button addEventButton;
    private EditText eventOverviewField;
    private EditText eventDescriptionField;
    private EditText eventYearField;
    private String eventOverview;
    private String eventDescription;
    private int eventDate;
    private TextView warningEventOverview;
    private TextView warningEventYear;
    private static final String EVENT_DATE = "event_date";
    private static final String EVENT_OVERVIEW = "event_overview";
    private static final String EVENT_DESCRIPTION = "event_description";
    public static final int REQUEST_CODE = 2;

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_editor_layout, parent, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        addEventButton = (Button) rootView.findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(this);

        eventOverviewField = ((EditText) rootView.findViewById(R.id.overviewText));
        eventDescriptionField = ((EditText) rootView.findViewById(R.id.eventDescription));
        eventYearField = ((EditText) rootView.findViewById(R.id.eventDate));

        warningEventYear = ((TextView) rootView.findViewById(R.id.warningMessageYear));
        warningEventOverview = ((TextView) rootView.findViewById(R.id.warningMessageOverview));

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().getWindow().setSoftInputMode(
          //      WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void onResume() {
        super.onResume();

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = (int) (0.9 * dm.widthPixels);
        int height = (int) (0.9 * dm.heightPixels);

        Window window = getDialog().getWindow();
        window.setLayout(width, height);
        window.setGravity(Gravity.CENTER);
    }

    public void onClick(View v) {
        boolean addEventFlag = true;
        eventYearField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                warningEventYear.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        if (isEmpty(eventYearField)) {
            warningEventYear.setText("Please provide an event year");
            warningEventYear.setVisibility(View.VISIBLE);
            addEventFlag = false;
        }
        else if (!(NumberUtils.isNumber(eventYearField.getText().toString())))
        {
            warningEventYear.setText("Year entered is not valid");
            warningEventYear.setVisibility(View.VISIBLE);
            addEventFlag = false;
        }
        else if ((eventYearField.getText().toString().startsWith("-")))
        {
            warningEventYear.setText("Use checkbox for years BCE");
            warningEventYear.setVisibility(View.VISIBLE);
            addEventFlag = false;
        }

        if (isEmpty(eventOverviewField)){
            warningEventOverview.setText("Please provide an event overview");
            warningEventOverview.setVisibility(View.VISIBLE);
            addEventFlag = false;
            eventOverviewField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    warningEventOverview.setVisibility(View.INVISIBLE);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        if(addEventFlag){
            eventOverview = eventOverviewField.getText().toString();
            CheckBox isBCE = (CheckBox) getView().findViewById(R.id.checkBoxBCE);
            eventDate = Integer.parseInt(eventYearField.getText().toString()) * ((isBCE.isChecked())?(-1):1);
            eventDescription = eventDescriptionField.getText().toString();

            Intent intent = new Intent();
            intent.putExtra(EVENT_DATE, eventDate);
            intent.putExtra(EVENT_OVERVIEW, eventOverview);
            intent.putExtra(EVENT_DESCRIPTION, eventDescription);
            getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
            dismiss();
        }
    }


    public boolean isEmpty(EditText eventField) {
        return (eventField.getText().toString().trim().length() == 0);
    }
}