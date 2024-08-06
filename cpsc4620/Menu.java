package cpsc4620;

import com.mysql.cj.util.StringUtils;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * This file is where the front end magic happens.
 * 
 * You will have to write the methods for each of the menu options.
 * 
 * This file should not need to access your DB at all, it should make calls to the DBNinja that will do all the connections.
 * 
 * You can add and remove methods as you see necessary. But you MUST have all of the menu methods (including exit!)
 * 
 * Simply removing menu methods because you don't know how to implement it will result in a major error penalty (akin to your program crashing)
 * 
 * Speaking of crashing. Your program shouldn't do it. Use exceptions, or if statements, or whatever it is you need to do to keep your program from breaking.
 * 
 */

public class Menu {

	public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws SQLException, IOException {

		System.out.println("Welcome to Pizzas-R-Us!");
		
		int menu_option = 0;

		// present a menu of options and take their selection
		
		PrintMenu();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String option = reader.readLine();
		menu_option = Integer.parseInt(option);

		while (menu_option != 9) {
			switch (menu_option) {
			case 1:// enter order
				EnterOrder();
				break;
			case 2:// view customers
				viewCustomers();
				break;
			case 3:// enter customer
				EnterCustomer();
				break;
			case 4:// view order
				// open/closed/date
				ViewOrders();
				break;
			case 5:// mark order as complete
				MarkOrderAsComplete();
				break;
			case 6:// view inventory levels
				ViewInventoryLevels();
				break;
			case 7:// add to inventory
				AddInventory();
				break;
			case 8:// view reports
				PrintReports();
				break;
			}
			PrintMenu();
			option = reader.readLine();
			menu_option = Integer.parseInt(option);
		}

	}

