package com.example.sunquest.midas_project;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Created by SunQuest on 1/6/2018.
 */

public class CommentariesListAdapter extends RecyclerView.Adapter<CommentariesListAdapter.MyViewHolder> {
    Context context;
    private final LayoutInflater inflater;
    List<InfoComment> comment_data= Collections.emptyList();

    static ReviewBackgroundTask likeManagerTask;

    public CommentariesListAdapter(Context context, List<InfoComment> comment_data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.comment_data=comment_data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.custom_comment_row,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.usernameTextView.setText(comment_data.get(position).username);
        holder.commentTextView.setText(comment_data.get(position).comment);


        holder.bufferingProgressBar.setVisibility(View.VISIBLE);
        likeManagerTask = new ReviewBackgroundTask(context,holder.bufferingProgressBar,holder.likesNumber,holder.likeButton);
        likeManagerTask.execute("getLikesNumber", comment_data.get(position).id_comment, String.valueOf(MainMenuActivity.CURRENT_USER_ID));

        String path="http://"+General.SERVER_IP+"/client/uploads/user"+comment_data.get(position).id_user+".png";
        Picasso.with(context)
                .load(path)
                .placeholder(R.drawable.avatar_icon)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(holder.userAvatar);

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!General.CONNECTION_BUFFERING) {

                    holder.bufferingProgressBar.setVisibility(View.VISIBLE);

                    likeManagerTask = new ReviewBackgroundTask(context,holder.bufferingProgressBar,holder.likesNumber,holder.likeButton);
                    likeManagerTask.execute("likeComment", comment_data.get(position).id_comment, String.valueOf(MainMenuActivity.CURRENT_USER_ID));

                }

            }
        });

        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    //This is the ViewHolder for an equipment
    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView;
        TextView commentTextView;
        ImageView userAvatar;
        ImageView likeButton;
        TextView likesNumber;
        TextView reply;

        ProgressBar bufferingProgressBar;

        public MyViewHolder(View itemView) {

            super(itemView);

            usernameTextView=itemView.findViewById(R.id.usernameTextView);
            commentTextView=itemView.findViewById(R.id.commentContentTextView);
            userAvatar=itemView.findViewById(R.id.userAvatar);
            likeButton=itemView.findViewById(R.id.likeButton);
            likesNumber=itemView.findViewById(R.id.likesNumberTextView);
            reply=itemView.findViewById(R.id.replyTextView);

            bufferingProgressBar=itemView.findViewById(R.id.bufferingProgressBar);

        }

    }

    @Override
    public int getItemCount() {
        return comment_data.size();
    }

}