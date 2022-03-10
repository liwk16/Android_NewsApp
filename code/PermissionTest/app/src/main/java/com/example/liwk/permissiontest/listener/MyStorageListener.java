package com.example.liwk.permissiontest.listener;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import com.example.liwk.permissiontest.adapter.NewsAdapter;
import com.example.liwk.permissiontest.database.MyDataBase;
import com.example.liwk.permissiontest.news.NewsBean;

import java.util.ArrayList;

public class MyStorageListener implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout srfl;
    private Context cxt;
    private NewsAdapter newsAdapter;
    private int contentID;
    public MyStorageListener(Context cxt, SwipeRefreshLayout srfl, NewsAdapter adp, int i)
    {
        this.contentID = i;
        this.srfl = srfl;
        this.cxt = cxt;
        this.newsAdapter = adp;
    }

    public ArrayList<NewsBean> getDataFromDataBase(int typeID)
    {
        ArrayList<NewsBean> list = new ArrayList<>();
        MyDataBase db = new MyDataBase(this.cxt);
        SQLiteDatabase sb = db.getReadableDatabase();
        String sql = "select * from news where type = "+ typeID;
        Cursor cursor = sb.rawQuery(sql, null);
        while(cursor.moveToNext())
        {
            NewsBean bean = new NewsBean();
            bean.title = cursor.getString(2);
            bean.des = cursor.getString(3);
            bean.icon_url = cursor.getString(4);
            bean.news_url = cursor.getString(5);
            list.add(bean);
        }
        cursor.close();
        db.close();
        return list;
    }

    @Override
    public void onRefresh() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Log.d("onRefresh", "before asking for news");

                final ArrayList<NewsBean> newsList = getDataFromDataBase(contentID);

                final String info = "已刷新";
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(newsList!=null) {
                            newsAdapter.replaceItems(newsList);
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
