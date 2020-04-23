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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codebee.v2.memennit.AchievementActivity;
import com.codebee.v2.memennit.Model.User;
import com.codebee.v2.memennit.R;
import com.codebee.v2.memennit.UploadsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class ProfileFragment2 extends Fragment {

    private String username;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User user;
    private ImageView dp_img;
    private TextView username_txt,rank_txt,user_title_txt,user_level_txt,xp_count_txt;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Button post_btn,achievement_btn;
    private ProgressBar progressBar,load_progressBar;
    private ScrollView scrollView;

    public ProfileFragment2() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile2,container,false);

        username_txt = root.findViewById(R.id.username_text);
        rank_txt = root.findViewById(R.id.rank_text);
        dp_img = root.findViewById(R.id.dp_image);
        post_btn = root.findViewById(R.id.show_uploads_button);
        achievement_btn = root.findViewById(R.id.show_achievements_button);
        user_title_txt = root.findViewById(R.id.user_title_text);
        user_level_txt = root.findViewById(R.id.user_level_text);
        progressBar = root.findViewById(R.id.user_level_progress_bar);
        xp_count_txt = root.findViewById(R.id.xp_count_text);
        load_progressBar = root.findViewById(R.id.user_profile_progress_bar);
        scrollView = root.findViewById(R.id.user_profile_scroll_view);

        loadUserData();

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
                i.putExtra("username",username);
                startActivity(i);
            }
        });

        return root;
    }

    private void loadUserData() {
        load_progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        username = getArguments().getString("username");
        db.collection("Users")
                .document(username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user = new User();
                        user = documentSnapshot.toObject(User.class);
                        username_txt.setText(user.getUsername());
                        Picasso.get().load(user.getDPurl()).into(dp_img);
                        user_level_txt.setText("Level " + user.getLevel());
                        user_title_txt.setText(user.getTitle());
                        progressBar.setProgress(Integer.parseInt(user.getXP())%1000);
                        xp_count_txt.setText(String.valueOf(Integer.parseInt(user.getXP())%1000) + "/1000 XP");
                        if(user.getRank().equals("0")){
                            rank_txt.setText("Unranked");
                        }else{
                            rank_txt.setText(user.getRank());
                        }
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
                                ((TextView)view.findViewById(R.id.user_name_text)).setText(user.getFName() + " " + user.getLName());
                                ((TextView)view.findViewById(R.id.user_bio_text)).setText(user.getBio());
                                builder.setView(view);
                                dialog = builder.create();
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.show();
                            }
                        });
                        scrollView.setVisibility(View.VISIBLE);
                        load_progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                load_progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(),"Failed to load user profile !",Toast.LENGTH_LONG).show();
            }
        });
    }
}
