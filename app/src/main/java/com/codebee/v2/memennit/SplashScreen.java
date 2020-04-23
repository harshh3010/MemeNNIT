package com.codebee.v2.memennit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.codebee.v2.memennit.Model.User;
import com.codebee.v2.memennit.Util.UserApi;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private  String Username ;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

    }

    private void updateUI(GoogleSignInAccount account) {
        if(account != null){
            UserApi userApi = UserApi.getInstance();
            userApi.setEmail(account.getEmail());
            if(account.getPhotoUrl() == null){
                userApi.setDPurl("https://firebasestorage.googleapis.com/v0/b/memennit-codebee.appspot.com/o/ProfilePictures%2Faccimg.jpg?alt=media&token=6cdcce94-cede-488a-a204-5848a1419f1f");
            }else{
                userApi.setDPurl(account.getPhotoUrl().toString());
            }
            userApi.setName(account.getDisplayName());
            userApi.setUserID(account.getId());
            checkUser(account);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this,MainActivity.class));
                    finish();
                }
            }, 1000);
        }
    }

    public void checkUser(final GoogleSignInAccount acc){
        db.collection("RegisteredEmails").document(acc.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot ds = task.getResult();
                if(ds.exists()){
                    Username = ds.getString("username");
                    updateData();
                }else {
                    startActivity(new Intent(SplashScreen.this, InfoActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SplashScreen.this,"An error occured !",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateData() {
        db.collection("Users").document(Username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot ds = task.getResult();
                    assert ds != null;
                    User user = ds.toObject(User.class);
                    UserApi userApi = UserApi.getInstance();
                    assert user != null;
                    userApi.setBio(user.getBio());
                    userApi.setFName(user.getFName());
                    userApi.setLName(user.getLName());
                    userApi.setUsername(user.getUsername());
                    userApi.setLevel(user.getLevel());
                    userApi.setStreak(user.getStreak());
                    userApi.setMaxStreak(user.getMaxStreak());
                    userApi.setRank(user.getRank());
                    userApi.setTitle(user.getTitle());
                    userApi.setXP(user.getXP());
                    userApi.setLastPost(user.getLastPost());
                    userApi.setDPurl(user.getDPurl());
                    startActivity(new Intent(SplashScreen.this, PostActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SplashScreen.this,"An error occured in updating data!",Toast.LENGTH_LONG).show();
            }
        });
    }
}
