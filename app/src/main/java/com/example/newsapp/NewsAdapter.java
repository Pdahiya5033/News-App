package com.example.newsapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> implements Filterable {
    private List<NewsData> listNews =new ArrayList<>();
    private List<NewsData> tempListNews;
    private static final String TAG="NewsAdapter";
    private Context mContext;
    private int heightCard;
    private boolean cardClicked=false;
    private int cardClickedPos;
    private CardView tempCard;
    private TextView tempTV;
    public NewsAdapter(Context mContext){
        this.mContext=mContext;
    }
    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_contents,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
            if(listNews.size()>0){
                NewsData temp= listNews.get(position);
                holder.heading.setText(temp.getHeading());
                holder.description.setText(temp.getDescription());
                holder.source.setText(temp.getSource());
                holder.time.setText(temp.getHours());
                Glide.with(mContext).load(temp.getImgUrl()).into(holder.imageNews);
            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams) holder.cardView.getLayoutParams();
//                    layoutParams.height=heightCard*2;
//                    holder.cardView.setLayoutParams(layoutParams);

                }

            });

    }

    @Override
    public int getItemCount() {
        if(listNews.size()>0)
            return listNews.size();
        return 10;
    }
    @Override
    public Filter getFilter() {
        return filter;
    }
    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<NewsData> Filtered = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0) {
                Filtered.addAll(tempListNews);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(NewsData s : tempListNews) {
                    if((s.getHeading().toLowerCase()).contains(filterPattern))
                        Filtered.add(s);
                }
            }

            FilterResults results = new FilterResults();
            results.values = Filtered;
            results.count = Filtered.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listNews.clear();
            listNews.addAll((ArrayList<NewsData>)filterResults.values);
            notifyDataSetChanged();
        }
    };

    public void getNews(List<NewsData> listNews){
        this.listNews =listNews;
        tempListNews=new ArrayList<>(listNews);
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView heading,time,description,source;
        ImageView imageNews;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.news_cardView);
            heading=itemView.findViewById(R.id.headingNewsTV);
            time=itemView.findViewById(R.id.timeNewsTV);
            description=itemView.findViewById(R.id.descriptionNewsTV);
            source=itemView.findViewById(R.id.sourceNewsTV);
            imageNews=itemView.findViewById(R.id.imageNews);
            heightCard=cardView.getHeight();
        }

    }
}
