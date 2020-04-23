package com.codebee.v2.memennit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.codebee.v2.memennit.AchievementActivity;
import com.codebee.v2.memennit.R;
import com.codebee.v2.memennit.UploadsActivity;
import com.codebee.v2.memennit.Util.UserApi;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private ImageView dp_img;
    private TextView username_txt,rank_txt,user_title_txt,user_level_txt,xp_count_txt;
    private UserApi userApi = UserApi.getInstance();
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Button post_btn,achievement_btn;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        dp_img = root.findViewById(R.id.dp_image);
        username_txt = root.findViewById(R.id.username_text);
        rank_txt = root.findViewById(R.id.rank_text);
        post_btn = root.findViewById(R.id.show_uploads_button);
        achievement_btn = root.findViewById(R.id.show_achievements_button);
        user_title_txt = root.findViewById(R.id.user_title_text);
        user_level_txt = root.findViewById(R.id.user_level_text);
        progressBar = root.findViewById(R.id.user_level_progress_bar);
        xp_count_txt = root.findViewById(R.id.xp_count_text);

        userApi = UserApi.getInstance();
        username_txt.setText(userApi.getUsername());
        user_level_txt.setText("Level " + userApi.getLevel());
        user_title_txt.setText(userApi.getTitle());
        progressBar.setProgress(Integer.parseInt(userApi.getXP())%1000);
        xp_count_txt.setText(String.valueOf(Integer.parseInt(userApi.getXP())%1000) + "/1000 XP");
        if(userApi.getRank().equals("0")){
            rank_txt.setText("Unranked");
        }else{
            rank_txt.setText(userApi.getRank());
        }
        Picasso.get().load(userApi.getDPurl()).into(dp_img);
        dp_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(getContext());
                View view = LayoutInflater.from(getContext()).inflate(R.layout.profile_picture_view,null);
                if( dp_img.getDrawable() == null){

                }else{
                    Bitmap image=((BitmapDrawable)dp_img.getDrawable()).getBitmap();
                    ((ImageView)view.findViewById(R.id.display_image)).setImageBitmap(image);
                }
                ((TextView)view.findViewById(R.id.user_name_text)).setText(userApi.getFName() + " " + userApi.getLName());
                ((TextView)view.findViewById(R.id.user_bio_text)).setText(userApi.getBio());
                builder.setView(view);
                dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        achievement_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AchievementActivity.class);
                startActivity(i);
            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), UploadsActivity.class);
                i.putExtra("username",userApi.getUsername());
                startActivity(i);
            }
        });

        return root;
    }
}