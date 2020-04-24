package com.codebee.v2.memennit.ui;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class LeaderboardFragment extends Fragment{

    private RecyclerView leaderboardRecyclerView;
    private LeaderboardAdapter leaderboardAdapter;
    private ArrayList<User> leaderboardArrayList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SearchView searchView;
    private ProgressBar progressBar;

    public LeaderboardFragment() {
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard,container,false);

        leaderboardRecyclerView = view.findViewById(R.id.leaderboard_recycler_view);
        searchView = view.findViewById(R.id.leaderboard_search_view);
        progressBar = view.findViewById(R.id.leaderboard_progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        leaderboardRecyclerView.setVisibility(View.GONE);

        generateLeaderboard();

        return  view;
    }

    public void generateLeaderboard(){
        leaderboardArrayList = new ArrayList<>();

        db.collection("Users")
                .orderBy("xp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                            leaderboardArrayList.add(snapshot.toObject(User.class));
                        }
                        for(int i = 0;i<leaderboardArrayList.size();i++){
                            leaderboardArrayList.get(i).setRank(String.valueOf(i+1));
                        }
                        leaderboardAdapter = new LeaderboardAdapter(leaderboardArrayList);
                        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        leaderboardRecyclerView.setAdapter(leaderboardAdapter);
                        progressBar.setVisibility(View.GONE);
                        leaderboardRecyclerView.setVisibility(View.VISIBLE);

                        for(int i = 0;i<leaderboardArrayList.size();i++){
                            db.collection("Users")
                                    .document(leaderboardArrayList.get(i).getUsername())
                                    .update("rank",String.valueOf(i+1));
                        }

                    }
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                leaderboardAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                leaderboardAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

}
