package ut.veikotiit.checkers.minimax;

import java.util.concurrent.TimeoutException;

import ut.veikotiit.checkers.Color;
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

  public double search(BitBoard board, Color color, int depth, int firstGuess, long startTime, BitBoardScorer scorer) throws TimeoutException {
    Negamax negamax = new Negamax(startTime, timeGiven, scorer, transpositionTable);
    int score = firstGuess;

    int upperBound = Integer.MAX_VALUE;
    int lowerBound = Integer.MIN_VALUE;

    while (lowerBound < upperBound) {
      int beta;
      if (score == lowerBound) {
        beta = score + 1;
      }
      else {
        beta = score;
      }

      score = negamax.recursive(board, color, beta - 1, beta, depth, depth > 1);

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
