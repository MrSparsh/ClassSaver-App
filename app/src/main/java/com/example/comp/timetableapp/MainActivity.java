package com.example.comp.timetableapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    ArrayList<DataModel> dataModels;
    ListView listView;
    CustomAdapter customAdapter;
    SQLiteDatabase inputDetailsDatabase;
    SQLiteDatabase attendanceManagerDatabase;


    String tableName;

    //Function for initialising tableName to be of current day

    public void currentDay(){

        tableName = "";

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (Calendar.MONDAY == dayOfWeek) {
            tableName = "mondaytimetable";
        } else if (Calendar.TUESDAY == dayOfWeek) {
            tableName = "tuesdaytimetable";
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            tableName = "wednesdaytimetable";
        } else if (Calendar.THURSDAY == dayOfWeek) {
            tableName = "thursdaytimetable";
        } else if (Calendar.FRIDAY == dayOfWeek) {
            tableName = "fridaytimetable";
        } else if (Calendar.SATURDAY == dayOfWeek) {
            tableName = "saturdaytimetable";
        } else if (Calendar.SUNDAY == dayOfWeek) {
            tableName = "sundaytimetable";
        }
    }

    // Creating List View Containing today's class

    public void createList() {
        try {

            Date time = Calendar.getInstance().getTime();

            //Initialise tableName to be of current Day
            currentDay();

            //Initialising a cursor to the current day database
            Cursor c = inputDetailsDatabase.rawQuery("SELECT * FROM "+tableName+" ORDER BY fromtime ASC", null);
            c.moveToFirst();


            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {

                    //Getting indices of variables of database from cursor
                    int subjectIndex = c.getColumnIndex("subjectname");
                    int fromTimeIndex = c.getColumnIndex("fromtime");
                    int toTimeIndex = c.getColumnIndex("totime");
                    int venueNameIndex = c.getColumnIndex("venue");

                    //Getting fromtime and totime from database
                    int from_time = c.getInt(fromTimeIndex);
                    int to_time = c.getInt(toTimeIndex);

                    //Converting fromtime and totime in database into hours and minutes
                    String hour_from_time = Integer.toString(from_time / 60);
                    String minute_from_time = Integer.toString(from_time % 60);
                    String hour_to_time = Integer.toString(to_time / 60);
                    String minute_to_time = Integer.toString(to_time % 60);

                    //Converting hours and minutes of both to time and from time to be of 00 format
                    if (hour_from_time.length() < 2) {
                        hour_from_time = "0" + hour_from_time;
                    }
                    if (minute_from_time.length() < 2) {
                        minute_from_time = "0" + minute_from_time;
                    }

                    if (hour_to_time.length() < 2) {
                        hour_to_time = "0" + hour_to_time;
                    }
                    if (minute_to_time.length() < 2) {
                        minute_to_time = "0" + minute_to_time;
                    }

                    //Setting the variables of database into list using custom adapter
                    dataModels.add(new DataModel(c.getString(subjectIndex), hour_from_time + ":" + minute_from_time + " - " + hour_to_time + ":" + minute_to_time, c.getString(venueNameIndex)));
                    customAdapter = new CustomAdapter(dataModels, getApplicationContext());
                    listView.setAdapter(customAdapter);

                    //Changing cursor to point to next row
                    c.moveToNext();
                }
            }
        }
        catch (Exception e) {
            Toast.makeText(this, "Failed1", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Creating database and all tables in database if they don't exist
        inputDetailsDatabase = openOrCreateDatabase("finaltime17",MODE_PRIVATE,null);
        String[] timeTableContent = {"mondaytimetable", "tuesdaytimetable", "wednesdaytimetable", "thursdaytimetable", "fridaytimetable", "saturdaytimetable","sundaytimetable"};
        for(int i=0;i<7;i++)
        {
            inputDetailsDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+timeTableContent[i]+" (subjectname VARCHAR, venue VARCHAR, fromtime INT[4], totime INT[4])");
        }
        attendanceManagerDatabase = this.openOrCreateDatabase("amd4",MODE_PRIVATE,null);
        attendanceManagerDatabase.execSQL("CREATE TABLE IF NOT EXISTS amt ( subjectname VARCHAR , present INT[4] , absent INT[4] , total INT[4] , count INT[4] )");

        listView=(ListView)findViewById(R.id.mainListView);

        //Setting the Navigation Bar

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    //Adding functionality to backbutton to show a disclaimer before exiting the app
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            //Building Exit Alert dialog

            final AlertDialog.Builder alertDialog =new AlertDialog.Builder(this);
            alertDialog.setIcon(R.drawable.ic_mood_bad_black);
            alertDialog.setTitle("Are You Sure?");
            alertDialog.setMessage("Are you sure you want to exit..");
            alertDialog.setCancelable(true);

            //Setting name and functionality to negative button

            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                }
            });

            //Setting name and functionality to positive button

            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            //Displaying alert Dialog Box

            alertDialog.show();
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

        //noinspection SimplifiableIfStatement

        //To Settings Activity
        if (id == R.id.action_settings) {
            Intent intent =new Intent(MainActivity.this,SettingActivity.class);
            startActivity(intent);
            return true;
        }

        // Creating and setting contents of About Alert Dialog
        if(id==R.id.about_app)
        {
            final AlertDialog.Builder alertDialog =new AlertDialog.Builder(this);
            alertDialog.setIcon(R.drawable.time_table_icon);
            alertDialog.setTitle("About");
            alertDialog.setMessage("Designed by: Piyush Gupta\n" + "Developed by: Mayank Rana and Sparsh Mittal\n" + "Version: 1.0\n" + "For any queries/suggestions, mail us at:" + "guptapiyush963@gmail.com");
            alertDialog.setCancelable(true);


            //Setting name and functionality to positive button
            alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            //Displaying alert Dialog Box
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    //Adding functionality to Navigation bar items
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        //Time table item
        if (id==R.id.nav_first_layout) {
            Intent intent=new Intent(MainActivity.this,TimeTable.class);
            startActivity(intent);
        }
        //Attendance manager item
        else if (id == R.id.nav_second_layout) {
            Intent intent=new Intent(MainActivity.this,AttendanceManager.class);
            startActivity(intent);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    //Updating list when user enters main Activity again
    protected void onStart() {
        super.onStart();
        dataModels= new ArrayList<>();
        createList();
        Toast.makeText(getApplicationContext(),"start",Toast.LENGTH_LONG).show();
    }

}
