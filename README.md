****************
* Final Project: Circuit Tracer
* CS-221
* 05/01/26
* Lincoln Sloan
****************

OVERVIEW:

CircuitTracer reads a circuit board from a text file and finds every shortest
path of open cells between the component marked 1 and the component marked 2.
The search can use a stack or a queue. Results are printed to the console.
GUI output is recognized on the command line and reported as unavailable.


INCLUDED FILES:

* CircuitTracer.java - program driver. Reads the command line, runs the search, and prints results.
* CircuitBoard.java - loads a board from a file and stores the grid, start, and end.
* TraceState.java - one partial path. Holds a copy of the board with that path marked.
* Storage.java - stores TraceState objects in either a stack or a queue.
* InvalidFileFormatException.java - thrown when a board file is readable but not a valid board.
* OccupiedPositionException.java - thrown when a trace is placed on a cell that is not open.
* CircuitTracerTester.java - instructor test harness. Builds its own input files and checks output.
* MethodTester.java - small manual driver used while developing the board reader and the search.
* tracer/valid1.dat through tracer/valid10.dat - sample boards with a legal layout.
* tracer/invalid1.dat through tracer/invalid12.dat - sample boards with format errors.
* README.md - this file.


COMPILING AND RUNNING:

From the directory that contains the .java files, compile the driver and the
classes it depends on:

$ javac CircuitTracer.java

Run it with three arguments. The first chooses the search structure, the second
chooses the output, and the third is the board file:

$ java CircuitTracer -s -c tracer/valid1.dat
$ java CircuitTracer -q -c tracer/valid1.dat

-s uses a stack (depth-first). -q uses a queue (breadth-first). -c prints each
shortest board to the console, with a blank line between boards. Open cells on
a solution are marked T. -g is accepted as the GUI option, but this program
does not open a window. It still runs the search, then prints "GUI output is
not available."

If the arguments are missing or not one of those choices, the program prints a
usage message and exits. If the file is missing or is not a valid board, it
prints the exception and exits. It does not print a Java stack trace for those
cases.

To compile and run the instructor tester from the same directory:

$ javac CircuitTracerTester.java
$ java CircuitTracerTester

The tester writes temporary .dat and .out files in the current directory.


ANALYSIS:

i.
The order in which paths are retrieved and built upon is completely different
when the search uses a stack instead of a queue. With a stack, the algorithm
takes the most recently stored path and extends it in every open direction,
then immediately continues from whichever of those extensions was stored last.
It rushes toward a dead end or the target, then backs up and tries the
branches it skipped. With a queue, the algorithm stores every one-step
extension of the current path and then returns to the oldest path that has not
been extended yet. The queue version spreads outward one step at a time until
each branch reaches a dead end or the target.

ii.
The total number of search states is the same for the stack and the queue.
The loop keeps going until the store is empty ("while (!stateStore.isEmpty())"),
and neither version discards a state just because a solution has already been
found. Unless the search is changed to prune longer paths, both structures
generate every reachable search state. They only differ in the order those
states are visited.

iii.
Because the stack ignores other branches and keeps extending the newest path
until it cannot, it is more likely to spend time inside a dead end than the
queue is. That is not true on every board. Neighbors are stored in a fixed
order (right, down, left, then up), so the stack always continues from the
last of those directions and the queue always continues from the first. If
that last direction happens to lead toward the target, the stack can reach a
solution while the queue is still expanding every other branch of the same
length.

iv.
The queue explores every path of a given length before it explores any longer
path, so the first solution it finds is guaranteed to be a shortest path.

v.
The queue has to keep every branch that has not been expanded yet. The stack
only keeps the branches it skipped while following its current path. The queue
therefore needs more memory on the same board.

vi.
Here n is the number of open cells, because a path is a sequence of those
cells and a cell is never reused. From each cell the search can step to at
most three new open neighbors (four directions, minus the cell just left,
which is already marked T). In the worst case that branching continues across
the open cells, so the number of paths is on the order of 3^n. That bound
ignores shorter paths and the fact that edges and blocked cells remove
choices, but the growth rate is O(3^n).


PROGRAM DESIGN AND IMPORTANT CONCEPTS:

