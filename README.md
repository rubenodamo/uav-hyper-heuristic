# UAV Animal Feeding Problem Domain and Selection Hyper-heuristics

## Overview
This project implements a HyFlex-compatible version of the UAV Zoo Feeding (UZF) problem and a selection hyper-heuristic to solve instances of this problem. The UZF problem is a combinatorial optimization problem where a single UAV must feed all animals in a zoo in the minimum amount of time.
- Full implementation of the UZF problem domain compatible with the HyFlex framework.
- Two selection hyper-heuristics to solve instances of the UZF problem:
  - `SR_IE_HH.java`: Implements a hyper-heuristic using a selection and replacement (SR) and iterated improvement (IE) approach.
  - `APCF_NW_HH.java`: Implements an adaptive choice function (Non-Worsening) hyper-heuristic combining heuristic performance rank and elapsed time since last application.
    
## Project Structure
The project consists of the following main components:

### 1. `UZFDomain` Class
This class is a subclass of the HyFlex `ProblemDomain` class and implements all the problem domain-specific operations as per the HyFlex API specification. It includes methods to load instances, create solutions, apply heuristics, and visualize solutions.

### 2. `UAVInstanceReader` Class
This class reads UZF problem instances from files and returns the problem information as instances of a `UZFInstanceInterface`. It parses the instance files and extracts information such as the name, comments, preparation area coordinates, and enclosure locations.

### 3. `UZFSolution` and `SolutionRepresentation` Classes
These classes handle the representation and manipulation of solutions for the UZF problem. They include methods for deep and shallow cloning, evaluating solutions, and applying heuristics.

### 4. `UZFInstance` Class
This class stores the problem instance information and provides methods to generate initial solutions based on either random initialization or a nearest neighbor greedy algorithm.

## Low-Level Heuristics
The implementation includes the following low-level heuristics for modifying and improving solutions:

- **Adjacent Swap**: Swaps adjacent elements in the solution representation.
- **Reinsertion**: Removes an element and reinserts it at a different position.
- **Next Descent**: Local search heuristic using adjacent swaps to find improvements.
- **Daviss Hill Climbing**: Local search heuristic similar to Davis’s Bit Hill Climbing using adjacent swaps.
- **Partially Mapped Crossover (PMX)**: Crossover heuristic combining two parent solutions to create a child solution.
- **Cycle Crossover (CX)**: Crossover heuristic based on the cycle crossover method.
- **Steepest Descent Hill Climbing**: Local search heuristic that always moves to the best neighboring solution.

## Selection Hyper-Heuristics
### SR_IE_HH.java
This hyper-heuristic uses a Selection and Replacement (SR) strategy combined with an Iterated Improvement (IE) approach to select and apply low-level heuristics.

### APCF_NW_HH.java
This hyper-heuristic implements an Adaptive Choice Function (Non-Worsening) strategy. It combines heuristic performance ranking and elapsed time since last application to intelligently choose and apply low-level heuristics for optimizing solutions.

## Setup and Running the Project

### Clone the Repository
```bash
git clone https://github.com/your-username/UAV-Zoo-Feeding.git](https://github.com/rubenodamo/uav-hyper-heuristic.git
cd uav-hyper-heuristic
```

### Running the Program
You can run the main class to test the implementation and visualize solutions, in an IDE of your choice (e.g. IntelliJ)

### Loading Instances
Instances are loaded using the `loadInstance(int instanceId)` method in the `UZFDomain` class. The following instance files are mapped to instance IDs:
- 0: `square.uzf`
- 1: `libraries-15.uzf`
- 2: `carparks-40.uzf`
- 3: `tramstops-85.uzf`
- 4: `grid.uzf`
- 5: `clustered-enclosures.uzf`
- 6: `chatgpt-instance-100-enclosures.uzf`

### Example of an Instance File
```plaintext
NAME : example-instance
COMMENT : Example instance for UAV Zoo Feeding problem
PREPARATION_AREA
0 0
ENCLOSURE_LOCATIONS
0 5
5 0
10 0
10 5
10 10
5 10
0 10
EOF
```
