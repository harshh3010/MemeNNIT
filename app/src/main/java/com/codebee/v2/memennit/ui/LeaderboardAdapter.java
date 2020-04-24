package com.codebee.v2.memennit.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.codebee.v2.memennit.Model.User;
import com.codebee.v2.memennit.PostActivity;
import com.codebee.v2.memennit.R;
import com.codebee.v2.memennit.Util.UserApi;

import java.util.ArrayList;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolderClass>  implements Filterable {

    private ArrayList<User> leaderBoardArrayList;
    private ArrayList<User> leaderBoardArrayListFull;
    private Context context;
    private UserApi userApi = UserApi.getInstance();

    public LeaderboardAdapter(ArrayList<User> leaderBoardArrayList) {
        this.leaderBoardArrayList = leaderBoardArrayList;
        leaderBoardArrayListFull = new ArrayList<>(leaderBoardArrayList);
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_view,parent,false);
        context = parent.getContext();
        return new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        holder.rank_txt.setText(leaderBoardArrayList.get(position).getRank());
        holder.username_txt.setText(leaderBoardArrayList.get(position).getUsername());
        holder.xp_txt.setText(leaderBoardArrayList.get(position).getXP() + " XP");
    }

    @Override
    public int getItemCount() {
        return leaderBoardArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<User> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length()==0){
                filteredList.addAll(leaderBoardArrayListFull);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(User user : leaderBoardArrayListFull){
                    if(user.getUsername().toLowerCase().contains(filterPattern)){
                        filteredList.add(user);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            leaderBoardArrayList.clear();
            leaderBoardArrayList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolderClass extends RecyclerView.ViewHolder{
        TextView rank_txt,username_txt,xp_txt;
        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            rank_txt = itemView.findViewById(R.id.leaderboard_rank_text);
            username_txt = itemView.findViewById(R.id.leaderboard_username_text);
            xp_txt = itemView.findViewById(R.id.leaderboard_xp_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = leaderBoardArrayList.get(getAdapterPosition()).getUsername();
                    if(username.equals(userApi.getUsername())){
                        Navigation.findNavController(v).navigate(R.id.navigation_profile);
                    }else{
                        Bundle bundle = new Bundle();
                        bundle.putString("username",username);
                        Navigation.findNavController(v).navigate(R.id.navigation_profile2,bundle);
                    }
                }
            });
        }
    }

}