CircuitBoard is the grid. Its file constructor reads the row and column counts,
then reads exactly that many rows. Each cell must be one character: O for
open, X for blocked, 1 for the start, 2 for the end, or T. A T is allowed in
an input file but is not an open cell, so the search will not step on it. The
constructor rejects the wrong number of rows or columns, a non-positive size,
a cell that is not one of those characters, a cell token longer than one
character, and any board that does not contain exactly one 1 and one 2. A
missing file throws FileNotFoundException. The copy constructor duplicates the
grid and both endpoints so that later paths cannot change an earlier board.

TraceState is one candidate path. The first constructor copies the starting
board and marks the first open neighbor of 1 with T. Later constructors copy
the previous state and mark one new open neighbor. Point.x is the row and
Point.y is the column, matching the order used when the board stores its
endpoints. A state is a solution when its last T is directly beside 2, not
diagonal to it. pathLength() is the number of T cells, which is the cost of
the path because every step has the same cost.

Storage is a bridge over java.util.Stack and a LinkedList used as a queue.
CircuitTracer asks it to store and retrieve TraceState objects and never calls
stack or queue methods itself. Choosing -s or -q only changes which structure
Storage builds. Retrieval from the stack is last-in first-out, so the search
is depth-first. Retrieval from the queue is first-in first-out, so the search
is breadth-first.

CircuitTracer ties those pieces together. After the command line is checked,
it loads the board and stores a TraceState for every open cell beside 1, in
the order right, down, left, up. It then retrieves states until the store is
empty. A solution is kept when it is the first one, when it is the same length
as the solutions already kept, or when it is shorter, in which case the old
list is cleared. A state that is not a solution is extended to each open
neighbor in that same order. When the store is empty, every remaining board in
the list is a shortest path. Console mode prints those boards. Each board's
text already ends with a newline, and an extra newline is printed after it so
that solutions are separated by a blank line.

The classes are split this way so the board, a single path, and the choice of
stack or queue can change without rewriting the search loop. One improvement
would be to stop expanding a state once its length is already at least the
best solution found so far. Breadth-first search could then stop as soon as
the next state is longer than the first solution. The current loop does not
do that, which is why the stack and the queue visit the same states.


TESTING:

I tested the board reader with MethodTester against the valid and invalid
files in tracer/. All 12 invalid files throw InvalidFileFormatException, and
all 10 valid files load and print the expected grid. I used the same driver
to check the usage message and the command-line checks before the search
existed.

Once the search was in place I relied on CircuitTracerTester. It generates
its own valid and invalid boards, runs CircuitTracer with both -s and -q,
and compares the printed boards with the known shortest paths. It also checks
bad arguments, a missing file, and the GUI option.

The program handles bad input by printing a short message and returning.
Wrong or missing arguments print the usage message. A missing file prints
FileNotFoundException. A malformed board prints InvalidFileFormatException.
Placing a trace on a blocked cell, or off the board, throws
OccupiedPositionException; the search avoids that by calling isOpen before it
builds the next TraceState.

CircuitTracerTester reports 86 of 86 tests passed. There is no GUI. Choosing
-g prints one line saying GUI output is not available.


DISCUSSION:

The search was the hard part. Two bugs kept the early versions off the
expected boards. In the main loop I built each new TraceState from the
original board instead of from the previous TraceState. That came from
copying the setup code and not changing the constructor call, so every step
replaced the path instead of extending it. I also had row and column
backwards when I read the start point: Point.x is the row and Point.y is the
column. After those two corrections, and after the printed boards matched the
tester's spacing, the suite went from about 60 percent passing to 84 of 86.

The remaining two failures were the GUI tests. The program accepted -g and
then printed nothing, so the tester treated the option as neither a window
nor a reported limitation. It now prints that GUI output is not available.

What finally clicked was that a stack and a queue do not change which paths
exist. They only change which partial path is expanded next. Once each
TraceState owned its own board copy, both structures could run through the
same loop and still come out with the same shortest boards in a different
order.

Reading the board had its own traps. Taking only the first character of a
token made a cell such as "OX" look like an open cell, a blank or huge
dimension could throw a Java runtime exception instead of
InvalidFileFormatException, and marking a cell outside the grid crashed
instead of throwing OccupiedPositionException. Those cases now report the
project's own exceptions.


EXTRA CREDIT:

No extra credit was attempted. The GUI flag is part of the command-line
interface, and this program reports that GUI output is not available rather
than opening a window.
