package com.codebee.v2.memennit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codebee.v2.memennit.Model.Comment;
import com.codebee.v2.memennit.Model.Post;
import com.codebee.v2.memennit.Model.Reply;
import com.codebee.v2.memennit.R;
import com.codebee.v2.memennit.Util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolderClass> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Comment> myArr;
    private Post post;
    private EditText reply_content_txt;
    private Button reply_button;
    private UserApi userApi = UserApi.getInstance();
    private ProgressDialog pd ;
    private Context context;
    private CardView replyTo;
    private RecyclerView.Adapter adapter;
    private ArrayList<Reply> newReplyArrayList;
    private Reply reply;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private View.OnClickListener x,y;
    private String m_Text;

    public CommentAdapter(ArrayList<Comment> myArr,Post post) {
        this.myArr = myArr;
        this.post = post;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_view,parent,false);
        context = parent.getContext();
        return new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        holder.username_txt.setText(myArr.get(position).getUsername());
        holder.comment_txt.setText(myArr.get(position).getCommentContent());
        Activity activity = (Activity) context;
        reply_content_txt = activity.findViewById(R.id.comment_text);
        reply_button = activity.findViewById(R.id.post_button);
        replyTo = activity.findViewById(R.id.replyToBox);

        x = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadReplies(position,holder);
            }
        };

        y = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.replies.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.GONE);
                holder.replies_found_txt.setVisibility(View.GONE);
                holder.show_replies_txt.setText("View replies");
                holder.show_replies_txt.setOnClickListener(x);
            }
        };

        if(holder.show_replies_txt.getText().toString().equals("View replies")){
            holder.show_replies_txt.setOnClickListener(x);
        }

        if(holder.show_replies_txt.getText().toString().equals("Hide replies")){
            holder.show_replies_txt.setOnClickListener(y);
        }

        holder.reply_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply_content_txt.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(reply_content_txt, InputMethodManager.SHOW_IMPLICIT);
                ((TextView)replyTo.findViewById(R.id.user_text)).setText("Reply to " + myArr.get(position).getUsername());
                replyTo.setVisibility(View.VISIBLE);

                (replyTo.findViewById(R.id.cross_image)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        replyTo.setVisibility(View.GONE);
                        reply_button.setOnClickListener((View.OnClickListener) activity);
                    }
                });

                reply_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Reply reply = new Reply();
                        reply.setUsername(userApi.getUsername());
                        reply.setCommentId(myArr.get(position).getId());
                        reply.setReplyContent(reply_content_txt.getText().toString());

                        postReply(reply,position,holder);
                    }
                });
            }
        });

        holder.options_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentOptions(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return myArr.size();
    }


    public class ViewHolderClass extends RecyclerView.ViewHolder{

        public ImageView options_img;
        public TextView username_txt,comment_txt,reply_txt,show_replies_txt ,replies_found_txt;
        public RecyclerView replies;
        public ProgressBar progressBar;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            options_img = itemView.findViewById(R.id.comment_options_image);
            username_txt = itemView.findViewById(R.id.username_comment_text);
            comment_txt = itemView.findViewById(R.id.comment_content_text);
            reply_txt = itemView.findViewById(R.id.reply_text);
            show_replies_txt = itemView.findViewById(R.id.show_replies_text);
            replies = itemView.findViewById(R.id.replies_recycler_view);
            replies_found_txt = itemView.findViewById(R.id.replies_found_text);
            progressBar = itemView.findViewById(R.id.replies_progress_bar);

        }
    }

    public void postReply(Reply reply,int position,ViewHolderClass holder){
        pd = new ProgressDialog(context);
        pd.setMessage("Posting reply...");
        pd.show();

        db.collection("Posts")
                .document(post.getUsername())
                .collection("Comments")
                .document(post.getTime())
                .collection("Replies")
                .document()
                .set(reply)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("Posts")
                                .document(post.getUsername())
                                .collection("Comments")
                                .document(post.getTime())
                                .collection("Replies")
                                .whereEqualTo("username",reply.getUsername())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                                            reply.setId(snapshot.getId());
                                            db.collection("Posts")
                                                    .document(post.getUsername())
                                                    .collection("Comments")
                                                    .document(post.getTime())
                                                    .collection("Replies")
                                                    .document(reply.getId())
                                                    .update("id",reply.getId());
                                        }
                                        loadReplies(position,holder);
                                        pd.dismiss();
                                        Toast.makeText(context,"Reply posted !",Toast.LENGTH_LONG).show();
                                        sendCommentNotification(reply);
                                        sendReplyNotification(reply,position);
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


    public void loadReplies(int position,ViewHolderClass holder){

        holder.progressBar.setVisibility(View.VISIBLE);

        ArrayList<Reply> replyArrayList = new ArrayList<>();
        db.collection("Posts")
                .document(post.getUsername())
                .collection("Comments")
                .document(post.getTime())
                .collection("Replies")
                .whereEqualTo("commentId",myArr.get(position).getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                                reply = snapshot.toObject(Reply.class);
                                replyArrayList.add(reply);
                        }
                        holder.progressBar.setVisibility(View.GONE);
                        if(replyArrayList.isEmpty()){
                            holder.replies_found_txt.setVisibility(View.VISIBLE);
                        }else{
                            holder.replies_found_txt.setVisibility(View.GONE);
                        }
                        holder.replies.setVisibility(View.VISIBLE);
                        holder.show_replies_txt.setText("Hide replies");
                        holder.show_replies_txt.setOnClickListener(y);
                        adapter = new ReplyAdapter(replyArrayList,post);
                        holder.replies.setLayoutManager(new LinearLayoutManager(context));
                        holder.replies.setAdapter(adapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Failed to load replies !",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showCommentOptions(int position){

        if(myArr.get(position).getUsername().equals(userApi.getUsername())){
            builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.comment_options_user,null);
            view.findViewById(R.id.delete_comment_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteComment(position);
                }
            });
            builder.setView(view);
            dialog = builder.create();
            dialog.show();
        }else{
            builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.comment_options_all,null);
            view.findViewById(R.id.report_comment_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    reportComment(position);
                }
            });
            builder.setView(view);
            dialog = builder.create();
            dialog.show();
        }
    }

    public void deleteComment(int position){
        pd = new ProgressDialog(context);
        pd.setMessage("Please wait...");
        pd.show();

        db.collection("Posts")
                .document(post.getUsername())
                .collection("Comments")
                .document(post.getTime())
                .collection("Comments")
                .document(myArr.get(position).getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("Posts")
                                .document(post.getUsername())
                                .collection("Comments")
                                .document(post.getTime())
                                .collection("Replies")
                                .whereEqualTo("commentId",myArr.get(position).getId())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                                            if(snapshot.exists()){
                                                db.collection("Posts")
                                                        .document(post.getUsername())
                                                        .collection("Comments")
                                                        .document(post.getTime())
                                                        .collection("Replies")
                                                        .document(snapshot.getId())
                                                        .delete();
                                            }
                                        }
                                        myArr.remove(position);
                                        notifyDataSetChanged();
                                        pd.dismiss();
                                        Toast.makeText(context,"Comment deleted !",Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Failed to delete comment !",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void reportComment(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("What's wrong with the comment ?");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT );
        builder.setView(input);
        builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                m_Text = input.getText().toString();
                pd = new ProgressDialog(context);
                pd.setMessage("Please wait...");
                pd.show();

                Map<String,String> data = new HashMap<>();
                data.put("reportedBy",userApi.getUsername());
                data.put("desc",m_Text);

                db.collection("Reports")
                        .document("Comments")
                        .collection(myArr.get(position).getUsername())
                        .document(myArr.get(position).getId())
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pd.dismiss();
                                Toast.makeText(context,"Reported successfully !",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(context,"Unable to report !",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void sendCommentNotification(Reply r){
        Map<String,String> data = new HashMap<>();
        data.put("recieverUsername",post.getUsername());
        data.put("title",userApi.getFName() + " replied to a comment on your post.");
        data.put("content",r.getReplyContent());

       db.collection("Notifications")
               .document()
               .set(data);
    }

    private void sendReplyNotification(Reply r, int position) {
        Map<String,String> data = new HashMap<>();
        data.put("recieverUsername",myArr.get(position).getUsername());
        data.put("title",userApi.getFName() + " replied to your comment on " + post.getUsername() + "'s post.");
        data.put("content",r.getReplyContent());

        db.collection("Notifications")
                .document()
                .set(data);
    }

}
