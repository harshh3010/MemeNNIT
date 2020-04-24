package com.codebee.v2.memennit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.codebee.v2.memennit.Model.Title;
import com.codebee.v2.memennit.Model.User;
import com.codebee.v2.memennit.Util.UserApi;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    private ImageView options_img;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserApi userApi = UserApi.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        options_img = findViewById(R.id.user_options_image);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        options_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(PostActivity.this, v);

                // This activity implements OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.option_logout:
                                logout();
                                return true;
                            case R.id.option_edit_profile:
                                editProfile();
                                return true;
                            default: return false;
                        }
                    }
                });
                popup.inflate(R.menu.user_options_menu);
                popup.show();
            }
        });

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,R.id.navigation_leaderboard, R.id.navigation_post, R.id.navigation_profile)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        checkLevel();
        checkLikes();

    }

    private void checkLikes() {
        db.collection("Posts")
                .document(userApi.getUsername())
                .collection("Upvotes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                            List<String> list = new ArrayList<>();
                            int upvotes = 0;
                            Map<String, Object> map = snapshot.getData();
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                if(entry.getValue().toString().equals("1"))
                                    upvotes++;
                            }
                            db.collection("Posts")
                                    .document(userApi.getUsername())
                                    .collection(userApi.getUsername())
                                    .document(snapshot.getId())
                                    .update("upvotes",String.valueOf(upvotes));
                        }
                    }
                });
    }

    public void logout(){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign out of MemeNNIT");
        builder.setMessage("Do you wish to sign out ?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Intent i = new Intent(PostActivity.this,MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }else{
                                    dialog.dismiss();
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public void editProfile(){
        Intent i = new Intent(PostActivity.this,EditProfile.class);
        startActivity(i);
    }

    public void checkLevel(){
        db.collection("Users")
                .document(userApi.getUsername())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        String level = documentSnapshot.get("level").toString();
                        if(!level.equals(userApi.getLevel())){
                            if(Integer.parseInt(level) > Integer.parseInt(userApi.getLevel())){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                                View v = getLayoutInflater().inflate(R.layout.level_up_dialog,null);
                                ((TextView)v.findViewById(R.id.dialog_level_text)).setText(level);
                                builder.setView(v);
                                AlertDialog dialog = builder.create();
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.show();
                                checkTitles(Integer.parseInt(level));
                            }
                            else
                                Toast.makeText(PostActivity.this,"Level Down" ,Toast.LENGTH_LONG).show();
                            userApi.setLevel(level);
                        }
                    }
                });
    }

    public void checkTitles(int level){

        String x = "title1";
        switch(level){
            case 20 : x = "title2";
            break;
            case 30 : x = "title3";
                break;
            case 40 : x = "title4";
                break;
            case 50 : x = "title5";
                break;
            case 60 : x = "title6";
                break;
            case 70 : x = "title7";
                break;
            case 80 : x = "title8";
                break;
            case 90 : x = "title9";
                break;
            case 100 : x = "title10";
                break;
            }
        String finalX = x;
        db.collection("Users")
                .document(userApi.getUsername())
                .collection("Titles")
                .document(x)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Title title = documentSnapshot.toObject(Title.class);
                        if(title.getStatus().equals("locked")){
                            userApi.setTitle(title.getName());
                            db.collection("Users")
                                    .document(userApi.getUsername())
                                    .update("title",userApi.getTitle());
                            db.collection("Users")
                                    .document(userApi.getUsername())
                                    .collection("Titles")
                                    .document(finalX)
                                    .update("status","unlocked");
                        }
                    }
                });
    }


}


