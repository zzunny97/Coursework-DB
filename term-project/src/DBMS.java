import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

		
		
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class DBMS {
	String type;
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;
	boolean login;
	String name;

	public DBMS() {
		System.out.println("[DBMS constructor]");
		conn = null;
		pstmt = null;
		rs = null;
		login = false;
		name = null;
		
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

	boolean write_to_hub(String path) {
		boolean ret = true;
		File inFile = new File(path);
		File outFile = new File("./hub/"+path);
		outFile.getParentFile().mkdirs();
		System.out.println("in path: " + inFile);
		System.out.println("out path: " + outFile);

		BufferedReader br = null;
		BufferedWriter bw = null;

		try {
			br = new BufferedReader(new FileReader(inFile));
			bw = new BufferedWriter(new FileWriter(outFile));
			String line;
			while((line = br.readLine()) != null) {
				bw.write(line);	
				bw.newLine();
				bw.flush();
			}

		} catch(FileNotFoundException e) {
			ret = false;
			e.printStackTrace();
			
		} catch(IOException e) {
			ret = false;
			e.printStackTrace();

		} finally {
			if(br != null) {
				try { br.close(); }
				catch(IOException e) {
					ret = false;
					e.printStackTrace();
				}
			}
			if(bw != null) {
				try { bw.close(); }
				catch (IOException e) {
					ret = false;
					e.printStackTrace();
				}
			}
		}
		return ret;

	}

	void printAllItem() {
		try {
			String query = "select * from item";
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();

			System.out.println("id\tname\ttype\tauthor\tcategory\tsize\tos\tdescription");
			while(rs.next()) {
				String rid = rs.getString("id");
				String rname  = rs.getString("name");
				String rtype = rs.getString("type");
				String rauthor = rs.getString("author");
				String rcategory =  rs.getString("category");
				int rsize = rs.getInt("size");
				String ros =  rs.getString("os");
				String rdescription = rs.getString("description");

				System.out.println(rid +"\t"+rname+"\t"+rtype+"\t"+rauthor+"\t"+rcategory+"\t"+String.valueOf(rsize)+"\t"+ros+"\t"+rdescription);
			}

		} catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}
	}

	void printByCategory() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Type category: " );
		String category = sc.next();
		try {
			String query = "select * from item where category=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, category);
			rs = pstmt.executeQuery();

			System.out.println("id\tname\ttype\tauthor\tcategory\tsize\tos\tdescription");
			while(rs.next()) {
				String rid = rs.getString("id");
				String rname  = rs.getString("name");
				String rtype = rs.getString("type");
				String rauthor = rs.getString("author");
				String rcategory =  rs.getString("category");
				int rsize = rs.getInt("size");
				String ros =  rs.getString("os");
				String rdescription = rs.getString("description");

				System.out.println(rid +"\t"+rname+"\t"+rtype+"\t"+rauthor+"\t"+rcategory+"\t"+String.valueOf(rsize)+"\t"+ros+"\t"+rdescription);
			}

		} catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}

	}

	void printAllCategory() {

	}


	void download() {
		System.out.println("download");
	}

}


class User extends DBMS {
	User() {
		login = false;
		System.out.println("User constructor");
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



		Date today = new Date();
		SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");



		try {
			//PreparedStatement pstmt = conn.prepareStatement("insert into user values(?, ?, ?, ?,?,?,?,?,?)");
			pstmt = conn.prepareStatement(
					"insert into user (name, address, account_number, phone_number, birthday, date_joined) values(?, ?, ?, ?,?, ?)");
			pstmt.setString(1, name);
			pstmt.setString(2, address);
			pstmt.setString(3, account_number);
			pstmt.setString(4, phone_number);
			pstmt.setString(5, birthday);
			//pstmt.setString(6, NULL); // access history
			//pstmt.setString(7, NULL); // subscription fee
			//pstmt.setString(8, NULL); // amount due
			pstmt.setString(6, date.format(today)); // date joined
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
			String query = "select * from user where account_number = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, account_number);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				String rname = rs.getString("name");
				name = rname;
				String access_history = rs.getString("access_history");
				Date today = new Date();
				SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
				boolean should_update_history = false;

				if(access_history == null) {
					should_update_history = true;
					access_history = date.format(today);
				}
				else {
					String[] all_dates = access_history.split(" ");
					boolean flag = false;
					for(String val : all_dates) {
						System.out.println("val: " + val);
						if(val.equals(date.format(today))) {
							flag = true;
							break;
						}
					}
					if(flag == false) {
						access_history += (" "+date.format(today));
						should_update_history = true;
					}
				}

					

				/*
				System.out.println("access_history: " + access_history);
				int subscription_fee  = rs.getInt("subscription_fee");
				System.out.println("subscription_fee: " + String.valueOf(subscription_fee));
				if(subscription_fee == 0) {
					System.out.println("Hello " + rname +"!");
					System.out.println("You did not subscribed yet");
					System.out.println("How long you want to subscribe? (If 1 month, type 1)");
					int how_long = sc.nextInt();
					amount_due = how_long * 30;
				}
				*/

				if(should_update_history) {
					String query2 = "update user set access_history=?";
					pstmt = conn.prepareStatement(query2);
					pstmt.setString(1, access_history);
					pstmt.executeUpdate();
					System.out.println("Hello " + rname + "!");
					login = true;
				}
				else {
					System.out.println("already logined today, so history not need to be updated");
					login = true;
				}
				System.out.println("Welcome " + name + "!");

				/*
				   String raddress = rs.getString("address");
				String raccount_number = rs.getString("account_number");
				String rphone_number = rs.getString("phone_number");
				String rbirthday = rs.getString("birthday");
				System.out.println(rname + "\t" + raddress + "\t" + raccount_number +"\t" + rphone_number + "\t" + rbirthday);
				*/
			}

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
		login = false;
		System.out.println("Provider constructor");
	}

