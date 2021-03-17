# cleaner-service
Cleaner service application implementing the cleaner robot logic.
This is a Spring Boot application.
CleanerService processCommandList method which takes command list, position , battery charge and input surface as two dimensional array.
Performs following logic : 

To map the operating space, the robot will receive information about the room as a set of cells. Each cell represents:
• A cleanable space of 1 by 1 that can be occupied and cleaned (S).
• A column of 1 by 1 which can’t be occupied or cleaned (C)
• A wall represented by an empty cell (null) or by being outside the matrix
The map is provided as a matrix of m by n in which each element of the matrix is one of those items or else it is empty (null). 
For example, the 4x4 map (top left is 0,0)

The robot also recognizes a set of basic commands. Each command drains the battery of the robot by a certain amount.
• Turn Left (TL). Instructs the robot to turn 90 degrees to the left. Consumes 1 unit of battery.
• Turn Right (TR). Instructs the robot to turn 90 degrees to the right. Consumes 1 unit of battery.
• Advance (A). Instructs the robot to advance one cell forward into the next cell. Consumes 2 unit of battery.
• Back (B). Instructs the robot to move back one cell without changing direction. Consumes 3 units of battery.
• Clean (C). Instructs the robot to clean the current cell. Consumes 5 units of battery

A sequence of valid commands may look like: [ C, TR, A, C, A, C, A, C, TL, B, C, A, A, C, A, A, C, TL, C ].
The robot will carry out the commands in the command set unless it hits an obstacle (a column or a wall) or runs out of battery.
If a command results in hitting an obstacle, it will consume the battery, but the robot will not move. Instead, it will initiate a back off strategy:
• Perform [TR, A, TL]. If an obstacle is hit, drop the rest of the sequence and
• perform [TR, A, TR]. If an obstacle is hit, drop the rest of the sequence and
• perform [TR, A, TR]. If an obstacle is hit, drop the rest of the sequence and
• perform [TR, B, TR, A]. If an obstacle is hit, drop the rest of the sequence and
• perform [TL, TL, A]. If an obstacle is hit,
• the robot is considered stuck. Skip all the remaining commands and finish the program.
The robot will execute each command in order until no more commands are left, the battery is spent, or all the back off sequences hit an obstacle.
For example, if the robot has 4 units of battery and a “clean” command (requiring 5 units of battery) is received,
the robot will stop and finish the program with 4 units of battery left. 
If the battery low condition is hit during a back off sequence, the robot does not continue with the next back off strategy,
but also finishes the program immediately.


