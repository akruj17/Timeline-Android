package com.myfirstapp.myapplicationtest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity implements Title_screen.initiateTimeline,
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static ArrayList<String> timelineNames = new ArrayList<>();
    private SharedPreferences prefs;
    private Menu navigationViewMenu;
    private static final String PREFS_NAME = "TIMELINE_INFO";
    private static final String PREFS_STRING = "TIMELINE_NAME";
    private static final String TIMELINE_NAME = "timeline_name";
    private static final String TIMELINE_IMAGE_PATH = "timeline_image_path";
    private static final String IS_NEW = "isNew";
    private Toolbar toolbar;
    private Timeline timeline;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    public ProgressBar loadingCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        prefs = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (prefs.contains(PREFS_STRING)) {
            String jsonRetrieval = prefs.getString(PREFS_STRING, null);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            timelineNames = gson.fromJson(jsonRetrieval, type);
        }

        Title_screen titleFragment = new Title_screen();
        Bundle args = new Bundle();
        args.putStringArrayList("timeline_names",timelineNames);
        titleFragment.setArguments(args);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, titleFragment);
        fragmentTransaction.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        loadingCircle = (ProgressBar)findViewById(R.id.progressBar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationViewMenu = navigationView.getMenu();
        createTimelineList();

    }

    public void onClick(View v) {
        final Toolbar instruction = (Toolbar) findViewById(R.id.switch_images_instructions_bar);
        int numImages = timeline.selectedBackgroundImages.size();
        if (v.getId() == R.id.add_image_to_background) {
            if(numImages > 1)
            {
                instruction.setTitle("Add an image to add to the end, or select an image to add a new image after");
                instruction.setVisibility(View.VISIBLE);
            }
            else
            {
                timeline.selectImageFromGallery();
            }

        }
        else if (v.getId() == R.id.move_to_front) {
            if(numImages == 0)
            {
                instruction.setTitle("Choose image(s) to move to the beginning of the timeline");
                instruction.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                instruction.setVisibility(View.VISIBLE);
            }
            else
            {
                timeline.moveImagePosition(0);
            }
        }
        else if (v.getId() == R.id.move_to_after)
        {
            if(numImages == 0)
            {
                instruction.setTitle("Choose image(s) to reposition");
                instruction.setVisibility(View.VISIBLE);
            }
            else
            {
                instruction.setTitle("Choose where to move your selected images after");
                instruction.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                instruction.setVisibility(View.VISIBLE);
                timeline.allowSelectionImages = false;
                timeline.registerNextImageClick = true;
            }
        }
        else if (v.getId() == R.id.move_to_end) {
            if(numImages == 0)
            {
                instruction.setTitle("Choose image(s) to move to the end of the timeline");
                instruction.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                instruction.setVisibility(View.VISIBLE);
            }
            else
            {
                timeline.moveImagePosition(timeline.backgroundIndex-1);
            }
        }
        else if (v.getId() == R.id.delete_image) {
            if(numImages == 0)
            {
                instruction.setTitle("Choose image(s) to delete");
                instruction.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                instruction.setVisibility(View.VISIBLE);
            }
            else
            {
                timeline.deleteImage();
            }
        }
        else if (v.getId() == R.id.done) {
            LinearLayout overall = (LinearLayout) findViewById(R.id.overall_timeline);
            overall.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);

            timeline.allowSelectionImages = false;
            timeline.unselectAllImages();
            instruction.setVisibility(View.GONE);

            timeline.colorEventBoxes();

        }

        if(instruction.getVisibility() ==View.VISIBLE) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    instruction.setVisibility(View.GONE);
                }
            }, 2000);
        }
    }

    public boolean verifyNewTimelineName(EditText name, final TextView errorMessage)
    {
        String title = name.getText().toString();

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                errorMessage.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if(title.isEmpty())
        {
            errorMessage.setText("Please enter a name");
            errorMessage.setVisibility(View.VISIBLE);
            return false;
        }
        else if(timelineNames.contains(title))
        {
            errorMessage.setText("A timeline with this name already exists");
            errorMessage.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            return true;
        }
    }

    public void createNewTimeline(String timelineName) {

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        timelineNames.add(0,timelineName);
        Bundle args = new Bundle();
        args.putString(TIMELINE_NAME, timelineName);
        args.putBoolean(IS_NEW, true);
        timeline = new Timeline();

        File root_directory =this.getDir("timeline_images", Context.MODE_PRIVATE);
        File imageDirectory = new File(root_directory, timelineName);
        imageDirectory.mkdir();

        args.putString(TIMELINE_IMAGE_PATH, imageDirectory.getAbsolutePath());
        timeline.setArguments(args);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, timeline);
        fragmentTransaction.commit();
        createTimelineList();

    }

    public void createTimelineList() {
            navigationViewMenu.clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);
            for (String name : timelineNames) {
                navigationViewMenu.add(R.id.timeline_list,Menu.NONE,Menu.NONE,name);
            }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            onBackPressed();
        }
        if (toggle.onOptionsItemSelected(item)){
            Toast.makeText(this, "LENGTH", Toast.LENGTH_LONG).show();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.background_image) {
            LinearLayout overall = (LinearLayout) findViewById(R.id.overall_timeline);
            overall.setVisibility(View.INVISIBLE);
            toolbar.setVisibility(View.INVISIBLE);
            timeline.allowSelectionImages = true;

        }

        return super.onOptionsItemSelected(item);
    }

    Handler loadingFinished = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message msg) {
            loadingCircle.setVisibility(View.GONE);
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, timeline);

            fragmentTransaction.commit();

        }
    };

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final String title = item.getTitle().toString();

        if (title.equals("New Timeline")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View root = inflater.inflate(R.layout.new_timeline_dialog, null);

            builder.setView(root)
                    .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            final AlertDialog dialog = builder.create();
            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Boolean closeDialog = false;
                    closeDialog = verifyNewTimelineName(((EditText)root.findViewById(R.id.newTimelineAlertBox)),
                            ((TextView)root.findViewById(R.id.newTimelineAlert)));

                    if(closeDialog) {
                        String name = ((EditText) root.findViewById(R.id.newTimelineAlertBox)).getText().toString();
                        dialog.dismiss();
                        createNewTimeline(name);
                    }
                }
            });


            return true;

        } else {
            Bundle args = new Bundle();
            args.putString(TIMELINE_NAME, title);
            args.putBoolean(IS_NEW, false);

            File root_directory = this.getDir("timeline_images", MODE_PRIVATE);
            File imageDirectory = new File(root_directory, title);
            final String imageDirectoryPath = imageDirectory.getAbsolutePath();
            args.putString(TIMELINE_IMAGE_PATH, imageDirectoryPath);

            timeline = new Timeline();
            timeline.setArguments(args);
            timeline.imageDirectory = new File(imageDirectoryPath);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            loadingCircle.setVisibility(View.VISIBLE);

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    setUpTimeline(title, imageDirectoryPath);
                    loadingFinished.sendEmptyMessage(0);
                }
            };
            Thread setUp = new Thread(r);
            setUp.start();

            return true;
        }
    }

    public void setUpTimeline(String timelineName, String imageDirectory) {

                FrameLayout f = (FrameLayout) findViewById(R.id.container);
                int height;

                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                {
                    height = f.getHeight();
                }
                else
                {
                    height = f.getWidth()- toolbar.getHeight();
                }
                timeline.timelineName = timelineName;

                EventInfoStorage retriever = new EventInfoStorage(this, null, null, 1);
                Collections.addAll(timeline.events, retriever.findEvents(timelineName));
                File imageOrderList = new File(imageDirectory, "order.txt");

                try {
                    FileReader reader = new FileReader(imageOrderList);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String imagePath;
                    while ((imagePath = bufferedReader.readLine()) != null) {
                        timeline.backgroundImages.add((timeline.loadImageFromStorage(imagePath, this)).createBackgroundImage(height));
                        timeline.backgroundIndex++;
                        timeline.fileNames.add(imagePath);
                    }
                    reader.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    public void onStop() {
        super.onStop();
        if (!(timelineNames.isEmpty())) {
            Gson gson = new Gson();
            String jsonText = gson.toJson(timelineNames);
            prefs = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREFS_STRING, jsonText);
            editor.apply();

        }
    }
}
