# Base Task (40 points)

A code skeleton is provided for the task, which must be completed based on the task description. In your solution, you may use threads or thread pools, with the condition that threads can be started and stopped in a normal (graceful) way.

In this task, you must implement a restaurant simulation.  
Guests of the restaurant can wait in the lobby or outside the restaurant until the waiters escort them to a free table. The waiters forward the taken orders to the chef, who prepares them in sequence. The ready orders are served by the waiters, and after eating, the guests leave.

## Opening, closing, queueing (10 points)

Implement the `open`, `close`, and `queue` methods of the `Restaurant` class!

The `open` method sets the value of `isOpen`, initializes the `lobby` and `tables` fields of the class, and starts the waiter (`Waiter`) threads. With the help of `lobby`, guests will be able to queue: `LOBBY_CAPACITY` of them in the restaurant lobby, while the rest can only queue outside the restaurant. It is important that the queueing is fair, i.e. guests should enter the restaurant in the order of their arrival! With the help of `tables`, the model keeps track of whether there is a guest seated at a given table and, if so, which one. The restaurant has `TABLE_COUNT` tables. In addition, `WAITER_COUNT` waiters start their shift (i.e. they start running on separate threads).

The `close` method changes the value of `isOpen` (the started threads will stop based on this).

The `queue` method receives a guest (`Customer`) and a patience level (maximum waiting time in milliseconds). The guest goes into the restaurant lobby if there is still room there; otherwise they queue outside the restaurant. If after `patience_ms` milliseconds they still have not managed to enter the lobby, they have had enough and leave.

Extend the `main` method of the class so that after opening the restaurant, but before closing it, over 5 iterations, every 2500 milliseconds 5 new customer threads are started!

The `open` method must print the following message: `Opening restaurant`.

The `close` method must print the following message: `Closing restaurant`.

The `queue` method must print messages in the following format:
```
Customer 9999999999 is queueing (patience: 9999ms)
Customer 9999999999 had enough of waiting in line!
```


## Seating (10 points)

Implement the constructor and `seat` method of the `Customer` class!

The constructor sets the value of `patience_ms` to a random number between `MIN_PATIENCE_MS` and `MAX_PATIENCE_MS`, then starts queueing by calling the `queue` method of the `Restaurant` class. The `seat` method stores the table number and prints the following message: `Customer 9999999999 has been seated: table 9` (tables are indexed from 0 to `TABLE_COUNT - 1`).

Implement the `Waiter` constructor and the `run` and `seatCustomer` methods!

The constructor receives the `lobby` and `tables` fields of the `Restaurant` class as parameters. During the restaurant's opening hours, the `run` method continuously seats guests at free tables using the `seatCustomers` (sic) method. The `seatCustomers` method seats the guest at the front of the queue at a randomly chosen free table (of course, only if the queue is not empty). Proper synchronization is important so that waiters cannot seat two guests at the same table at the same time! (You may limit the number of iterations when choosing a random table.)

## Orders, chef (10 points)

Implement the `takeOrders` method of the `Waiter` class! The waiters go through those tables where a guest is seated who has not yet ordered. They take the guests’ orders by calling the `order` method of the `Customer` class, then forward them to the chef using the `giveOrder` method of the `Chef` class. Extend the `run` method of the `Waiter` class so that it also takes the orders! Proper synchronization is important so that a guest’s order is taken by exactly one waiter!

Implement the `order` method of the `Customer` class! The method creates a new order (`Order`) and changes the value of `hasOrdered`, provided that the guest is already seated but has not yet ordered. When ordering, it must print a message in the following format: `Customer 9999999999 has ordered: Order 9999999999`.

Extend the `Chef` class! The `orders` and `readyOrders` fields of the class contain the orders to be prepared and the ready orders. Proper synchronization is important, and orders must be prepared in the order they are received! Waiters can add the given order to the queue of orders to be prepared using the `giveOrder` method, and they can take the earliest finished order using the `getReadyOrder` method (or `null` if there is none). In the `run` method of the class, the chef always works on the oldest unprepared order (if there is one), for a randomly chosen time between 0 and `MAX_WORKING_TIME_MS`. The finished order is appended to the list of ready orders. Extend the `open` method of the `Restaurant` class so that the chef’s thread is also started!

## Serving, impatient guests (10 points)

Implement the `leave` method of the `Restaurant` class! The method marks the table of the given guest as free (`null`), then prints the following message: `Customer 9999999999 left`.

Implement the `giveOrder` method of the `Customer` class, which sets the value of `hasReceivedOrder` and prints the following message: `Customer 9999999999 received Order 9999999999`! Extend the `run` method of the `Customer` class so that after being seated at the table it waits `patience_ms` milliseconds, and then leaves the restaurant (`Restaurant.leave`) if it still has not been able to place an order! The same happens if, `patience_ms` milliseconds after ordering, the guest still has not received their order (it is enough to start measuring this time after the previous timeout expires). If everything goes well and the guest receives their food, they take `EATING_TIME_MS` milliseconds to eat it, then leave the restaurant!

In these cases, the following messages must be printed:
```
Customer 9999999999 had enough of waiting to order!
Customer 9999999999 had enough of waiting to receive their order!
Customer 9999999999 finished their food
```

Implement the `serveOrder` method of the `Waiter` class! The method runs when there is a ready order that the waiter can deliver. In this case, it checks which guest the order belongs to (`Order.getCustomer`) and goes through the tables until it finds the guest, then serves the order using the `Customer.giveOrder` method. Proper synchronization is important so that each order is served by exactly one waiter!

# Extended Task (10 points)

## Lazy Chef (10 points)

Extend the `run` method of the `Chef` class so that the chef goes to sleep if, after finishing an order, there are no more orders left to prepare! In this case, the following message must be printed: `Chef is going to sleep`.

When a waiter forwards an order to the sleeping chef, they must wake the chef up! In this case, the following message must be printed: `Chef woke up`.
