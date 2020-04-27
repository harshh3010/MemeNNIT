package com.codebee.v2.memennit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codebee.v2.memennit.Model.Notification;
import com.codebee.v2.memennit.Model.Post;
import com.codebee.v2.memennit.R;
import com.codebee.v2.memennit.Util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NotificationFragment extends Fragment {

    private RecyclerView notificationsRecyclerView;
    private ArrayList<Notification> notificationArrayList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserApi userApi = UserApi.getInstance();
    private RecyclerView.Adapter adapter;
    private TextView no_notifications_txt;
    private ProgressBar progressBar;

    public NotificationFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications,container,false);

        notificationsRecyclerView = view.findViewById(R.id.notification_recycler_view);
        no_notifications_txt = view.findViewById(R.id.no_notifications_text);
        progressBar = view.findViewById(R.id.notifications_progressBar);

        notificationsRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        no_notifications_txt.setVisibility(View.GONE);

        loadNotifications();

        return view;
    }

    private void loadNotifications(){
        notificationArrayList = new ArrayList<>();
        db.collection("Notifications")
                .whereEqualTo("recieverUsername",userApi.getUsername())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                            Notification notification = snapshot.toObject(Notification.class);
                            notification.setId(snapshot.getId());
                            notificationArrayList.add(notification);
                        }
                        Collections.sort(notificationArrayList, new Comparator<Notification>() {
                            public int compare(Notification n1, Notification n2) {
                                return n2.getTime().compareTo(n1.getTime());
                            }
                        });
                        adapter = new NotificationAdapter(notificationArrayList);
                        notificationsRecyclerView.setAdapter(adapter);
                        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        if(notificationArrayList.isEmpty()){
                            progressBar.setVisibility(View.GONE);
                            notificationsRecyclerView.setVisibility(View.GONE);
                            no_notifications_txt.setVisibility(View.VISIBLE);
                        }else{
                            progressBar.setVisibility(View.GONE);
                            notificationsRecyclerView.setVisibility(View.VISIBLE);
                            no_notifications_txt.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Failed to load notifications !",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
