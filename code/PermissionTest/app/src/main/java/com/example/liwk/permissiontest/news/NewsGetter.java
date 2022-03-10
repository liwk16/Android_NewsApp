package com.example.liwk.permissiontest.news;

import android.util.Log;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class NewsGetter {

    public static ArrayList<NewsBean> getNews(int typeID)
    {
        ArrayList<NewsBean> newsList = new ArrayList<>();
        try {
            //Log.d("before socket", "hahaha");

            //Socket socket = new Socket("59.66.130.32", 2000); // old version, never timeout
            Socket socket = new Socket();
            SocketAddress ad = new InetSocketAddress("59.66.130.32", 2000);
            socket.connect(ad, 1200);
            //Log.d("after socket","woc");
            PrintWriter os = new PrintWriter(socket.getOutputStream());
            InputStream inputStream = socket.getInputStream();

            os.println(typeID);
            os.flush();

            ObjectInputStream objIn = new ObjectInputStream(inputStream);
            Object obj = null;
            while ((obj = objIn.readObject()) != null) {
                NewsBean entry = (NewsBean) obj;

                newsList.add(entry);

                //System.out.println("标题：" + entry.title);
                //System.out.println("连接地址：" + entry.news_url);
                //System.out.println("标题简介：" + entry.des);

            }
            objIn.close();
            os.close();
            socket.close();
            return newsList;
        }catch(Exception e) {
            Log.d("socket", "return null");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception
    {
        ArrayList<NewsBean> newsList = getNews(0);
        System.out.println(newsList.size());

    }
    /*public static void main(String[] args)
    {
        try
        {
            Socket socket = new Socket("59.66.130.32", 2000);
            //BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));

            PrintWriter os = new PrintWriter(socket.getOutputStream());
            //BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
            InputStream inputStream = socket.getInputStream();
            String line;
            line = "0";
            int choice = Integer.parseInt(line.trim());
            while(choice!=60) {
                os.println(choice);
                os.flush();
                System.out.println("hehe,waiting ans");

                checkString(inputStream);
                System.out.println("In Client Check Done!");
                line = "60";
                choice = Integer.parseInt(line.trim());
            }
            os.println(line);
            os.flush();

            socket.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void checkString(InputStream inputStream) throws Exception
    {
        ObjectInputStream objIn = new ObjectInputStream(inputStream);
        Object obj = null;
        while((obj = objIn.readObject())!=null)
        {
            NewsBean entry = (NewsBean)obj;

            System.out.println("标题：" + entry.title);
            System.out.println("连接地址：" + entry.news_url);
            System.out.println("标题简介：" + entry.des);

        }
        objIn.close();
    }
    */
}

