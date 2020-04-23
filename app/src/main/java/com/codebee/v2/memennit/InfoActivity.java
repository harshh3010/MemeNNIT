package com.codebee.v2.memennit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codebee.v2.memennit.Model.Title;
import com.codebee.v2.memennit.Model.User;
import com.codebee.v2.memennit.Util.UserApi;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleSignInClient mGoogleSignInClient;
    private EditText fname_txt,lname_txt,bio_txt,username_txt;
    private ImageView dp_img;
    private TextView invalid_txt;
    private Button continue_btn;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        fname_txt = findViewById(R.id.fname_text);
        lname_txt = findViewById(R.id.lname_text);
        bio_txt = findViewById(R.id.bio_text);
        username_txt = findViewById(R.id.username_text);
        dp_img = findViewById(R.id.dp_image);
        invalid_txt = findViewById(R.id.invalid_username_text);
        continue_btn = findViewById(R.id.continue_button);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        continue_btn.setOnClickListener(this);
        dp_img.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.continue_button :
                pd = new ProgressDialog(this);
                pd.setMessage("Please Wait...");
                pd.show();
                setProfile();
            break;
            case R.id.dp_image : selectImage();
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                dp_img.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setProfile() {

        String fname = fname_txt.getText().toString();
        String lname = lname_txt.getText().toString();
        String bio = bio_txt.getText().toString();

        final String username = username_txt.getText().toString();
        final UserApi userApi = UserApi.getInstance();

        userApi.setBio(bio);
        userApi.setFName(fname);
        userApi.setLName(lname);
        userApi.setUsername(username);
        userApi.setLevel("1");
        userApi.setStreak("0");
        userApi.setMaxStreak("0");
        userApi.setRank("0");
        userApi.setTitle("Beginner");
        userApi.setXP("0");
        userApi.setLastPost("0");

        firebaseFirestore.collection("Users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        invalid_txt.setVisibility(View.VISIBLE);
                        pd.dismiss();
                    }else{
                        invalid_txt.setVisibility(View.GONE);
                        if(filePath != null){
                            storageReference.child("ProfilePictures").child(username).putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        storageReference.child("ProfilePictures").child(username).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                userApi.setDPurl(uri.toString());
                                                addUser();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(InfoActivity.this,"Error updating profile picture",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(InfoActivity.this,"You can update your profile picture later.",Toast.LENGTH_LONG).show();
                            addUser();
                        }
                    }
                }
            }
        });
    }

    private void addUser() {

        UserApi userApi = UserApi.getInstance();
        User user = new User();
        user.setBio(userApi.getBio());
        user.setDPurl(userApi.getDPurl());
        user.setEmail(userApi.getEmail());
        user.setFName(userApi.getFName());
        user.setLName(userApi.getLName());
        user.setUserId(userApi.getUserID());
        user.setUsername(userApi.getUsername());
        user.setLevel(userApi.getLevel());
        user.setStreak(userApi.getStreak());
        user.setMaxStreak(userApi.getMaxStreak());
        user.setRank(userApi.getRank());
        user.setTitle(userApi.getTitle());
        user.setXP(userApi.getXP());
        user.setLastPost(userApi.getLastPost());

        firebaseFirestore.collection("Users").document(user.getUsername()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    registerUser();
                    setProgressData();
                }else{
                    pd.dismiss();
                    Toast.makeText(InfoActivity.this,"Error creating profile !",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(InfoActivity.this,"An error occured !",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void registerUser(){
        UserApi userApi = UserApi.getInstance();
        Map<String,String> data  = new HashMap<>();
        data.put("username",userApi.getUsername());
        firebaseFirestore.collection("RegisteredEmails").document(userApi.getEmail()).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(InfoActivity.this,"Profile created sucessfully !",Toast.LENGTH_LONG).show();
                startActivity(new Intent(InfoActivity.this, PostActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(InfoActivity.this,"Error !",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setProgressData(){
        UserApi userApi = UserApi.getInstance();
        firebaseFirestore.collection("Titles")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                            Title title = snapshot.toObject(Title.class);
                            firebaseFirestore.collection("Users")
                                    .document(userApi.getUsername())
                                    .collection("Titles")
                                    .document(snapshot.getId())
                                    .set(title)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(InfoActivity.this,"Error !",Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InfoActivity.this,"Error !",Toast.LENGTH_LONG).show();
            }
        });
    }

}
