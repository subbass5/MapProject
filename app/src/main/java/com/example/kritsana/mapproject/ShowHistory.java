package com.example.kritsana.mapproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kritsana on 4/19/17.
 */

public class ShowHistory extends AppCompatActivity{
    private Button btnGomain;
    private DatabaseReference myRef;
    private ListView listView;
    private ArrayAdapter<String> mAdapter;
    private List<String> mChat = new ArrayList<String>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showhistory);
        getSupportActionBar().hide();

        listView=(ListView) findViewById(R.id.listHistory);
        getData();




    }
    private void getData(){
        myRef = FirebaseDatabase.getInstance().getReference();
        myRef = myRef.child("history");
        Query query = myRef.limitToLast(10);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String,Object> newHistory = (Map<String,Object>) dataSnapshot.getValue();
                String lati1 = newHistory.get("latitude").toString();
                String longi1 = newHistory.get("longitude").toString();
                String date1 = newHistory.get("date").toString();
                String time1 = newHistory.get("time").toString();
                mChat.add(0,""+date1+":"+time1+" : "+lati1+","+longi1);
                if(mAdapter==null){
                    mAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,mChat);
                    listView.setAdapter(mAdapter);
                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
