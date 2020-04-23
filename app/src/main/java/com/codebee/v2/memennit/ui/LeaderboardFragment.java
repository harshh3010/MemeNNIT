package com.codebee.v2.memennit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codebee.v2.memennit.Model.User;
import com.codebee.v2.memennit.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class LeaderboardFragment extends Fragment {

    private RecyclerView leaderboardRecyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<User> leaderboardArrayList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LeaderboardFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard,container,false);

        leaderboardRecyclerView = view.findViewById(R.id.leaderboard_recycler_view);

        setupLeaderboard();

        return  view;
    }

    public  void setupLeaderboard(){
        leaderboardArrayList = new ArrayList<>();

        db.collection("Users")
                .orderBy("rank")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                            leaderboardArrayList.add(snapshot.toObject(User.class));
                        }
                        adapter = new LeaderboardAdapter(leaderboardArrayList);
                        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        leaderboardRecyclerView.setAdapter(adapter);
                    }
                });
    }

}
