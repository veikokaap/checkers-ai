# Checkers AI
Checkers AI written in java using MTD-f, iterative deepening, negamax, alpha-beta pruning and transposition tables.

## Running
##### Prerequisites
* Java 8 and Maven (tested on 3.3.9) installed and available in `PATH` environment variable.

### Command line
1. Navigate to project's root directory.
2. Run `mvn clean package`.
3. Run `java -cp target/checkers-ai-1.0-SNAPSHOT.jar ut.veikotiit.checkers.Checkers` to launch a GUI to play with AI or to visualize AI vs AI gameplay.
3. Run `java -cp target/checkers-ai-1.0-SNAPSHOT.jar ut.veikotiit.checkers.CheckersAITournament#main` to run scorer implementation testing tournament. Each scorer implementation will play with every scorer implementation and itself as both white and black. Each scorer pair plays `ut.veikotiit.checkers.CheckersAITournament.GAME_COUNT` times.

### Through IDE
1. Import this Maven project into your favourite IDE.
2. Run `ut.veikotiit.checkers.Checkers#main` to launch a GUI to play with AI or to visualize AI vs AI gameplay.
3. Run `ut.veikotiit.checkers.CheckersAITournament#main` to run scorer implementation testing tournament. 