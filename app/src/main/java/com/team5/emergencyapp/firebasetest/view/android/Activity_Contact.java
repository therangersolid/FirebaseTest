package com.team5.emergencyapp.firebasetest.view.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team5.emergencyapp.firebasetest.R;
import com.team5.emergencyapp.firebasetest.core.controller.GlobalData;
import com.team5.emergencyapp.firebasetest.core.controller.Run;
import com.team5.emergencyapp.firebasetest.core.model.User;
import com.team5.emergencyapp.firebasetest.firebase.dao.DUser;
import com.team5.emergencyapp.firebasetest.firebase.dao.DUserList;

import java.util.ArrayList;
import java.util.Set;

public class Activity_Contact extends AppCompatActivity {
    public static final String MESSAGE = "com.team5.emergencyapp.MESSAGE"; // The key of the message
    public static final String MESSAGE_TYPE = "com.team5.emergencyapp.MESSAGE_TYPE"; // the data will be broadcast or individual or bot
    public static final String BROADCAST = "BROADCAST"; // the data will be broadcast or individual or bots
    public static final String INDIVIDUAL = "INDIVIDUAL"; // the data will be broadcast or individual or bots
    public static final String BOT = "BOT"; // the data will be broadcast or individual or bots

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        final Activity_Contact activityContact = this;
        final LinearLayout messageList = (LinearLayout) findViewById(R.id.groupList);
        TextView chatWithBot = (TextView) findViewById(R.id.chatWithBot);
        chatWithBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activityContact, Activity_Main.class);
                intent.putExtra(MESSAGE_TYPE, BOT);
                intent.putExtra(MESSAGE, "");
                activityContact.startActivity(intent);
            }
        });
        Thread t = new Thread(new Runnable() { // get the user group first!
            @Override
            public void run() {


                try {
                    GlobalData.u = DUser.crud(GlobalData.u, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("Test", GlobalData.u.getEmail());
                final User us = GlobalData.u;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Set<String> keysets = us.getBroadcast().keySet();
                        for (final String key : keysets) {
                            TextView tempTextView = new TextView(activityContact);
                            tempTextView.setText(key);
                            tempTextView.setPadding(0, Utility.dipToPX(5), 0, Utility.dipToPX(5));
                            tempTextView.setTextColor(getResources().getColor(R.color.black));
                            tempTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(activityContact, Activity_Main.class);
                                    intent.putExtra(MESSAGE_TYPE, BROADCAST);
                                    intent.putExtra(MESSAGE, key);
                                    activityContact.startActivity(intent);
                                }
                            });
                            messageList.addView(tempTextView);
                            Log.e("Test", key);
                        }
                    }
                });
                try {
                    final ArrayList<User> users3 = DUserList.r();// Get full array of users

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < users3.size(); i++) {
                                if (!us.getId().equals(users3.get(i).getId())) {// do not show the current id!
                                    TextView tempTextView = new TextView(activityContact);
                                    final String userID = users3.get(i).getId();
                                    tempTextView.setText(userID);
                                    tempTextView.setPadding(0, Utility.dipToPX(5), 0, Utility.dipToPX(5));
                                    tempTextView.setTextColor(getResources().getColor(R.color.black));
                                    tempTextView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(activityContact, Activity_Main.class);
                                            intent.putExtra(MESSAGE_TYPE, INDIVIDUAL);
                                            intent.putExtra(MESSAGE, userID);
                                            activityContact.startActivity(intent);
                                        }
                                    });
                                    messageList.addView(tempTextView);
                                    Log.e("Test", users3.get(i).getId());
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("Test", e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }
}
