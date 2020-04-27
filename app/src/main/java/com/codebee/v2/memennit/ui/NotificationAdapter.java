package com.codebee.v2.memennit.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codebee.v2.memennit.Model.Notification;
import com.codebee.v2.memennit.Model.Post;
import com.codebee.v2.memennit.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolderClass> {

    private ArrayList<Notification> myArr;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Post> post;
    private PostAdapter adapter;
    private RecyclerView post_recyclerView;

    public NotificationAdapter(ArrayList<Notification> myArr) {
        this.myArr = myArr;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_view,parent,false);
        context = parent.getContext();
        return new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        holder.notification_title_txt.setText(myArr.get(position).getTitle());
        holder.notification_body_txt.setText(myArr.get(position).getContent());
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(Long.parseLong(myArr.get(position).getTime()));
        holder.notification_time_txt.setText(timeAgo);
    }

    @Override
    public int getItemCount() {
        return myArr.size();
    }

    public  class ViewHolderClass extends RecyclerView.ViewHolder{
        public TextView notification_title_txt,notification_body_txt,notification_time_txt;
        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            notification_title_txt = itemView.findViewById(R.id.notification_title_text);
            notification_body_txt = itemView.findViewById(R.id.notification_body_text);
            notification_time_txt = itemView.findViewById(R.id.notification_time_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPost(getAdapterPosition(),v);
                }
            });

        }
    }

    private  void openPost(int position,View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.fragment_post_view,null);
        post_recyclerView = v.findViewById(R.id.single_post_recycler_view);
        loadPost(myArr.get(position).getPostUsername(),myArr.get(position).getPostId());
        builder.setView(v);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        db.collection("Notifications")
                .document(myArr.get(position).getId())
                .delete();
    }

    private void loadPost(String username,String time){

        post = new ArrayList<>();

        db.collection("Posts")
                .document(username)
                .collection(username)
                .document(time)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        post.add(documentSnapshot.toObject(Post.class));
                        adapter = new PostAdapter(post);
                        post_recyclerView.setAdapter(adapter);
                        post_recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    }
                });
    }

}
