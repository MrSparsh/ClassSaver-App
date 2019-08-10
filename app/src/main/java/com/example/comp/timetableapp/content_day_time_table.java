package com.example.comp.timetableapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;


public class content_day_time_table extends AppCompatActivity {

    SQLiteDatabase inputDetailsDatabase;
    SQLiteDatabase attendanceManagerDatabase;

    String tableName;
    ArrayList<DataModel> dataModels;

    private static CustomAdapter adapter;
    ListView listView;
    AlertDialog inputAlertDialog;

    Context dayTimeTablesContext=this;
    View inputAlertDialogueView;
    int day;
    Button from_time,to_time;

    EditText venueEditText;
    EditText subjectNameEditText;


    PendingIntent pendingIntent;
    Intent intent;
    Calendar calendar;
    AlarmManager alarmManager;

    //Function to delete data From Attendance Manager Database

    public void deleteFromAttendanceManager(String subjectNameToBeDeleted)
    {
        Cursor attendanceManagerCursor = attendanceManagerDatabase.rawQuery("SELECT * FROM amt",null);
        attendanceManagerCursor.moveToFirst();
        if(attendanceManagerCursor.moveToFirst())
        {
            while(!attendanceManagerCursor.isAfterLast())
            {
                int attendanceManagerSubjectIndex = attendanceManagerCursor.getColumnIndex("subjectname");
                if(attendanceManagerCursor.getString(attendanceManagerSubjectIndex).equals(subjectNameToBeDeleted))
                    break;
                attendanceManagerCursor.moveToNext();
            }
            int countIndex = attendanceManagerCursor.getColumnIndex("count");
            if(attendanceManagerCursor.getInt(countIndex)==1) {
                attendanceManagerDatabase.execSQL("DELETE FROM amt WHERE subjectname = '" + subjectNameToBeDeleted + "' ");
                Toast.makeText(this, "Delete!!", Toast.LENGTH_LONG).show();
            }
            else {
                attendanceManagerDatabase.execSQL("UPDATE amt SET count = " + Integer.toString(attendanceManagerCursor.getInt(countIndex)-1) + " WHERE subjectname = '" + subjectNameToBeDeleted + "' ");
                Toast.makeText(this,"Update!",Toast.LENGTH_LONG).show();
            }
        }
    }


    //Function to add data to Attendance Manager Database
    public void toAttendanceManagerDatabase(String addedSubjectName){

    Cursor c =attendanceManagerDatabase.rawQuery("SELECT * FROM amt ",null);
    c.moveToFirst();
    if(c.moveToFirst())
    {
        while(!c.isAfterLast())
        {
            int subjectnameindex = c.getColumnIndex("subjectname");
            if(c.getString(subjectnameindex).equals(addedSubjectName))
            {
                int countIndex = c.getColumnIndex("count");
                int count = c.getInt(countIndex);
                attendanceManagerDatabase.execSQL("UPDATE amt SET count = "+Integer.toString(count+1)+" WHERE subjectname = '"+addedSubjectName+"' ");
                Toast.makeText(this,Integer.toString(c.getInt(countIndex)),Toast.LENGTH_LONG).show();
                return;
            }
            c.moveToNext();
        }
    }
        attendanceManagerDatabase.execSQL("INSERT INTO amt ( subjectname , present , absent , total , count ) VALUES ( '" + addedSubjectName +"' , 0 , 0 , 0 , 1 )");
    }

    //Function to change from time button text in hh:mm format
    public void set_from_time(int hourOfDay,int minute)
    {
        if(hourOfDay<10)
        {
            if(minute<10)
                from_time.setText('0'+Integer.toString(hourOfDay) + ":" + '0'+Integer.toString(minute));
            else
                from_time.setText('0'+Integer.toString(hourOfDay) + ":" + Integer.toString(minute));
        }
        else
        {
            if(minute<10)
                from_time.setText(Integer.toString(hourOfDay) + ":" + '0'+Integer.toString(minute));
            else
                from_time.setText(Integer.toString(hourOfDay) + ":" +Integer.toString(minute));
        }
    }

    public void set_to_time(int hourOfDay,int minute){
        if(hourOfDay<10)
        {
            if(minute<10)
                to_time.setText('0'+Integer.toString(hourOfDay) + ":" + '0'+Integer.toString(minute));
            else
                to_time.setText('0'+Integer.toString(hourOfDay) + ":" + Integer.toString(minute));
        }
        else
        {
            if(minute<10)
                to_time.setText(Integer.toString(hourOfDay) + ":" + '0'+Integer.toString(minute));
            else
                to_time.setText(Integer.toString(hourOfDay) + ":" +Integer.toString(minute));
        }

    }



    //Setting from time button text from timepicker
    public void from_time(final View v)
    {
        final Calendar calendar=Calendar.getInstance();

        TimePickerDialog timePickerDialog=new TimePickerDialog(content_day_time_table.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                set_from_time(hourOfDay,minute);

            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false );

        timePickerDialog.show();
    }

