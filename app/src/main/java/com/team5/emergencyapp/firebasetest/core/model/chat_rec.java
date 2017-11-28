package com.team5.emergencyapp.firebasetest.core.model;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.team5.emergencyapp.firebasetest.R;


public class chat_rec extends RecyclerView.ViewHolder {


    public TextView leftText, rightText;

    public chat_rec(View itemView) {
        super(itemView);

        leftText = (TextView) itemView.findViewById(R.id.leftText);
        rightText = (TextView) itemView.findViewById(R.id.rightText);


    }
}
