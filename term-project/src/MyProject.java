import java.util.Scanner;

public class MyProject {

    public static void main(String[] args){
		boolean done = false;
		Scanner sc = new Scanner(System.in);
		int input;
		System.out.println("\nWelcome to market place for file sharing!");
		System.out.println("Subscription fee for user is $10 per month!\n");
		System.out.println("Joining fee for provider is $20!");
		System.out.println("Upload fee for provider is $1 per byte!");
		System.out.println("If you are provider, you will earn $0.0025 * size per download\n");
		System.out.print("As user(1) / provider(2): ");
		int type = sc.nextInt();

		if(type == 1) {
			user_action();
		}
		else if(type == 2) {
			provider_action();
		}
		else {
			System.out.println("Error in selecting type");
		}

	}

	public static void user_action() {
		User user = new User();
		Scanner sc = new Scanner(System.in);
		int choice;
		while(true) {
			if(user.login) {
				System.out.println();
				System.out.println("1) View all files");
				System.out.println("2) View by category");
				System.out.println("3) View files by rank"); 
				System.out.println("4) View all purchasing history");
				System.out.println("5) View purged files");
				System.out.println("6) Search by key");
				System.out.println("7) Download");
				System.out.println("8) Show bills");
				System.out.println("9) Cancel subscription");
				System.out.println("10) Delete account");
				System.out.println("11) Logout");
				choice = sc.nextInt();
				if(choice == 1) {
					user.printAllItem();
				}
				else if(choice == 2) {
					user.printByCategory();
				}
				else if(choice == 3) {
					user.printByRank();
				}
				else if(choice == 4) {
					user.printHistory();
				}
				else if(choice == 5) {
					user.printPurged();
				}
				else if(choice == 6) {
					user.printAllItemByKey();
				}
				else if(choice == 7) {
					user.download();
				}
				else if(choice == 8) {
					user.show_bill();
				}
				else if(choice == 9) {
					user.cancel_subscription();
				}
				else if(choice == 10) {
					user.delete_account();
				}
				else {
					System.out.println("logout");
					user.login = false;
				}
			}

			else {
				System.out.println();
				System.out.println("1) register");
				System.out.println("2) login");
				System.out.println("3) quit");
				choice = sc.nextInt();
				if(choice == 1) {
					user.register();
				}
				else if(choice == 2) {
					user.login();
				}
				else {
					System.out.println("Bye");
					System.exit(0);
				}
			}
		}



	}

	public static void provider_action() {
		Provider provider = new Provider();
		Scanner sc = new Scanner(System.in);
		int choice;
		while(true) {
			if(provider.login) {
				/*
				System.out.println();
				System.out.println("1) View all files");
				System.out.println("2) View by category");
				System.out.println("3) View files by rank"); 
				System.out.println("4) View all purchasing history");
				System.out.println("5) View purged files");
				System.out.println("6) Search by key");
				System.out.println("7) Download");
				System.out.println("8) Show bills");
				System.out.println("9) Cancel subscription");
				System.out.println("10) Delete account");
				System.out.println("11) Logout");
				choice = sc.nextInt();
				*/




				System.out.println();
				System.out.println("1) View all files");
				System.out.println("2) View by category"); 
				System.out.println("3) View files by rank"); 
				System.out.println("4) View all purchasing history");
				System.out.println("5) View purged files");
				System.out.println("6) Upload");
				System.out.println("7) Update");
				System.out.println("8) See statistics");
				System.out.println("9) Cancel subscription");
				System.out.println("10) Delete account");
				System.out.println("11) Logout");
				choice = sc.nextInt();
				if(choice == 1) {
					provider.printAllItem();
				}
				else if(choice == 2) {
					provider.printByCategory();
				}
				else if(choice == 3) {
					provider.printByRank();
				}
				else if(choice == 4) {
					provider.printHistory();
				}
				else if(choice == 5) {
					provider.printPurged();
				}
				else if(choice == 6) {
					provider.upload();
				}
				else if(choice == 7) {
					provider.update();
				}
				else if(choice == 8) {
					provider.printStat();
				}
				else if(choice == 9) {
					provider.cancel_subscription();
				}
				else if(choice == 10) {
					provider.delete_account();
				}
				else {
					System.out.println("logout");
					provider.login = false;
				}
			}
			else {
				System.out.println();
				System.out.println("1) register");
				System.out.println("2) login");
				System.out.println("3) quit");
				choice = sc.nextInt();
				if(choice == 1) {
					provider.register();
				}
				else if(choice == 2) {
					provider.login();
				}
				else {
					System.out.println("Bye");
					System.exit(0);
				}
			}
		}

	}



	/*	

		DBMS dbms = new DBMS();
		while(true) {
			System.out.println("1) register");
			System.out.println("2) login");
			int init = sc.nextInt();
			if(init == 1) {
				System.out.print("Register as user(1) / provider(2): ");
				int register_type = sc.nextInt();
				if(register_type == 1) {
					String[] register_info = new String[5];
					int idx = 0;
					System.out.print("Type name: ");
					register_info[idx++] = sc.next();
					System.out.print("Type address: ");
					register_info[idx++] = sc.next();
					System.out.print("Type account number: ");
					register_info[idx++] = sc.next();
					System.out.print("Type phone number: ");
					register_info[idx++] = sc.next();
					System.out.print("Type birthday: ");
					register_info[idx++] = sc.next();
					// more info : access history, subscription fee, amount due, data joined
					if(dbms.register(register_info, "user")) {
						System.out.println("register success");
					}
					else {
						System.out.println("register failed");
					}
				}

				else if(register_type == 2) {
					String[] register_info = new String[5];
					int idx = 0;
					System.out.print("Type name: ");
					register_info[idx++] = sc.next();
					System.out.print("Type address: ");
					register_info[idx++] = sc.next();
					System.out.print("Type account number: ");
					register_info[idx++] = sc.next();
					System.out.print("Type phone number: ");
					register_info[idx++] = sc.next();
					System.out.print("Type birthday: ");
					register_info[idx++] = sc.next();
					// more info : joining fee, amount due you, amount still to be paid to you, amount to be paid to provider
					if(dbms.register(register_info, "provider")) {
						System.out.println("register success");
					}
					else {
						System.out.println("register failed");
					}
				}

				else {
					System.out.println("You should type 1 or 2");
				}
				continue;
			}

			else if(init==2) {
				// get id, passwd and it is already in the table
				String login_name, account_number;
				System.out.print("Type name: ");
				login_name = sc.next();
				System.out.print("Type account_number: ");
				account_number = sc.next();
				if(dbms.login(login_name, account_number)) {
					System.out.println("login success!");
					break;
				}
				else {
					System.out.println("wrong name or address");
					continue;
				}
			}

			else {
				System.out.println("Wrong input number");
				continue;
			}
		}
		

		while(!done) {
			System.out.println("(1) upload a file");
			System.out.println("(2) download a file");
			System.out.println("(3) quit");
			input = sc.nextInt();
			if(input == 1) {
				dbms.upload();
			}
			else if(input == 2) {
				dbms.download();
			}
			else if(input == 3) {
				done = true;
			}
		}

		*/
    
}
