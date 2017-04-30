package com.example.kritsana.mapproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.content.DialogInterface.OnClickListener;

import java.util.Map;

/**
 * Created by kritsana on 3/20/17.
 */

public class MainActivity extends ActionBarActivity {
    private Button btnGomap,btnSetstate,btnGOhistory;
    private Intent go = null;
    private float lati,longi;
    private String msg ="",strStatus="";
    private TextView txtStatus;
    private ProgressDialog con ;
    private int n= 0;
    private Notification.Builder notification;
    final int id = 11;
    private FirebaseDatabase database;
    private DatabaseReference myRef_Lati,myRef_Longi,myRef_state;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ones);
        getSupportActionBar().hide();
        btnGomap = (Button) findViewById(R.id.btngoViewmap);
        btnSetstate = (Button) findViewById(R.id.btnSuccess);
        btnGOhistory = (Button) findViewById(R.id.btnGoHistory);
        txtStatus = (TextView) findViewById(R.id.txtStatusCar);
        con = new ProgressDialog(MainActivity.this);
        btnGomap.setEnabled(false);
        openDialog(0);

        getLocation();
        btnGOhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go = new Intent(MainActivity.this,ShowHistory.class);
                startActivity(go);
            }
        });
        btnSetstate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef_state.setValue("false");
            }
        });

        btnGomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go =new Intent(MainActivity.this,MapsActivity.class);
                go.putExtra("lati",lati);
                go.putExtra("longi",longi);
                go.putExtra("msg",msg);
                startActivity(go);
            }
        });



    }
    private void getLocation(){
        database = FirebaseDatabase.getInstance();
        myRef_Lati = database.getReference("location/latitude");
        myRef_Longi = database.getReference("location/longitude");
        myRef_state = database.getReference("location/state");

        myRef_Lati.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               lati = Float.parseFloat(dataSnapshot.getValue(String.class));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef_Longi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                longi = Float.parseFloat(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        myRef_state.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strStatus = dataSnapshot.getValue(String.class);
                openDialog(1);
                btnGomap.setEnabled(true);
                if (strStatus.equals("true")){
                    txtStatus.setText("ผิดปกติ ณ ตำแหน่ง\n       "+lati+","+longi);
                    txtStatus.setTextColor(Color.rgb(254,1,1));
                    doClick();
                }else {
                    txtStatus.setText("ใช้งาน ปกติ ณ ตำแหน่ง\n       "+lati+","+longi);
                    txtStatus.setTextColor(Color.rgb(48,254,1));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void doClick(){
        notification = new Notification.Builder(MainActivity.this);
        notification.setSmallIcon(R.drawable.iconblur);
        notification.setWhen(SystemClock.currentThreadTimeMillis());
        notification.setContentTitle("คำเตือน");
        notification.setTicker("มีข้อความเข้ามาใหม่");
        notification.setContentText("รถคุณถูกบุกรุกในตอนนี้");
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pending= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pending);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.setSound(soundUri);
        //ใช้ในการจัดการการแจ้งเตือน
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        nm.notify(id,notification.build());
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        AlertDialog.Builder dialog2 = new AlertDialog.Builder(MainActivity.this);
        dialog2.setTitle("Exit");
        dialog2.setCancelable(true);
        dialog2.setMessage("Do you want to exit?");
        dialog2.setPositiveButton("Yes.", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        });
        dialog2.setNegativeButton("No.", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog2.show();
    }
    public void openDialog(int status){
        if(status==0){
            Log.i("Connect","Connected");
            con.setTitle("โปรดรอ...");
            con.setMessage("กำลังเชื่อมต่อ firebase......");
            con.setIcon(R.drawable.firebaseicon);
            con.show();


        }else if (status==1){
            Log.i("Connect","Connected.");
            con.hide();
        }else {
            n = 3;
        }
    }
}
