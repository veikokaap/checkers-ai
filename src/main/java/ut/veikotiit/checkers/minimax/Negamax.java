package ut.veikotiit.checkers.minimax;

import java.util.List;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.transposition.CachedValue;
import ut.veikotiit.checkers.transposition.TranspositionTable;

public class Negamax {
  
  private final long startTime;
  private final long timeGiven;

  public Negamax(long startTime, long timeGiven) {
    this.startTime = startTime;
    this.timeGiven = timeGiven;
  }

  public Result recursive(BitBoard board, Color color, int alpha, int beta, int depth, TranspositionTable transpositionTable) {
    if (System.currentTimeMillis() - startTime >= timeGiven) {
      return null; // Time exceeded
    }
    
    int originalAlpha = alpha;
    CachedValue cachedValue = transpositionTable.get(board);

    if (cachedValue != null) {
      if (cachedValue.getDepth() >= depth) {
        CachedValue.Flag flag = cachedValue.getFlag();
        int value = cachedValue.getValue();
        if (flag == CachedValue.Flag.EXACT) {
          return new Result(value, null);
        }
        else if (flag == CachedValue.Flag.LOWERBOUND && value > alpha) {
          alpha = value;
        }
        else if (flag == CachedValue.Flag.UPPERBOUND && value < beta) {
          beta = value;
        }

        if (alpha >= beta) {
          return new Result(value, null);
        }
      }
    }

    if (depth <= 0) {
      int score = board.getScore(color);
      transpositionTable.put(board, createNewCachedValue(alpha, beta, depth, score));
      return new Result(score, null);
    }
    
    List<BitBoard> childBoards = board.getChildBoards(color);
    if (childBoards.isEmpty()) {
      int score = -10000 - depth; // Defeat
      transpositionTable.put(board, createNewCachedValue(alpha, beta, depth, score));
      return new Result(score, null);
    }

    int bestValue = -1000000;
    BitBoard bestChild = null;
    for (BitBoard child : childBoards) {
      Result result = recursive(child, color.getOpponent(), -beta, -alpha, depth - 1, transpositionTable);
      
      if (result == null) { 
        return null; // time exceeded
      }
      
      int score = result.getScore();
      score *= -1;

      if (score > bestValue) {
        bestValue = score;
        bestChild = child;
      }
      if (bestValue > alpha) {
        alpha = bestValue;
      }
      if (bestValue >= beta) {
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

    return new Result(bestValue, bestChild);
  }

  private CachedValue createNewCachedValue(int alpha, int beta, int depth, int score) {
    CachedValue newCachedValue;
    if (score < alpha) {
      newCachedValue = new CachedValue(CachedValue.Flag.LOWERBOUND, score, depth);
    }
    else if (score >= beta) {
      newCachedValue = new CachedValue(CachedValue.Flag.UPPERBOUND, score, depth);
    }
    else {
      newCachedValue = new CachedValue(CachedValue.Flag.EXACT, score, depth);
    }
    return newCachedValue;
  }

  public static class Result {
    private final int score;
    private final BitBoard board;

    public Result(int score, BitBoard board) {
      this.score = score;
      this.board = board;
    }

    public BitBoard getBoard() {
      return board;
    }

    public int getScore() {
      return score;
    }
  }
}
