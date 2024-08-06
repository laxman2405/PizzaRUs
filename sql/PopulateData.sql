-- Created by Vishnu Sai Nara

USE Pizzeria;

INSERT INTO topping
(
ToppingName, 
ToppingPriceToCustomer, 
ToppingPriceToBusiness, 
ToppingMinInvLvl,
ToppingCurrentInvLvl, 
ToppingQuantityForSmall,
ToppingQuantityForMedium,
ToppingQuantityForLarge,
ToppingQuantityForXLarge
) VALUES ('Pepperoni', 1.25, 0.2, 50, 100, 2, 2.75, 3.5, 4.5),
 ('Sausage', 1.25, 0.15, 50, 100, 2.5, 3, 3.5, 4.25),
 ('Ham', 1.5, 0.15, 25, 78, 2, 2.5, 3.25, 4),
 ('Chicken', 1.75, 0.25, 25, 56, 1.5, 2, 2.25, 3),
 ('Green Pepper', 0.5, 0.02, 25, 79, 1, 1.5, 2, 2.5),
 ('Onion', 0.5, 0.02, 25, 85, 1, 1.5, 2, 2.75),
 ('Roma Tomato', 0.75, 0.03, 10, 86, 2, 3, 3.5, 4.5),
 ('Mushrooms', 0.75, 0.1, 50, 52, 1.5, 2, 2.5, 3),
 ('Black Olives', 0.6, 0.1, 25, 39, 0.75, 1, 1.5, 2),
 ('Pineapple', 1, 0.25, 0, 15, 1, 1.25, 1.75, 2),
 ('Jalapenos', 0.5, 0.05, 0, 64, 0.5, 0.75, 1.25, 1.75),
 ('Banana Peppers', 0.5, 0.05, 0, 36, 0.6, 1, 1.3, 1.75),
 ('Regular Cheese', 0.5, 0.12, 50, 250, 2, 3.5, 5, 7),
 ('Four Cheese Blend', 1, 0.15, 25, 150, 2, 3.5, 5, 7),
 ('Feta Cheese', 1.5, 0.18, 0, 75, 1.75, 3, 4, 5.5),
 ('Goat Cheese', 1.5, 0.2, 0, 54, 1.6, 2.75, 4, 5.5),
 ('Bacon', 1.5, 0.25, 0, 89, 1, 1.5, 2, 3);
  
 -- Empty as in zero or null?
INSERT INTO discount
(
	DiscountName,
    IsPercent,
    DiscountAmount
) VALUE ('Employee', true, '15'),
		('Lunch Special Medium',false, '1.0'),
        ('Lunch Special Large',false, '2.0'),
        ('Specialty Pizza',false, '1.5'),
        ('Happy Hour' ,true, '10'),
        ('Gameday Special',true, '20');
        
        
INSERT INTO baseprice
(
	BasePriceSize,
    BasePriceCrust,
    BasePriceCostToCustomer,
    BasePricePriceToBusiness
) VALUES('Small', 'Thin', 3, 0.5),
('Small', 'Original', 3, 0.75),
('Small', 'Pan', 3.5, 1),
('Small', 'Gluten-Free', 4, 2),
('Medium', 'Thin', 5, 1),
('Medium', 'Original', 5, 1.5),
('Medium', 'Pan', 6, 2.25),
('Medium', 'Gluten-Free', 6.25, 3),
('Large', 'Thin', 8, 1.25),
('Large', 'Original', 8, 2),
('Large', 'Pan', 9, 3),
('Large', 'Gluten-Free', 9.5, 4),
('XLarge', 'Thin', 10, 2),
('XLarge', 'Original', 10, 3),
('XLarge', 'Pan', 11.5, 4.5),
('XLarge', 'Gluten-Free', 12.5, 6);

INSERT INTO customer
(
	CustomerFirstName,
    CustomerLastName,
    CustomerMobile
) VALUES ('Andrew', 'Wilkes-Krier', '8642545861'),
		 ('Matt','Engers','8644749953'),
         ('Frank', 'Turner', '8642328944'),
         ('Milo', 'Auckerman', '8648785679');
      
