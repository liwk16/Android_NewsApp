package rssparse;

import java.io.*;
import java.net.*;

public class Client {
	public static void main(String[] args)
	{
		try
		{
			Socket socket = new Socket("127.0.0.1", 2000);
			//BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
			
			PrintWriter os = new PrintWriter(socket.getOutputStream());
			//BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
			InputStream inputStream = socket.getInputStream();
			String line;
			line = "0";
			int choice = Integer.parseInt(line.trim());
			while(choice!=60) {
				os.println(line);
				os.flush();
				System.out.println("hehe,waiting ans");
				
				RssParser.checkString(inputStream);
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
}
