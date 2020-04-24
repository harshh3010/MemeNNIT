package com.codebee.v2.memennit.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codebee.v2.memennit.Model.Post;
import com.codebee.v2.memennit.Model.Reply;
import com.codebee.v2.memennit.R;
import com.codebee.v2.memennit.Util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolderClass> {

    private ArrayList<Reply> myArr;
    private UserApi userApi = UserApi.getInstance();
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Context context;
    private ProgressDialog pd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Post post;
    private String m_Text;

    public ReplyAdapter(ArrayList<Reply> myArr, Post post) {
        this.myArr = myArr;
        this.post = post;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_view,parent,false);
        context = parent.getContext();
        return new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        holder.username_txt.setText(myArr.get(position).getUsername());
        holder.reply_txt.setText(myArr.get(position).getReplyContent());

        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReplyOptions(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myArr.size();
    }

    public  class ViewHolderClass extends RecyclerView.ViewHolder{

        public ImageView options;
        public TextView username_txt,reply_txt;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            options = itemView.findViewById(R.id.reply_options_image);
            reply_txt = itemView.findViewById(R.id.reply_content_text);
            username_txt = itemView.findViewById(R.id.username_reply_text);
        }
    }

    public void showReplyOptions(int position){
        if(myArr.get(position).getUsername().equals(userApi.getUsername())){
            builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.comment_options_user,null);
            view.findViewById(R.id.delete_comment_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteReply(position);
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
                    reportReply(position);
                }
            });
            builder.setView(view);
            dialog = builder.create();
            dialog.show();
        }
    }

    public  void deleteReply(int position){
        pd = new ProgressDialog(context);
        pd.setMessage("Please wait...");
        pd.show();

        db.collection("Posts")
                .document(post.getUsername())
                .collection("Comments")
                .document(post.getTime())
                .collection("Replies")
                .document(myArr.get(position).getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        myArr.remove(position);
                        notifyDataSetChanged();
                        pd.dismiss();
                        Toast.makeText(context,"Reply deleted !",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Failed to delete reply !",Toast.LENGTH_LONG).show();
                    }
                });
    }

    public  void reportReply(int position){
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
                        .document("Replies")
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

}