	void register() {
		System.out.println("Provider register");
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



		Date today = new Date();
		SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");



		try {
			//PreparedStatement pstmt = conn.prepareStatement("insert into user values(?, ?, ?, ?,?,?,?,?,?)");
			pstmt = conn.prepareStatement(
					"insert into provider (name, address, account_number, phone_number, birthday, date_joined) values(?, ?, ?, ?,?, ?)");
			pstmt.setString(1, name);
			pstmt.setString(2, address);
			pstmt.setString(3, account_number);
			pstmt.setString(4, phone_number);
			pstmt.setString(5, birthday);
			//pstmt.setString(6, NULL); // joining fee 
			//pstmt.setString(7, NULL); // amount due you 
			//pstmt.setString(8, NULL); // amount still to be paid to you 
			//pstmt.setString(8, NULL); // amount to be paid to provider
			pstmt.setString(6, date.format(today)); // date joined
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
			String query = "select * from provider where account_number = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, account_number);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				String rname = rs.getString("name");
				name = rname;
				/*
				   String raddress = rs.getString("address");
				String raccount_number = rs.getString("account_number");
				String rphone_number = rs.getString("phone_number");
				String rbirthday = rs.getString("birthday");
				System.out.println(rname + "\t" + raddress + "\t" + raccount_number +"\t" + rphone_number + "\t" + rbirthday);
				*/
				login = true;
				System.out.println("Welcome " + name + "!");
			}
			else {
				System.out.println("No such account number, sorry");
				return;
			}

		} catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}
		
	}

	int getFileSize(String path) {
		File file = new File(path);
		if(file.exists()){
			return (int)file.length();
		}
		else {
			return -1;
		}


	}
	
	void upload() {
		Scanner sc = new Scanner(System.in);
		System.out.println("upload");
		System.out.print("Type file name you want to upload, e.g. /home/2016312029/[path]: ");
		String file_path = sc.next();
		int file_size = getFileSize(file_path);
		if(file_size > 0) {
			System.out.println("Calculated file size: " + String.valueOf(file_size) + " bytes");
			String file_id, file_name, file_type, author, file_category, os, description;
			System.out.print("File id: ");
			file_id = sc.next();
			System.out.print("File name: ");
			file_name = sc.next();
			System.out.print("File type: ");
			file_type = sc.next();
			System.out.print("File category: ");
			file_category = sc.next();
			System.out.print("Required os: ");
			os = sc.next();
			System.out.print("Short description: ");
			description = sc.next();

			boolean write_result = write_to_hub(file_path);
			if(!write_result) {
				System.out.println("Failed to upload file to hub, please try again");
				return;
			}
			System.out.println("Successfully uploaded to hub");
			try {
				pstmt = conn.prepareStatement(
						"insert into item (id, name, type, author, category, size, os, description) values(?,?,?,?,?,?,?,?)");
				pstmt.setString(1, file_id);
				pstmt.setString(2, file_name);
				pstmt.setString(3, file_type);
				pstmt.setString(4, name);
				pstmt.setString(5, file_category);
				pstmt.setInt(6, file_size);
				pstmt.setString(7, os);
				pstmt.setString(8, description);
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
		else {
			System.out.println("Oops! File not exists, Pleas check again");
			return;
		}

	}
}
