package com.myfirstapp.myapplicationtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Timeline extends Fragment {


    private int eventIndex = 0;
    public int backgroundIndex = 0;
    public static final int REQUEST_EVENT_EDITOR = 2;
    public static final int RESULT_DELETE_EVENT = 3;
    public String timelineName;
    private boolean isNewTimeline = true;
    private String imageLocation;
    private static final String EVENT_DATE = "event_date";
    private static final String EVENT_OVERVIEW = "event_overview";
    private static final String EVENT_DESCRIPTION = "event_description";
    private static final String TIMELINE_NAME = "timeline_name";
    private static final String IS_NEW = "isNew";
    private static final String TIMELINE_IMAGE_PATH = "timeline_image_path";
    private static final int RESULT_LOAD_IMG = 1;
   // private Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    public ArrayList<Event> events = new ArrayList<>();
    public ArrayList<BackgroundImageView> backgroundImages = new ArrayList<>();
    public ArrayList<String> fileNames = new ArrayList<>();
    public LinearLayout backgroundCarousel;
    private LinearLayout topRow;
    private LinearLayout bottomRow;
    private LinearLayout topRowLines;
    private LinearLayout bottomRowLines;
    public File imageDirectory = null;
    private Menu menu;
    public boolean allowSelectionImages = false;
    public ArrayList<BackgroundImageView> selectedBackgroundImages = new ArrayList<>();
    public boolean registerNextImageClick = false;
    public Timeline parent = this;
    private Event eventInFocus;

    public Timeline() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.timeline_layout, parent, false);
        setHasOptionsMenu(true);

        topRow = (LinearLayout) rootView.findViewById(R.id.topRow);
        bottomRow = (LinearLayout) rootView.findViewById(R.id.bottomRow);
        topRowLines = (LinearLayout) rootView.findViewById(R.id.topRowLines);
        bottomRowLines = (LinearLayout) rootView.findViewById(R.id.bottomRowLines);
        backgroundCarousel = (LinearLayout) rootView.findViewById(R.id.background_carousel);


        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();

        timelineName = args.getString(TIMELINE_NAME);
        isNewTimeline = args.getBoolean(IS_NEW);

        imageLocation = args.getString(TIMELINE_IMAGE_PATH);
        //imageDirectory = new File(imageLocation);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(timelineName);
        if (!isNewTimeline) {
            setUpTimeline();
            isNewTimeline = true;
       }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public void setUpTimeline() {

        for (BackgroundImageView b : backgroundImages) {
            backgroundCarousel.addView(b);
            makeImageClickable(b);
        }

        addEventsToTimeline(events.size(), 0);

    }


    public void selectImageFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMG);
    }

    @Nullable
    public BackgroundImage loadImageFromStorage(String path, Context c) {
        BackgroundImage backgroundImage;
        try {
            String[] blubber = imageDirectory.list();
            int x = blubber.length;
            File f = new File(imageDirectory, path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            backgroundImage = new BackgroundImage(c, b.getWidth(), b.getHeight(), b);
            return backgroundImage;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void saveToInternalStorage(Bitmap bitmapImage, String extension, int index) throws IOException {
        File image_path = new File(imageDirectory, "background_image_" + index + extension);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(image_path);
            if (extension.equals(".jpg") || extension.equals(".JPG")) {
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                fileNames.add(index, "background_image_" + index + extension);
            } else if (extension.equals(".png") || extension.equals(".PNG")) {
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fileNames.add(index, "background_image_" + index + extension);
            } else {
                Toast.makeText(getActivity(), "Sorry, this image can't be used", Toast.LENGTH_LONG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }

    }

    public void addEventsToTimeline(int numberOfBoxes, int startIndex) {
        addEventBoxes(numberOfBoxes);
        colorEventBoxes();
        addTextToEvents(startIndex);
    }

    public void addEventBoxes(int numberOfBoxes) {

        float boxLength = getResources().getDimension(R.dimen.event_box_length); //in pixels
        float eventStickHeight = getResources().getDimension(R.dimen.event_stick_height); //in pixels
        float marginRight = getResources().getDimension(R.dimen.marginRight);

        for (int i = 0; i < numberOfBoxes; i++) {
            TextView box = new TextView(getActivity());
            box.setWidth(((int) boxLength));
            box.setBackgroundResource(R.drawable.border);
            LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            boxParams.setMargins(0, 0, ((int) marginRight), 0);
            box.setPadding(10, 5, 10, 5);
            box.setLayoutParams(boxParams);
            box.setTextColor(Color.BLACK);
            box.setTextSize(16f);
            Typeface t = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Bold.ttf");
            box.setTypeface(t);

            box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventInfoDisplay e = new EventInfoDisplay();
                    int index;
                    int color;
                    if(topRow.indexOfChild(view) >= 0)
                    {
                        index = topRow.indexOfChild(view);
                        color = ((EventStick) topRowLines.getChildAt(index)).color;
                        index = 2 * index;
                    }
                    else {
                        index = bottomRow.indexOfChild(view);
                        color = ((EventStick) bottomRowLines.getChildAt(index)).color;
                        index = (2 * index) + 1;
                    }

                    Bundle args = new Bundle();
                    args.putInt("event_color", color);
                    args.putInt("start_year", events.get(0).getEventDate());
                    args.putInt("end_year", events.get(eventIndex-1).getEventDate());
                    e.setArguments(args);
                    //EventEditor editor = new EventEditor();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction().add(R.id.container, e).addToBackStack(null);
                    e.setTargetFragment(parent, RESULT_DELETE_EVENT);
                    fragmentTransaction.commit();
                    e.setEvent(events.get(index));

                    eventInFocus = events.get(index);
                }
            });

            EventStick e = new EventStick(getActivity());
            LinearLayout.LayoutParams stickParams = new LinearLayout.LayoutParams((int) boxLength, (int) eventStickHeight);
            stickParams.setMargins(0, 0, ((int) marginRight), 0);
            e.setLayoutParams(stickParams);

            if (eventIndex % 2 == 0) {
                topRow.addView(box);
                topRowLines.addView(e);

            } else {
                bottomRow.addView(box);
                bottomRowLines.addView(e);
            }
            eventIndex++;
        }

        colorEventBoxes();

    }

    public void colorEventBoxes() {
        int i = 0;
        int x = events.size();
        int backgroundColor;
        boolean isEnd = false;
        TextView eventBox;
        EventStick stick;
        float boxWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.coloring_padding), getResources().getDisplayMetrics());
        BackgroundImage backgroundImage;
        if (x != 0) {
            for (BackgroundImageView backgroundImageFrame : backgroundImages) {
                backgroundImage = backgroundImageFrame.backgroundImage;
                int eventsLength = 0;
                backgroundColor = backgroundImage.getColor();
                int imageWidth = (int) (0.65 * backgroundImage.getBackgroundImageWidth()); //in pixels
                while (eventsLength <= imageWidth) {
                    if (i < x) {
                        if (i == 0) {
                            eventBox = (TextView) topRow.getChildAt(0);
                            stick = (EventStick) topRowLines.getChildAt(0);
                            eventsLength += (int) getResources().getDimension(R.dimen.bottom_row_padding);

                        } else if (i % 2 == 0) {
                            eventBox = (TextView) topRow.getChildAt(i / 2);
                            stick = (EventStick) topRowLines.getChildAt(i / 2);

                        } else {
                            eventBox = (TextView) bottomRow.getChildAt((i - 1) / 2);
                            stick = (EventStick) bottomRowLines.getChildAt((i-1) / 2);

                        }

                        eventsLength += (int) boxWidth;  //in pixels
                        ((GradientDrawable) eventBox.getBackground().mutate()).setStroke(5, backgroundColor);
                        stick.color = backgroundColor;
                        stick.invalidate();
                        i++;
                    } else {
                        isEnd = true;
                        break;
                    }
                }
                if (isEnd) {
                    break;
                }
            }
        }
    }

    public void addTextToEvents(int start) {
        int length = events.size();
        for (int i = start; i < length; i++) {
            if (i % 2 == 0) {
                ((TextView) topRow.getChildAt(i / 2)).setText(events.get(i).getOverview());
            } else {
                ((TextView) bottomRow.getChildAt((i - 1) / 2)).setText(events.get(i).getOverview());
            }
        }
    }

    public void makeImageClickable(final BackgroundImageView b){
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allowSelectionImages) {

                    if (b.isChecked) {
                        b.isChecked(false);
                        selectedBackgroundImages.remove(view);
                    } else {
                        b.isChecked(true);
                        selectedBackgroundImages.add((BackgroundImageView) view);
                    }
                }

                if (registerNextImageClick) {
                    if(!(selectedBackgroundImages.indexOf(view) >= 0))
                    {
                        moveImagePosition(b);
                        allowSelectionImages = true;
                    }
                }
            }
        });
    }

    public void addImageToBackground(String picturePath, String extension) {
        Bitmap originalImage = BitmapFactory.decodeFile(picturePath);

        try {
            saveToInternalStorage(originalImage, extension, backgroundIndex);
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Sorry, this image can't be used", Toast.LENGTH_LONG);
        }

        BackgroundImage convertToBackgroundImage = new BackgroundImage(getActivity(), originalImage.getWidth(), originalImage.getHeight(), originalImage);
        BackgroundImageView backgroundImage = convertToBackgroundImage.createBackgroundImage(backgroundCarousel.getHeight());
        makeImageClickable(backgroundImage);
        if(selectedBackgroundImages.size() == 1)
        {
            int index = backgroundImages.indexOf(selectedBackgroundImages.get(0).backgroundImage);
            backgroundImages.add(index+1, backgroundImage);
            backgroundCarousel.addView(backgroundImage, index+1);
            unselectAllImages();
        }
        else
        {
            backgroundImages.add(backgroundImage);
            backgroundCarousel.addView(backgroundImage);
        }
        backgroundIndex++;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EVENT_EDITOR) {
            int eventDate = data.getIntExtra(EVENT_DATE, 0);
            String eventOverview = data.getStringExtra(EVENT_OVERVIEW);
            String eventDetailed = data.getStringExtra(EVENT_DESCRIPTION);
            Event event = new Event(eventOverview, eventDetailed, eventDate, timelineName);
            EventInfoStorage inserter = new EventInfoStorage(this.getActivity(), null, null, 1);
            inserter.addEvent(event);
            events.add(event);
            Collections.sort(events);
            int index = events.indexOf(event);
            addEventBoxes(1);
            addTextToEvents(index);
        } else if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK && data != null) {
            int bb = backgroundCarousel.getChildCount();
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            String extension = picturePath.substring(picturePath.lastIndexOf("."));
            cursor.close();
            addImageToBackground(picturePath, extension);
        }
        else if (requestCode == RESULT_DELETE_EVENT) {
            int id = data.getIntExtra("event_databaseID", 0);
            EventInfoStorage deleter = new EventInfoStorage(this.getActivity(), null, null, 1);
            deleter.deleteEvent(id);

            int index = events.indexOf(eventInFocus);
            int size = events.size();
            events.remove(index);

            if(size%2 == 1) {
                topRow.removeViewAt(topRow.getChildCount()-1);
                topRowLines.removeViewAt(topRowLines.getChildCount()-1);
            }
            else {
                bottomRow.removeViewAt(bottomRow.getChildCount()-1);
                bottomRowLines.removeViewAt(bottomRowLines.getChildCount()-1);
            }
            addTextToEvents(index);
            eventIndex--;

        }
    }

    public void moveImagePosition(int position)
    {
        for(BackgroundImageView b: selectedBackgroundImages)
        {
            int index = backgroundCarousel.indexOfChild(b);
            backgroundCarousel.removeView(b);
            backgroundImages.remove(index);
            String relocateImage = fileNames.remove(index);

            backgroundCarousel.addView(b, position);
            backgroundImages.add(position, b);
            fileNames.add(position, relocateImage);
        }

        unselectAllImages();
    }

    public void moveImagePosition(BackgroundImageView destination)
    {
        for(BackgroundImageView b: selectedBackgroundImages) {
            int index = backgroundCarousel.indexOfChild(b);
            backgroundCarousel.removeView(b);
            backgroundImages.remove(index);
            String relocateImage = fileNames.remove(index);

            int destIndex = backgroundCarousel.indexOfChild(destination);
            backgroundCarousel.addView(b, destIndex+1);
            backgroundImages.add(destIndex+1, b);
            fileNames.add(destIndex+1, relocateImage);
        }

        allowSelectionImages = true;
        unselectAllImages();
    }

    public void unselectAllImages(){
        for(BackgroundImageView b: selectedBackgroundImages)
        {
            b.isChecked(false);
        }

        selectedBackgroundImages.clear();
    }

    public void deleteImage()
    {
        for(BackgroundImageView b: selectedBackgroundImages)
        {
            int index = backgroundCarousel.indexOfChild(b);
            backgroundCarousel.removeView(b);
            backgroundImages.remove(index);

            String path = fileNames.get(index);
            File deleteImage = new File(imageDirectory, path);
            fileNames.remove(index);


        }

        selectedBackgroundImages.clear();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.menu_timeline, this.menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_event) {
            android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            EventEditor editor = new EventEditor();
            editor.setTargetFragment(this, REQUEST_EVENT_EDITOR);
            editor.show(getActivity().getSupportFragmentManager(), "Dialog Fragment");
            fragmentTransaction.commit();
            return true;
        }
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();

        File oldImageOrderList = new File(imageDirectory, "order.txt");
        oldImageOrderList.delete();

        File newImageOrderList = new File(imageDirectory, "order.txt");
        try {

            FileWriter writer = new FileWriter(newImageOrderList, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            for (String path : fileNames) {
                bufferedWriter.write(path);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

