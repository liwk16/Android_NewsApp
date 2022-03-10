package com.example.liwk.permissiontest.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.example.liwk.permissiontest.activity.WebViewActivity;
import com.example.liwk.permissiontest.adapter.NewsAdapter;

public class MyClickListener implements AdapterView.OnItemClickListener {

    private NewsAdapter adapter;
    private Context cxt;

    public MyClickListener(Context context, NewsAdapter adp)
    {
        this.cxt = context;
        this.adapter = adp;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //浏览器打开
        //Intent intent = new Intent();
        //intent.setAction(Intent.ACTION_VIEW);
        //intent.setData(Uri.parse(adapter.mList.get(position).news_url));
        //this.cxt.startActivity(intent);
        
        Intent intent = new Intent(this.cxt, WebViewActivity.class);
        //NewsBean temp = adapter.mList.get(position);
        intent.putExtra("url", adapter.mList.get(position).news_url);
        intent.putExtra("title", adapter.mList.get(position).title);
        intent.putExtra("des", adapter.mList.get(position).des);
        intent.putExtra("icon_url",adapter.mList.get(position).icon_url);
        //intent.putExtra("news", temp);
        this.cxt.startActivity(intent);


        this.adapter.recordPosition(position);
        this.adapter.notifyDataSetChanged();

    }
}
