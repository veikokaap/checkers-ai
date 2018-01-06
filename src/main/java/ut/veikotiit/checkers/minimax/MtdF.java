package ut.veikotiit.checkers.minimax;

import java.util.concurrent.TimeoutException;

import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.scorer.BitBoardScorer;
import ut.veikotiit.checkers.transposition.TranspositionTable;

public class MtdF {

  private final TranspositionTable transpositionTable;
  private final long timeGiven;

  public MtdF(long timeGiven, TranspositionTable transpositionTable) {
    this.timeGiven = timeGiven;
    this.transpositionTable = transpositionTable;
  }

  public double search(BitBoard board, int depth, double firstGuess, long startTime, BitBoardScorer scorer) throws TimeoutException {
    Negamax negamax = new Negamax(startTime, timeGiven, scorer, transpositionTable);
    double score = firstGuess;

    double upperBound = Double.MAX_VALUE;
    double lowerBound = -Double.MAX_VALUE;

    while (lowerBound < upperBound) {
      double beta;
      if (score == lowerBound) {
        beta = score + 1;
      }
      else {
        beta = score;
      }
      score = negamax.recursive(board, board.getPreviousMove().getColor(), beta - 1, beta, depth);

      if (score < beta) {
        upperBound = score;
      }
      else {
        lowerBound = score;
      }
    }

    return score;
  }
}
