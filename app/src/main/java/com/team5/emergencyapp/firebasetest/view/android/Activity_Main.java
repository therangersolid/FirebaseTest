package com.team5.emergencyapp.firebasetest.view.android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.team5.emergencyapp.firebasetest.R;
import com.team5.emergencyapp.firebasetest.core.model.ChatMessage;
import com.team5.emergencyapp.firebasetest.core.model.Message;
import com.team5.emergencyapp.firebasetest.core.model.User;
import com.team5.emergencyapp.firebasetest.firebase.dao.DAutoIncrement;
import com.team5.emergencyapp.firebasetest.firebase.dao.DMessage;
import com.team5.emergencyapp.firebasetest.firebase.dao.DMessageList;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class Activity_Main extends AppCompatActivity implements AIListener {

    EditText editText;
    RelativeLayout addBtn;
    DatabaseReference ref;
    Boolean flagFab = true;
    LinearLayout chatList;
    private AIService aiService;
    Activity_Main activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        final String intent_MessageType = intent.getStringExtra(Activity_Contact.MESSAGE_TYPE);
        Log.e("Test", "MAIN: MessageType");
        final String intent_Message = intent.getStringExtra(Activity_Contact.MESSAGE);
        activityMain = this;

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);


        editText = (EditText) findViewById(R.id.editText);
        addBtn = (RelativeLayout) findViewById(R.id.addBtn);
        chatList = (LinearLayout) findViewById(R.id.chatList);


        final AIConfiguration config = new AIConfiguration("e8eab6c8f98247cfa2af3e7f2739b797",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest();


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String message = editText.getText().toString().trim();

                if (!message.equals("")) {
                    addChat(message, true);// add message to view
                    if (intent_MessageType.equals(Activity_Contact.BOT)) {// If it's bot, do not fill or contact the database!!!
                        aiRequest.setQuery(message);
                        new AsyncTask<AIRequest, Void, AIResponse>() {

                            @Override
                            protected AIResponse doInBackground(AIRequest... aiRequests) {
                                final AIRequest request = aiRequests[0];
                                try {
                                    Log.e("Test", "Chat is Requesting");
                                    final AIResponse response = aiDataService.request(aiRequest);
                                    return response;
                                } catch (AIServiceException e) {
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(AIResponse response) {
                                if (response != null) {

                                    Result result = response.getResult();
                                    String reply = result.getFulfillment().getSpeech();
                                    Log.e("Test", "Chat is Responding " + reply);
                                    addChat(reply, false);// add message to view
                                }
                            }
                        }.execute(aiRequest);

                    } else if (intent_MessageType.equals(Activity_Contact.INDIVIDUAL)) {
                        Log.e("Test", "Enter individual");
                        (new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message(); // Craft message
                                msg.setMessage(message);
                                msg.setTimestamp(System.currentTimeMillis());
                                msg.setBlobname(null);
                                msg.setBlob(null);
                                try {
                                    msg.setOrder(DAutoIncrement.order(DAutoIncrement.MESSAGE));
                                    msg = DMessage.crud(msg, false, false);
                                    // Hardcoded from
                                    User u = new User();
                                    u.setId("6ZxSGeHS4DOoFHEE2McBcGH7XHP2");
                                    User u2 = new User();
                                    u2.setId(intent_Message);
                                    DMessageList.crd(u2, u, msg, false, false);
                                } catch (Exception e) {
                                    Log.e("TestError", e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        })).start();

                    }

                } else {
                    aiService.startListening();
                }

                editText.setText("");

            }
        });


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ImageView fab_img = (ImageView) findViewById(R.id.fab_img);
                Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.ic_send_white_24dp);
                Bitmap img1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mic_white_24dp);


                if (s.toString().trim().length() != 0 && flagFab) {
                    ImageViewAnimatedChange(Activity_Main.this, fab_img, img);
                    flagFab = false;

                } else if (s.toString().trim().length() == 0) {
                    ImageViewAnimatedChange(Activity_Main.this, fab_img, img1);
                    flagFab = true;

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


//                Log.e("Test", "chatIs: Firebase handler");
//                if (model.getMsgUser().equals("user")) {
//
//                    viewHolder.rightText.setText(model.getMsgText());
//
//                    viewHolder.rightText.setVisibility(View.VISIBLE);
//                    viewHolder.leftText.setVisibility(View.GONE);
//                } else {
//                    viewHolder.leftText.setText(model.getMsgText());
//
//                    viewHolder.rightText.setVisibility(View.GONE);
//                    viewHolder.leftText.setVisibility(View.VISIBLE);
//                }


    }

    public void addChat(String msg, boolean user) { // Add message to the screen. If it's user, then set on the left, else on the right
        TextView tv = new TextView(activityMain);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int tenDp = Utility.dipToPX(10);
        params.leftMargin = tenDp;
        params.topMargin = tenDp;
        params.rightMargin = tenDp;
        params.bottomMargin = tenDp;
        int eightDp = Utility.dipToPX(8);
        if (user) {
            params.gravity = Gravity.LEFT;
            tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.left_background));
            tv.setTextColor(getResources().getColor(R.color.blackText));
        } else {
            params.gravity = Gravity.RIGHT;
            tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_background));
            tv.setTextColor(getResources().getColor(R.color.white));
        }
        tv.setText(msg);
        tv.setPadding(eightDp, eightDp, eightDp, eightDp);
        tv.setLayoutParams(params);
        tv.setFocusable(true);
        tv.setFocusableInTouchMode(true);
        chatList.addView(tv);
        tv.requestFocus();
        tv.requestFocusFromTouch();
        editText.requestFocus();
        editText.requestFocusFromTouch();
    }

    public void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        final Animation anim_in = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

    @Override
    public void onResult(ai.api.model.AIResponse response) {


        Result result = response.getResult();

        String message = result.getResolvedQuery();
        ChatMessage chatMessage0 = new ChatMessage(message, "user");
        ref.child("chat").push().setValue(chatMessage0);
        Log.e("Test", "chatIs2" + chatMessage0.getMsgText());

        String reply = result.getFulfillment().getSpeech();
        ChatMessage chatMessage = new ChatMessage(reply, "bot");
        ref.child("chat").push().setValue(chatMessage);
        Log.e("Test", "chatIs2" + chatMessage.getMsgText());

    }

    @Override
    public void onError(ai.api.model.AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }
}
