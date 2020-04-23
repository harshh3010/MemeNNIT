package com.codebee.v2.memennit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codebee.v2.memennit.Model.Post;
import com.codebee.v2.memennit.Model.User;
import com.codebee.v2.memennit.ui.PostAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class UploadsActivity extends AppCompatActivity {

    private TextView streaks_txt,max_streaks_txt,username_txt,uploads_count_txt;
    private ImageView close_img,streaks_img;
    private RecyclerView uploadsRecyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<Post> uploadsArrayList;
    private String username;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploads);

        streaks_txt = findViewById(R.id.user_streaks_text);
        max_streaks_txt = findViewById(R.id.max_streaks_text);
        username_txt = findViewById(R.id.uploads_username_text);
        uploads_count_txt = findViewById(R.id.user_uploads_count_text);
        close_img = findViewById(R.id.close_uploads_image);
        uploadsRecyclerView = findViewById(R.id.uploads_recycler_view);
        streaks_img = findViewById(R.id.user_streaks_image);
        progressBar = findViewById(R.id.user_uploads_progress_bar);

        username = getIntent().getStringExtra("username");

        username_txt.setText(username);
        close_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadData();
    }

    public void loadData(){
        progressBar.setVisibility(View.VISIBLE);
        streaks_img.setVisibility(View.GONE);
        streaks_txt.setVisibility(View.GONE);
        max_streaks_txt.setVisibility(View.GONE);
        uploads_count_txt.setVisibility(View.GONE);

        db.collection("Users")
                .document(username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        streaks_txt.setText(user.getStreak());
                        max_streaks_txt.setText("Highest " + user.getMaxStreak());

                        loadPosts();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadsActivity.this,"Unable to load user data !" ,Toast.LENGTH_LONG).show();
            }
        });
    }

    public  void loadPosts(){
        uploadsArrayList = new ArrayList<>();
        db.collection("Posts")
                .document(username)
                .collection(username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                            Post post = snapshot.toObject(Post.class);
                            uploadsArrayList.add(post);
                        }
                        Collections.sort(uploadsArrayList, new Comparator<Post>() {
                            public int compare(Post p1, Post p2) {
                                return p2.getTime().compareTo(p1.getTime());
                            }
                        });
                        adapter  = new PostAdapter(uploadsArrayList);
                        uploadsRecyclerView.setLayoutManager(new LinearLayoutManager(UploadsActivity.this));
                        uploadsRecyclerView.setAdapter(adapter);
                        uploads_count_txt.setText(uploadsArrayList.size() + " Uploads");

                        progressBar.setVisibility(View.GONE);
                        streaks_img.setVisibility(View.VISIBLE);
                        streaks_txt.setVisibility(View.VISIBLE);
                        max_streaks_txt.setVisibility(View.VISIBLE);
                        uploads_count_txt.setVisibility(View.VISIBLE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadsActivity.this,"Unable to load posts !" ,Toast.LENGTH_LONG).show();
            }
        });
    }
}
