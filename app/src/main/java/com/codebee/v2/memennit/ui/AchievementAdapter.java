package com.codebee.v2.memennit.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.codebee.v2.memennit.Model.Achievement;
import com.codebee.v2.memennit.R;

import java.util.ArrayList;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolderClass> {
    private ArrayList<Achievement> achievementArrayList;
    private Context context;

    public AchievementAdapter(ArrayList<Achievement> achievementArrayList) {
        this.achievementArrayList = achievementArrayList;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievement_view,parent,false);
        context = parent.getContext();
        return new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        holder.achievement_title_txt.setText(achievementArrayList.get(position).getTitle());
        holder.achievement_status_txt.setText(achievementArrayList.get(position).getStatus());
        if(achievementArrayList.get(position).getStatus().equals("Unlocked")){
            holder.unlock_status.setVisibility(View.VISIBLE);
            holder.achievementLayout.setBackground(ContextCompat.getDrawable(context,R.drawable.achievement_complete_bg));
            holder.achievement_title_txt.setTextColor(Color.BLACK);
            holder.achievement_status_txt.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return achievementArrayList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder{
        TextView achievement_title_txt,achievement_status_txt;
        ImageView unlock_status;
        ConstraintLayout achievementLayout;
        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            achievement_title_txt = itemView.findViewById(R.id.achievement_title_text);
            achievement_status_txt = itemView.findViewById(R.id.achievement_status_text);
            unlock_status = itemView.findViewById(R.id.achievement_status_image);
            achievementLayout = itemView.findViewById(R.id.achievement_layout);
        }
    }

    //This is to test whether commit works or not

}
