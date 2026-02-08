# Base Task (38 points)

A code skeleton is provided for the task, which must be completed based on the description. In your solution, you may use threads or thread pools, with the requirement that starting and stopping the threads must be possible in a normal (graceful) way.

Nowadays, IT plays a prominent role in healthcare applications, including modelling the spread of infectious diseases. This requires specialized software that can process even large amounts of data in a short time. In this task, you must implement such a multi-threaded piece of software.

## Initialization and Shutdown (10 points)

Implement the `initializeGrid` method of the `Simulation` class, which creates a field (`grid`) for the simulation that is `GRID_WIDTH` cells wide and `GRID_HEIGHT` cells high, and then randomly places `INITIAL_SUSCEPTIBLE_COUNT` healthy people (`State.SUSCEPTIBLE`) and infected people (`State.INFECTED`) on it. It is important that only one person may stand on a cell!

Implement the `printGrid` method of the `Simulation` class, which prints the current state of the field to standard output. Proper synchronization is important; it must not happen that one part of a single print shows the field in one state while another part of the same print shows the field in a different state!

The output format should resemble the following (framed above and below by `GRID_WIDTH` equality signs, people represented by a letter corresponding to their state, and empty cells represented by a space character):
```
====================
      S
 I
      S
              S
      S          S
====================
```


When placing people, start a separate thread for each person, on which that person’s logic will be executed. For the threads it is required that in the event of an `InterruptedException` they shut down in a normal (graceful) manner.

Implement the `shutdown` method of the `Simulation` class so that it causes the simulation’s threads to stop.

## Movement Logic (8 points)

Implement the `move` method of the `Person` class, which moves the person in a randomly chosen direction (up, down, left, or right). It is important that people must not step off the field and must not collide with each other, that is, they may only attempt to move onto a cell where no one else is already standing!

The class’s `run` method calls the `move` method every `SLEEP_MS` milliseconds until the simulation is shut down.

## Infection Logic (10 points)

Implement the `getInfected` method of the `Person` class! If the method is called on a person who is not healthy (`State.SUSCEPTIBLE`), it should have no effect. If the method is called on a healthy person, then if there is an infected (`State.INFECTED`) person in any of the 6 neighbouring cells, they should become infected as well.

When a person becomes infected, the following output should appear:
```
A susceptible person got infected!
```

Infected people, every `SLEEP_MS` milliseconds, recover with probability `RECOVERY_CHANCE` (`State.RECOVERED`), and if they do not recover, they die from the disease with probability `FATALITY_CHANCE` (in which case they are removed from the simulation grid).

Depending on what happens, one of the following outputs should appear:
```
An infected person recovered!
An infected person died!
```


## Doctor (10 points)

Implement the constructor and the `requestAppointment` method of the `Doctor` class! The doctor’s waiting list has length `WAITING_LIST_LENGTH`. The `requestAppointment` method attempts to put the given person on the waiting list, but if there is still no free slot after `MAX_WAITING_TIME_MS` milliseconds, it gives up.

The doctor always treats the first person on the waiting list first, provided that the waiting list is not empty and the given person is infected (`State.INFECTED`). This should take `TREATMENT_TIME_MS` milliseconds. If the waiting list is empty, the doctor “falls asleep” (since the many overtime hours since the start of the pandemic have really exhausted them), and will be “woken up” by a patient when that patient is successfully added to the waiting list.

When the doctor finishes treating a patient, the following output should appear:
```
An infected person has been cured!
```


# Extended Task (12 points)

## More Efficient Synchronization (12 points)

Modify your solution so that for people’s movement and infection, synchronization is done not on the entire field but only on specific cells! Make sure that your solution still satisfies all previous constraints and does not run into deadlock! (Solving this task may require modifying the parameter lists of constructors or methods!)
