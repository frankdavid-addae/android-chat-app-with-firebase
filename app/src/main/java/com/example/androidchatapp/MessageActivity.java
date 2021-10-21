package com.example.androidchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private ImageView ivToolbarImg, ivSendMessage;
    private TextView tvChattingWith;
    private ProgressBar progressMessage;
    private EditText etMessage;

    private MessageAdapter messageAdapter;
    private ArrayList<Messages> messages;

    private String usernameOfChatMate, emailOfChatMate, chatRoomId;

    FirebaseAuth firebaseAuthInstance = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabaseInstance = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        usernameOfChatMate = getIntent().getStringExtra("username_of_chat_mate");
        emailOfChatMate = getIntent().getStringExtra("email_of_chat_mate");

        rvMessages = findViewById(R.id.rvMessages);
        ivToolbarImg = findViewById(R.id.ivToolbarImg);
        ivSendMessage = findViewById(R.id.ivSendMessage);
        tvChattingWith = findViewById(R.id.tvChattingWith);
        progressMessage = findViewById(R.id.progressMessage);
        etMessage = findViewById(R.id.etMessage);

        tvChattingWith.setText(usernameOfChatMate);

        messages = new ArrayList<>();

        ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabaseInstance.getReference("messages/" + chatRoomId).push().setValue(
                    new Messages(firebaseAuthInstance.getCurrentUser().getEmail(), emailOfChatMate,
                        etMessage.getText().toString()
                    )
                );
                etMessage.setText("");
            }
        });

        messageAdapter = new MessageAdapter(messages,
                getIntent().getStringExtra("current_user_image"),
                getIntent().getStringExtra("image_of_chat_mate"),
                MessageActivity.this
        );
        rvMessages.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        rvMessages.setAdapter(messageAdapter);

        Glide.with(MessageActivity.this).load(getIntent().getStringExtra("image_of_chat_mate"))
            .error(R.drawable.ic_account).placeholder(R.drawable.ic_account).into(ivToolbarImg);

        setUpChatRoom();

    }

    private void setUpChatRoom() {
        firebaseDatabaseInstance.getReference("user/" + firebaseAuthInstance.getUid())
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String currentUsername = snapshot.getValue(User.class).getUsername();
                    if (usernameOfChatMate.compareTo(currentUsername) > 0) {
                        chatRoomId = currentUsername + usernameOfChatMate;
                    } else if (usernameOfChatMate.compareTo(currentUsername) == 0) {
                        chatRoomId = currentUsername + usernameOfChatMate;
                    } else {
                        chatRoomId = usernameOfChatMate + currentUsername;
                    }
                    attachMessageListener(chatRoomId);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }
        );
    }

    private void attachMessageListener(String chatRoomId) {
        firebaseDatabaseInstance.getReference("messages/" + chatRoomId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    messages.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        messages.add(dataSnapshot.getValue(Messages.class));
                    }
                    messageAdapter.notifyDataSetChanged();
                    rvMessages.scrollToPosition(messages.size() - 1);
                    rvMessages.setVisibility(View.VISIBLE);
                    progressMessage.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }
        );
    }
}