	// allow for a new order to be placed
	public static void EnterOrder() throws SQLException, IOException 
	{

		/*
		 * EnterOrder should do the following:
		 * 
		 * Ask if the order is delivery, pickup, or dinein
		 *   if dine in....ask for table number
		 *   if pickup...
		 *   if delivery...
		 * 
		 * Then, build the pizza(s) for the order (there's a method for this)
		 *  until there are no more pizzas for the order
		 *  add the pizzas to the order
		 *
		 * Apply order discounts as needed (including to the DB)
		 * 
		 * return to menu
		 * 
		 * make sure you use the prompts below in the correct order!
		 */
		//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		 // User Input Prompts...

		System.out.println("Is this order for: \n1.) Dine-in\n2.) Pick-up\n3.) Delivery\nEnter the number of your choice:");
		String input = reader.readLine();
		int orderType = Integer.parseInt(input);
		double customerPrice = 0.0, businessPrice = 0.0;
		Order lastOrder = DBNinja.getLastOrder();
		int orderId = 0;
		if(lastOrder == null)
			orderId = 1;
		else
			orderId = lastOrder.getOrderID() + 1;


		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String timestamp = dtf.format(now);

		int custID = DBNinja.getLastInsertedCustId();
		Order order = new Order(orderId,custID, "type", timestamp, customerPrice, businessPrice, 0);
		DBNinja.addOrder(order);
		if(orderType == 1) {
			//dine-in
			System.out.println("What is the table number for this order?");
			int tableNumber = Integer.parseInt(reader.readLine());
			order.setOrderType("dinein");
			DBNinja.updatedinein(orderId, tableNumber);
		} else if(orderType == 2) {
			System.out.println("Is this order for an existing customer? Answer y/n: ");
			char existing = Character.toLowerCase((char)reader.read());
			reader.readLine();
			if(existing == 'y') {
				System.out.println("Here's a list of current customers: ");
				ArrayList<Customer> customers = DBNinja.getCustomerList();
				for(Customer customer : customers) {
					System.out.println(customer.toString());
				}
				System.out.println("Which customer is this order for? Enter ID Number:");
				int customerId = Integer.parseInt(reader.readLine());
				Customer cust = null;
				for(Customer c : customers) {
					if(c.getCustID() == customerId) {
						cust = c;
					}
				}
				if(cust == null) {
					System.out.println("ERROR: I don't understand your input for: Is this order an existing customer?");
					PrintMenu();
				} else {
					order.setOrderType("pickup");
					order.setCustID(customerId);
					DBNinja.updatepickup(orderId);
				}
			} else {
				EnterCustomer();
				int latestCustId = DBNinja.getLastInsertedCustId();
				order.setOrderType("pickup");
				order.setCustID(latestCustId);
				DBNinja.updatepickup(orderId);
			}
		} else if(orderType == 3) {
			System.out.println("Is this order for an existing customer? Answer y/n: ");
			char existing = Character.toLowerCase((char)reader.read());
			reader.readLine();
			if(existing == 'y') {
				System.out.println("Here's a list of current customers: ");
				ArrayList<Customer> customers = DBNinja.getCustomerList();
				for(Customer customer : customers) {
					System.out.println(customer.toString());
				}
				System.out.println("Which customer is this order for? Enter ID Number:");
				int customerId = Integer.parseInt(reader.readLine());
				Customer cust = null;
				for(Customer c : customers) {
					if(c.getCustID() == customerId) {
						cust = c;
					}
				}
				if(cust == null) {
					System.out.println("ERROR: I don't understand your input for: Is this order an existing customer?");
				} else {
					String address = DBNinja.getAddressFromCustomerId(customerId);
					if(!StringUtils.isNullOrEmpty(address)) {
						order.setOrderType("delivery");
						order.setCustID(customerId);
						DBNinja.updatedelivery(orderId, address);
					} else {
						System.out.println("What is the House Number for this order? (e.g., 111)");
						String houseNo = reader.readLine();
						System.out.println("What is the Street for this order? (e.g., Smile Street)");
						String street = reader.readLine();
						System.out.println("What is the City for this order? (e.g., Greenville)");
						String city = reader.readLine();
						System.out.println("What is the State Abbreviation for this order? (e.g., SC)");
						String state = reader.readLine();
						System.out.println("What is the Zip Code for this order? (e.g., 20605)");
						String zipCode = reader.readLine();
						order.setOrderType("delivery");
						order.setCustID(customerId);
						DBNinja.updatedelivery(orderId, houseNo+" "+street + ", "+city+" "+state+" "+zipCode);
					}
				}
			} else {
				System.out.println("Please Enter the Customer name (First Name <space> Last Name):");
				String[] names = reader.readLine().split(" ");
				System.out.println("What is this customer's phone number (##########) (No dash/space)");
				String mobile = reader.readLine();
				System.out.println("What is the House Number for this order? (e.g., 111)");
				String houseNo = reader.readLine();
				System.out.println("What is the Street for this order? (e.g., Smile Street)");
				String street = reader.readLine();
				System.out.println("What is the City for this order? (e.g., Greenville)");
				String city = reader.readLine();
				System.out.println("What is the State Abbreviation for this order? (e.g., SC)");
				String state = reader.readLine();
				System.out.println("What is the Zip Code for this order? (e.g., 20605)");
				String zipCode = reader.readLine();
				int custId = DBNinja.getLastInsertedCustId();
				Customer newCustomer = new Customer(custId+1, names[0], names[1], mobile);
				newCustomer.setAddress(street, city, state, zipCode);
				DBNinja.addCustomer(newCustomer);
				order.setOrderType("delivery");
				order.setCustID(custId + 1);
				DBNinja.updatedelivery(orderId, houseNo+" "+street + ", "+city+" "+state+" "+zipCode);
			}
		} else {
			System.out.println("Please provide valid OrderType");
		}

		boolean buildPizza = true;
		while(buildPizza) {
			System.out.println("Let's build a pizza!");
			Pizza pizza = buildPizza(orderId);
			order.addPizza(pizza);
			order.setCustPrice(order.getCustPrice() + pizza.getCustPrice());
			order.setBusPrice(order.getBusPrice() + pizza.getBusPrice());
			System.out.println("Enter -1 to stop adding pizzas...Enter anything else to continue adding pizzas to the order.");
			int number = Integer.parseInt(reader.readLine());
			if(number == -1) {
				buildPizza = false;
			}
		}
		addOrderDiscounts(order);
		System.out.println("Finished adding order...Returning to menu...");
	}

