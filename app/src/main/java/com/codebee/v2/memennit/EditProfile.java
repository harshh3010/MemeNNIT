package com.codebee.v2.memennit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codebee.v2.memennit.Util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class EditProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 0;
    private ImageView back_btn,profile_img;
    private EditText fname_txt,lname_txt,bio_txt;
    private Button save_btn;
    private UserApi userApi = UserApi.getInstance();
    private Uri filePath;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AlertDialog dialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        back_btn = findViewById(R.id.edit_profile_back_button);
        profile_img = findViewById(R.id.edit_profile_image);
        fname_txt = findViewById(R.id.edit_profile_fname_text);
        lname_txt = findViewById(R.id.edit_profile_lname_text);
        bio_txt = findViewById(R.id.edit_profile_bio_text);
        save_btn = findViewById(R.id.edit_profile_save_button);

        loadData();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptions();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fname_txt.getText().toString().isEmpty() || lname_txt.getText().toString().isEmpty()){
                    Toast.makeText(EditProfile.this,"Please fill up the necessary fields.",Toast.LENGTH_LONG).show();
                }else{
                    updateUser();
                }
            }
        });
    }

    public void showOptions(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.edit_profile_pic_dialog,null);
        v.findViewById(R.id.edit_new_profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectImage();
            }
        });
        v.findViewById(R.id.edit_remove_profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                removeImage();
            }
        });
        builder.setView(v);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void removeImage(){
        profile_img.setImageResource(R.drawable.profile_icon);
        storageReference.child("ProfilePictures")
                .child("accimg.jpg")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        db.collection("Users")
                                .document(userApi.getUsername())
                                .update("dpurl",String.valueOf(uri))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        userApi.setDPurl(String.valueOf(uri));
                                        storageReference.child("ProfilePictures").child(userApi.getUsername()).delete();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditProfile.this,"An error occured !",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this,"Unable to update profile picture !",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void selectImage(){
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
                profile_img.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateUser(){
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Please wait...");
        pd.show();

        if(filePath != null){
            storageReference
                    .child("ProfilePictures")
                    .child(userApi.getUsername())
                    .putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference
                                    .child("ProfilePictures")
                                    .child(userApi.getUsername())
                                    .getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            db.collection("Users")
                                                    .document(userApi.getUsername())
                                                    .update("fname",fname_txt.getText().toString());
                                            db.collection("Users")
                                                    .document(userApi.getUsername())
                                                    .update("lname",lname_txt.getText().toString());
                                            db.collection("Users")
                                                    .document(userApi.getUsername())
                                                    .update("bio",bio_txt.getText().toString());
                                            db.collection("Users")
                                                    .document(userApi.getUsername())
                                                    .update("dpurl",String.valueOf(uri));
                                            userApi.setFName(fname_txt.getText().toString());
                                            userApi.setLName(lname_txt.getText().toString());
                                            userApi.setBio(bio_txt.getText().toString());
                                            userApi.setDPurl(String.valueOf(uri));
                                            pd.dismiss();
                                            Toast.makeText(EditProfile.this,"Details updated successfully !",Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(EditProfile.this,"An error occured !",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(EditProfile.this,"Unable to update profile picture !",Toast.LENGTH_LONG).show();
                }
            });
        }else{
            db.collection("Users")
                    .document(userApi.getUsername())
                    .update("fname",fname_txt.getText().toString());
            db.collection("Users")
                    .document(userApi.getUsername())
                    .update("lname",lname_txt.getText().toString());
            db.collection("Users")
                    .document(userApi.getUsername())
                    .update("bio",bio_txt.getText().toString());
            userApi.setFName(fname_txt.getText().toString());
            userApi.setLName(lname_txt.getText().toString());
            userApi.setBio(bio_txt.getText().toString());
            pd.dismiss();
            Toast.makeText(EditProfile.this,"Details updated successfully !",Toast.LENGTH_LONG).show();
        }
    }
    public void loadData(){
        Picasso.get().load(userApi.getDPurl()).into(profile_img);
        fname_txt.setText(userApi.getFName());
        lname_txt.setText(userApi.getLName());
        bio_txt.setText(userApi.getBio());
    }
}
