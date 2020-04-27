package com.codebee.v2.memennit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codebee.v2.memennit.Model.User;
import com.codebee.v2.memennit.Util.UserApi;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private  String Username ;
    UserApi userApi = UserApi.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            Log.w("SignIn Result : ", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(final GoogleSignInAccount account) {
        if(account != null){
            userApi.setEmail(account.getEmail());
            if(account.getPhotoUrl() == null){
                userApi.setDPurl("https://firebasestorage.googleapis.com/v0/b/memennit-codebee.appspot.com/o/ProfilePictures%2Faccimg.jpg?alt=media&token=6cdcce94-cede-488a-a204-5848a1419f1f");
            }else{
                userApi.setDPurl(account.getPhotoUrl().toString());
            }
            userApi.setName(account.getDisplayName());
            userApi.setUserID(account.getId());
            checkUser(account);
        }
    }

    public void checkUser(final GoogleSignInAccount acc){
        db.collection("RegisteredEmails").document(acc.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot ds = task.getResult();
                if(ds.exists()){
                    Username = ds.getString("username");
                    userApi.setUsername(Username);
                    updateData();
                    startActivity(new Intent(MainActivity.this, PostActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(MainActivity.this, InfoActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"An error occured !",Toast.LENGTH_LONG).show();
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
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"An error occured in updating data!",Toast.LENGTH_LONG).show();
            }
        });
    }
}

