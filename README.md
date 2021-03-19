# cleaner-service
Cleaner service is a REST API application implementing the cleaner robot logic.

Accessible after run at the http://localhost:8080/cleaner/start which accepts POST request in following JSON template/format CleanerRequest : 

{

"map":[ 

  ["SPACE", "SPACE", "SPACE", "SPACE"],
  
  ["SPACE", "SPACE", "COLUMN", "SPACE"],
  
  ["SPACE", "SPACE", "SPACE", "SPACE"],
  
  ["SPACE", null, "SPACE", "SPACE"] 
  
],

"start": {"x": 2, "y": 3, "direction": "NORTH"},

"commands": [ "TL","ADVANCE","CLEAN","ADVANCE","CLEAN","TR","ADVANCE","CLEAN"],

"battery": 80

}

and returns the following JSON response template :


CleanerResponse: 

{

    "visited":
    [
        {
            "x": 2,
            "y": 3,
            "direction": "WEST"
        },
        {
            "x": 2,
            "y": 2,
            "direction": "WEST"
        },
        {
            "x": 2,
            "y": 1,
            "direction": "NORTH"
        }
    ],
    
    "cleaned":
    [
        {
            "x": 2,
            "y": 2,
            "direction": "WEST"
        },
        {
            "x": 2,
            "y": 1,
            "direction": "WEST"
        },
        {
            "x": 1,
            "y": 1,
            "direction": "NORTH"
        }
    ],
    
    "resultPosition":
    {
        "x": 1,
        "y": 1,
        "direction": "NORTH"
    },
    
    "batteryCharge": 
    {
        "charge": 57
    }
    
}


CleanerService has processCommandList method which takes command list, position , battery charge and input surface as two dimensional array.
Performs following logic : 

To map the operating space, the robot will receive information about the room as a set of cells. Each cell represents:
• A cleanable space of 1 by 1 that can be occupied and cleaned (SPACE).

• A column of 1 by 1 which can’t be occupied or cleaned (COLUMN)

• A wall represented by an empty cell (null) or by being outside the matrix

The map is provided as a matrix of m by n in which each element of the matrix is one of those items or else it is empty (null). 
For example, the 4x4 map (top left is 0,0)

The robot also recognizes a set of basic commands. Each command drains the battery of the robot by a certain amount.
• Turn Left (TL). Instructs the robot to turn 90 degrees to the left. Consumes 1 unit of battery.

• Turn Right (TR). Instructs the robot to turn 90 degrees to the right. Consumes 1 unit of battery.

• Advance (ADVANCE). Instructs the robot to advance one cell forward into the next cell. Consumes 2 unit of battery.

• Back (BACK). Instructs the robot to move back one cell without changing direction. Consumes 3 units of battery.

• Clean (CLEAN). Instructs the robot to clean the current cell. Consumes 5 units of battery


A sequence of valid commands may look like: [ CLEAN, TR, ADVANCE, CLEAN, ADVANCE, CLEAN, ADVANCE, CLEAN, TL, BACK, CLEAN, ADVANCE, ADVANCE, CLEAN, ADVANCE].
The robot will carry out the commands in the command set unless it hits an obstacle (a column or a wall) or runs out of battery.
If a command results in hitting an obstacle, it will consume the battery, but the robot will not move. Instead, it will initiate a back off strategy:

• Perform [TR, ADVANCE, TL]. If an obstacle is hit, drop the rest of the sequence and

• perform [TR, ADVANCE, TR]. If an obstacle is hit, drop the rest of the sequence and

• perform [TR, ADVANCE, TR]. If an obstacle is hit, drop the rest of the sequence and

• perform [TR, BACK, TR, ADVANCE]. If an obstacle is hit, drop the rest of the sequence and

• perform [TL, TL, ADVANCE]. If an obstacle is hit,

• the robot is considered stuck. 
Skip all the remaining commands and finish the program.
The robot will execute each command in order until no more commands are left, the battery is spent, or all the back off sequences hit an obstacle.
For example, if the robot has 4 units of battery and a “clean” command (requiring 5 units of battery) is received,
the robot will stop and finish the program with 4 units of battery left. 
If the battery low condition is hit during a back off sequence, the robot does not continue with the next back off strategy,
but also finishes the program immediately.