	public static void addOrderDiscounts(Order order) throws SQLException, IOException
	{
		System.out.println("Do you want to add discounts to this order? Enter y/n?");
		char orderDiscountOption = Character.toLowerCase((char)reader.read());
		reader.readLine();
		if(orderDiscountOption == 'y') {
			boolean orderDiscounts = true;
			while(orderDiscounts) {
				ArrayList<Discount> discounts = DBNinja.getDiscountList();
				for(Discount discount : discounts) {
					System.out.println(discount.toString());
				}
				System.out.println("Which Order Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
				int discountId = Integer.parseInt(reader.readLine());
				if(discountId != -1) {
					Discount discount = null;
					for(Discount d : discounts) {
						if(d.getDiscountID() == discountId) {
							discount = d;
						}
					}
					order.addDiscount(discount);
					DBNinja.useOrderDiscount(order, discount);
				} else {
					orderDiscounts = false;
				}
			}
		}
		order.setCustPrice(order.getCustPrice());
		DBNinja.addOrder(order);
	}
	
	
	public static void viewCustomers() throws SQLException, IOException 
	{
		/*
		 * Simply print out all of the customers from the database. 
		 */
		ArrayList<Customer> customersList = DBNinja.getCustomerList();
		for(Customer customer: customersList) {
			System.out.println(customer.toString());
		}
	}
	

	// Enter a new customer in the database
	public static void EnterCustomer() throws SQLException, IOException 
	{
		/*
		 * Ask for the name of the customer:
		 *   First Name <space> Last Name
		 * 
		 * Ask for the  phone number.
		 *   (##########) (No dash/space)
		 * 
		 * Once you get the name and phone number, add it to the DB
		 */
		
		// User Input Prompts...
		//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		 System.out.println("Please Enter the Customer name (First Name <space> Last Name):");
		 String[] customerName = reader.readLine().split(" ");
		 System.out.println("What is this customer's phone number (##########) (No dash/space):");
		 String mobile = reader.readLine();
		 DBNinja.addCustomer(new Customer(0, customerName[0], customerName[1], mobile));
	}

	// View any orders that are not marked as completed
	public static void ViewOrders() throws SQLException, IOException 
	{
		/*  
		* This method allows the user to select between three different views of the Order history:
		* The program must display:
		* a.	all open orders
		* b.	all completed orders 
		* c.	all the orders (open and completed) since a specific date (inclusive)
		* 
		* After displaying the list of orders (in a condensed format) must allow the user to select a specific order for viewing its details.  
		* The details include the full order type information, the pizza information (including pizza discounts), and the order discounts.
		* 
		*/
			
		
		// User Input Prompts...
		//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Would you like to:\n(a) display all orders [open or closed]\n(b) display all open orders\n(c) display all completed [closed] orders\n(d) display orders since a specific date");
		char option = Character.toLowerCase((char) reader.read());
		reader.readLine();
		if(option == 'a') {
			ArrayList<Order> ordersList = DBNinja.getOrders(false);
			if(ordersList.isEmpty()) {
				System.out.println("No orders to display, returning to menu.");
				PrintMenu();
			} else {
				for (Order order : ordersList) {
					//Question: toSimplePrint has Dateplaced also, but the video doesn't have that. should we write another fn?
					System.out.println(order.toSimplePrint());
				}
				DisplayOrdersInDetail(ordersList, reader);
			}
		} else if(option == 'b') {
			ArrayList<Order> ordersList = DBNinja.getOrders(true);
			if(ordersList.isEmpty()) {
				System.out.println("No orders to display, returning to menu.");
				PrintMenu();
			} else {
				for (Order order : ordersList) {
					//Question: toSimplePrint has Dateplaced also, but the video doesn't have that. should we write another fn?
					System.out.println(order.toSimplePrint());
				}
				DisplayOrdersInDetail(ordersList, reader);
			}
		} else if(option == 'c') {
			ArrayList<Order> ordersList = DBNinja.getCompletedOrders();
			if(ordersList.isEmpty()) {
				System.out.println("No orders to display, returning to menu.");
				PrintMenu();
			} else {
				for (Order order : ordersList) {
					//Question: toSimplePrint has Dateplaced also, but the video doesn't have that. should we write another fn?
					System.out.println(order.toSimplePrint());
				}
				DisplayOrdersInDetail(ordersList, reader);
			}
		} else if(option == 'd') {
			System.out.println("What is the date you want to restrict by? (FORMAT= YYYY-MM-DD)");
			String dateformat = reader.readLine();
			ArrayList<Order> ordersList = DBNinja.getOrdersByDate(dateformat);
			if(ordersList.isEmpty()) {
				System.out.println("No orders to display, returning to menu.");
				PrintMenu();
			} else {
				for(Order order: ordersList) {
					//Question: toSimplePrint has Dateplaced also, but the video doesn't have that. should we write another fn?
					System.out.println(order.toSimplePrint());
				}
				DisplayOrdersInDetail(ordersList, reader);
			}
		} else {
			System.out.println("I don't understand that input, returning to menu");
			PrintMenu();
		}
		//System.out.println("Incorrect entry, returning to menu.");
	}

	public static void DisplayOrdersInDetail(ArrayList<Order> ordersList, BufferedReader reader) throws SQLException, IOException {
		System.out.println("Which order would you like to see in detail? Enter the number (-1 to exit):");
		int orderId = Integer.parseInt(reader.readLine());
		if(orderId == -1) {
			PrintMenu();
		} else {
			Order o = null;
			for(Order order:ordersList) {
				if(order.getOrderID() == orderId) {
					//get order of that orderid
					o = order;
					if(order.getOrderType().equalsIgnoreCase("dinein")) {
						System.out.println(order + " | Customer was sat at table number " +DBNinja.getTableNumberFromOrderId(order.getOrderID()));
					} else {
						System.out.println(order);
					}
				}
			}
			if(o == null) {
				System.out.println("Incorrect entry, returning to menu.");
				PrintMenu();
			} else {
				//send orderid to orderdiscountable to get discountid, and send discountid to discount table to get name
				List<String> orderDiscountsList = DBNinja.getDiscountNameByOrderID(orderId);
				if(orderDiscountsList.isEmpty()) {
					System.out.println("NO ORDER DISCOUNTS");
				} else {
					System.out.print("ORDER DISCOUNTS: ");
					for(String Olist: orderDiscountsList) {
						System.out.println(Olist);
					}
				}

				//send the orderid to pizza table and search with orderid, to get pizzas
				//check the pizza date here, it will give empty - correct this
				ArrayList<Pizza> pizza = DBNinja.getPizzaByOrderId(orderId);
				if(pizza.size() == 0) {
					System.out.println("No Pizzas or discounts added to Order");
				} else {
					for(int i=0;i<pizza.size();i++) {
						System.out.println(pizza.get(i).toString());
						//send pizzaid to pizzadiscounttable to get discountid, and send discountid to discount table to get name
						List<String> pizzaDiscountsList = DBNinja.getDiscountNameByPizzaID(pizza.get(i).getPizzaID());
						if(pizzaDiscountsList.isEmpty()) {
							System.out.println("NO PIZZA DISCOUNTS");
						} else {
							System.out.print("PIZZA DISCOUNTS: ");
							for(String list: pizzaDiscountsList) {
								System.out.println(list);
							}
						}
					}
				}
			}
		}
	}

	
	// When an order is completed, we need to make sure it is marked as complete
	public static void MarkOrderAsComplete() throws SQLException, IOException 
	{
		/*
		 * All orders that are created through java (part 3, not the orders from part 2) should start as incomplete
		 * 
		 * When this method is called, you should print all of the "opoen" orders marked
		 * and allow the user to choose which of the incomplete orders they wish to mark as complete
		 * 
		 */

		//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		ArrayList<Order> openOrders = DBNinja.getOrders(true);
		if(openOrders.isEmpty()) {
			System.out.println("There are no open orders currently... returning to menu...");
			PrintMenu();
		} else {
			for (Order order : openOrders) {
				//Question: toSimplePrint has Dateplaced also, but the video doesn't have that. should we write another fn?
				System.out.println(order.toSimplePrint());
			}
			System.out.println("Which order would you like mark as complete? Enter the OrderID: ");
			int orderId = Integer.parseInt(reader.readLine());
			Order o = null;
			for(Order order : openOrders) {
				if(order.getOrderID() == orderId) {
					//get order of that orderid
					o = order;
				}
			}
			if(o == null) {
				System.out.println("Incorrect entry, not an option");
			} else {
				DBNinja.completeOrder(o);
			}
		}
		// User Input Prompts...
//		System.out.println("There are no open orders currently... returning to menu...");
//		System.out.println("Which order would you like mark as complete? Enter the OrderID: ");
//		System.out.println("Incorrect entry, not an option");
	}

	public static void ViewInventoryLevels() throws SQLException, IOException 
	{
		/*
		 * Print the inventory. Display the topping ID, name, and current inventory
		*/
		DBNinja.printInventory();
		
	}


	public static void AddInventory() throws SQLException, IOException 
	{
		/*
		 * This should print the current inventory and then ask the user which topping (by ID) they want to add more to and how much to add
		 */
		// User Input Prompts...
		//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		DBNinja.printInventory();
		System.out.println("Which topping do you want to add inventory to? Enter the number: ");
		int toppingId = Integer.parseInt(reader.readLine());
		System.out.println("How many units would you like to add? ");
		int quantity = Integer.parseInt(reader.readLine());

		Topping topping = DBNinja.getToppingFromToppingID(toppingId);
		if(topping == null) {
			System.out.println("Incorrect entry, not an option");
		} else {
			DBNinja.addToInventory(topping, quantity);
		}
	}

	// A method that builds a pizza. Used in our add new order method
	public static Pizza buildPizza(int orderID) throws SQLException, IOException 
	{
		
		/*
		 * This is a helper method for first menu option.
		 * 
		 * It should ask which size pizza the user wants and the crustType.
		 * 
		 * Once the pizza is created, it should be added to the DB.
		 * 
		 * We also need to add toppings to the pizza. (Which means we not only need to add toppings here, but also our bridge table)
		 * 
		 * We then need to add pizza discounts (again, to here and to the database)
		 * 
		 * Once the discounts are added, we can return the pizza
		 */

		 Pizza ret = null;
		
		// User Input Prompts...
		//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("What size is the pizza?");
			System.out.println("1."+DBNinja.size_s);
			System.out.println("2."+DBNinja.size_m);
			System.out.println("3."+DBNinja.size_l);
			System.out.println("4."+DBNinja.size_xl);
			System.out.println("Enter the corresponding number: ");
			int pizzaSizeNumber = Integer.parseInt(reader.readLine());
			System.out.println("What crust for this pizza?");
			System.out.println("1."+DBNinja.crust_thin);
			System.out.println("2."+DBNinja.crust_orig);
			System.out.println("3."+DBNinja.crust_pan);
			System.out.println("4."+DBNinja.crust_gf);
			System.out.println("Enter the corresponding number: ");
			int pizzaCrustNumber = Integer.parseInt(reader.readLine());

			String size = "";
			if(pizzaSizeNumber == 1) {
				size = DBNinja.size_s;
			} else if(pizzaSizeNumber == 2) {
				size = DBNinja.size_m;
			} else if(pizzaSizeNumber == 3) {
				size = DBNinja.size_l;
			} else if(pizzaSizeNumber == 4) {
				size = DBNinja.size_xl;
			}

			String crust = "";
			if(pizzaCrustNumber == 1) {
				crust = DBNinja.crust_thin;
			} else if(pizzaCrustNumber == 2) {
				crust = DBNinja.crust_orig;
			} else if(pizzaCrustNumber == 3) {
				crust = DBNinja.crust_pan;
			} else if(pizzaCrustNumber == 4) {
				crust = DBNinja.crust_gf;
			}

			int pizzaId = DBNinja.getMaxPizzaId();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String pizzaTimestamp = dtf.format(now);

			ret = new Pizza(pizzaId, size, crust, orderID, "In Progress",
					pizzaTimestamp, 0.0, 0.0);
			DBNinja.addPizza(ret);

			boolean addToppings = true;
			while(addToppings) {
				System.out.println("Available Toppings:");
				DBNinja.printInventory();
				System.out.println("Which topping do you want to add? Enter the TopID. Enter -1 to stop adding toppings:");
				int toppingId = Integer.parseInt(reader.readLine());
				if(toppingId != -1) {
					Topping topping = DBNinja.getToppingFromToppingID(toppingId);
					if(topping == null) {
						System.out.println("Enter valid Topping ID");
					} else {
						System.out.println("Do you want to add extra topping? Enter y/n");
						char option = Character.toLowerCase((char) reader.read());
						reader.readLine();
						if(option == 'y') {
							DBNinja.useTopping(ret, topping, true);
							ret.addToppings(topping, true);
							DBNinja.updatePizzaTopping(ret, topping, true);
						} else {
							DBNinja.useTopping(ret, topping, false);
							ret.addToppings(topping, false);
							DBNinja.updatePizzaTopping(ret, topping, false);
						}
					}
				} else {
					addToppings = false;
				}
				ret.setCustPrice(ret.getCustPrice());
				ret.setBusPrice(ret.getBusPrice());
			}
			double priceToCustomer = DBNinja.getBaseCustPrice(size, crust);
			double priceToBusiness = DBNinja.getBaseBusPrice(size, crust);
			ret.setCustPrice(ret.getCustPrice() + priceToCustomer);
			ret.setBusPrice(ret.getBusPrice() + priceToBusiness);

			System.out.println("Do you want to add discounts to this Pizza? Enter y/n?");
			char discountOption = Character.toLowerCase((char)reader.read());
			reader.readLine();
			if(discountOption == 'y') {
				boolean pizzaDiscounts = true;
				while(pizzaDiscounts) {
					ArrayList<Discount> discounts = DBNinja.getDiscountList();
					for(Discount discount : discounts) {
						System.out.println(discount.toString());
					}
					System.out.println("Which Pizza Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
					int discountId = Integer.parseInt(reader.readLine());
					if(discountId != -1) {
						Discount discount = null;
						for(Discount d : discounts) {
							if(d.getDiscountID() == discountId) {
								discount = d;
							}
						}
						ret.addDiscounts(discount);
						DBNinja.usePizzaDiscount(ret, discount);
					} else {
						pizzaDiscounts = false;
					}
				}
			}
			ret.setCustPrice(ret.getCustPrice());
			DBNinja.addPizza(ret);
			//System.out.println("Do you want to add more discounts to this Pizza? Enter y/n?");
		return ret;
	}
	
	
	public static void PrintReports() throws SQLException, NumberFormatException, IOException
	{
		/*
		 * This method asks the use which report they want to see and calls the DBNinja method to print the appropriate report.
		 * 
		 */

		// User Input Prompts...
		//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Which report do you wish to print? Enter\n(a) ToppingPopularity\n(b) ProfitByPizza\n(c) ProfitByOrderType:");
		char option = Character.toLowerCase((char) reader.read());
		reader.readLine();
		if(option == 'a') {
			DBNinja.printToppingPopReport();
		} else if(option == 'b') {
			DBNinja.printProfitByPizzaReport();
		} else if(option == 'c') {
			DBNinja.printProfitByOrderType();
		} else {
			System.out.println("I don't understand that input... returning to menu...");
			PrintMenu();
		}
	}

	//Prompt - NO CODE SHOULD TAKE PLACE BELOW THIS LINE
	// DO NOT EDIT ANYTHING BELOW HERE, THIS IS NEEDED TESTING.
	// IF YOU EDIT SOMETHING BELOW, IT BREAKS THE AUTOGRADER WHICH MEANS YOUR GRADE WILL BE A 0 (zero)!!

	public static void PrintMenu() {
		System.out.println("\n\nPlease enter a menu option:");
		System.out.println("1. Enter a new order");
		System.out.println("2. View Customers ");
		System.out.println("3. Enter a new Customer ");
		System.out.println("4. View orders");
		System.out.println("5. Mark an order as completed");
		System.out.println("6. View Inventory Levels");
		System.out.println("7. Add Inventory");
		System.out.println("8. View Reports");
		System.out.println("9. Exit\n\n");
		System.out.println("Enter your option: ");
	}

	/*
	 * autograder controls....do not modiify!
	 */

	public final static String autograder_seed = "6f1b7ea9aac470402d48f7916ea6a010";

	
	private static void autograder_compilation_check() {

		try {
			Order o = null;
			Pizza p = null;
			Topping t = null;
			Discount d = null;
			Customer c = null;
			ArrayList<Order> alo = null;
			ArrayList<Discount> ald = null;
			ArrayList<Customer> alc = null;
			ArrayList<Topping> alt = null;
			double v = 0.0;
			String s = "";

			DBNinja.addOrder(o);
			DBNinja.addPizza(p);
			DBNinja.useTopping(p, t, false);
			DBNinja.usePizzaDiscount(p, d);
			DBNinja.useOrderDiscount(o, d);
			DBNinja.addCustomer(c);
			DBNinja.completeOrder(o);
			alo = DBNinja.getOrders(false);
			o = DBNinja.getLastOrder();
			alo = DBNinja.getOrdersByDate("01/01/1999");
			ald = DBNinja.getDiscountList();
			d = DBNinja.findDiscountByName("Discount");
			alc = DBNinja.getCustomerList();
			c = DBNinja.findCustomerByPhone("0000000000");
			alt = DBNinja.getToppingList();
			t = DBNinja.findToppingByName("Topping");
			DBNinja.addToInventory(t, 1000.0);
			v = DBNinja.getBaseCustPrice("size", "crust");
			v = DBNinja.getBaseBusPrice("size", "crust");
			DBNinja.printInventory();
			DBNinja.printToppingPopReport();
			DBNinja.printProfitByPizzaReport();
			DBNinja.printProfitByOrderType();
			s = DBNinja.getCustomerName(0);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}


}


