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
	PreparedStatement pstmt, pstmt2;
	ResultSet rs, rs2;

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

			String query = "select provider_id, item_name, max(time) as m from history group by provider_id and item_name having datediff(m, current_date) > 7";
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				String rid = rs.getString("provider_id");
				String ritem_name = rs.getString("item_name");
				System.out.println("Purging provider: " + rid + " item_name: " + ritem_name);
				String query2 = "delete from item where id = ? and item_name = ?";
				pstmt2 = conn.prepareStatement(query2);
				pstmt2.executeUpdate();

				String query3 = "insert into purged(provider_id, item_name) values(?,?)";
				pstmt2 = conn.prepareStatement(query3);
				pstmt2.setString(1, rid);
				pstmt2.setString(2, ritem_name);
				pstmt2.executeUpdate();
			}
			System.out.println("[JDBC Connector Driver successed]");
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
		} catch (Exception e) { 
		
		}
	}

	void printByRank() {
		try {
			String query = "select provider_id, item_name, count(*) as download_num from history group by provider_id and item_name order by download_num";
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();

			System.out.println("name\tauthor\tdownload_num");
			while(rs.next()) {
				String rname  = rs.getString("item_name");
				String rauthor = rs.getString("provider_id");
				int rdownloaded = rs.getInt("download_num");
				System.out.println(rname+"\t"+rauthor+"\t"+String.valueOf(rdownloaded));
			}
		} catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}
	}

	void printHistory() {
		try {
			String query = "select * from history";
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			System.out.println("item_name\tauthor\tpurchaser\ttime");
			while(rs.next()) {
				String rname  = rs.getString("item_name");
				String rauthor = rs.getString("provider_id");
				String rpurchaser = rs.getString("user_id");
				String rtime = rs.getString("time");
				System.out.println(rname+"\t"+rauthor+"\t"+rpurchaser+"\t"+rtime);
			}
		} catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}
	}

	void printPurged() {
		try {
			String query = "select * from purged";
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();

			System.out.println("name\tauthor\tpurged_time");
			while(rs.next()) {
				String rname  = rs.getString("name");
				String rauthor = rs.getString("author");
				String purged_time = rs.getString("purged_time");
				System.out.println(rname+"\t"+rauthor+"\t"+purged_time);
			}
		} catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}

	}

	void printAllItem() {
		try {
			String query = "select * from item";
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();

			System.out.println("name\ttype\tauthor\t\tcategory\tsize\tdescription\tlast_updated");
			while(rs.next()) {
				String rname  = rs.getString("name");
				String rtype = rs.getString("type");
				String rauthor = rs.getString("author");
				String rcategory =  rs.getString("category");
				int rsize = rs.getInt("size");
				String rdescription = rs.getString("description");
				String rlast_updated = rs.getString("last_updated");

				System.out.println(rname+"\t"+rtype+"\t"+rauthor+"\t"+rcategory+"\t"+String.valueOf(rsize)+"\t"+rdescription+"\t"+rlast_updated);
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

				System.out.println("name\ttype\tauthor\t\tcategory\tsize\tdescription\tlast_updated");
				while(rs.next()) {
					String rname  = rs.getString("name");
					String rtype = rs.getString("type");
					String rauthor = rs.getString("author");
					String rcategory =  rs.getString("category");
					int rsize = rs.getInt("size");
					String rdescription = rs.getString("description");
					String rlast_updated = rs.getString("last_updated");
					System.out.println(rname+"\t"+rtype+"\t"+rauthor+"\t"+rcategory+"\t"+String.valueOf(rsize)+"\t"+rdescription+"\t"+rlast_updated);

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

	void printAllItemByKey() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Type any key you want to find about: " );
		String key = sc.nextLine();

		try {
			String query = "select * from item natural join prereq"; //where type=? or architecture=? or os=? or description=?";
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();

			System.out.println("name\ttype\tauthor\t\tcategory\tsize\tarchitecture\tos\tdescription\tlast_updated");
			while(rs.next()) {
				String rname  = rs.getString("name");
				String rtype = rs.getString("type");
				String rauthor = rs.getString("author");
				String rcategory =  rs.getString("category");
				int rsize = rs.getInt("size");
				String rarchitecture = rs.getString("architecture");
				String ros = rs.getString("os");
				String rdescription = rs.getString("description");
				String rlast_updated = rs.getString("last_updated");
				if(rname.contains(key) || rtype.contains(key) || rauthor.contains(key) || rcategory.contains(key) || rarchitecture.contains(key) || ros.contains(key) || rdescription.contains(key)) {
					System.out.println(rname+"\t"+rtype+"\t"+rauthor+"\t"+rcategory+"\t"+String.valueOf(rsize)+"\t"+rarchitecture+"\t"+ros+"\t"+rdescription+"\t"+rlast_updated);
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

	void printByAuthor(String author) {
		Scanner sc = new Scanner(System.in);
		try {
			String query = "select * from item where author=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, author);
			rs = pstmt.executeQuery();

			System.out.println("name\ttype\tauthor\t\tcategory\tsize\tdescription\tlast_updated");
			while(rs.next()) {
				String rname  = rs.getString("name");
				String rtype = rs.getString("type");
				String rauthor = rs.getString("author");
				String rcategory =  rs.getString("category");
				int rsize = rs.getInt("size");
				String rdescription = rs.getString("description");
				String rlast_updated = rs.getString("last_updated");
				System.out.println(rname+"\t"+rtype+"\t"+rauthor+"\t"+rcategory+"\t"+String.valueOf(rsize)+"\t"+rdescription+"\t"+rlast_updated);

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
			pstmt = conn.prepareStatement(
					"insert into user (id, password, name, address, account_number, phone_number, birthday) values(?,?,?,?,?,?,?)");
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
				int size = rs.getInt("size");
				query = "insert into history (user_id, provider_id, item_name, price) values(?,?,?,?)";
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, id);
				pstmt.setString(2, file_author);
				pstmt.setString(3, file_name);
				pstmt.setFloat(4, (float)0.0025 * size);
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

	void cancel_subscription() {
		System.out.println("If you cancel subscription, your information, items downloaded will all deleted after this month");
		System.out.print("[Y/N]: ");
		Scanner sc = new Scanner(System.in);
		String answer = sc.nextLine();
		if(answer.equals("Y")) {
			try {
				String query = "select end_date from user where id = ?";
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();
				if(rs.next()) {
					String end_date = rs.getString("end_date");
					System.out.println("You've been unsubscribed you can use the service until " + end_date + ", hope to see you again!");
				}
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

	void show_bill() {
		try {
			String query = "select date_joined, start_date, end_date from user where id = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				String date_joined = rs.getString("date_joined");
				String start_date  = rs.getString("start_date");
				String end_date    = rs.getString("end_date");
				System.out.println("Registered date: " + date_joined);
				System.out.println("Last subscription date: " + start_date);
				System.out.println("End subscription date: " + end_date + " (If you do not cancel subscription, it will be subscribed automatically");
				System.out.println("Subscription fee is $10 per month");
				String query2 = "select provider_id, item_name, time,price from history where user_id = ?";
				pstmt = conn.prepareStatement(query2);
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();
				System.out.println("Price is determined by $0.0025 * size");
				float total_price = 0;
				while(rs.next()) {
					String provider_id = rs.getString("provider_id");
					String item_name = rs.getString("item_name");
					String time = rs.getString("time");
					float price = rs.getFloat("price");
					total_price += price;
					System.out.println(item_name + " by "+ provider_id + " at " + time + ": $" + String.valueOf(price));
				}
				System.out.println("Total price: " + String.valueOf(total_price));
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
		System.out.println("If you delete your account, your information, items downloaded will all deleted");
		System.out.print("[Y/N]: ");
		Scanner sc = new Scanner(System.in);
		String answer = sc.nextLine();
		if(answer.equals("Y")) {
			try {
				String delete_provider = "delete from user where id = ?";
				pstmt = conn.prepareStatement(delete_provider);
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
			pstmt = conn.prepareStatement(
					"insert into provider (id, password, name, address, account_number, phone_number, birthday) values(?,?,?,?,?,?,?)");
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
						"insert into item (name, type, author, category, size,description) values(?,?,?,?,?,?)");
				pstmt.setString(1, file_name);
				pstmt.setString(2, file_type);
				pstmt.setString(3, id);
				pstmt.setString(4, file_category);
				pstmt.setInt(5, file_size);
				pstmt.setString(6, description);
				pstmt.executeUpdate();


				System.out.print("If the submitted file has any pre-requirements(architecture, os)? \ntype[Y/N]: ");
				String ans = sc.nextLine();
				if(ans.equals("Y")) {
					System.out.print("Machine architecture required(mac, pc, workstation type, all, etc..): ");
					architecture = sc.nextLine();
					System.out.print("OS required (mac, windows, all, etc...): ");
					os = sc.nextLine();
					pstmt = conn.prepareStatement("insert into prereq (name, author, architecture, os) values(?,?,?,?)");
					pstmt.setString(1, file_name);
					pstmt.setString(2, id);
					pstmt.setString(3, architecture);
					pstmt.setString(4, os);
					pstmt.executeUpdate();
				}
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

	public void renameFile(String filename, String newFilename) {
		File file = new File( "./hub/"+filename );
		File fileNew = new File( "./hub/"+newFilename );
		if( file.exists() ) file.renameTo(fileNew);
	}

	void update() {
		printByAuthor(id);
		

		try {
			Scanner sc = new Scanner(System.in);
			System.out.print("Type the file name you want to update: ");
			String file_name = sc.nextLine();
			String query = "select * from item where name = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, file_name);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				String rname = rs.getString("name");
				String rtype = rs.getString("type");
				String rcategory = rs.getString("category");
				String rarch="", ros="";
				String rauthor = rs.getString("author");
				String rdescription = rs.getString("description");
				boolean prereq = false;

				pstmt = conn.prepareStatement("select architecture, os from prereq where name = ? and author = ?");
				pstmt.setString(1, rname);
				pstmt.setString(2, id);
				rs = pstmt.executeQuery();

				if(rs.next()) {
					rarch = rs.getString("architecture");
					ros = rs.getString("os");
					prereq = true;
				}


				System.out.println("Current contents of your item");
				System.out.println("File name: " + rname + "\nType: " + rtype + "\nCategory: "+rcategory+"\nArchitecture: " + rarch + "\nOS: " + ros + "\nDescription: " + rdescription);

				System.out.println("Choose what you want to update");

				System.out.println("1) Name");
				System.out.println("2) Type");
				System.out.println("3) Category");
				System.out.println("4) Architecture");
				System.out.println("5) OS");
				System.out.println("6) Description");
				System.out.println("7) Reupload file");
				System.out.print("Which you want to update?: ");

				String answer_str = sc.nextLine();
				int answer = Integer.parseInt(answer_str);
				String query2; 

				if(answer == 1) { // name
					System.out.print("Type update name: ");
					String str = sc.nextLine();
					query2 = "update item set name = ? where name = ? and author = ?";
					pstmt = conn.prepareStatement(query2);
					pstmt.setString(1, str); 
					pstmt.setString(2, rname);
					pstmt.setString(3, rauthor);
					pstmt.executeUpdate();
					renameFile(rauthor+"_"+rname, rauthor+"_"+str);
				}
				else if(answer == 2) { // type
					System.out.print("Type update name: ");
					String str = sc.nextLine();
					query2 = "update item set type = ? where name = ? and author = ?";
					pstmt = conn.prepareStatement(query2);
					pstmt.setString(1, str); 
					pstmt.setString(2, rname);
					pstmt.setString(3, rauthor);
					pstmt.executeUpdate();
				}
				else if(answer == 3) { // category
					System.out.print("Type update category: ");
					String str = sc.nextLine();
					query2 = "update item set category = ? where name = ? and author = ?";
					pstmt = conn.prepareStatement(query2);
					pstmt.setString(1, str); 
					pstmt.setString(2, rname);
					pstmt.setString(3, rauthor);
					pstmt.executeUpdate();

				}
				else if(answer == 4) { // architecture
					if(prereq) {
						System.out.print("Type update architecture: ");
						String str = sc.nextLine();
						query2 = "update prereq set architecture = ? where name = ? and author = ?";
						pstmt = conn.prepareStatement(query2);
						pstmt.setString(1, str); 
						pstmt.setString(2, rname);
						pstmt.setString(3, rauthor);
						pstmt.executeUpdate();
					}
					else {
						System.out.println("Prerequisite not defined");
					}

				}
				else if(answer == 5) { // os
					if(prereq) {
						System.out.print("Type update os: ");
						String str = sc.nextLine();
						query2 = "update prereq set os = ? where name = ? and author = ?";
						pstmt = conn.prepareStatement(query2);
						pstmt.setString(1, str); 
						pstmt.setString(2, rname);
						pstmt.setString(3, rauthor);
						pstmt.executeUpdate();
					}
					else {
						System.out.println("Prerequisite not defined");

					}
				}
				else if(answer == 6) { // description
					System.out.print("Type update description: ");
					String str = sc.nextLine();
					query2 = "update item set description = ? where name = ? and author = ?";
					pstmt = conn.prepareStatement(query2);
					pstmt.setString(1, str); 
					pstmt.setString(2, rname);
					pstmt.setString(3, rauthor);
					pstmt.executeUpdate();
				}
				else if(answer == 7) {
					System.out.println("You chooosed reuploading file." );
					System.out.print("Type file name you want to reupload, e.g. /home/2016312029/[path]: ");
					String file_path = sc.nextLine();
					int file_size = getFileSize(file_path);
					if(file_size > 0) {
						System.out.println("Calculated file size: " + String.valueOf(file_size) + " bytes");
						boolean write_result = write_to_hub(file_path, file_name);
						if(!write_result) {
							System.out.println("Failed to upload file to hub, please try again");
							return;
						}

						System.out.println("Successfully reuploaded to hub");
						pstmt = conn.prepareStatement(
								"update item set size = ? where name = ? and author = ?");
						pstmt.setInt(1, file_size);
						pstmt.setString(2, rname);
						pstmt.setString(3, rauthor);
						pstmt.executeUpdate();
						System.out.println("Reupload complete");
					}
					else {
						System.out.println("Oops! File not exists, Pleas check again");
						return;
					}

				}
				else{
					System.out.println("Wrong answer, return");
				}
			}
			else {
				System.out.println("No such item");
			}
		}
		catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}
	}

	void printStat() {
		try {
			System.out.println("Joining_fee: " + String.valueOf(JOINING_FEE));
			System.out.println("=========== You should pay =========");
			System.out.println("$1 per byte for local storage fee");
			pstmt = conn.prepareStatement("select name, size from item where author = ?");
			pstmt.setString(1,id);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				String rname = rs.getString("name");
				int rsize = rs.getInt("size");
				System.out.println(rname + ": $" + rsize);
			}


			System.out.println("=========== You earned =========");
			String ruser_id, rprovider_id, ritem_name;
			float rprice;
			float total_earn = 0;
			String query = "select * from history where provider_id=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1,id);
			rs = pstmt.executeQuery();

			System.out.println("User\tAuthor\tFile Name\tIncome");
			while(rs.next()) {
				ruser_id = rs.getString("user_id");
				rprovider_id = rs.getString("provider_id");
				ritem_name = rs.getString("item_name");
				rprice = rs.getFloat("price");
				total_earn += rprice;
				System.out.println(ruser_id + "\t" + rprovider_id + "\t" + ritem_name+"\t$"+String.valueOf(rprice));
			}
			System.out.println("Total you earned: " + String.valueOf(total_earn));

			query = "select * from purged where provider_id=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1,id);
			rs = pstmt.executeQuery();
			System.out.println("If your uploaded file is not accessed by anyone more than 7 days, the item is purged from market.");
			System.out.println("========= Purged item ==========");
			while(rs.next()) {
				ritem_name = rs.getString("item_name");
				String time = rs.getString("purged_time");
				System.out.println(ritem_name + "\t" + time);
			}
		} catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}
	}

	void cancel_subscription() {
		System.out.println("If you cancel subscription, your information, items uploaded will all deleted after this month");
		System.out.print("[Y/N]: ");
		Scanner sc = new Scanner(System.in);
		String answer = sc.nextLine();
		if(answer.equals("Y")) {
			try {
				String query = "select end_date from provider where id = ?";
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();
				if(rs.next()) {
					String end_date = rs.getString("end_date");
					System.out.println("You've been unsubscribed you can use the service until " + end_date + ", hope to see you again!");
				}
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

