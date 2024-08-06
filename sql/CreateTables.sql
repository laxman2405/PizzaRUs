-- Created by Laxman Madipadige

DROP SCHEMA IF EXISTS Pizzeria;
CREATE SCHEMA Pizzeria;
USE Pizzeria;

CREATE TABLE baseprice
(
    BasePriceSize varchar(255) NOT NULL,
    BasePriceCrust varchar(255) NOT NULL,
    BasePricePriceToBusiness float NOT NULL,
    BasePriceCostToCustomer float NOT NULL,
    PRIMARY KEY(BasePriceSize, BasePriceCrust)
);

CREATE TABLE customer
(
	CustomerID int AUTO_INCREMENT,
    CustomerFirstName varchar(255) NOT NULL,
    CustomerLastName varchar(255) NOT NULL,
    CustomerMobile varchar(255) NOT NULL,
    PRIMARY KEY(CustomerID)
);

CREATE TABLE orderlist
(
	OrderListID int AUTO_INCREMENT,
    OrderListCustomerID int,
    OrderListPriceToBusiness decimal(8,2) NOT NULL,
    OrderListPriceToCustomer decimal(8,2) NOT NULL,
    OrderListDate varchar(255),
    IsCompleted int NOT NULL,
    OrderListType varchar(255),
    PRIMARY KEY(OrderListID),
    FOREIGN KEY(OrderListCustomerID) references customer(CustomerID)
);

CREATE TABLE pizza
(
	PizzaID int AUTO_INCREMENT,
    PizzaSize varchar(255),
    PizzaCrustType varchar(255),
    PizzaOrderListID int NOT NULL,
    PizzaPriceToBusiness decimal(8,2) NOT NULL,
    PizzaPriceToCustomer decimal(8,2) NOT NULL,
    PizzaStatus varchar(255),
    PRIMARY KEY(PizzaID),
    FOREIGN KEY(PizzaOrderListID) REFERENCES orderlist(OrderListID),
    FOREIGN KEY(PizzaSize, PizzaCrustType) REFERENCES baseprice(BasePriceSize, BasePriceCrust)
);

CREATE TABLE topping
(
	ToppingID int AUTO_INCREMENT,
    ToppingName varchar(255) NOT NULL,
    ToppingPriceToCustomer double NOT NULL,
    ToppingPriceToBusiness double NOT NULL,
    ToppingCurrentInvLvl int NOT NULL,
    ToppingMinInvLvl int,
    ToppingQuantityForSmall double NOT NULL,
    ToppingQuantityForMedium double NOT NULL,
    ToppingQuantityForLarge double NOT NULL,
    ToppingQuantityForXLarge double NOT NULL,
    PRIMARY KEY(ToppingID)
);

CREATE TABLE pizzatopping
(
	PizzaToppingPizzaID int,
    PizzaToppingToppingID int,
    PizzaToppingIsExtra boolean DEFAULT false,
    PRIMARY KEY(PizzaToppingPizzaID, PizzaToppingToppingID),
    FOREIGN KEY(PizzaToppingPizzaID) REFERENCES pizza(PizzaID),
    FOREIGN KEY(PizzaToppingToppingID) REFERENCES topping(ToppingID)
);

CREATE TABLE discount
(
	DiscountID int AUTO_INCREMENT,
    DiscountName varchar(255) NOT NULL,
    IsPercent boolean NOT NULL,
    DiscountAmount decimal(8,2) NOT NULL,
    PRIMARY KEY(DiscountID)
);

CREATE TABLE pizzadiscount
(
	PizzaDiscountPizzaID int,
    PizzaDiscountDiscountID int,
    PRIMARY KEY(PizzaDiscountPizzaId, PizzaDiscountDiscountID),
    FOREIGN KEY(PizzaDiscountPizzaId) REFERENCES pizza(PizzaID),
    FOREIGN KEY(PizzaDiscountDiscountID) REFERENCES discount(DiscountID)
);

CREATE TABLE orderdiscount
(
	OrderDiscountOrderListID int,
    OrderDiscountDiscountID int,
    PRIMARY KEY(OrderDiscountOrderListID, OrderDiscountDiscountID),
    FOREIGN KEY(OrderDiscountOrderListID) REFERENCES orderlist(OrderListID),
    FOREIGN KEY(OrderDiscountDiscountID) REFERENCES discount(DiscountID)
);

CREATE TABLE dinein
(
	DineinOrderListID int NOT NULL,
    DineinTableNumber int NOT NULL,
    PRIMARY KEY(DineinOrderListID),
    FOREIGN KEY(DineinOrderListID) REFERENCES orderlist(OrderListID) on update cascade
);

CREATE TABLE pickup
(
	PickupOrderListID int NOT NULL,
    PRIMARY KEY(PickupOrderListID),
    FOREIGN KEY(PickupOrderListID) REFERENCES orderlist(OrderListID) on update cascade
);

CREATE TABLE delivery
(
	DeliveryOrderListID int NOT NULL,
    DeliveryAddress varchar(255),
    PRIMARY KEY(DeliveryOrderListID),
    FOREIGN KEY(DeliveryOrderListID) REFERENCES orderlist(OrderListID) on update cascade
);