INSERT INTO orderlist
(
    OrderListCustomerID,
    OrderListPriceToBusiness,
    OrderListPriceToCustomer,
    OrderListDate,
    IsCompleted,
    OrderListType
) VALUES ( null, '3.68','20.75', '2023-03-05 12:03:00', 1, 'dinein'),
		 ( null, '4.63','19.78', '2023-04-03 12:05:00', 1, 'dinein'),
         (1, '19.8','89.28', '2023-03-03 21:30:00', 1, 'pickup'),
         (1,'23.62','86.19', '2023-04-20 19:11:00', 1, 'delivery'),
         (2, '7.88','27.45', '2023-03-02 17:30:00', 1, 'pickup'),
         (3, '4.24','25.81', '2023-03-02 18:17:00', 1, 'delivery'),
         (4,'6.00','37.25', '2023-04-13 20:32:00', 1, 'delivery');

INSERT INTO pizza
(
    PizzaSize,
    PizzaCrustType,
	PizzaOrderListID,
    PizzaPriceToBusiness,
    PizzaPriceToCustomer,
    PizzaStatus
) VALUES ('Large','Thin',1, 3.68, 20.75, 'Completed'),
		 ('Medium','Pan',2, 3.23, 12.85, 'Completed'),
         ('Small','Original',2, 1.40, 6.93, 'Completed'),
         ('Large', 'Original',3, 3.30, 14.88, 'Completed'),
         ('Large', 'Original',3, 3.30, 14.88, 'Completed'),
         ('Large', 'Original',3, 3.30, 14.88, 'Completed'),
         ('Large', 'Original',3, 3.30, 14.88, 'Completed'),
         ('Large', 'Original',3, 3.30, 14.88, 'Completed'),
         ('Large', 'Original',3, 3.30, 14.88, 'Completed'),
         ('XLarge', 'Original',4, 9.19, 27.94, 'Completed'),
         ('XLarge', 'Original',4, 6.25, 31.50, 'Completed'),
         ('XLarge', 'Original', 4, 8.18, 26.75, 'Completed'),
         ('XLarge', 'Gluten-Free',5, 7.88, 27.45, 'Completed'),
         ('Large','Thin',6, 4.24, 25.81, 'Completed'), 
         ('Large','Thin',7, 2.75, 18.00, 'Completed'), 
         ('Large','Thin',7, 3.25, 19.25, 'Completed');

INSERT INTO pizzatopping
(
	PizzaToppingPizzaID,
    PizzaToppingToppingID,
    PizzaToppingIsExtra) VALUES(1,13,true),(1,1,false),(1,2,false),
							   (2,15,false),(2,9,false),(2,7,false),(2,8,false),(2,12,false),
                               (3,13,false),(3,4,false),(3,12,false),
                               (4,13,false),(4,1,false),
                               (5,13,false),(5,1,false),
                               (6,13,false),(6,1,false),
                               (7,13,false),(7,1,false),
                               (8,13,false),(8,1,false),
                               (9,13,false),(9,1,false),
                               (10,1,false),(10,2,false),(10,14,false),
                               (11,3,true),(11,10,true),(11,14,false),
                               (12,4,false),(12,17,false),(12,14,false),
                               (13,5,false),(13,6,false),(13,7,false),(13,8,false),(13,9,false),(13,16,false),
                               (14,4,false),(14,5,false),(14,6,false),(14,8,false),(14,14,true),
                               (15,14,true),
                               (16,13,false),(16,1,true);

INSERT INTO pizzadiscount
(
PizzaDiscountPizzaID,
PizzaDiscountDiscountID
) VALUES(1,3),(2,2),(2,4),(10,6),(11,6),(11,4),(12,6),(13,4),(15,1),(16,1);

INSERT INTO dinein
(
DineinOrderListID,
DineinTableNumber
) VALUES(1,21),(2,4);

INSERT INTO pickup
(
	PickupOrderListID
) VALUES(3),(5);

INSERT INTO delivery
(
DeliveryOrderListID,
DeliveryAddress
) VALUES(4, '115 Party Blvd, Anderson SC 29621' ),
		(6, '6745 Wessex St, Anderson SC 29621'),
        (7, '8879 Suburban Home, Anderson SC 29621');
                               