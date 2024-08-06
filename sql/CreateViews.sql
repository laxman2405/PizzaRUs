-- Created by Laxman Madipadige

USE Pizzeria;

CREATE OR REPLACE VIEW ToppingPopularity AS 
SELECT topping.ToppingName as Topping,
CASE 
	WHEN count(pizzatopping.PizzaToppingToppingID) + sum(pizzatopping.PizzatoppingIsExtra) IS NULL THEN 0
	ELSE count(pizzatopping.PizzaToppingToppingID) + sum(pizzatopping.PizzatoppingIsExtra)
END AS ToppingCount
from pizzatopping 
right join topping on pizzatopping.PizzaToppingToppingID = topping.ToppingID
group by topping.ToppingName
order by ToppingCount desc;

CREATE OR REPLACE VIEW ProfitByPizza AS
SELECT PizzaSize AS 'Pizza Size', 
	   PizzaCrustType AS 'Pizza Crust',
       round(sum(PizzaPriceToCustomer - PizzaPriceToBusiness), 2) AS 'Profit',
       orderlist.OrderListDate AS 'LastOrderDate'
FROM pizza join orderlist ON
pizza.PizzaOrderListID = orderlist.OrderListID
GROUP BY PizzaSize, PizzaCrustType
ORDER BY Profit DESC;

CREATE OR REPLACE VIEW ProfitByOrderType AS 
SELECT orderlist.OrderListType AS 'OrderType', 
date_format(orderlist.OrderListDate, '%Y-%M') AS 'OrderMonth',
round(sum(orderlist.OrderListPriceToCustomer), 2) AS 'TotalOrderPrice',
round(sum(orderlist.OrderListPriceToBusiness), 2) AS 'TotalOrderCost',
round(sum(orderlist.OrderListPriceToCustomer) - sum(orderlist.OrderListPriceToBusiness), 2) as 'Profit'
from orderlist
GROUP BY OrderType, OrderMonth
union select ' ', 'Grand Total' as 'OrderMonth',
round(sum(orderlist.OrderListPriceToCustomer), 2) as 'TotalOrderPrice',
round(sum(orderlist.OrderListPriceToBusiness), 2) as 'TotalOrderCost',
round(sum(orderlist.OrderListPriceToCustomer - orderlist.OrderListPriceToBusiness), 2) as 'Profit'
from orderlist;

select * from ToppingPopularity;
select * from ProfitByPizza;
select * from ProfitByOrderType;


       