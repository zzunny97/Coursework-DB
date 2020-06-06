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
import java.util.ArrayList;



public class DBMS {
	String type, name;
	boolean login;
	int SUBSCRIPTION_FEE, JOINING_FEE;

	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;

	public DBMS() {
		System.out.println("[DBMS constructor]");
		conn = null;
		pstmt = null;
		rs = null;
		login = false;
		name = null;
		SUBSCRIPTION_FEE = 10;
		JOINING_FEE = 20;
		
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



	void printAllItem() {
		try {
			String query = "select * from item";
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();

			System.out.println("name\ttype\tauthor\tcategory\tarchitecure\tos\tsize\tdescription\tlast_updated");
			while(rs.next()) {
				String rname  = rs.getString("name");
				String rtype = rs.getString("type");
				String rauthor = rs.getString("author");
				String rcategory =  rs.getString("category");
				String rarchitecture =  rs.getString("architecture");
				String ros =  rs.getString("os");
				int rsize = rs.getInt("size");
				String rdescription = rs.getString("description");
				String rlast_updated = rs.getString("last_updated");

				System.out.println(rname+"\t"+rtype+"\t"+rauthor+"\t"+rcategory+"\t"+rarchitecture+"\t"+ros+"\t"+String.valueOf(rsize)+"\t"+rdescription+"\t"+rlast_updated);
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
		System.out.print("Type category (If you want view all existing categories in current market, type 0): " );
		String category = sc.nextLine();


		try {
			if(category.equals("0")) {
				String q = "select distinct category from item";
				pstmt = conn.prepareStatement(q);
				rs = pstmt.executeQuery();
				while(rs.next()) {
					System.out.println(rs.getString("category"));
				}
				System.out.println();
			}
			else {
				String query = "select * from item where category=?";
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, category);
				rs = pstmt.executeQuery();

				System.out.println("name\ttype\tauthor\tcategory\tarchitecure\tos\tsize\tdescription\tlast_updated");
				while(rs.next()) {
					String rname  = rs.getString("name");
					String rtype = rs.getString("type");
					String rauthor = rs.getString("author");
					String rcategory =  rs.getString("category");
					String rarchitecture =  rs.getString("architecture");
					String ros =  rs.getString("os");
					int rsize = rs.getInt("size");
					String rdescription = rs.getString("description");
					String rlast_updated = rs.getString("last_updated");
					System.out.println(rname+"\t"+rtype+"\t"+rauthor+"\t"+rcategory+"\t"+rarchitecture+"\t"+ros+"\t"+String.valueOf(rsize)+"\t"+rdescription+"\t"+rlast_updated);

				}
			}


		} catch(SQLException sqle) {
			System.out.println("SQLException:/" + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}

	}

}


class User extends DBMS {
	String id, password, name, address, account_number, phone_number, birthday;
	User() {
		login = false;
	}


	void register() {
		Scanner sc = new Scanner(System.in);
		
		System.out.print("ID: ");
		id = sc.nextLine();
		System.out.print("PASSWD: ");
		password = sc.nextLine();
		System.out.print("name: ");
		name = sc.nextLine();
		System.out.print("address: ");
		address = sc.nextLine();
		System.out.print("account number: ");
		account_number = sc.nextLine();
		System.out.print("phone_number: ");
		phone_number = sc.nextLine();
		System.out.print("birthday: ");
		birthday = sc.nextLine();
		int subscription_fee = SUBSCRIPTION_FEE;
		int amount_due = 0;

		//Date today = new Date();
		//SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");

		try {
			pstmt = conn.prepareStatement("insert into user (id, password, name, address, account_number, phone_number, birthday) values(?,?,?,?,?,?,?)");
			pstmt.setString(1, id);
			pstmt.setString(2, password);
			pstmt.setString(3, name);
			pstmt.setString(4, address);
			pstmt.setString(5, account_number);
			pstmt.setString(6, phone_number);
			pstmt.setString(7, birthday);
			pstmt.executeUpdate();
			System.out.println("You successfully registered! Thanks");
		} catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}
	}

	void login() {
		String input_id, input_password;
		Scanner sc = new Scanner(System.in);
		System.out.print("ID: " );
		input_id = sc.next();
		System.out.print("PASSWORD: " );
		input_password = sc.next();

		try {
			String query = "select * from user where id = ? and password = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, input_id);
			pstmt.setString(2, input_password);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				id = rs.getString("id");
				password = rs.getString("password");
				name = rs.getString("name");
				account_number = rs.getString("account_number");
				phone_number = rs.getString("phone_number");
				birthday = rs.getString("birthday");
				login = true;
				System.out.println("Hello, " + name + "!");
			}
			else {
				System.out.println("No such user");
				login = false;
			}

		} catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}
		

	}

