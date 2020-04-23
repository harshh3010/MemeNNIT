package com.codebee.v2.memennit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codebee.v2.memennit.Model.Comment;
import com.codebee.v2.memennit.Model.Post;
import com.codebee.v2.memennit.Util.UserApi;
import com.codebee.v2.memennit.ui.CommentAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog pd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String post_user,post_time;
    private Button post_btn;
    private EditText comment_txt;
    private UserApi userApi = UserApi.getInstance();
    private RecyclerView commentsRecyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<Comment> commentsArrayList;
    private Post post;
    private ImageView back_img;
    private ProgressBar progressBar;
    private TextView no_comments_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        post_btn = findViewById(R.id.post_button);
        comment_txt = findViewById(R.id.comment_text);
        commentsRecyclerView = findViewById(R.id.comments_recycler_view);
        back_img = findViewById(R.id.back_image);
        progressBar = findViewById(R.id.comments_progress_bar);
        no_comments_txt = findViewById(R.id.no_comments_text);

        progressBar.setVisibility(View.VISIBLE);
        commentsRecyclerView.setVisibility(View.INVISIBLE);

        post = (Post) getIntent().getSerializableExtra("post");

        loadComments();

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comment_txt.getText().toString().isEmpty()){
                    Toast.makeText(CommentActivity.this,"Please enter something before posting...",Toast.LENGTH_LONG).show();
                }else{
                    Comment comment = new Comment();
                    comment.setUsername(userApi.getUsername());
                    comment.setCommentContent(comment_txt.getText().toString());
                    postComment(comment);
                }
            }
        });
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db.collection("Posts")
                .document(post.getUsername())
                .collection("Comments")
                .document(post.getTime())
                .collection("Comments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        loadComments();
                    }
                });

    }

    public void postComment(Comment comment){
        pd = new ProgressDialog(this);
        pd.setMessage("Posting your comment...");
        pd.show();

        db.collection("Posts")
                .document(post.getUsername())
                .collection("Comments")
                .document(post.getTime())
                .collection("Comments")
                .document()
                .set(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("Posts")
                                .document(post.getUsername())
                                .collection("Comments")
                                .document(post.getTime())
                                .collection("Comments")
                                .whereEqualTo("username",comment.getUsername())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                                            comment.setId(snapshot.getId());
                                            db.collection("Posts")
                                                    .document(post.getUsername())
                                                    .collection("Comments")
                                                    .document(post.getTime())
                                                    .collection("Comments")
                                                    .document(comment.getId())
                                                    .update("id",comment.getId());
                                        }
                                        pd.dismiss();
                                        Toast.makeText(CommentActivity.this,"Comment posted !",Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(CommentActivity.this,"Unable to post comment !",Toast.LENGTH_LONG).show();
            }
        });
    }

    public  void loadComments(){
        commentsArrayList = new ArrayList<>();

        db.collection("Posts")
                .document(post.getUsername())
                .collection("Comments")
                .document(post.getTime())
                .collection("Comments")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                            Comment comment = new Comment();
                            comment = snapshot.toObject(Comment.class);
                            commentsArrayList.add(comment);
                        }
                        adapter = new CommentAdapter(commentsArrayList,post);
                        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
                        commentsRecyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        commentsRecyclerView.setVisibility(View.VISIBLE);
                        if(commentsArrayList.isEmpty()){
                            no_comments_txt.setVisibility(View.VISIBLE);
                        }else{
                            no_comments_txt.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommentActivity.this,"Failed to load comments !",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(comment_txt.getText().toString().isEmpty()){
            Toast.makeText(CommentActivity.this,"Please enter something before posting...",Toast.LENGTH_LONG).show();
        }else{
            Comment comment = new Comment();
            comment.setUsername(userApi.getUsername());
            comment.setCommentContent(comment_txt.getText().toString());
            postComment(comment);
        }
    }
}
