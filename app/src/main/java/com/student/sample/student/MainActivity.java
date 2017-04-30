package com.student.sample.student;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private EditText std_id, std_name,cid_id;
    private String std_trip_sts;
    private Button add, update, delete, view, view_all;
    private Switch trip_switch;
    SQLiteDatabase sdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView text = (TextView) findViewById(R.id.textView);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        std_id = (EditText) findViewById(R.id.sid);
        cid_id = (EditText) findViewById(R.id.cid);
        std_name = (EditText) findViewById(R.id.sName);
        trip_switch = (Switch) findViewById(R.id.sSTS);
        add = (Button) findViewById(R.id.add_button);
        update = (Button) findViewById(R.id.upd_button);
        delete = (Button) findViewById(R.id.del_button);
        view = (Button) findViewById(R.id.sdtls_button);
        view_all = (Button) findViewById(R.id.viewall_button);
        if (trip_switch.isChecked()) {
            std_trip_sts = "TAKING-COURSE";
        } else {
            std_trip_sts = "NOT-TAKING-COURSE";
        }
        sdb = openOrCreateDatabase("Trip_manage", Context.MODE_PRIVATE, null);
        sdb.execSQL("CREATE TABLE IF NOT EXISTS STUDENTCOURSES(STUDENTID INTEGER primary key,COURSEID VARCHAR, STUDENTNAME VARCHAR, STATUS VARCHAR);");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (std_id.getText().toString().trim().length() == 0 ||cid_id.getText().toString().trim().length() == 0|| std_name.getText().toString().trim().length() == 0 || std_trip_sts.trim().length() == 0) {
                    showDialog("Error", "Please enter all Values");
                    return;
                }
                if (trip_switch.isChecked()) {
                    std_trip_sts = "TAKING-COURSE";
                } else {
                    std_trip_sts = "NOT-TAKING-COURSE";
                }
                sdb.execSQL("INSERT INTO STUDENTCOURSES VALUES('" + std_id.getText() + "','" +cid_id.getText() + "','"+ std_name.getText() + "','" + std_trip_sts + "')");
                showDialog("Success", "Student added successfully");
                clearText();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (std_id.getText().toString().trim().length() == 0) {
                    showDialog("Error", "Please enter Student ID");
                    return;
                }
                Cursor c = sdb.rawQuery("SELECT * FROM STUDENTCOURSES WHERE STUDENTID='" + std_id.getText() + "'", null);
                if (c.moveToFirst()) {
                    sdb.execSQL("DELETE FROM STUDENTCOURSES WHERE STUDENTID='" + std_id.getText() + "'");
                    showDialog("Success", "Student Deleted");
                } else {
                    showDialog("Error", "Invalid StudentID");
                }
                clearText();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (std_id.getText().toString().trim().length() == 0) {
                    showDialog("Error", "Please enter Student ID");
                    return;
                }
                if (cid_id.getText().toString().trim().length() == 0) {
                    showDialog("Error", "Please enter Course ID");
                    return;
                }
                if (trip_switch.isChecked()) {
                    std_trip_sts = "TAKING-COURSE";
                } else {
                    std_trip_sts = "NOT-TAKING-COURSE";
                }
                Cursor c = sdb.rawQuery("SELECT * FROM STUDENTCOURSES WHERE STUDENTID='" + std_id.getText() + "'", null);
                if (c.moveToFirst()) {
                    sdb.execSQL("UPDATE STUDENTCOURSES SET STATUS='" + std_trip_sts + "',COURSEID='"+cid_id.getText()+"' where STUDENTID='" + std_id.getText() + "'");
                    showDialog("Success", "Student details modified");
                } else {
                    showDialog("Error", "Invalid StudentID");
                }
                clearText();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (std_id.getText().toString().trim().length() == 0) {
                    showDialog("Error", "Please enter Student ID");
                    return;
                }
                Cursor c = sdb.rawQuery("SELECT * FROM STUDENTCOURSES WHERE STUDENTID='" + std_id.getText() + "'", null);
                if (c.moveToFirst()) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("StudentID: " + c.getString(0) + "\n");
                    sb.append("CourseID: " + c.getString(1) + "\n");
                    sb.append("Student Name: " + c.getString(2) + "\n");
                    sb.append("Trip Status: " + c.getString(3));
                    showDialog("Student Details", sb.toString());

                } else {
                    showDialog("Error", "Invalid StudentID");
                }
                clearText();

            }
        });
        view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cid_id.getText().toString().trim().length() == 0) {
                    showDialog("Error", "Please enter Course ID");
                    return;
                }

                Cursor c = sdb.rawQuery("SELECT * FROM STUDENTCOURSES where upper(COURSEID)='"+
                        cid_id.getText().toString().trim().toUpperCase()+"'", null);
                if (c.getCount() == 0) {
                    showDialog("Error", "No Students Added");
                    return;
                }
                Cursor c1 = sdb.rawQuery("SELECT * FROM STUDENTCOURSES where upper(COURSEID)='"+cid_id.getText().toString().trim().toUpperCase() +"' and STATUS='TAKING-COURSE'", null);

                StringBuffer sb = new StringBuffer();

                sb.append("student taking the course '"+cid_id.getText().toString()+"': "+c1.getCount()+"\n");
                sb.append("SID || CID || NAME || STATUS \n");
                while (c.moveToNext()) {
                    sb.append(c.getString(0) + " || " + c.getString(1) + " || " + c.getString(2) + " || " +c.getString(3)+ "\n");
                }
                showDialog("Student Details", sb.toString());
            }
        });
    }

    /*    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }*/
    private void showDialog(String titlestatus, String msg) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(true);
        b.setTitle(titlestatus);
        b.setMessage(msg);
        b.show();

    }

    private void clearText() {
        std_id.setText("");
        std_name.setText("");
        trip_switch.setChecked(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
