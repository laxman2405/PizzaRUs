package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// Change these variables to however you record dine-in, pick-up and delivery, and sizes and crusts
	public final static String pickup = "pickup";
	public final static String delivery = "delivery";
	public final static String dine_in = "dinein";

	public final static String size_s = "Small";
	public final static String size_m = "Medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";



	
	private static boolean connect_to_db() throws SQLException, IOException {

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	
	public static void addOrder(Order o) throws SQLException, IOException 
	{
		//connect_to_db();
		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, and pickup tables
		 * 
		 */
		try {
			if(isOrderIdExists(o.getOrderID())) {
				connect_to_db();
				String updateStatement = "update orderlist set OrderListPriceToCustomer=?,OrderListPriceToBusiness=?, OrderListType=?, OrderListCustomerID=? where OrderListID=?";
				PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);
				preparedStatement.setDouble(1, o.getCustPrice());
				preparedStatement.setDouble(2, o.getBusPrice());
				preparedStatement.setString(3, o.getOrderType());
				preparedStatement.setInt(4, o.getCustID());
				preparedStatement.setInt(5, o.getOrderID());
				preparedStatement.executeUpdate();
			} else {
				connect_to_db();
				String sql = "INSERT INTO orderlist(OrderListCustomerID, OrderListPriceToBusiness, OrderListPriceToCustomer, OrderListDate, IsCompleted, OrderListType) values(?,?,?,?,?,?)";
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
				preparedStatement.setInt(1, o.getCustID());
				preparedStatement.setDouble(2, o.getBusPrice());
				preparedStatement.setDouble(3, o.getCustPrice());
				preparedStatement.setString(4, o.getDate());
				preparedStatement.setInt(5, o.getIsComplete());
				preparedStatement.setString(6, o.getOrderType());
				preparedStatement.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	private static boolean isOrderIdExists(int orderId) {
		boolean exists = false;
		try {
			connect_to_db();
			String sql = "SELECT * FROM orderlist WHERE OrderListID = "+orderId;
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			ResultSet result = preparedStatement.executeQuery();
			while(result.next()) {
				exists = true;
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return exists;
	}

	public static int getMaxPizzaId() throws SQLException, IOException
	{
		int pizzaId = -1;
		try {
			connect_to_db();
			String sql = "SELECT MAX(PizzaID) AS 'MaxPizza' from pizza";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			ResultSet result = preparedStatement.executeQuery();
			while(result.next()) {
				pizzaId = result.getInt("MaxPizza");
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return pizzaId + 1;
	}
	
	public static void addPizza(Pizza p) throws SQLException, IOException
	{
		/*
		 * Add the code needed to insert the pizza into into the database.
		 * Keep in mind adding pizza discounts and toppings associated with the pizza,
		 * there are other methods below that may help with that process.
		 * 
		 */
		try {
			//connect_to_db();
			if(isPizzaIdExists(p.getPizzaID())) {
				connect_to_db();
				String updateStatement = "update pizza set PizzaPriceToBusiness=?,PizzaPriceToCustomer=? where PizzaID=?";
				PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);
				preparedStatement.setDouble(1, p.getBusPrice());
				preparedStatement.setDouble(2, p.getCustPrice());
				preparedStatement.setInt(3, p.getPizzaID());
				preparedStatement.executeUpdate();
			} else {
				connect_to_db();
				String sql = "INSERT INTO pizza(PizzaOrderListID, PizzaCrustType, PizzaSize, PizzaStatus, PizzaPriceToBusiness, PizzaPriceToCustomer) values(?, ?, ?,?,?,?)";
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
				preparedStatement.setInt(1, p.getOrderID());
				preparedStatement.setString(2, p.getCrustType());
				preparedStatement.setString(3, p.getSize());
				preparedStatement.setString(4, p.getPizzaState());
				preparedStatement.setDouble(5, p.getBusPrice());
				preparedStatement.setDouble(6, p.getCustPrice());
				preparedStatement.executeUpdate();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	private static boolean isPizzaIdExists(int pizzaId) {
		boolean exists = false;
		try {
			connect_to_db();
			String sql = "SELECT * FROM pizza WHERE PizzaID = "+pizzaId;
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			ResultSet result = preparedStatement.executeQuery();
			while(result.next()) {
				exists = true;
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return exists;
	}

	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException //this method will update toppings inventory in SQL and add entities to the Pizzatops table. Pass in the p pizza that is using t topping
	{
		/*
		 * This method should do 2 two things.
		 * - update the topping inventory every time we use t topping (accounting for extra toppings as well)
		 * - connect the topping to the pizza
		 *   What that means will be specific to your yimplementatinon.
		 * 
		 * Ideally, you should't let toppings go negative....but this should be dealt with BEFORE calling this method.
		 * 
		 */
		try {
			connect_to_db();
			String size = p.getSize();
			double toppingQty = 0.0;
			if(size == size_s) {
				toppingQty = t.getPerAMT();
			} else if(size == size_m) {
				toppingQty = t.getMedAMT();
			} else if(size == size_l) {
				toppingQty = t.getLgAMT();
			} else {
				toppingQty = t.getXLAMT();
			}

			if(isDoubled) {
				toppingQty = 2 * toppingQty;
				updateToppingMinInvLvl(t, toppingQty);
			} else {
				updateToppingMinInvLvl(t, toppingQty);
			}
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void updateToppingMinInvLvl(Topping t, double toppingQty) throws SQLException {
		try {
			if(t.getCurINVT() - toppingQty < 0) {
				System.out.println("We are out of topping");
			} else {
				String updateStatement = "UPDATE topping set ToppingCurrentInvLvl=ToppingCurrentInvLvl- " + toppingQty + " where ToppingID= " + t.getTopID();
				PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);
				preparedStatement.executeUpdate();
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}


	public static String getAddressFromCustomerId(int customerId) throws SQLException, IOException
	{
		String address = "";
		try {
			connect_to_db();
			String sql = "SELECT DeliveryAddress FROM delivery WHERE DeliveryOrderListID IN (SELECT OrderListID FROM orderlist WHERE OrderListCustomerID = "+customerId+")";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			ResultSet result = preparedStatement.executeQuery();
			while(result.next()) {
				address = result.getString("DeliveryAddress");
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return address;
	}
	
	
	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException
	{
		/*
		 * This method connects a discount with a Pizza in the database.
		 * 
		 * What that means will be specific to your implementatinon.
		 */
		try {
			connect_to_db();
			String sql = "INSERT INTO pizzadiscount(PizzaDiscountPizzaId, PizzaDiscountDiscountID) values(?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, p.getPizzaID());
			preparedStatement.setInt(2, d.getDiscountID());
			preparedStatement.executeUpdate();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException
	{
		/*
		 * This method connects a discount with an order in the database
		 * 
		 * You might use this, you might not depending on where / how to want to update
		 * this information in the dabast
		 */
		try {
			connect_to_db();
			String sql = "insert into orderdiscount(OrderDiscountOrderListID, OrderDiscountDiscountID) values(?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, o.getOrderID());
			preparedStatement.setInt(2, d.getDiscountID());
			preparedStatement.executeUpdate();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static List<String> getDiscountNameByOrderID(int orderId) throws SQLException, IOException
	{
		List<String> discountName = new ArrayList<>();
		try {
			connect_to_db();
			String sql = "SELECT DiscountName FROM discount WHERE DiscountID IN (SELECT OrderDiscountDiscountID FROM orderdiscount WHERE OrderDiscountOrderListID = "+orderId+")";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			ResultSet result = preparedStatement.executeQuery();
			while(result.next()) {
				discountName.add(result.getString("DiscountName"));
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return discountName;
	}

	public static List<String> getDiscountNameByPizzaID(int pizzaId) throws SQLException, IOException
	{
		List<String> discountName = new ArrayList<>();
		try {
			connect_to_db();
			String sql = "SELECT DiscountName FROM discount WHERE DiscountID IN (SELECT PizzaDiscountDiscountID FROM pizzadiscount WHERE PizzaDiscountPizzaId = "+pizzaId+")";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			ResultSet result = preparedStatement.executeQuery();
			while(result.next()) {
				discountName.add(result.getString("DiscountName"));
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return discountName;
	}

	public static ArrayList<Pizza> getPizzaByOrderId(int orderId) throws SQLException, IOException
	{
		ArrayList<Pizza> pizzaList = new ArrayList<>();
		try {
			connect_to_db();
			String sql = "SELECT * FROM pizza WHERE PizzaOrderListID = "+orderId+"";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			ResultSet result = preparedStatement.executeQuery();
			int pizzaId = 0, basePriceId = 0;
			String status = "";
			String size = "", crust = "";
			double priceToBusiness = 0.0, priceToCustomer = 0.0;
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String timestamp = dtf.format(now);
			while(result.next()) {
				pizzaId = result.getInt("PizzaID");
				size = result.getString("PizzaSize");
				crust = result.getString("PizzaCrustType");
				priceToBusiness = result.getDouble("PizzaPriceToBusiness");
				priceToCustomer = result.getDouble("PizzaPriceToCustomer");
				status = result.getString("PizzaStatus");
				pizzaList.add(new Pizza(pizzaId, size, crust, orderId, status, timestamp, priceToCustomer, priceToBusiness));
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return pizzaList;
	}

	public static void updatePizzaTopping(Pizza p, Topping t, boolean isExtra) throws SQLException, IOException
	{
		try {
			connect_to_db();
			String sql = "insert into pizzatopping(PizzaToppingPizzaID, PizzaToppingToppingID, PizzaToppingIsExtra) values(?,?,?)";

			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, p.getPizzaID());
			preparedStatement.setInt(2, t.getTopID());
			preparedStatement.setBoolean(3, isExtra);
			preparedStatement.executeUpdate();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void addCustomer(Customer c) throws SQLException, IOException {
		connect_to_db();
		/*
		 * This method adds a new customer to the database.
		 * 
		 */
		try {
			connect_to_db();
			String addCustomerSQL = "INSERT INTO customer(" +
					"CustomerFirstName, CustomerLastName, CustomerMobile)" +
					"values(?,?,?)";
			PreparedStatement addCustomerStatement = conn.prepareStatement(addCustomerSQL);
			addCustomerStatement.setString(1, c.getFName());
			addCustomerStatement.setString(2, c.getLName());
			addCustomerStatement.setString(3, c.getPhone());
			addCustomerStatement.executeUpdate();
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static int getLastInsertedCustId() throws SQLException, IOException
	{
		int customerId = -1;
		try {
			connect_to_db();
			String sql = "SELECT * FROM customer where CustomerID = (SELECT MAX(CustomerID) from customer)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			ResultSet result = preparedStatement.executeQuery();
			while(result.next()) {
				customerId = result.getInt("CustomerID");
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return customerId;
	}

	public static void completeOrder(Order o) throws SQLException, IOException {
		/*
		 * Find the specifed order in the database and mark that order as complete in the database.
		 * 
		 */
		try {
			connect_to_db();

			String updateOrder = "UPDATE orderlist SET IsCompleted = ? where OrderListID = ?";
			PreparedStatement orderPs = conn.prepareStatement(updateOrder);
			orderPs.setInt(1, 1);
			orderPs.setInt(2, o.getOrderID());
			orderPs.executeUpdate();

			String updatePizza = "UPDATE pizza SET PizzaStatus = ?  where PizzaOrderListID = ?" ;
			PreparedStatement pizzaPS = conn.prepareStatement(updatePizza);
			pizzaPS.setString(1,"Completed");
			pizzaPS.setInt(2,o.getOrderID());
			pizzaPS.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static ArrayList<Order> getOrders(boolean openOnly) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Return an arraylist of all of the orders.
		 * 	openOnly == true => only return a list of open (ie orders that have not been marked as completed)
		 *           == false => return a list of all the orders in the database
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 * 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		 */
		ArrayList<Order> ordersList = new ArrayList<>();
		try {
			connect_to_db();
			if(openOnly) {
				String ordersSQL = "SELECT * FROM orderlist WHERE IsCompleted = 0";
				PreparedStatement completedOrders = conn.prepareStatement(ordersSQL);
				ResultSet ordersSet = completedOrders.executeQuery();

				while(ordersSet.next()) {
					int orderlistID = ordersSet.getInt("OrderListID");
					int customerID = ordersSet.getInt("OrderListCustomerID");
					double businessPrice = ordersSet.getDouble("OrderListPriceToBusiness");
					double cusomterPrice = ordersSet.getDouble("OrderListPriceToCustomer");
					int orderStatus = ordersSet.getInt("IsCompleted");
					String orderdate = ordersSet.getString("OrderListDate");
					String orderType = ordersSet.getString("OrderListType");
					ordersList.add(new Order(orderlistID, customerID, orderType, orderdate,
									cusomterPrice, businessPrice, orderStatus));
				}
			} else {
				String ordersSQL = "SELECT * FROM orderlist";
				PreparedStatement completedOrders = conn.prepareStatement(ordersSQL);
				ResultSet ordersSet = completedOrders.executeQuery();

				while (ordersSet.next()) {
					int orderlistID = ordersSet.getInt("OrderListID");
					int customerID = ordersSet.getInt("OrderListCustomerID");
					double businessPrice = ordersSet.getDouble("OrderListPriceToBusiness");
					double cusomterPrice = ordersSet.getDouble("OrderListPriceToCustomer");
					String orderdate = ordersSet.getString("OrderListDate");
					int orderStatus = ordersSet.getInt("IsCompleted");
					String orderType = ordersSet.getString("OrderListType");
					ordersList.add(new Order(orderlistID, customerID, orderType, orderdate,
							cusomterPrice, businessPrice, orderStatus));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return ordersList;
	}

	public static ArrayList<Order> getCompletedOrders() throws SQLException, IOException {
		ArrayList<Order> ordersList = new ArrayList<>();
		try {
			connect_to_db();
			String ordersSQL = "SELECT * FROM orderlist WHERE IsCompleted = 1";
			PreparedStatement completedOrders = conn.prepareStatement(ordersSQL);
			ResultSet ordersSet = completedOrders.executeQuery();

			while(ordersSet.next()) {
				int orderlistID = ordersSet.getInt("OrderListID");
				int customerID = ordersSet.getInt("OrderListCustomerID");
				double businessPrice = ordersSet.getDouble("OrderListPriceToBusiness");
				double cusomterPrice = ordersSet.getDouble("OrderListPriceToCustomer");
				int orderStatus = ordersSet.getInt("IsCompleted");
				String orderdate = ordersSet.getString("OrderListDate");
				String orderType = ordersSet.getString("OrderListType");
				ordersList.add(new Order(orderlistID, customerID, orderType, orderdate,
						cusomterPrice, businessPrice, orderStatus));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return ordersList;
	}
	
	public static Order getLastOrder() throws SQLException {
		/*
		 * Query the database for the LAST order added
		 * then return an Order object for that order.
		 * NOTE...there should ALWAYS be a "last order"!
		 */

		try {
			connect_to_db();
			String ordersSQL = "SELECT * FROM orderlist ORDER BY OrderListID DESC LIMIT 1";
			PreparedStatement prepStmt = conn.prepareStatement(ordersSQL);
			ResultSet lastOrder = prepStmt.executeQuery();
			while(lastOrder.next()) {
				int orderlistID = lastOrder.getInt("OrderListID");
				int customerID = lastOrder.getInt("OrderListCustomerID");
				double businessPrice = lastOrder.getDouble("OrderListPriceToBusiness");
				double cusomterPrice = lastOrder.getDouble("OrderListPriceToCustomer");
				String orderdate = lastOrder.getString("OrderListDate");
				int isCompleted = lastOrder.getInt("IsCompleted");
				String orderType = lastOrder.getString("OrderListType");
				return new Order(orderlistID, customerID, orderType, orderdate,
						cusomterPrice, businessPrice, isCompleted);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		 return null;
	}

	public static ArrayList<Order> getOrdersByDate(String date) throws SQLException, IOException {
		/*
		 * Query the database for ALL the orders placed on a specific date
		 * and return a list of those orders.
		 *  
		 */

		ArrayList<Order> ordersList = new ArrayList<>();
		try {
			connect_to_db();
			String sql = "select * from orderlist where CAST(OrderListDate AS DATE) = '"+date+"'";
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			ResultSet orders = prepStmt.executeQuery();

			while(orders.next()) {
				int orderlistID = orders.getInt("OrderListID");
				int customerID = orders.getInt("OrderListCustomerID");
				double businessPrice = orders.getDouble("OrderListPriceToBusiness");
				double cusomterPrice = orders.getDouble("OrderListPriceToCustomer");
				String orderdate = orders.getString("OrderListDate");
				int orderStatus = orders.getInt("IsCompleted");
				String orderType = orders.getString("OrderListType");
				ordersList.add(new Order(orderlistID, customerID, orderType, orderdate,
						cusomterPrice, businessPrice, orderStatus));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		 return ordersList;
	}
		
	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
		connect_to_db();
		/* 
		 * Query the database for all the available discounts and 
		 * return them in an arrayList of discounts.
		 * 
		*/
		ArrayList<Discount> discountList = new ArrayList<>();
		try {
			connect_to_db();
			String sql = "select * from discount";
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			ResultSet discounts = prepStmt.executeQuery();

			while(discounts.next()) {
				int discountID = discounts.getInt("DiscountID");
				String name = discounts.getString("DiscountName");
				boolean isPercentage = discounts.getBoolean("IsPercent");
				double amount = discounts.getDouble("DiscountAmount");
				discountList.add(new Discount(discountID, name, amount, isPercentage));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return discountList;
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static Discount findDiscountByName(String name) throws SQLException, IOException {
		/*
		 * Query the database for a discount using it's name.
		 * If found, then return an OrderDiscount object for the discount.
		 * If it's not found....then return null
		 *  
		 */
		try {
			connect_to_db();
			String sql = "SELECT * FROM discount where DiscountName = '"+name+"'";
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			ResultSet discounts = prepStmt.executeQuery();

			while(discounts.next()) {
				int discountID = discounts.getInt("DiscountID");
				String discountName = discounts.getString("DiscountName");
				boolean isPercentage = discounts.getBoolean("IsPercent");
				double amount = discounts.getDouble("DiscountAmount");
				return new Discount(discountID, discountName, amount, isPercentage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		 return null;
	}


	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Query the data for all the customers and return an arrayList of all the customers. 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		*/
		ArrayList<Customer> customersList = new ArrayList<>();
		try {
			connect_to_db();
			String sql = "SELECT * FROM customer ORDER BY CustomerID";
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			ResultSet customers = prepStmt.executeQuery();

			while(customers.next()) {
				int custID = customers.getInt("CustomerID");
				String fname = customers.getString("CustomerFirstName");
				String lname = customers.getString("CustomerLastName");
				String mobile = customers.getString("CustomerMobile");
				customersList.add(new Customer(custID, fname, lname, mobile));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return customersList;
	}

	public static Customer findCustomerByPhone(String phoneNumber) {
		/*
		 * Query the database for a customer using a phone number.
		 * If found, then return a Customer object for the customer.
		 * If it's not found....then return null
		 *  
		 */

		try {
			connect_to_db();
			String sql = "SELECT * FROM customer WHERE CustomerMobile = '"+phoneNumber+"'";
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			ResultSet customers = prepStmt.executeQuery();

			while(customers.next()) {
				int custID = customers.getInt("CustomerID");
				String fname = customers.getString("CustomerFname");
				String lname = customers.getString("CustomerLname");
				String mobile = customers.getString("CustomerMobile");
				return new Customer(custID, fname, lname, mobile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}


	public static ArrayList<Topping> getToppingList() throws SQLException, IOException {
		//connect_to_db();
		/*
		 * Query the database for the aviable toppings and 
		 * return an arrayList of all the available toppings. 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		 */
		ArrayList<Topping> toppingList = new ArrayList<>();
		try {
			connect_to_db();
			String sql = "SELECT * FROM topping";
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			ResultSet toppings = prepStmt.executeQuery();

			while(toppings.next()) {
				int toppingId = toppings.getInt("ToppingID");
				String toppingName = toppings.getString("ToppingName");
				double toppingPriceToCustomer = toppings.getDouble("ToppingPriceToCustomer");
				double toppingPriceToBusiness = toppings.getDouble("ToppingPriceToBusiness");
				int toppingCurrentInvLvl = toppings.getInt("ToppingCurrentInvLvl");
				int toppingMinInvLvl = toppings.getInt("ToppingMinInvLvl");
				double toppingQuantityForSmall = toppings.getDouble("ToppingQuantityForSmall");
				double toppingQuantityForMedium = toppings.getDouble("ToppingQuantityForMedium");
				double toppingQuantityForLarge = toppings.getDouble("ToppingQuantityForLarge");
				double toppingQuantityForXLarge = toppings.getDouble("ToppingQuantityForXLarge");
				toppingList.add(new Topping(toppingId, toppingName,
						toppingQuantityForSmall, toppingQuantityForMedium, toppingQuantityForLarge,
						toppingQuantityForXLarge, toppingPriceToCustomer, toppingPriceToBusiness,
						toppingMinInvLvl, toppingCurrentInvLvl));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return toppingList;
	}

	public static Topping findToppingByName(String name){
		/*
		 * Query the database for the topping using it's name.
		 * If found, then return a Topping object for the topping.
		 * If it's not found....then return null
		 *  
		 */

		try {
			connect_to_db();
			String sql = "SELECT * FROM topping WHERE ToppingName = '"+name+"'";
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			ResultSet topping = prepStmt.executeQuery();

			while(topping.next()) {
				int toppingId = topping.getInt("ToppingID");
				String toppingName = topping.getString("ToppingName");
				double toppingPriceToCustomer = topping.getDouble("ToppingPriceToCustomer");
				double toppingPriceToBusiness = topping.getDouble("ToppingPriceToBusiness");
				int toppingCurrentInvLvl = topping.getInt("ToppingCurrentInvLvl");
				int toppingMinInvLvl = topping.getInt("ToppingMinInvLvl");
				double toppingQuantityForSmall = topping.getDouble("ToppingQuantityForSmall");
				double toppingQuantityForMedium = topping.getDouble("ToppingQuantityForMedium");
				double toppingQuantityForLarge = topping.getDouble("ToppingQuantityForLarge");
				double toppingQuantityForXLarge = topping.getDouble("ToppingQuantityForXLarge");
				return new Topping(toppingId, toppingName,
						toppingQuantityForSmall, toppingQuantityForMedium, toppingQuantityForLarge,
						toppingQuantityForXLarge, toppingPriceToCustomer, toppingPriceToBusiness,
						toppingMinInvLvl, toppingCurrentInvLvl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		 return null;
	}


	public static void addToInventory(Topping t, double quantity) throws SQLException, IOException {
		//connect_to_db();
		/*
		 * Updates the quantity of the topping in the database by the amount specified.
		 * 
		 * */
		try {
			connect_to_db();
			String sql = "UPDATE topping SET ToppingCurrentInvLvl = ToppingCurrentInvLvl+? WHERE ToppingID = ?";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setDouble(1, quantity);
			preparedStatement.setInt(2, t.getTopID());
			preparedStatement.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static Topping getToppingFromToppingID(int toppingId) throws SQLException, IOException
	{
		try {
			connect_to_db();
			String maxOrdSql = "SELECT * FROM topping where ToppingID = "+toppingId;
			PreparedStatement preparedStatement = conn.prepareStatement(maxOrdSql);
			ResultSet topping = preparedStatement.executeQuery();

			while (topping.next()) {
				int id = topping.getInt("ToppingID");
				String toppingName = topping.getString("ToppingName");
				double toppingPriceToCustomer = topping.getDouble("ToppingPriceToCustomer");
				double toppingPriceToBusiness = topping.getDouble("ToppingPriceToBusiness");
				int toppingCurrentInvLvl = topping.getInt("ToppingCurrentInvLvl");
				int toppingMinInvLvl = topping.getInt("ToppingMinInvLvl");
				double toppingQuantityForSmall = topping.getDouble("ToppingQuantityForSmall");
				double toppingQuantityForMedium = topping.getDouble("ToppingQuantityForMedium");
				double toppingQuantityForLarge = topping.getDouble("ToppingQuantityForLarge");
				double toppingQuantityForXLarge = topping.getDouble("ToppingQuantityForXLarge");

				return new Topping(id, toppingName, toppingQuantityForSmall,
						toppingQuantityForMedium, toppingQuantityForLarge, toppingQuantityForXLarge,
						toppingPriceToCustomer, toppingPriceToBusiness, toppingMinInvLvl, toppingCurrentInvLvl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		/* 
		 * Query the database fro the base customer price for that size and crust pizza.
		 * 
		*/
		double baseCustomerPrice = 0.0;
		try {
			connect_to_db();
			String sql = "SELECT BasePriceCostToCustomer FROM baseprice where BasePriceSize = '"+size+"' AND BasePriceCrust = '"+crust+"'";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			ResultSet record = preparedStatement.executeQuery();

			while (record.next()) {
				baseCustomerPrice = record.getDouble("BasePriceCostToCustomer");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return baseCustomerPrice;
	}

	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		//connect_to_db();
		/* 
		 * Query the database fro the base business price for that size and crust pizza.
		 * 
		*/
		double baseBusinessPrice = 0.0;
		try {
			connect_to_db();
			String sql = "SELECT BasePricePriceToBusiness FROM baseprice where BasePriceSize = '"+size+"' AND BasePriceCrust = '"+crust+"'";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			ResultSet record = preparedStatement.executeQuery();

			while (record.next()) {
				baseBusinessPrice = record.getDouble("BasePricePriceToBusiness");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return baseBusinessPrice;
	}

	public static void printInventory() throws SQLException, IOException {
		//connect_to_db();
		/*
		 * Queries the database and prints the current topping list with quantities.
		 *  
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		try {
			connect_to_db();
			String sql = "SELECT ToppingID, ToppingName, ToppingCurrentInvLvl FROM topping order by ToppingID";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			ResultSet toppings = preparedStatement.executeQuery();

			System.out.println("ID\tName\t\tCurINVT");
			while (toppings.next()) {
				System.out.println(toppings.getString("ToppingID") +"\t"+ toppings.getString("ToppingName") + "\t\t" + toppings.getString("ToppingCurrentInvLvl"));
			}
			conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void printToppingPopReport() throws SQLException, IOException
	{
		/*
		 * Prints the ToppingPopularity view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		try {
			connect_to_db();
			String maxOrdSql = "SELECT * FROM ToppingPopularity";
			PreparedStatement preparedStatement = conn.prepareStatement(maxOrdSql);
			ResultSet toppings = preparedStatement.executeQuery();
			System.out.printf("Topping\t\tToppingCount");
			while (toppings.next()) {
				System.out.printf("\n"+toppings.getString("Topping")+"\t\t"+toppings.getInt("ToppingCount"));
			}
			conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void printProfitByPizzaReport() throws SQLException, IOException
	{
		/*
		 * Prints the ProfitByPizza view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		try {
			connect_to_db();
			String maxOrdSql = "SELECT * FROM ProfitByPizza";
			PreparedStatement preparedStatement = conn.prepareStatement(maxOrdSql);
			ResultSet report = preparedStatement.executeQuery();
			System.out.println("Pizza Size\t\tPizza Crust\t\tProfit\t\tLastOrderDate");
			while (report.next()) {
				String size = report.getString("Pizza Size");
				String crust = report.getString("Pizza Crust");
				Double profit = report.getDouble("Profit");
				String orderDate = report.getString("LastOrderDate");
				System.out.println(size+"\t\t"+crust+"\t\t"+profit+"\t\t"+orderDate);
			}
			//DO NOT FORGET TO CLOSE YOUR CONNECTION
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void printProfitByOrderType() throws SQLException, IOException
	{
		//connect_to_db();
		/*
		 * Prints the ProfitByOrderType view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		try {
			connect_to_db();
			String maxOrdSql = "SELECT * FROM ProfitByOrderType";
			PreparedStatement preparedStatement = conn.prepareStatement(maxOrdSql);
			ResultSet report = preparedStatement.executeQuery();
			System.out.println("OrderType\t\tOrder Month\t\tTotalOrderPrice\t\tTotalOrderCost\t\tProfit");
			while (report.next()) {
				String orderType = report.getString("OrderType");
				String orderMonth = report.getString("OrderMonth");
				Double orderPrice = report.getDouble("TotalOrderPrice");
				String orderCost = report.getString("TotalOrderCost");
				Double profit = report.getDouble("Profit");
				System.out.println(orderType+"\t\t"+orderMonth+"\t\t"+orderPrice+"\t\t"+orderCost+"\t\t"+profit);
			}
			//DO NOT FORGET TO CLOSE YOUR CONNECTION
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION	
	}
	
	
	
	public static String getCustomerName(int CustID) throws SQLException, IOException
	{
	/*
		 * This is a helper method to fetch and format the name of a customer
		 * based on a customer ID. This is an example of how to interact with 
		 * your database from Java.  It's used in the model solution for this project...so the code works!
		 * 
		 * OF COURSE....this code would only work in your application if the table & field names match!
		 *
		 */

		 connect_to_db();

		/* 
		 * an example query using a constructed string...
		 * remember, this style of query construction could be subject to sql injection attacks!
		 * 
		 */
		String cname1 = "";
		String query = "Select CustomerFirstName, CustomerLastName From customer WHERE CustomerID=" + CustID + ";";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		
		while(rset.next())
		{
			cname1 = rset.getString(1) + " " + rset.getString(2); 
		}

		/* 
		* an example of the same query using a prepared statement...
		* 
		*/
		String cname2 = "";
		PreparedStatement os;
		ResultSet rset2;
		String query2;
		query2 = "Select CustomerFirstName, CustomerLastName From customer WHERE CustomerID=?;";
		os = conn.prepareStatement(query2);
		os.setInt(1, CustID);
		rset2 = os.executeQuery();
		while(rset2.next())
		{
			cname2 = rset2.getString("CustomerFirstName") + " " + rset2.getString("CustomerLastName"); // note the use of field names in the getSting methods
		}

		conn.close();
		return cname1; // OR cname2
	}

	public static void updatedinein(int orderId, Integer tableNumber) throws SQLException, IOException {
		connect_to_db();
		try {
			String sql = "INSERT INTO dinein(DineinOrderListID,DineinTableNumber) VALUES (?, ?)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, orderId);
			preparedStatement.setInt(2, tableNumber);
			preparedStatement.executeUpdate();
			conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static int getTableNumberFromOrderId(int orderId) throws SQLException, IOException {
		int tableNumber = -1;
		try {
			connect_to_db();
			String sql = "SELECT DineinTableNumber FROM dinein WHERE DineinOrderListID = ?";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, orderId);
			ResultSet result = preparedStatement.executeQuery();
			while(result.next()) {
				tableNumber = result.getInt("DineinTableNumber");
			}
			conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return tableNumber;
	}

	public static void updatepickup(int orderId) throws SQLException, IOException {

		connect_to_db();
		try {
			String sql = "INSERT INTO pickup(PickupOrderListID) VALUES (?)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, orderId);
			preparedStatement.executeUpdate();
			conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void updatedelivery(int orderId, String address)throws SQLException, IOException {
		connect_to_db();
		try {
			String sql = "INSERT INTO delivery(DeliveryOrderListID," +
					"DeliveryAddress) " +
					"VALUES (?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, orderId);
			preparedStatement.setString(2, address);
			preparedStatement.executeUpdate();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}



	/*
	 * The next 3 private methods help get the individual components of a SQL datetime object. 
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}
	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}
	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}

	public static boolean checkDate(int year, int month, int day, String dateOfOrder)
	{
		if(getYear(dateOfOrder) > year)
			return true;
		else if(getYear(dateOfOrder) < year)
			return false;
		else
		{
			if(getMonth(dateOfOrder) > month)
				return true;
			else if(getMonth(dateOfOrder) < month)
				return false;
			else
			{
				if(getDay(dateOfOrder) >= day)
					return true;
				else
					return false;
			}
		}
	}


}