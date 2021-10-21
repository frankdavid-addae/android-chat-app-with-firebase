package com.example.androidchatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private ArrayList<Messages> messages;
    private String senderImg, receiverImg;
    private Context context;

    FirebaseAuth firebaseAuthInstance = FirebaseAuth.getInstance();

    public MessageAdapter(ArrayList<Messages> messages, String senderImg, String receiverImg, Context context) {
        this.messages = messages;
        this.senderImg = senderImg;
        this.receiverImg = receiverImg;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_holder, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        holder.tvMessage.setText(messages.get(position).getContent());

        ConstraintLayout constraintLayout = holder.cLayout;
        if (messages.get(position).getSender().equals(firebaseAuthInstance.getCurrentUser().getEmail())) {
            Glide.with(context).load(senderImg).error(R.drawable.ic_account).placeholder(R.drawable.ic_account)
                .into(holder.ivChatProfileImg);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(R.id.cvProfile, ConstraintSet.LEFT);
            constraintSet.clear(R.id.tvMessage, ConstraintSet.LEFT);
            constraintSet.connect(
                R.id.cvProfile, ConstraintSet.RIGHT,
                R.id.cLayout, ConstraintSet.RIGHT, 0
            );
            constraintSet.connect(
                R.id.tvMessage, ConstraintSet.RIGHT,
                R.id.cvProfile, ConstraintSet.LEFT, 0
            );
            constraintSet.applyTo(constraintLayout);
        } else {
            Glide.with(context).load(receiverImg).error(R.drawable.ic_account).placeholder(R.drawable.ic_account)
                    .into(holder.ivChatProfileImg);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(R.id.cvProfile, ConstraintSet.RIGHT);
            constraintSet.clear(R.id.tvMessage, ConstraintSet.RIGHT);
            constraintSet.connect(
                    R.id.cvProfile, ConstraintSet.LEFT,
                    R.id.cLayout, ConstraintSet.LEFT, 0
            );
            constraintSet.connect(
                    R.id.tvMessage, ConstraintSet.LEFT,
                    R.id.cvProfile, ConstraintSet.RIGHT, 0
            );
            constraintSet.applyTo(constraintLayout);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cLayout;
        CardView cvProfile;
        ImageView ivChatProfileImg;
        TextView tvMessage;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            cLayout = itemView.findViewById(R.id.cLayout);
            cvProfile = itemView.findViewById(R.id.cvProfile);
            ivChatProfileImg = itemView.findViewById(R.id.ivChatProfileImg);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }
}
