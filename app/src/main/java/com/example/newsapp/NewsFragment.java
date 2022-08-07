package com.example.newsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsFragment extends Fragment {
    private static final String NEWS_API = "6599e7b9fec14087882e16cbc0da649d";
    private String q1 = "bitcoin";
    private final String TAG = "NewsFrag";
    ImageView signOutBtn;
    RecyclerView recyclerView;
    NewsAdapter mNewsAdapter;
    List<NewsData> mNewsData;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseUser mUser=mAuth.getCurrentUser();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewsAdapter=new NewsAdapter(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent, Bundle savedInstanceState) {

        View view = layoutInflater.inflate(R.layout.home_page, parent, false);
        recyclerView = view.findViewById(R.id.news_home_page_recView);
        signOutBtn=view.findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mAuth.signOut();
                    LoginManager.getInstance().logOut();
                    launchMainAct();
            }
        });
        fetchNews();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mNewsAdapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.mainact_menu,menu);

        MenuItem item1 = menu.findItem(R.id.search_option);
        SearchView searchView = (SearchView) item1.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search Here");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mNewsAdapter.getFilter().filter(s);
                return false;
            }
        });
    }

    private void fetchNews() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        newsApi api = retrofit.create(newsApi.class);
        retrofit2.Call<Object> call = api.getNewsData(q1, NEWS_API);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try{
                    JSONObject jsonObject=new JSONObject(new Gson().toJson(response.body()));
                    Log.d(TAG,"..."+jsonObject);
                    parseItems(jsonObject);
                }catch(Exception e){
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(),"failed to fetch",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseItems(JSONObject jsonObject) throws JSONException {
        mNewsData=new ArrayList<>();
        JSONArray jsonArray=jsonObject.getJSONArray("articles");
        //Toast.makeText(getContext(),""+jsonArray.length(),Toast.LENGTH_SHORT).show();
        for(int i=0;i<20;i++){
            NewsData nD=new NewsData();
            JSONObject jsonObject1=jsonArray.getJSONObject(i);
            JSONObject jsonSource=jsonObject1.getJSONObject("source");
            String time=jsonObject1.getString("publishedAt");
            String temp="";
            for(int j=0;j<time.length()-4;j++){
                if(Character.isAlphabetic(time.charAt(j))){
                    temp=temp+" ";
                    continue;
                }
                temp=temp+time.charAt(j);

            }
            Log.d(TAG,"???"+temp);
            DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime=LocalDateTime.parse(temp,dtf);
            int days=dateTime.getDayOfMonth();
            nD.setHours(days+" days ago");
            nD.setHeading(jsonObject1.getString("title"));
            nD.setDescription(jsonObject1.getString("description"));
            nD.setSource(jsonSource.getString("name"));
            nD.setImgUrl(jsonObject1.getString("urlToImage"));
            mNewsData.add(nD);

        }
        mNewsAdapter.getNews(mNewsData);
    }
    private void launchMainAct(){
        Fragment fragment=new LoginFrag();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment)
                .addToBackStack(null).commit();
    }
}