	boolean check(String provider_id, String file_name) {
		try {
			String query = "select * from history where user_id=? and provider_id=? and item_name=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			pstmt.setString(2, provider_id);
			pstmt.setString(3, file_name);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				return true;
			}
			return false;
		} catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}
		return false;


	}

	boolean get_from_hub(String path, String file_name) {
		boolean ret = true;
		File inFile = new File(path);
		File outFile = new File("./"+id+"/downloaded/"+file_name);
		System.out.println("Downloaded path: " + "./"+id+"/downloaded/"+file_name);
		outFile.getParentFile().mkdirs();

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

	void download() {
		System.out.println("download");
		Scanner sc = new Scanner(System.in);
		boolean success = false;

		System.out.print("Type the file name and its author you want to download(e.g. zzunny hello.txt): ");
		String line = sc.nextLine();
		String[] strtok = line.split(" ");
		String file_author = strtok[0];
		String file_name = strtok[1];

		if(check(file_author, file_name)) {
			System.out.println("You already downloaded this item, back to menu");
			return;
		}

		try {

			String query = "select * from item where name = ? and author = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, file_name);
			pstmt.setString(2, file_author);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				/*
				String rname = rs.getString("name");
				String rtype  = rs.getString("type");
				String rauthor = rs.getString("author");
				String rcategory = rs.getString("category");
				String rarchitecture = rs.getString("architecture");
				String ros = rs.getString("os");
				int rsize= rs.getInt("size");
				String rdescription = rs.getString("description");
				*/

				query = "insert into history (user_id, provider_id, item_name) values(?,?,?)";
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, id);
				pstmt.setString(2, file_author);
				pstmt.setString(3, file_name);
				pstmt.executeUpdate();
				get_from_hub("./hub/"+ file_author+"_"+file_name, file_name);

			} else {
				System.out.println("No such item");
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
	String id, password, name, address, phone_number, birthday, account_number;
	int joining_fee;
	int amount_due_admin;
	float earn; // amount to be paird to provider
	Provider() {
		login = false;
		System.out.println("Provider constructor");
	}

	void register() {
		Scanner sc = new Scanner(System.in);
		System.out.print("ID: ");
		id = sc.nextLine();
		System.out.print("PASSWORD: ");
		password = sc.nextLine();
		System.out.print("Name: ");
		name = sc.nextLine();
		System.out.print("Address: ");
		address = sc.nextLine();
		System.out.print("Account number: ");
		account_number = sc.nextLine();
		System.out.print("Phone_number: ");
		phone_number = sc.nextLine();
		System.out.print("Birthday: ");
		birthday = sc.nextLine();


		try {
			pstmt = conn.prepareStatement("insert into provider (id, password, name, address, account_number, phone_number, birthday) values(?,?,?,?,?,?,?)");
			pstmt.setString(1, id);
			pstmt.setString(2, password);
			pstmt.setString(3, name);
			pstmt.setString(4, address);
			pstmt.setString(5, account_number);
			pstmt.setString(6, phone_number);
			pstmt.setString(7, birthday);
			pstmt.executeUpdate();
			System.out.println("You successfully registered");
		} catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}
	}

	void login() {
		Scanner sc = new Scanner(System.in);
		System.out.print("ID: " );
		String input_id = sc.nextLine();
		System.out.print("PASSWORD: ");
		String input_password = sc.nextLine();

		try {
			String query = "select * from provider where id=? and password = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, input_id);
			pstmt.setString(2, input_password);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				id = rs.getString("id");
				password = rs.getString("password");
				name = rs.getString("name");
				address = rs.getString("address");
				account_number = rs.getString("account_number");
				phone_number = rs.getString("phone_number");
				birthday = rs.getString("birthday");
				login = true;
				System.out.println("Hello, " + name + "!");
			}
			else {
				System.out.println("No such provider");
				login = false;
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

	boolean write_to_hub(String path, String file_name) {
		boolean ret = true;
		File inFile = new File(path);
		File outFile = new File("./hub/"+id+"_"+file_name);
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
	
	void upload() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Type file name you want to upload, e.g. /home/2016312029/[path]: ");
		String file_path = sc.nextLine();
		int file_size = getFileSize(file_path);
		if(file_size > 0) {
			System.out.println("Calculated file size: " + String.valueOf(file_size) + " bytes");
			String file_name, file_type, file_category, architecture, os, description;
			
			System.out.print("File name: ");
			file_name = sc.nextLine();
			System.out.print("File type(program, video, etc..): ");
			file_type = sc.nextLine();
			System.out.print("File category: ");
			file_category = sc.nextLine();
			System.out.print("Machine architecture required(mac, pc, workstation type, all, etc..): ");
			architecture = sc.nextLine();
			System.out.print("Os required (mac, windows, all, etc...): ");
			os = sc.nextLine();
			System.out.print("Short description: ");
			description = sc.nextLine();


			boolean write_result = write_to_hub(file_path, file_name);
			if(!write_result) {
				System.out.println("Failed to upload file to hub, please try again");
				return;
			}

			System.out.println("Successfully uploaded to hub");
			try {
				pstmt = conn.prepareStatement(
						"insert into item (name, type, author, category, architecture, os, size,description) values(?,?,?,?,?,?,?,?)");
				pstmt.setString(1, file_name);
				pstmt.setString(2, file_type);
				pstmt.setString(3, id);
				pstmt.setString(4, file_category);
				pstmt.setString(5, architecture);
				pstmt.setString(6, os);
				pstmt.setInt(7, file_size);
				pstmt.setString(8, description);
				pstmt.executeUpdate();
				System.out.println("Upload complete");
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

	void update() {


	}

	void printStat() {
		try {
			String ruser_id, rprovider_id, ritem_name;
			String query = "select * from history where provider_id=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1,id);
			rs = pstmt.executeQuery(query);

			while(rs.next()) {
				ruser_id = rs.getString("user_id");
				rprovider_id = rs.getString("provider_id");
				ritem_name = rs.getString("item_name");
				System.out.println(ruser_id + "\t" + rprovider_id + "\t" + ritem_name);
			}

		} catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}
	}

	void delete_account() {
		System.out.println("If you delete your account, your information, items uploaded will all deleted");
		System.out.print("[Y/N]: ");
		Scanner sc = new Scanner(System.in);
		String answer = sc.nextLine();
		if(answer.equals("Y")) {
			try {
				String delete_provider = "delete from provider where id = ?";
				pstmt = conn.prepareStatement(delete_provider);
				pstmt.setString(1, id);
				pstmt.executeUpdate();

				String delete_item = "delete from item where author = ?";
				pstmt = conn.prepareStatement(delete_item);
				pstmt.setString(1, id);
				pstmt.executeUpdate();
				login = false;
			} catch(SQLException sqle) {
				System.out.println("SQLException: " + sqle);
				System.exit(1);
			} catch(Exception e) {
				System.out.println("Exception: " + e);
				System.exit(1);
			}
		}
		else if(answer.equals("N")){
			System.out.println("Ok good choice");
		}
		else {
			System.out.println("Wrong answer return");

		}
	}

}

