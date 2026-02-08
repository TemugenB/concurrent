Prologue - Fractals are fun
The Program below calculates and renders a fractal tree. The drawing starts with a single line. Then, for each level of recursion (height), the end of the line branches into two new lines with slightly altered angles and reduced lengths. For the last few levels of recursion, the lines are drawn in green, while beforehand we use a black brush.

For the graphical elements we use Swing (Java). By instantiating the Canvas, the main element of the graphics framework, the JVM starts a second thread. This is called the Event Dispatch Thread, or EDT for short. The EDT behaves similarly to all other threads and is expected to work on the graphical elements, while the main thread handles the rest.

In the base code, the main thread executes the main method, while the EDT executes the paint method. We are not interested in Swing in further detail, and as such, the only Swing code you will need to write are the setColor and drawLine methods, both of which are already present in the base code. Some methods also require a Graphics object as argument. Such an object is either explicitly present in the same context or can be accessed through Canvas.getGraphics() (e.g. tree.getGraphics() in the main method).

In order to see how the tree is drawn step by step, you can slow down the drawing process by starting the program is slow mode. You can do this by flipping the slowMode flag in the program. The easiest way to do this is by passing true as the first console argument when executing the program. As is evident in slowMode, each line is drawn sequentially. There is no multithreading. Furthermore, the entire tree is made solely by the EDT.

Act 1 - Decoupling logic and graphics
The first step to a more efficient code would be to move all calculations to the main thread, while the EDT only handles the graphics. To make this happen, makeFractalTree should be called by the main thread, but with all Swing code removed. Instead of drawing the new line, just collect the necessary data. The four integers and the color should be packed into a new object and pushed into a container. From the same container, the EDT will continuously take objects and render lines based on the coordinates and color.

The communication must be thread-safe with a reasonably selected maximum size for the container. The main thread and the EDT should be able to work simultaneously.

Act 2 - Parallel computation
While offloading the EDT from non-graphical workload is generally a good idea, polluting the main thread with computation is not necessarily the optimal solution. We should create an ExecutorService with its own worker-threads instead.

Initially, the main thread is expected to kickstart the calculations by invoking the makeFractalTree method inside the ExecutorService object. Once the first makeFractalTree call is up and running, the ExecutorService object should be able to feed itself with more and more tasks. This is possible by making the recursive calls found inside the makeFractalTree method create additional Runnable objects instead of direct calls.

The first recursive call must be submitted to the ExecutorService object, while the second can remain a direct call. As a result, whenever the tree branches into two, a new Runnable object will be placed into the ExecutorService object to handle the first branch, while the pre-existing Runnable object will continue on the second branch.

Make sure the thread count is capped at 128 and slowMode is turned on. Observe how much faster the tree is drawn compared to the single-threaded version.

Act 3 - Controlled shutdown
It would be a lot nicer if the main thread was aware of the progress of the ExecutorService object and could shut it down properly after all tasks are submitted.

Use a thread-safe counter to keep track of the number of Runnable objects submitted. The counter should reach zero if and only if the tree has been completely drawn. When that occurs the main thread should shut down the ExecutorService object and communicate all of this to the world by printing the "Main has finished" string to the console.

While the main thread is waiting, it should not strain the CPU. Use the wait-notify mechanism to put it into sleep and wake it up when a Runnable object is about to be finished. Make sure the main thread is capable of re-entering sleep in case there are other Runnable objects still under execution.

Note: do not be alarmed if the EDT blocks the program from termination by running an endless draw loop.