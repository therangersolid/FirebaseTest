package com.team5.emergencyapp.firebasetest.view.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team5.emergencyapp.firebasetest.R;
import com.team5.emergencyapp.firebasetest.core.controller.Run;
import com.team5.emergencyapp.firebasetest.core.model.User;
import com.team5.emergencyapp.firebasetest.firebase.dao.DUser;
import com.team5.emergencyapp.firebasetest.firebase.dao.DUserList;

import java.util.ArrayList;
import java.util.Set;

public class Activity_Contact extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Utility.initialize(getResources());
        Run.initialize();
        final Activity_Contact activity_Contact = this;
        final LinearLayout messageList = (LinearLayout) findViewById(R.id.groupList);
        TextView chatWithBot = (TextView) findViewById(R.id.chatWithBot);
        chatWithBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        Thread t = new Thread(new Runnable() { // get the user group first!
            @Override
            public void run() {
                User u = new User();
                u.setId("6ZxSGeHS4DOoFHEE2McBcGH7XHP2");

                try {
                    u = DUser.crud(u, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("Test", u.getEmail());
                final User us = u;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Set<String> keysets = us.getBroadcast().keySet();
                        for (String key : keysets) {
                            TextView tempTextView = new TextView(activity_Contact);
                            tempTextView.setText(key);
                            tempTextView.setPadding(0, Utility.dipToPX(5), 0, Utility.dipToPX(5));
                            tempTextView.setTextColor(getResources().getColor(R.color.black));
                            messageList.addView(tempTextView);
                            Log.e("Test", key);
                        }
                    }
                });
            }
        });
        t.start();
        Thread t2 = new Thread(new Runnable() { // get all contact
            @Override
            public void run() {
                try {
                    final ArrayList<User> users3 = DUserList.r();// Get full array of users

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < users3.size(); i++) {
                                TextView tempTextView = new TextView(activity_Contact);
                                tempTextView.setText(users3.get(i).getId());
                                tempTextView.setPadding(0, Utility.dipToPX(5), 0, Utility.dipToPX(5));
                                tempTextView.setTextColor(getResources().getColor(R.color.black));
                                messageList.addView(tempTextView);
                                Log.e("Test", users3.get(i).getId());
                            }
                        }
                    });


                } catch (Exception e) {
                    Log.e("Test", e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        t2.start();
    }
}
