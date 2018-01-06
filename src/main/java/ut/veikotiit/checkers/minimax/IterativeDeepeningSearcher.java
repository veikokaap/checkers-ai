package ut.veikotiit.checkers.minimax;

import java.util.List;
import java.util.concurrent.TimeoutException;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.moves.Move;
import ut.veikotiit.checkers.scorer.BitBoardScorer;
import ut.veikotiit.checkers.transposition.TranspositionTable;

public class IterativeDeepeningSearcher {

  private final int max_depth;
//  private final MtdF mtdf;
  private final TranspositionTable transpositionTable;
  private final int timeGiven;

  public IterativeDeepeningSearcher(int max_depth, int timeGiven) {
    this.max_depth = max_depth;
    this.timeGiven = timeGiven;
    transpositionTable = new TranspositionTable();
//    this.mtdf = new MtdF(timeGiven, transpositionTable);
  }

  public Move findBestMove(BitBoard bitBoard, BitBoardScorer scorer) {
    List<BitBoard> childBoards = bitBoard.getChildBoards();

    double bestScore = -Double.MAX_VALUE;
    Move bestMove = null;
    for (BitBoard childBoard : childBoards) {
      double score = calculateScore(childBoard, bitBoard.getNextColor(), scorer);
      if (score > bestScore) {
        bestScore = score;
        bestMove = childBoard.getPreviousMove();
      }
    }

//    System.out.println(bitBoard.getNextColor() + ": " + bestScore);
    return bestMove;
  }

  private double calculateScore(BitBoard board, Color color, BitBoardScorer scorer) {
    long startTime = System.currentTimeMillis();
    Negamax negamax = new Negamax(startTime, timeGiven, scorer, transpositionTable);

    int depth = 0;
    double firstGuess = scorer.getScore(board, color);

    while (depth < max_depth) {
      depth += 1;
      try {
//        firstGuess = mtdf.search(board, color, depth, firstGuess, startTime, scorer);
        firstGuess = negamax.recursive(board, color, -Double.MAX_VALUE, Double.MAX_VALUE, depth);
      } catch (TimeoutException e) {
        break;
      }
    }

//    System.out.println("  " + depth + ": " + firstGuess);
    return firstGuess;
  }
}
