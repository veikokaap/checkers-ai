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
  private final MtdF mtdf;

  public IterativeDeepeningSearcher(int max_depth, int timeGiven) {
    this.max_depth = max_depth;
    this.mtdf = new MtdF(timeGiven, new TranspositionTable());
  }

  public Move findBestMove(BitBoard bitBoard, BitBoardScorer scorer) {
    List<BitBoard> childBoards = bitBoard.getChildBoards();

    double bestScore = -Double.MAX_VALUE;
    Move bestMove = null;
    for (BitBoard childBoard : childBoards) {
      double score = calculateScore(childBoard, childBoard.getPreviousMove().getColor(), scorer);
      if (score > bestScore) {
        bestScore = score;
        bestMove = childBoard.getPreviousMove();
      }
    }

    return bestMove;
  }

  private double calculateScore(BitBoard board, Color color, BitBoardScorer scorer) {
    long startTime = System.currentTimeMillis();

    int depth = 0;
    double firstGuess = scorer.getScore(board, color);

    while (depth < max_depth) {
      depth += 1;
      try {
        firstGuess = mtdf.search(board, depth, firstGuess, startTime, scorer);
      } catch (TimeoutException e) {
        break;
      }
    }

    return firstGuess;
  }
}
