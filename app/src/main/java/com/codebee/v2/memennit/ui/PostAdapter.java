package com.codebee.v2.memennit.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.codebee.v2.memennit.CommentActivity;
import com.codebee.v2.memennit.Model.Notification;
import com.codebee.v2.memennit.Model.Post;
import com.codebee.v2.memennit.Model.User;
import com.codebee.v2.memennit.PostActivity;
import com.codebee.v2.memennit.R;
import com.codebee.v2.memennit.Util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static android.view.WindowManager.LayoutParams.FLAG_BLUR_BEHIND;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolderClass> {

    private ArrayList<Post> myArr;
    private Context context;
    private AlertDialog.Builder builder1,builder2;
    private AlertDialog dialog1,dialog2,dialog3;
    private Button edit_btn,delete_btn,report_btn;
    private EditText caption_txt;
    private UserApi userApi = UserApi.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference ref = FirebaseStorage.getInstance().getReference("Posts");
    private ProgressDialog pd;

    public PostAdapter(ArrayList<Post> myArr) {
        this.myArr = myArr;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_view,parent,false);
        context = parent.getContext();
        return  new ViewHolderClass(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderClass holder, final int position) {

        // To set the username
        holder.username_txt.setText(myArr.get(position).getUsername());

        // To set the caption
        if(myArr.get(position).getCaption().isEmpty()){
            holder.caption_txt.setVisibility(View.GONE);
        }else{
            holder.caption_txt.setText(myArr.get(position).getCaption());
        }

        // To display the profile picture
        db.collection("Users")
                .document(myArr.get(position).getUsername())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Picasso.get().load(documentSnapshot.get("dpurl").toString()).into(holder.dp_img);
                    }
                });

        // To display the post image
        Picasso.get().load(myArr.get(position).getPostUrl()).into(holder.post_img);

        // To display the likes on the post
        holder.likes_txt.setText(myArr.get(position).getUpvotes());

        // To display the time when the post was uploaded
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(Long.parseLong(myArr.get(position).getTime()));
        holder.time_txt.setText(timeAgo);

        // Checking whether the post is liked by the user or not
        holder.liked_state.setText("0");
        db.collection("Posts")
                .document(myArr.get(position).getUsername())
                .collection("Upvotes")
                .document(myArr.get(position).getTime())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()
                                && documentSnapshot.contains(userApi.getUsername())
                                && documentSnapshot.get(userApi.getUsername()).equals("1")){
                            holder.like_img.setImageResource(R.drawable.liked_state);
                            holder.liked_state.setText("1");
                            holder.like_img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    isLiked(position,holder);
                                }
                            });
                        }else{
                            holder.like_img.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                            holder.liked_state.setText("0");
                            holder.like_img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    isLiked(position,holder);
                                }
                            });
                        }
                    }
                });

        holder.username_txt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                if(context instanceof PostActivity){
                    String username = myArr.get(position).getUsername();
                    if(username.equals(userApi.getUsername())){
                        Navigation.findNavController(v).navigate(R.id.navigation_profile);
                    }else{
                        Bundle bundle = new Bundle();
                        bundle.putString("username",username);
                        Navigation.findNavController(v).navigate(R.id.navigation_profile2,bundle);
                    }
                }else{
                    AlertDialog.Builder b = new AlertDialog.Builder(context);
                    View view = LayoutInflater.from(context).inflate(R.layout.dialog_meme1,null);
                    b.setView(view);
                    AlertDialog d = b.create();
                    d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    d.show();
                }
            }
        });
        holder.menu_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(holder.username_txt.getText().toString(),position);
            }
        });

        holder.comment_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentBox(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return myArr.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder{

        public ImageView dp_img,post_img,menu_img,like_img,comment_img;
        public TextView username_txt,caption_txt,likes_txt,time_txt,liked_state;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            dp_img = itemView.findViewById(R.id.dp_image);
            post_img = itemView.findViewById(R.id.post_image);
            menu_img = itemView.findViewById(R.id.menu_image);
            like_img = itemView.findViewById(R.id.like_image);
            likes_txt = itemView.findViewById(R.id.like_text);
            caption_txt = itemView.findViewById(R.id.caption_text);
            username_txt = itemView.findViewById(R.id.username_text);
            time_txt = itemView.findViewById(R.id.time_text);
            comment_img = itemView.findViewById(R.id.comment_image);
            liked_state = itemView.findViewById(R.id.liked_state_text);

        }
    }

    private void showPopup(String username, final int position) {
        if(username.equals(userApi.getUsername())){
            builder1 = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.post_alert_dialog_user,null);
            edit_btn = view.findViewById(R.id.edit_post_button);
            delete_btn = view.findViewById(R.id.delete_post_button);

            edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editPost(position);
                }
            });
            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                    deletePost(position);
                }
            });

            builder1.setView(view);
            dialog1 = builder1.create();
            dialog1.show();}
        else if(!username.equals(userApi.getUsername())){
            builder1 = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.post_alert_dialog_all,null);
            report_btn = view.findViewById(R.id.report_post_button);

            report_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportPost(position);
                }
            });

            builder1.setView(view);
            dialog1 = builder1.create();
            dialog1.show();
        }
    }

    private void reportPost(int position) {
        //TODO : code to report post
    }

    private void deletePost(final int position) {
        pd = new ProgressDialog(context);
        pd.setMessage("Please wait...");
        pd.show();
        ref.child(myArr.get(position).getUsername())
                .child(myArr.get(position).getTime())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db.collection("Posts")
                        .document(myArr.get(position).getUsername())
                        .collection(myArr.get(position).getUsername())
                        .document(myArr.get(position).getTime()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        long t1 = myArr.get(position).getTimeInLong();
                        long t2 = System.currentTimeMillis();
                        long diff = (t2 - t1)/1000/60/60/24;

                        int Level = Integer.parseInt(userApi.getLevel());

                        if(diff < 1){
                            int Streak = Integer.parseInt(userApi.getStreak());
                            int MaxStreak  = Integer.parseInt(userApi.getMaxStreak());
                            int XP  = Integer.parseInt(userApi.getXP());

                            XP = XP - 100 - 2*(Streak + MaxStreak);
                            Streak = 0;
                            Level = XP/1000 + 1;

                            userApi.setStreak(String.valueOf(Streak));
                            userApi.setMaxStreak(String.valueOf(MaxStreak));
                            userApi.setXP(String.valueOf(XP));
                        }
                        myArr.remove(position);
                        notifyDataSetChanged();
                        pd.dismiss();
                        Toast.makeText(context,"Post deleted successfully !",Toast.LENGTH_LONG).show();

                        db.collection("Users")
                                .document(userApi.getUsername())
                                .update("streak",userApi.getStreak());
                        db.collection("Users")
                                .document(userApi.getUsername())
                                .update("xp",userApi.getXP());
                        db.collection("Users")
                                .document(userApi.getUsername())
                                .update("level", String.valueOf(Level));
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(context,"An error occured !",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void editPost(final int position) {


        builder2 = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.edit_post_dialog,null);
        Picasso.get().load(myArr.get(position).getPostUrl()).into((ImageView) v.findViewById(R.id.edit_post_image));
        caption_txt = v.findViewById(R.id.edit_caption_text);
        caption_txt.setText(myArr.get(position).getCaption());
        v.findViewById(R.id.save_edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(context);
                pd.setMessage("Please Wait...");
                pd.show();
                updateCaption(position);
            }
        });

        builder2.setView(v);
        dialog2 = builder2.create();
        dialog2.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_slide_up_down;
        dialog1.dismiss();
        dialog2.show();
    }

    private void updateCaption(final int position) {
        db.collection("Posts")
                .document(userApi.getUsername())
                .collection(userApi.getUsername())
                .document(myArr.get(position).getTime())
                .update("caption",caption_txt.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                myArr.get(position).setCaption(caption_txt.getText().toString());
                notifyDataSetChanged();
                pd.dismiss();
                dialog2.dismiss();
                Toast.makeText(context,"Saved changes.",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                dialog2.dismiss();
                Toast.makeText(context,"An error occured !",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void likePost(int position,ViewHolderClass holder){

        if(myArr.get(position).getUsername().equals(userApi.getUsername())){
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            View v = LayoutInflater.from(context).inflate(R.layout.dialog_meme2,null);
            b.setView(v);
            Dialog d = b.create();
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            d.show();
        }

        int upvotes = Integer.parseInt(holder.likes_txt.getText().toString());
        upvotes = upvotes + 1;
        holder.likes_txt.setText(String.valueOf(upvotes));
        holder.like_img.setImageResource(R.drawable.liked_state);
        myArr.get(position).setUpvotes(String.valueOf(upvotes));

        db.collection("Posts")
                .document(myArr.get(position).getUsername())
                .collection("Upvotes")
                .document(myArr.get(position).getTime())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    db.collection("Posts")
                            .document(myArr.get(position).getUsername())
                            .collection("Upvotes")
                            .document(myArr.get(position).getTime())
                            .update(userApi.getUsername(),"1")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            db.collection("Posts")
                                    .document(myArr.get(position).getUsername())
                                    .collection(myArr.get(position).getUsername())
                                    .document(myArr.get(position).getTime())
                                    .update("upvotes",myArr.get(position).getUpvotes());
                            sendNotification(position);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"Failed !",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Map<String,String> data = new HashMap<>();
                    data.put(userApi.getUsername(),"1");
                    db.collection("Posts")
                            .document(myArr.get(position).getUsername())
                            .collection("Upvotes")
                            .document(myArr.get(position).getTime())
                            .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            db.collection("Posts")
                                    .document(myArr.get(position).getUsername())
                                    .collection(myArr.get(position).getUsername())
                                    .document(myArr.get(position).getTime())
                                    .update("upvotes",myArr.get(position).getUpvotes());
                            sendNotification(position);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"Failed !",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void sendNotification(int position) {
        String message = userApi.getFName() + " " + userApi.getLName() + " liked your post.";
        Notification notification = new Notification(message,userApi.getEmail(),myArr.get(position).getEmail());
        if(!userApi.getEmail().equals(myArr.get(position).getEmail())){
            db.collection("notifications")
                    .document(myArr.get(position).getEmail())
                    .collection("userNotifications")
                    .document()
                    .set(notification);
        }
    }

    private void dislikePost(int position,ViewHolderClass holder){

        int upvotes = Integer.parseInt(holder.likes_txt.getText().toString());
        upvotes = upvotes - 1;
        holder.likes_txt.setText(String.valueOf(upvotes));
        holder.like_img.setImageResource(R.drawable.ic_thumb_up_black_24dp);
        myArr.get(position).setUpvotes(String.valueOf(upvotes));

        db.collection("Posts")
                .document(myArr.get(position).getUsername())
                .collection("Upvotes")
                .document(myArr.get(position).getTime())
                .update(userApi.getUsername(),"0").
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("Posts")
                                .document(myArr.get(position).getUsername())
                                .collection(myArr.get(position).getUsername())
                                .document(myArr.get(position).getTime())
                                .update("upvotes",String.valueOf(myArr.get(position).getUpvotes()));
                    }
                });
    }

    private void isLiked(int position, ViewHolderClass holder){
        if(holder.liked_state.getText().toString().equals("1")){
            dislikePost(position,holder);
            holder.liked_state.setText("0");
        }else{
            likePost(position,holder);
            holder.liked_state.setText("1");
        }
    }

    private void showCommentBox(int position){

        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra("username",myArr.get(position).getUsername());
        intent.putExtra("time",myArr.get(position).getTime());
        intent.putExtra("post", myArr.get(position));
        context.startActivity(intent);

    }
}
