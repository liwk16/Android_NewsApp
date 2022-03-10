package com.example.liwk.permissiontest.listener;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import com.example.liwk.permissiontest.adapter.NewsAdapter;
import com.example.liwk.permissiontest.database.MyDataBase;
import com.example.liwk.permissiontest.news.NewsBean;
import com.example.liwk.permissiontest.news.NewsGetter;

import java.util.ArrayList;

public class MyRefreshListener implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout srfl;
    private Context cxt;
    private NewsAdapter newsAdapter;
    private int contentID;
    public MyRefreshListener(Context cxt, SwipeRefreshLayout srfl, NewsAdapter adp, int i)
    {
        this.contentID = i;
        this.srfl = srfl;
        this.cxt = cxt;
        this.newsAdapter = adp;
    }

    public void doInsert(ArrayList<NewsBean> newsList)
    {
        if(newsList == null)return;
        MyDataBase db = new MyDataBase(this.cxt);
        SQLiteDatabase sb = db.getWritableDatabase();
        String sql = "insert into news values(null,?,?,?,?,?)";
        for(NewsBean item: newsList) {
            sb.execSQL(sql,
                    new Object[]{contentID,
                            item.title, item.des,
                            item.icon_url, item.news_url});
        }
        Log.d("Insert", ""+newsList.size());
        db.close();

    }

    @Override
    public void onRefresh() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Log.d("onRefresh", "before asking for news");
                final ArrayList<NewsBean> newsList = NewsGetter.getNews(contentID);
                //newsAdapter.addItems(newsList);
                //Log.d("onRefresh","out of getNews");
                //newsAdapter.notifyDataSetChanged(); //wrong thread
                String temp;
                if(newsList != null) temp = "刷新"+newsList.size()+"条";
                else temp = "刷新失败";

                /*此处写本地存储 */
                doInsert(newsList);

                final String info = temp;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(newsList!=null) {
                            newsAdapter.addItems(newsList);
                            newsAdapter.notifyDataSetChanged();
                        }
                        //ArrayList<NewsBean> newsList = NewsGetter.getNews(contentID);
                        //String temp;
                        //if(newsList != null) temp = "刷新"+newsList.size()+"条";
                        //else temp = "刷新失败";
                        Toast.makeText(cxt, info, Toast.LENGTH_SHORT).show();
                        srfl.setRefreshing(false);

                    }
                },1500);
            }
        }).start();
        //new Handler().postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //        ArrayList<NewsBean> newsList = NewsGetter.getNews(contentID);
        //        String temp;
        //        if(newsList != null) temp = "刷新"+newsList.size()+"条";
        //        else temp = "刷新失败";
        //        Toast.makeText(cxt, temp, Toast.LENGTH_SHORT).show();
        //        srfl.setRefreshing(false);

        //    }
        //},3000);
    }
}