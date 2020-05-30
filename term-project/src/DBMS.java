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
	String type;
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;
	boolean login;
	String name;
	int SUBSCRIPTION_FEE, JOINING_FEE;

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

	boolean write_to_hub(String path, String file_id) {
		boolean ret = true;
		File inFile = new File(path);
		String ss[] = file_id.split("_");
		String outlink = ss[1]+"_"+ss[2];
		File outFile = new File("./hub/"+outlink);
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

			System.out.println("id\tname\ttype\tauthor\tcategory\tarchitecure\tos\tsize\tdescription");
			while(rs.next()) {
				String rid = rs.getString("id");
				String rname  = rs.getString("name");
				String rtype = rs.getString("type");
				String rauthor = rs.getString("author");
				String rcategory =  rs.getString("category");
				String rarchitecture =  rs.getString("architecture");
				String ros =  rs.getString("os");
				int rsize = rs.getInt("size");
				String rdescription = rs.getString("description");

				System.out.println(rid+"\t"+rname+"\t"+rtype+"\t"+rauthor+"\t"+rcategory+"\t"+rarchitecture+"\t"+ros+"\t"+String.valueOf(rsize)+"\t"+rdescription);
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

				System.out.println("id\tname\ttype\tauthor\tcategory\tarchitecure\tos\tsize\tdescription");
				while(rs.next()) {
					String rid  = rs.getString("id");
					String rname  = rs.getString("name");
					String rtype = rs.getString("type");
					String rauthor = rs.getString("author");
					String rcategory =  rs.getString("category");
					String rarchitecture =  rs.getString("architecture");
					String ros =  rs.getString("os");
					int rsize = rs.getInt("size");
					String rdescription = rs.getString("description");

					System.out.println(rid+"\t"+rname+"\t"+rtype+"\t"+rauthor+"\t"+rcategory+"\t"+rarchitecture+"\t"+ros+"\t"+String.valueOf(rsize)+"\t"+rdescription);
				}
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


class User extends DBMS {
	String id, password, name, address, account_number, phone_number, birthday;
	User() {
		login = false;
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
		amount_due += subscription_fee;

		Date today = new Date();
		SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");

		try {
			pstmt = conn.prepareStatement(
					"insert into user (id, password, name, address, account_number, phone_number, birthday, subscription_fee, amount_due, date_joined) values(?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, id);
			pstmt.setString(2, password);
			pstmt.setString(3, name);
			pstmt.setString(4, address);
			pstmt.setString(5, account_number);
			pstmt.setString(6, phone_number);
			pstmt.setString(7, birthday);
			pstmt.setInt(8, subscription_fee);
			pstmt.setInt(9, amount_due);
			pstmt.setString(10, date.format(today)); // date joined
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

	boolean check(String file_id) {
		try {
			String select_history_query = "select access_history from user where id = ?";
			pstmt = conn.prepareStatement(select_history_query);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			String[] substr = file_id.split("_");
			String target_search = substr[1] + "_" + substr[2];
			//System.out.println("Target search: " + target_search);
			if(rs.next()) {
				String cur_access_history = rs.getString("access_history");	
				if(cur_access_history == null) 
					return false;
				else {
					String[] history_split = cur_access_history.split(" ");
					for(String tmp : history_split) {
						//System.out.println("Tmp: " + tmp);
						if(tmp.contains(target_search)) {
							return true;
						}
					}
				}
			}

		} catch(SQLException sqle) {
			System.out.println("SQLException: " + sqle);
			System.exit(1);
		} catch(Exception e) {
			System.out.println("Exception: " + e);
			System.exit(1);
		}
		return false;


	}

	void download() {
		System.out.println("download");
		Scanner sc = new Scanner(System.in);
		boolean success = false;

		System.out.print("Type the file ID you want to download: ");
		String file_id = sc.nextLine();

		if(check(file_id)) {
			System.out.println("You already downloaded this item, back to menu");
			return;
		}

		try {
			String query = "select * from item where id = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, file_id);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				String rid = rs.getString("id");
				String rname = rs.getString("name");
				String rtype  = rs.getString("type");
				String rauthor = rs.getString("author");
				String rcategory = rs.getString("category");
				String rarchitecture = rs.getString("architecture");
				String ros = rs.getString("os");
				int rsize= rs.getInt("size");
				String rdescription = rs.getString("description");
				int rdownloaded = rs.getInt("downloaded");

				// update number of downloades in item table
				rdownloaded++;
				String update_query = "update item set downloaded=? where id=?";
				pstmt = conn.prepareStatement(update_query);
				pstmt.setInt(1, rdownloaded);
				pstmt.setString(2, rid);
				pstmt.executeUpdate();

				// update earn of provider
				String query2 = "select earn from provider where id= ?";
				pstmt = conn.prepareStatement(query2);
				pstmt.setString(1, rauthor);
				rs = pstmt.executeQuery();

				if(rs.next()) {
					float cur_earn = rs.getInt("earn");
					float unit_earn = (float)25 / rsize;
					String update_query2 = "update provider set earn=? where id=?";
					pstmt = conn.prepareStatement(update_query2);
					pstmt.setFloat(1, cur_earn + unit_earn);
					pstmt.setString(2, rauthor);
					pstmt.executeUpdate();
				}

				else {
					System.out.println("download error");
					System.exit(1);
				}

				// now it's time to update access_history in user table
				Date curtime = new Date();
				SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss");

				String select_history_query = "select access_history from user where id = ?";
				pstmt = conn.prepareStatement(select_history_query);
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					String cur_access_history = rs.getString("access_history");	
					if(cur_access_history == null) 
						cur_access_history = date.format(curtime)+"_"+rid+ " ";
					else 
						cur_access_history += date.format(curtime)+"_"+rid+" ";
					String update_history_query = "update user set access_history=? where id=?";
					pstmt = conn.prepareStatement(update_history_query);
					pstmt.setString(1, cur_access_history);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
					success = true;
				}
				else {
					System.out.println("download error");
				}
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

		if(success) {
			String ss[] = file_id.split("_");
			get_from_hub("./hub/"+ ss[1]+"_"+ss[2], ss[1]+"_"+ss[2]);
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
		joining_fee = JOINING_FEE;
		amount_due_admin = 0;
		earn = 0;

		Date today = new Date();
		SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");



		try {
			//PreparedStatement pstmt = conn.prepareStatement("insert into user values(?, ?, ?, ?,?,?,?,?,?)");
			pstmt = conn.prepareStatement(
					"insert into provider (id, password, name, address, account_number, phone_number, birthday, joining_fee, amount_due_admin, earn, date_joined) values(?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, id);
			pstmt.setString(2, password);
			pstmt.setString(3, name);
			pstmt.setString(4, address);
			pstmt.setString(5, account_number);
			pstmt.setString(6, phone_number);
			pstmt.setString(7, birthday);
			pstmt.setInt(8, joining_fee);
			pstmt.setInt(9, amount_due_admin);
			pstmt.setFloat(10, earn); 
			pstmt.setString(11, date.format(today)); // date joined
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
				joining_fee = rs.getInt("joining_fee");
				amount_due_admin = rs.getInt("amount_due_admin");
				earn = rs.getInt("earn");
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
	
	void upload() {
		Scanner sc = new Scanner(System.in);
		System.out.println("upload");
		System.out.print("Type file name you want to upload, e.g. /home/2016312029/[path]: ");
		String file_path = sc.nextLine();
		int file_size = getFileSize(file_path);
		if(file_size > 0) {
			System.out.println("Calculated file size: " + String.valueOf(file_size) + " bytes");
			String file_id, file_name, file_type, author, file_category, architecture, os, description;
			Date now = new Date();
			SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss");
			

			System.out.print("File name: ");
			file_name = sc.nextLine();
			file_id = date.format(now) + "_" + id + "_" + file_name; 
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

			
			boolean write_result = write_to_hub(file_path, file_id);
			if(!write_result) {
				System.out.println("Failed to upload file to hub, please try again");
				return;
			}
			
			System.out.println("Successfully uploaded to hub");
			try {
				pstmt = conn.prepareStatement(
						"insert into item (id, name, type, author, category, architecture, os, description, updated,size, downloaded) values(?,?,?,?,?,?,?,?,?,?,?)");
				pstmt.setString(1, file_id);
				pstmt.setString(2, file_name);
				pstmt.setString(3, file_type);
				pstmt.setString(4, id);
				pstmt.setString(5, file_category);
				pstmt.setString(6, architecture);
				pstmt.setString(7, os);
				pstmt.setString(8, description);
				pstmt.setString(9, date.format(now));
				pstmt.setInt(10, file_size);
				pstmt.setInt(11, 0);
				pstmt.executeUpdate();

				String provider_query = "select amount_due_admin from provider where id = ?";
				pstmt = conn.prepareStatement(provider_query);
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();
				if(rs.next()) {
					int cur_amount_due_admin = rs.getInt("amount_due_admin");
					String provider_update_query = "update provider set amount_due_admin = ? where id = ?";
					pstmt = conn.prepareStatement(provider_update_query);
					pstmt.setInt(1, cur_amount_due_admin + file_size);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
				}
				else {
					System.out.println("Error in upload");
					System.exit(1);
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
		else {
			System.out.println("Oops! File not exists, Pleas check again");
			return;
		}

	}

	void update() {


	}

	void printStat() {
		try {
			ArrayList<String> targetList = new ArrayList<>();
			
			String select_my_upload = "select id from item where author = ?";
			pstmt = conn.prepareStatement(select_my_upload);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			rs.last();
			int rowCount = rs.getRow();
			if(rowCount == 0) {
				System.out.println("You've not uploaded any item yet");
				return;
			}
			rs.first();
			while(rs.next()) {
				String item_id = rs.getString("id");
				String item_id_split[] = item_id.split("_");
				String target_search = item_id_split[1] + "_" + item_id_split[2];
				String file_name = item_id_split[2];
				targetList.add(target_search);
			}

			String find_in_history = "select id, access_history from user";
			pstmt = conn.prepareStatement(find_in_history);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				String rid = rs.getString("id");
				String raccess_history = rs.getString("access_history");
				String[] h_split = raccess_history.split(" ");
				for(String tmp : targetList) {
					for(String tmp2 : h_split) {
						String file_name[] = tmp.split("_");
						System.out.println("========"+file_name[1]+"========");
						if(tmp2.contains(tmp)) {
							System.out.println("- " + rid);
						}
					}
				}

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
		else {
			System.out.println("Ok good choice");
		}
	}
}
