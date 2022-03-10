package rssparse;

import java.net.*;
import java.util.ArrayList;
import java.io.*;
import com.example.liwk.permissiontest.news.*;

public class CntThread extends Thread{
	Socket socket = null;
	RssParser parser = null;
	
	public CntThread(Socket socket, RssParser parser)
	{
		this.socket = socket;
		this.parser = parser;
	}
	
	public void run()
	{
		try
		{
			String line = null;
			BufferedReader sin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
			
			System.out.println("waiting to read");
			line = sin.readLine().trim();
			System.out.println("line:"+line);
			int choice = Integer.parseInt(line);
			ArrayList<NewsBean> tempList = null;
			while(choice != 60)
			{
				System.out.println(choice);
				
				tempList = parser.fetch(choice);
				
				for(int i=0;i<tempList.size();++i)
				{
					objOut.writeObject(tempList.get(i));
				}
				objOut.writeObject(null);
				objOut.flush();
				System.out.println("start to update");
				parser.updateRepository(choice);
				if(sin== null)break;
				line = sin.readLine().trim();
				System.out.println("line:"+line);
				
				choice = Integer.parseInt(line);
			}
			objOut.close();
			sin.close();
			socket.close();
			System.out.println("Thread: complete!");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("Thread: complete!");
	}
}
