package com.example.liwk.permissiontest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.liwk.permissiontest.R;
import com.example.liwk.permissiontest.news.NewsBean;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsAdapter extends BaseAdapter {
    public ArrayList<NewsBean> mList;
    private ArrayList<Integer> readTags;
    private Context mContext;
    private int typeID;

    public NewsAdapter(Context context, ArrayList<NewsBean> list)
    {
        this.mContext = context;
        this.mList = list;
    }

    public NewsAdapter(Context context, int typeID)
    {
        this.mContext = context;
        this.mList = new ArrayList<>();
        this.typeID = typeID;
        this.readTags = new ArrayList<>();
    }

    public void replaceItems(ArrayList<NewsBean> arrayList)
    {
        mList.clear();
        mList.addAll(arrayList);
        readTags.clear();
    }

    public void addItems(ArrayList<NewsBean> arrayList)
    {
        //mList.addAll(arrayList);
        int len = arrayList.size();
        arrayList.addAll(mList);
        mList = arrayList;
        for(int i=0;i<readTags.size();++i)
        {
            readTags.set(i, readTags.get(i)+len);
        }
    }


    public void recordPosition(int position)
    {
        this.readTags.add(position);
    }


    @Override
    public int getCount()
    {
        return mList.size();
    }

    @Override
    public NewsBean getItem(int position)
    {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.news_item, null);
            holder.tv_title = (TextView)convertView.findViewById(R.id.tv_title);
            holder.tv_des = (TextView)convertView.findViewById(R.id.tv_des);
            holder.iv_icon = (ImageView)convertView.findViewById(R.id.iv_icon);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        NewsBean item = getItem(position);
        holder.tv_title.setText(item.title);
        holder.tv_title.setTextColor(Color.BLACK);
        holder.tv_des.setText(item.des);
        //Log.d("newsItem", "title"+item.title);
        //Log.d("newsItem","icon_url:"+item.icon_url);

        if(item.icon_url!=null&&!item.icon_url.contains("http"))
        {
            item.icon_url = "http:"+item.icon_url;
        }

        Glide.with(this.mContext)
                .load(item.icon_url)
                .into(holder.iv_icon);
        ////img1.gtimg.com/gamezone/pics/hv1/219/41/2200/143065674.jpg
        ////img1.gtimg.com/gamezone/pics/hv1/219/41/2200/143065674.jpg
        //http://img1.gtimg.com/gamezone/pics/hv1/184/42/2200/143065894.jpg
        //holder.iv_icon.setImageDrawable(null);
        //holder.iv_icon.setImageURI(Uri.parse(item.icon_url));
        /*
        try {
            //Log.d("intry","just enter");
            //URL picURL = new URL(item.icon_url);
            //Log.d("intry", "after url");
            //Bitmap pngBM = BitmapFactory.decodeStream(picURL.openStream());
            //Log.d("intry", "after decode");

            //holder.iv_icon.setImageBitmap(pngBM);
            ////holder.iv_icon.setImageURI(Uri.parse(item.icon_url));

            Bitmap bitmap = getBitmap(item.icon_url);
            holder.iv_icon.setImageBitmap(bitmap);


        }catch(Exception e)
        {
            holder.iv_icon.setImageDrawable(null);
        }*/
        //convertView.setBackgroundColor(Color.WHITE);
        for(int i: readTags)
        {
            if(position == i)
            {
                holder.tv_title.setTextColor(Color.GRAY);
                //convertView.setBackgroundColor(Color.GRAY);
            }
        }


        return convertView;
    }

    public static Bitmap getBitmap(String path) throws IOException {

        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200){
            System.out.println("Enter");
            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }
        return null;
    }

}

class ViewHolder
{
    TextView tv_title;
    TextView tv_des;
    ImageView iv_icon;
}