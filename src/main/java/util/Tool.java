package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;





public class Tool {

	public static void main(String[] args) {
		
		
	}
	//連線
	public static Connection getDb()
	{
		String url="jdbc:mysql://localhost:3306/german_lms";
		String user="root";
		String password="1234";
		Connection conn=null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn=DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			System.out.println("no Driver");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("no Connection");
			e.printStackTrace();
		}
		
		return conn;
	}
	
	//存檔-->writeObject
	public static void saveObject(Object object,String saveFileName)
	{
		try {
			FileOutputStream fos=new FileOutputStream(saveFileName+".txt");
			ObjectOutputStream oos=new ObjectOutputStream(fos);
			oos.writeObject(object);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//讀檔-->readObject
	public static Object readObject(String fileName)
	{
		Object object=null;
		try {
			FileInputStream fis=new FileInputStream(fileName+".txt");
			ObjectInputStream ois=new ObjectInputStream(fis);
			
			object=ois.readObject();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return object;
	}

}