import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

		
		

import java.util.Scanner;

public class DBMS {
	String type;
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;

	public DBMS() {
		System.out.println("[DBMS constructor]");
		conn = null;
		pstmt = null;
		rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2016312029", "2016312029", "changethis");
			System.out.println("[Database Connection success]");
		} catch (SQLException e) { 
			System.out.println("[SQL Error : " + e.getMessage() +"]"); 
			System.exit(1);
		} catch (ClassNotFoundException e1) { 
			System.out.println("[JDBC Connector Driver Error : " + e1.getMessage() + "]"); 
			System.exit(1);
		}
	}

	public void finalize() {
		System.out.println("call DBMS destructor");
		try {
			conn.close();
		} catch (Exception e) { }
	}

	void upload() {
		System.out.println("upload");
	}

	void download() {
		System.out.println("download");
	}

}

class User extends DBMS {
	User() {
		System.out.println("User constructor");
		pstmt = null;
	}

	void register() {
		System.out.println("User register");
		Scanner sc = new Scanner(System.in);
		System.out.print("name: ");
		String name = sc.next();
		System.out.print("address: ");
		String address = sc.next();
		System.out.print("account number: ");
		String account_number = sc.next();
		System.out.print("phone_number: ");
		String phone_number = sc.next();
		System.out.print("birthday: ");
		String birthday = sc.next();

		try {
			//PreparedStatement pstmt = conn.prepareStatement("insert into user values(?, ?, ?, ?,?,?,?,?,?)");
			pstmt = conn.prepareStatement(
					"insert into user (name, address, account_number, phone_number, birthday) values(?, ?, ?, ?,?)");
			pstmt.setString(1, name);
			pstmt.setString(2, address);
			pstmt.setString(3, account_number);
			pstmt.setString(4, phone_number);
			pstmt.setString(5, birthday);
			//pstmt.setString(6, NULL); // access history
			//pstmt.setString(7, NULL); // subscription fee
			//pstmt.setString(8, NULL); // amount due
			//pstmt.setString(9, NULL); // data joined
			pstmt.executeUpdate();
			System.out.println("Query updated");
		} catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}
	}

	void login() {
		System.out.println("User login");
		Scanner sc = new Scanner(System.in);
		System.out.print("account number: " );
		String account_number = sc.next();

		try {
			String query = String.format("select * from user where account_number = '%s'", account_number);
			pstmt = conn.prepareStatement(query);
			//pstmt.set(1, account_number);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				String rname = rs.getString("name");
				String raddress = rs.getString("address");
				String raccount_number = rs.getString("account_number");
				String rphone_number = rs.getString("phone_number");
				String rbirthday = rs.getString("birthday");
				System.out.println(rname + "\t" + raddress + "\t" + raccount_number +"\t" + rphone_number + "\t" + rbirthday);
			}
			System.out.println("Query updated");

		} catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}
		

	}
}

class Provider extends DBMS {
	Provider() {
		System.out.println("Provider constructor");
	}

	void register() {
		System.out.println("Provider register");
	}


	void login() {
		System.out.println("Provider login");
	}

}
