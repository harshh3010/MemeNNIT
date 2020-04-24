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
    private SearchView searchView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list;

    public LeaderboardFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard,container,false);

        leaderboardRecyclerView = view.findViewById(R.id.leaderboard_recycler_view);
        searchView = view.findViewById(R.id.leaderboard_search_view);

               searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                   @Override
                   public boolean onQueryTextSubmit(String query) {
                       if(list.contains(query)){
                           arrayAdapter.getFilter().filter(query);

                       }else{
                           Toast.makeText(getContext(), "No Match found",Toast.LENGTH_LONG).show();
                       }
                       return false;
                   }

                   @Override
                   public boolean onQueryTextChange(String newText) {
                       return false;
                   }
               });

        generateLeaderboard();

        return  view;
    }

    public void generateLeaderboard(){
            leaderboardArrayList = new ArrayList<>();
            list = new ArrayList<>();

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
                                list.add(leaderboardArrayList.get(i).getUsername());
                            }
                            arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,list);
                            adapter = new LeaderboardAdapter(leaderboardArrayList);
                            leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            leaderboardRecyclerView.setAdapter(adapter);
                            for(int i = 0;i<leaderboardArrayList.size();i++){
                                db.collection("Users")
                                        .document(leaderboardArrayList.get(i).getUsername())
                                        .update("rank",String.valueOf(i+1));
                            }

                        }
                    });
        }

}
