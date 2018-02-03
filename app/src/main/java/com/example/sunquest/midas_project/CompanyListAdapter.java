package com.example.sunquest.midas_project;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
 * Created by SunQuest on 12/27/2017.
 */

public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.MyViewHolder> {
    Context context;
    private final LayoutInflater inflater;
    List<InfoCompany> company_data= Collections.emptyList();

    public CompanyListAdapter(Context context, List<InfoCompany> company_data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.company_data=company_data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.custom_company_row,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.company_name.setText(company_data.get(position).company_name);
        holder.author_name.setText(company_data.get(position).username_author);
        holder.grade.setText(company_data.get(position).grade);
        holder.reviewsNumber.setText(company_data.get(position).reviews_number+" reviews");
        holder.bufferingProgressBar.setVisibility(View.VISIBLE);

        ReviewBackgroundTask bgTask=new ReviewBackgroundTask(context,holder.bufferingProgressBar,holder.star1,holder.star2,holder.star3,holder.star4,holder.star5);
        bgTask.execute("highlightStars",MainMenuActivity.CURRENT_USER_DATA[0],company_data.get(position).company_id);

        holder.star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!General.CONNECTION_BUFFERING){

                    holder.bufferingProgressBar.setVisibility(View.VISIBLE);

                    MainMenuActivity.RATING_MANAGER_TASK=new ReviewBackgroundTask(context,holder.bufferingProgressBar,holder.star1,holder.star2,holder.star3,holder.star4,holder.star5,holder.grade);
                    MainMenuActivity.RATING_MANAGER_TASK.execute("manageRating","1",MainMenuActivity.CURRENT_USER_DATA[0],company_data.get(position).company_id);

                }
            }
        });

        holder.star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!General.CONNECTION_BUFFERING) {

                    holder.bufferingProgressBar.setVisibility(View.VISIBLE);

                    MainMenuActivity.RATING_MANAGER_TASK=new ReviewBackgroundTask(context,holder.bufferingProgressBar,holder.star1,holder.star2,holder.star3,holder.star4,holder.star5,holder.grade);
                    MainMenuActivity.RATING_MANAGER_TASK.execute("manageRating","2",MainMenuActivity.CURRENT_USER_DATA[0],company_data.get(position).company_id);

                }
            }
        });

        holder.star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!General.CONNECTION_BUFFERING) {

                    holder.bufferingProgressBar.setVisibility(View.VISIBLE);

                    MainMenuActivity.RATING_MANAGER_TASK=new ReviewBackgroundTask(context,holder.bufferingProgressBar,holder.star1,holder.star2,holder.star3,holder.star4,holder.star5,holder.grade);
                    MainMenuActivity.RATING_MANAGER_TASK.execute("manageRating","3",MainMenuActivity.CURRENT_USER_DATA[0],company_data.get(position).company_id);

                }
            }
        });

        holder.star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!General.CONNECTION_BUFFERING) {

                    holder.bufferingProgressBar.setVisibility(View.VISIBLE);

                    MainMenuActivity.RATING_MANAGER_TASK=new ReviewBackgroundTask(context,holder.bufferingProgressBar,holder.star1,holder.star2,holder.star3,holder.star4,holder.star5,holder.grade);
                    MainMenuActivity.RATING_MANAGER_TASK.execute("manageRating","4",MainMenuActivity.CURRENT_USER_DATA[0],company_data.get(position).company_id);

                }
            }
        });

        holder.star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!General.CONNECTION_BUFFERING) {

                    holder.bufferingProgressBar.setVisibility(View.VISIBLE);

                    MainMenuActivity.RATING_MANAGER_TASK=new ReviewBackgroundTask(context,holder.bufferingProgressBar,holder.star1,holder.star2,holder.star3,holder.star4,holder.star5,holder.grade);
                    MainMenuActivity.RATING_MANAGER_TASK.execute("manageRating","5",MainMenuActivity.CURRENT_USER_DATA[0],company_data.get(position).company_id);

                }
            }
        });

        holder.companyViewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!General.CONNECTION_BUFFERING){
                    CompanyDashboardActivity.CURRENT_COMPANY_ID=company_data.get(position).company_id;
                    context.startActivity(new Intent(context,CompanyDashboardActivity.class));
                }

            }
        });

        String path="http://"+General.SERVER_IP+"/client/uploads/company"+company_data.get(position).company_id+".png";
        Picasso.with(context)
                .load(path)
                .placeholder(R.drawable.no_photo)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(holder.logo);

    }

    //This is the ViewHolder for an equipment
    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView company_name;
        ImageView logo;
        TextView author_name;
        TextView grade;
        TextView reviewsNumber;
        ImageView star1;
        ImageView star2;
        ImageView star3;
        ImageView star4;
        ImageView star5;

        ProgressBar bufferingProgressBar;

        RelativeLayout companyViewHolder;

        public MyViewHolder(View itemView) {

            super(itemView);

            company_name=(TextView)itemView.findViewById(R.id.companyNameTextView);
            logo=(ImageView)itemView.findViewById(R.id.logo);
            author_name=(TextView)itemView.findViewById(R.id.authorTextView);
            grade=(TextView)itemView.findViewById(R.id.gradeTextView);
            reviewsNumber=itemView.findViewById(R.id.nReviewsTextView);
            star1=(ImageView)itemView.findViewById(R.id.star1);
            star2=(ImageView)itemView.findViewById(R.id.star2);
            star3=(ImageView)itemView.findViewById(R.id.star3);
            star4=(ImageView)itemView.findViewById(R.id.star4);
            star5=(ImageView)itemView.findViewById(R.id.star5);

            bufferingProgressBar=(ProgressBar) itemView.findViewById(R.id.bufferingProgressBar);
            companyViewHolder=(RelativeLayout)itemView.findViewById(R.id.companyRelativeLayout);

        }

    }

    @Override
    public int getItemCount() {
        return company_data.size();
    }


}