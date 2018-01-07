# Checkers AI
Checkers AI written in java using MTD-f, iterative deepening, negamax, alpha-beta pruning and transposition tables.
Optimal experience when running on Linux. Will work on Windows too, but problems might appear due to lacking native ANSI terminal support.

## Running
##### Prerequisites
* Java 8 and Maven (tested on 3.3.9) installed and available in `PATH` environment variable.

### Command line
1. Navigate to project's root directory.
2. Run `mvn clean package`.
3. Run `java -jar target/checkers-ai-1.0-SNAPSHOT.jar` to launch a GUI to play with AI or to visualize AI vs AI gameplay. If running on Windows, use `javaw` instead of `java`.
3. Run `java -cp target/checkers-ai-1.0-SNAPSHOT.jar ut.veikotiit.checkers.CheckersAITournament` to run scorer implementation testing tournament. Again, if running on Windows, use `javaw` instead of `java`. Each scorer implementation will play with every scorer implementation and itself as both white and black. Each scorer pair plays `ut.veikotiit.checkers.CheckersAITournament.GAME_COUNT` times.

### Through IDE
1. Import this Maven project into your favourite IDE.
2. Run `ut.veikotiit.checkers.Checkers#main` to launch a GUI to play with AI or to visualize AI vs AI gameplay.
3. Run `ut.veikotiit.checkers.CheckersAITournament#main` to run scorer implementation testing tournament. 
