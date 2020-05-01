import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MultiServer {

	static PreparedStatement psmt;
	static Connection con;
	static ResultSet rs;
	
	public static void dbCon() {
		//데이터베이스 연결
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			con = DriverManager.getConnection
					("jdbc:oracle:thin://@localhost:1521:orcl", 
							"kosmo","1234"
					);
		}
		catch (ClassNotFoundException e) {
			System.out.println("오라클 드라이버 로딩 실패");
			e.printStackTrace();
		}
		catch (SQLException e) {
			System.out.println("DB 연결 실패");
			e.printStackTrace();
		}
		catch (Exception e) {
			System.out.println("알수 없는 예외 발생");
		}
	}
	public static void close() {
		try { 
			if(rs!=null) { 
				rs.close();
			} 
			if(psmt!=null) 
			{ 
				psmt.close();
			} 
		} 
		catch (Exception e) {
			System.out.println("insert_db(close) 예외 : " +  e);
		}
	}
	public static void insert_db(String name, String s) {
		dbCon();
		try{ 
			String query = "INSERT INTO chatting_tb VALUES "
					+ "	(chatting_seq.nextval, ?, ?, sysdate)";

			psmt = con.prepareStatement(query);
			psmt.setString(1, name);
			psmt.setString(2, s);
			psmt.executeUpdate();
		} 
		catch (Exception e) {
			System.out.println("insert_db 예외 : " +  e);
			e.printStackTrace();
		}
		finally { 
			close();
		}
	}
	
	public static void main(String[] args) {

		ServerSocket serverSocket = null;
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		String s = "";
		String name = "";

		try {
			serverSocket = new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다.");

			socket = serverSocket.accept();

			/*** ① ***/
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new 
			InputStreamReader(socket.getInputStream()));

			if(in != null) {
				name = in.readLine();
				System.out.println(name +" 접속");
				out.println("> "+ name +"님이 접속했습니다.");
			}

			while(in != null) {
				s = in.readLine();
				if(s==null) {
					break;
				}
				
				if(s.indexOf("광고") != -1) {
					System.out.println("'" + s + "' 는 금지단어이므로 출력되지 않습니다. " );
					out.println("'" + s + "' 는 금지단어이므로 출력되지 않습니다. " );
				}
				else {
					System.out.println(name +" ==> "+ s);
					out.println(">  "+ name +" ==> "+ s);
					insert_db(name, s);
				}
			}

			System.out.println("Bye...!!!");
		}
		catch (Exception e) {
			System.out.println("예외1:"+ e);
		}
		finally {
			try {
				in.close();
				out.close();
				socket.close();
				serverSocket.close();
			}
			catch (Exception e) {
				System.out.println("예외2:"+ e);
			}
		}
	}
}
