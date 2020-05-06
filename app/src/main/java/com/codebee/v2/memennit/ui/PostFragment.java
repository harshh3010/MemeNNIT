package com.codebee.v2.memennit.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.codebee.v2.memennit.Model.Post;
import com.codebee.v2.memennit.Model.User;
import com.codebee.v2.memennit.R;
import com.codebee.v2.memennit.Util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class PostFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    private ImageView post_img;
    private EditText caption_txt;
    private Button upload_btn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("Posts");
    private UserApi userApi = UserApi.getInstance();
    private String timestamp;
    private ProgressDialog pd;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_post, container, false);

        post_img = root.findViewById(R.id.post_image);
        caption_txt = root.findViewById(R.id.caption_text);
        upload_btn = root.findViewById(R.id.upload_btn);

        post_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath == null) {
                    Snackbar.make(getView(), "Please select an image to post !", Snackbar.LENGTH_LONG).show();
                } else {
                    timestamp = String.valueOf(System.currentTimeMillis());
                    pd = new ProgressDialog(getContext());
                    pd.setMessage("Please wait...");
                    pd.show();
                    uploadImage();
                }
            }
        });

        return root;
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);

        if (filePath == null) {
            post_img.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                post_img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        storageReference
                .child(userApi.getUsername())
                .child(timestamp)
                .putFile(filePath)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            storageReference
                                    .child(userApi.getUsername())
                                    .child(timestamp)
                                    .getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            updatePosts(uri);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    resetScreen();
                                    Toast.makeText(getContext(), "An error occured !", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            pd.dismiss();
                            resetScreen();
                            Toast.makeText(getContext(), "Failed to upload image !", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void updatePosts(Uri uri) {
        Post post = new Post();
        post.setCaption(caption_txt.getText().toString().trim());
        post.setPostUrl(uri.toString());
        post.setTime(timestamp);
        post.setUpvotes("0");
        post.setUsername(userApi.getUsername());
        post.setDpUrl(userApi.getDPurl());
        post.setEmail(userApi.getEmail());

        db.collection("Posts")
                .document(userApi.getUsername())
                .collection(userApi.getUsername())
                .document(timestamp)
                .set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update score here
                        updateScore(post);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                resetScreen();
                Toast.makeText(getContext(), "Unable to post !", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resetScreen() {
        filePath = null;
        caption_txt.setText("");
        post_img.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
    }

    public void updateScore(Post post) {

        db.collection("Users")
                .document(userApi.getUsername())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        user = documentSnapshot.toObject(User.class);

                        long t1 = Long.parseLong(user.getLastPost());
                        long t2 = Long.parseLong(post.getTime());
                        long diff = (t2 - t1) / 1000 / 60 / 60 / 24;

                        int Streak = Integer.parseInt(user.getStreak());
                        int MaxStreak = Integer.parseInt(user.getMaxStreak());
                        int Level = Integer.parseInt(user.getLevel());
                        int XP = Integer.parseInt(user.getXP());

                        if (diff == 1) {
                            Streak = Streak + 1;
                            if (Streak > MaxStreak) {
                                MaxStreak = Streak;
                            }
                        } else if (diff > 1) {
                            Streak = 0;
                        }

                        XP = XP + 100 + 2 * (Streak + MaxStreak);

                        Level = XP / 1000 + 1;

                        userApi.setStreak(String.valueOf(Streak));
                        userApi.setMaxStreak(String.valueOf(MaxStreak));
                        userApi.setXP(String.valueOf(XP));

                        if (diff >= 1) {
                            userApi.setLastPost(post.getTime());
                        }

                        user.setStreak(userApi.getStreak());
                        user.setMaxStreak(userApi.getMaxStreak());
                        user.setXP(userApi.getXP());
                        user.setLevel(String.valueOf(Level));
                        user.setLastPost(userApi.getLastPost());

                        db.collection("Users")
                                .document(userApi.getUsername())
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        resetScreen();
                                        pd.dismiss();
                                        Toast.makeText(getContext(), "Posted successfully !", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to update data !", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
    }
}