    //Setting from time button text from timepicker
    public void to_time(final View v)
    {
        final Calendar calendar=Calendar.getInstance();

        TimePickerDialog timePickerDialog=new TimePickerDialog(content_day_time_table.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                set_to_time(hourOfDay,minute);
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false );

        timePickerDialog.show();
    }

    public void createList() {
        try {

        //Inialising cursor to the selected day table in database
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

                //Changing cursor to point to next row
                c.moveToNext();
            }
        }
            adapter = new CustomAdapter(dataModels, getApplicationContext());
            listView.setAdapter(adapter);
    }
         catch (Exception e) {
            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    //to get the selected day
    public String selectDay(int day)
    {
        switch (day)
        {
            case 0:  return "mondaytimetable";
            case 1:  return "tuesdaytimetable";
            case 2:  return "wednesdaytimetable";
            case 3:  return "thursdaytimetable";
            case 4:  return "fridaytimetable";
            case 5:  return "saturdaytimetable";
            case 6:  return "sundaytimetable";
        }
        return "";
    }
    int set_calendar_day(int day){
        switch (day)
        {
            case 0:  calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
            return Calendar.MONDAY;
            case 1:  calendar.set(Calendar.DAY_OF_WEEK,Calendar.TUESDAY);
                return Calendar.TUESDAY;
            case 2:  calendar.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY);
                return Calendar.WEDNESDAY;
            case 3:  calendar.set(Calendar.DAY_OF_WEEK,Calendar.THURSDAY);
                return Calendar.THURSDAY;
            case 4:  calendar.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
                return Calendar.FRIDAY;
            case 5:  calendar.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
                return Calendar.SATURDAY;
            case 6:  calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
                return Calendar.SUNDAY;
            default:
                return -1;
        }

    }


    public static int getAlarmId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int alarmId = preferences.getInt("ALARM", 1);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("ALARM", alarmId + 1).apply();
        return alarmId;
    }

    //Submit button functionality in alert dialog box
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void submitInputDetails(View view)
    {
        venueEditText= (EditText)inputAlertDialogueView.findViewById(R.id.venueEditText);
        subjectNameEditText= (EditText)inputAlertDialogueView.findViewById(R.id.subjectNameEditText);

        //Getting data inserted into the alert dialog box
        String subjectName =subjectNameEditText.getText().toString();
        String venue = venueEditText.getText().toString();
        String fromTime = from_time.getText().toString();
        String toTime = to_time.getText().toString();

        //converting from time string into fromtime int variable in database
        String hourstring = fromTime.substring(0,2);
        int hours= Integer.parseInt(hourstring);
        String minutesstring = fromTime.substring(3,5);
        int minutes= Integer.parseInt(minutesstring);
        int finalfromtime= hours*60+minutes;

        //converting to time string into totime int variable in database
        String hourstring2 = toTime.substring(0,2);
        int hours2= Integer.parseInt(hourstring2);
        String minutesstring2 = toTime.substring(3,5);
        int minutes2= Integer.parseInt(minutesstring2);
        int finaltotime= hours2*60+minutes2;

        //A check if anyhthing is left unfilled or there is invalid data
        if(subjectName.equals("")||venue.equals("")||finalfromtime==0||finaltotime==0)
        {
            Toast.makeText(this,"ENTRY IS MISSING",Toast.LENGTH_SHORT).show();
            return;
        }
        if(finalfromtime>finaltotime)
        {
            Toast.makeText(this,"INVALID DATA",Toast.LENGTH_SHORT).show();
            return;
        }

        //A check whether two classes times are clashing
        Cursor c= inputDetailsDatabase.rawQuery("SELECT * FROM "+tableName,null);
        c.moveToFirst();
        if(c.moveToFirst()) {
            while (!c.isAfterLast()) {
                int fromTimeIndex = c.getColumnIndex("fromtime");
                int toTimeIndex = c.getColumnIndex("totime");
                if (finalfromtime > c.getInt(fromTimeIndex) && finalfromtime < c.getInt(toTimeIndex) || (finaltotime > c.getInt(fromTimeIndex) && finaltotime < c.getInt(toTimeIndex)) || (c.getInt(fromTimeIndex) < finaltotime && c.getInt(fromTimeIndex) > finalfromtime) || (c.getInt(toTimeIndex) < finaltotime && c.getInt(toTimeIndex) > finalfromtime)) {
                    Toast.makeText(this, "Class Time is clashing", Toast.LENGTH_SHORT).show();
                    return;
                }
                c.moveToNext();
            }
        }

        long time_in_millis;

        int tapped_day=set_calendar_day(day);
        calendar.set(Calendar.HOUR_OF_DAY,hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar calendar1 = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        intent=new Intent(getApplicationContext(),AlarmReceiver.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));

        if(dayOfWeek>tapped_day||(dayOfWeek==tapped_day)&&calendar.getTimeInMillis()<System.currentTimeMillis())
            time_in_millis= calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY*7);
        else
            time_in_millis=calendar.getTimeInMillis();
        intent.putExtra("subjectname",subjectName);
        intent.putExtra("venue",venue);
        intent.putExtra("fromtime",finalfromtime);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), getAlarmId(getApplicationContext()), intent, PendingIntent.FLAG_ONE_SHOT);

        // set alarm manager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,time_in_millis-15*60*1000, AlarmManager.INTERVAL_DAY*7,pendingIntent);


    //Setting alert dialog box entries to their default for next use
        venueEditText.setText("");
        subjectNameEditText.setText("");
        from_time.setText("00:00");
        to_time.setText("00:00");

        try{

            inputDetailsDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+tableName+" (subjectname VARCHAR, venue VARCHAR, fromtime INT[4], totime INT[4])");

            inputDetailsDatabase.execSQL("INSERT INTO "+tableName+" (subjectname, venue, fromtime, totime) VALUES ('"+subjectName+"', '"+venue+"', '"+finalfromtime+"', '"+finaltotime+"')");

            inputAlertDialog.dismiss();

            toAttendanceManagerDatabase(subjectName);

            dataModels= new ArrayList<>();
            createList();
        }

            catch(Exception e) {
                Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
            }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_time_tables);

        final AlertDialog.Builder inputAlertDialogueBuilder = new AlertDialog.Builder(dayTimeTablesContext);
        inputAlertDialogueView= getLayoutInflater().inflate(R.layout.inputdetails,null);


        inputAlertDialogueBuilder.setView(inputAlertDialogueView);
        inputAlertDialog = inputAlertDialogueBuilder.create();
        from_time=(Button)inputAlertDialogueView.findViewById(R.id.from_time);
        to_time=(Button)inputAlertDialogueView.findViewById(R.id.to_time);

        Intent intent = getIntent();
        day= intent.getIntExtra("day",-1);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputAlertDialog.show();
                Button submitInputDetailsButton = (Button)inputAlertDialogueView.findViewById(R.id.submitInputDetailsButton);



            }
        });

        inputDetailsDatabase = this.openOrCreateDatabase("finaltime17", MODE_PRIVATE, null);
        tableName = selectDay(day);

        attendanceManagerDatabase = this.openOrCreateDatabase("amd4",MODE_PRIVATE,null);
        attendanceManagerDatabase.execSQL("CREATE TABLE IF NOT EXISTS amt ( subjectname VARCHAR , present INT[4] , absent INT[4] , total INT[4] , count INT[4] )");

        listView=(ListView)findViewById(R.id.day_time_table_list_view);
        dataModels= new ArrayList<>();
        createList();


        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        calendar=Calendar.getInstance();

        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0,v.getId(),0,"Edit");
        menu.add(0,v.getId(),0,"Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //For getting position of list item which is long pressed
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;

        Cursor c= inputDetailsDatabase.rawQuery("SELECT * FROM "+tableName+" ORDER BY fromtime",null);
        c.moveToFirst();
        if(c.moveToFirst())
        {
            for(int i=0;i<listPosition;i++)
            {
                c.moveToNext();
            }
        }
        int subjectIndex = c.getColumnIndex("subjectname");
        int fromTimeIndex = c.getColumnIndex("fromtime");
        int toTimeIndex = c.getColumnIndex("totime");
        int venueNameIndex = c.getColumnIndex("venue");

        if(item.getTitle()=="Edit")
        {
            inputAlertDialog.show();
            venueEditText= (EditText)inputAlertDialogueView.findViewById(R.id.venueEditText);
            venueEditText.setText(c.getString(venueNameIndex));

            subjectNameEditText= (EditText)inputAlertDialogueView.findViewById(R.id.subjectNameEditText);
            subjectNameEditText.setText(c.getString(subjectIndex));

            int fromtime = c.getInt(fromTimeIndex);
            int fromhours= fromtime/60;
            int fromminutes = fromtime%60;
            int totime = c.getInt(toTimeIndex);
            int tohours= totime/60;
            int tominutes = totime%60;

            set_from_time(fromhours,fromminutes);
            set_to_time(tohours,tominutes);

            String subjectNameToBeDeleted = c.getString(subjectIndex);
            inputDetailsDatabase.execSQL("DELETE FROM "+tableName+" WHERE fromtime = "+Integer.toString(c.getInt(fromTimeIndex)));
            deleteFromAttendanceManager(subjectNameToBeDeleted);

        }
        else if(item.getTitle()=="Delete")
        {

            String subjectNameToBeDeleted = c.getString(subjectIndex);
            inputDetailsDatabase.execSQL("DELETE FROM "+tableName+" WHERE fromtime = "+Integer.toString(c.getInt(fromTimeIndex)));
            deleteFromAttendanceManager(subjectNameToBeDeleted);



            dataModels = new ArrayList<>();
            createList();
        }
        return super.onContextItemSelected(item);
    }

}





