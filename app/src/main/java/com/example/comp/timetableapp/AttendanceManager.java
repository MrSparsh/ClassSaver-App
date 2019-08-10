package com.example.comp.timetableapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class AttendanceManager extends AppCompatActivity {

    ListView attendanceManagerListView;
    ArrayList<AttendanceDataModel> arrayList;
    private static AttendanceManagerAdapter attendanceManagerAdapter;

    SQLiteDatabase attendanceManagerDatabase;

    public void createList() {
        try {

            Cursor c = attendanceManagerDatabase.rawQuery("SELECT * FROM amt", null);

            c.moveToFirst();

            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {

                    int subjectIndex = c.getColumnIndex("subjectname");
                    int totalIndex=c.getColumnIndex("total");
                    int presentIndex=c.getColumnIndex("present");
                    int absentIndex=c.getColumnIndex("absent");
                    Double percentage=(c.getInt(presentIndex)*100.0)/c.getInt(totalIndex);
                    new DecimalFormat("$#.00").format(percentage);


                    arrayList.add(new AttendanceDataModel(c.getString(subjectIndex),Double.toString(percentage),Integer.toString(c.getInt(totalIndex)),Integer.toString(c.getInt(presentIndex)),Integer.toString(c.getInt(absentIndex))));

                    c.moveToNext();
                }
            }
            attendanceManagerAdapter = new AttendanceManagerAdapter(arrayList, getApplicationContext());
            attendanceManagerListView.setAdapter(attendanceManagerAdapter);
        }
        catch (Exception e) {
            Toast.makeText(this, "Failed1", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_manager);

        attendanceManagerListView=(ListView)findViewById(R.id.attendance_manager_list_view);
        arrayList=new ArrayList<>();


        attendanceManagerDatabase = this.openOrCreateDatabase("amd4",MODE_PRIVATE,null);

        createList();

        registerForContextMenu(attendanceManagerListView);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0,v.getId(),0,"Present");
        menu.add(0,v.getId(),0,"Absent");
        menu.add(0,v.getId(),0,"Undo Present");
        menu.add(0,v.getId(),0,"Undo Absent");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //For getting position of list item which is long pressed
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;

        arrayList=new ArrayList<>();

        Cursor c= attendanceManagerDatabase.rawQuery("SELECT * FROM amt ",null);
        c.moveToFirst();
        if(c.moveToFirst())
        {
            for(int i=0;i<listPosition;i++)
            {
                c.moveToNext();
            }
        }
        int subjectIndex = c.getColumnIndex("subjectname");
        int presentIndex = c.getColumnIndex("present");
        int totalIndex = c.getColumnIndex("total");
        int absentIndex = c.getColumnIndex("absent");

        if(item.getTitle()=="Present")
        {
            attendanceManagerDatabase.execSQL("UPDATE amt SET total = "+Integer.toString(c.getInt(totalIndex)+1)+" WHERE subjectname = '"+c.getString(subjectIndex)+"' ");
            attendanceManagerDatabase.execSQL("UPDATE amt SET present = "+Integer.toString(c.getInt(presentIndex)+1)+" WHERE subjectname = '"+c.getString(subjectIndex)+"' ");
            Toast.makeText(this,Integer.toString(c.getInt(presentIndex)),Toast.LENGTH_LONG).show();
            createList();
        }
        else if(item.getTitle()=="Absent")
        {
            attendanceManagerDatabase.execSQL("UPDATE amt SET total = "+Integer.toString(c.getInt(totalIndex)+1)+" WHERE subjectname = '"+c.getString(subjectIndex)+"' ");
            attendanceManagerDatabase.execSQL("UPDATE amt SET absent = "+Integer.toString(c.getInt(absentIndex)+1)+" WHERE subjectname = '"+c.getString(subjectIndex)+"' ");
            Toast.makeText(this,Integer.toString(c.getInt(presentIndex)),Toast.LENGTH_LONG).show();
            createList();
        }
        else if(item.getTitle()=="Undo Present"&&c.getInt(presentIndex)>0)
        {
            attendanceManagerDatabase.execSQL("UPDATE amt SET total = "+Integer.toString(c.getInt(totalIndex)-1)+" WHERE subjectname = '"+c.getString(subjectIndex)+"' ");
            attendanceManagerDatabase.execSQL("UPDATE amt SET present = "+Integer.toString(c.getInt(presentIndex)-1)+" WHERE subjectname = '"+c.getString(subjectIndex)+"' ");
            Toast.makeText(this,Integer.toString(c.getInt(presentIndex)),Toast.LENGTH_LONG).show();
            createList();
        }
        else if(item.getTitle()=="Undo Absent"&&c.getInt(absentIndex)>0)
        {
            attendanceManagerDatabase.execSQL("UPDATE amt SET total = "+Integer.toString(c.getInt(totalIndex)-1)+" WHERE subjectname = '"+c.getString(subjectIndex)+"' ");
            attendanceManagerDatabase.execSQL("UPDATE amt SET absent = "+Integer.toString(c.getInt(absentIndex)-1)+" WHERE subjectname = '"+c.getString(subjectIndex)+"' ");
            Toast.makeText(this,Integer.toString(c.getInt(presentIndex)),Toast.LENGTH_LONG).show();
            createList();
        }
        return super.onContextItemSelected(item);
    }

}
