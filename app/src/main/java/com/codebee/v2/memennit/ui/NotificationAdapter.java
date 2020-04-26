package com.codebee.v2.memennit.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codebee.v2.memennit.Model.Notification;
import com.codebee.v2.memennit.Model.Post;
import com.codebee.v2.memennit.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolderClass> {

    private ArrayList<Notification> myArr;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                    openPost(getAdapterPosition());
                }
            });

        }
    }

    private  void openPost(int position){
        //TODO : write code to open the post
    }

}
