package com.example.android_chat_application;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText messageEditText;
    private Button sendButton;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        recyclerView = findViewById(R.id.recyclerView);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
            return;
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        loadMessages();
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();

        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Map<String, Object> message = new HashMap<>();
            message.put("text", messageText);
            message.put("sender", user.getUid());
            message.put("timestamp", System.currentTimeMillis());

            db.collection("messages").add(message);
            messageEditText.setText("");
        }
    }

    private void loadMessages() {
        db.collection("messages").orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, "Error loading messages", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Message message = dc.getDocument().toObject(Message.class);
                            messageList.add(message);
                            messageAdapter.notifyItemInserted(messageList.size() - 1);
                            recyclerView.scrollToPosition(messageList.size() - 1);
                            break;
                    }
                }
            }
        });
    }
}