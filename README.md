# ☕ Concurrent Java Programming Exercises

> **Overview:** A collection of advanced Java concurrency exercises focusing on multi-threading, synchronization, thread safety, and parallel computing. These projects simulate real-world scenarios—from restaurant management to epidemic modeling and fractal rendering—to demonstrate core concepts like `ExecutorService`, `wait/notify` mechanisms.

---

## 📂 Projects Included

### 🍽️ Restaurant Simulation
A multi-threaded service cycle involving **Customer**, **Waiter**, and **Chef** threads.
* **Core Logic:** Implements fair FIFO queueing, synchronized table seating, and thread-safe order processing.
* **Optimization:** Includes a "Lazy Chef" mechanism where the consumer thread sleeps (`wait`) during inactivity and is woken (`notify`) by incoming orders.

### 🦠 Pandemic Spread Simulation
A concurrent 2D cellular automaton tracking disease transmission among moving agents.
* **Core Logic:** Simulates infection spread via proximity, recovery/mortality rates, and a **Doctor** thread processing a patient waiting list.
* **Optimization:** Progresses from global grid locking to **fine-grained cell locking**, allowing concurrent movement in non-overlapping areas without deadlocks.

### 🌳 Fractal Tree Rendering
A graphical application optimizing recursive drawing by decoupling computation from rendering.
* **Core Logic:** Offloads heavy calculation from the Swing Event Dispatch Thread (EDT) using a producer-consumer pattern.
* **Optimization:** Parallelizes recursive branches using `ExecutorService` and implements a controlled shutdown using atomic counters to track active tasks.

---

## 🛠️ Technical Concepts Covered

### 🔹 Thread Management
* **Lifecycle:** Starting, running, and graceful stopping (`interrupt`) of threads.
* **Thread Pools:** Using `ExecutorService` for task management.
* **Swing Concurrency:** Understanding the single-threaded nature of Swing painting.

### 🔹 Synchronization & Safety
* **Locks & Monitors:** Using `synchronized` blocks and methods.
* **Coordination:** `wait()`, `notify()`, and `notifyAll()` for inter-thread communication.
* **Atomic Operations
* **Deadlock Prevention:** Strategies for locking resources in a safe order.