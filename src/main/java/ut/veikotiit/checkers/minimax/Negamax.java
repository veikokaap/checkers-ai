package ut.veikotiit.checkers.minimax;

import java.util.List;
import java.util.concurrent.TimeoutException;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.scorer.BitBoardScorer;
import ut.veikotiit.checkers.transposition.CachedValue;
import ut.veikotiit.checkers.transposition.TranspositionTable;

public class Negamax {

  private final long startTime;
  private final long timeGiven;
  private final BitBoardScorer scorer;
  private final TranspositionTable transpositionTable;

  public Negamax(long startTime, long timeGiven, BitBoardScorer scorer, TranspositionTable transpositionTable) {
    this.startTime = startTime;
    this.timeGiven = timeGiven;
    this.scorer = scorer;
    this.transpositionTable = transpositionTable;
  }

  public double recursive(BitBoard board, Color color, double alpha, double beta, int depth) throws TimeoutException {
    if (System.currentTimeMillis() - startTime >= timeGiven) {
      throw new TimeoutException();
    }

    double originalAlpha = alpha;
    CachedValue cachedValue = transpositionTable.get(board);

    if (cachedValue != null && cachedValue.getDepth() >= depth) {
      CachedValue.Flag flag = cachedValue.getFlag();
      double value = cachedValue.getValue();
      if (flag == CachedValue.Flag.EXACT) {
        return value;
      }
      else if (flag == CachedValue.Flag.LOWERBOUND && value > alpha) {
        alpha = value;
      }
      else if (flag == CachedValue.Flag.UPPERBOUND && value < beta) {
        beta = value;
      }

      if (alpha >= beta) {
        return value;
      }
    }

    if (depth <= 0) {
      return scorer.getScore(board, color);
    }

    List<BitBoard> childBoards = board.getChildBoards();
    if (childBoards.isEmpty()) {
      return 10000 + depth; //Victory
    }

    double bestValue = -1000000.0;
    for (BitBoard child : childBoards) {
      double score = -recursive(child, color.getOpponent(), -beta, -alpha, depth - 1);

      if (score > bestValue) {
        bestValue = score;
      }
      if (bestValue > alpha) {
        alpha = bestValue;
      }
      if (alpha >= beta) {
        break;
      }
    }

    CachedValue newCachedValue;
    if (bestValue <= originalAlpha) {
      newCachedValue = new CachedValue(CachedValue.Flag.UPPERBOUND, bestValue, depth);
    }
    else if (bestValue >= beta) {
      newCachedValue = new CachedValue(CachedValue.Flag.LOWERBOUND, bestValue, depth);
    }
    else {
      newCachedValue = new CachedValue(CachedValue.Flag.EXACT, bestValue, depth);
    }
    transpositionTable.put(board, newCachedValue);

    return bestValue;
  }
}
