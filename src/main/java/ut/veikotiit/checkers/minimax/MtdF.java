package ut.veikotiit.checkers.minimax;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.moves.Move;
import ut.veikotiit.checkers.scorer.BitBoardScorer;
import ut.veikotiit.checkers.transposition.TranspositionTable;

public class MtdF {
  
  private long startTime;
  private final long timeGiven;

  public MtdF(long timeGiven) {
    this.timeGiven = timeGiven;
  }

  public Move search(BitBoard board, Color color, int max_depth, BitBoardScorer scorer) {
    startTime = System.currentTimeMillis();
    TranspositionTable transpositionTable = new TranspositionTable();

    int depth = 0;
    Negamax.Result bestResult = null;
    int firstGuess;
    while (depth < max_depth) {
      depth += 1;
      if (bestResult == null) {
        firstGuess = scorer.getScore(color, board);
      }
      else{
        firstGuess = bestResult.getScore();
      }

      Negamax.Result newResult = internal(board, color, depth, transpositionTable, firstGuess, scorer);

      if (newResult == null) {
        break; // Time exceeded
      } else {
        bestResult = newResult;
      }
    }

    if (bestResult == null || bestResult.getBoard() == null) {
      return null;
    }
    
    return bestResult.getBoard().getMove();
  }

  private Negamax.Result internal(BitBoard board, Color color, int depth,
                                  TranspositionTable transpositionTable, int firstGuess, BitBoardScorer scorer) {
    Negamax negamax = new Negamax(startTime, timeGiven, scorer);

    int upperBound = Integer.MAX_VALUE;
    int lowerBound = Integer.MIN_VALUE;

    Negamax.Result bestResult = null;
    while (lowerBound < upperBound) {
      int beta = Math.max(firstGuess, lowerBound + 1);
      Negamax.Result result = negamax.recursive(board, color, beta - 1, beta, depth, transpositionTable);
      
      if (result == null) {
        return null; // Time exceeded
      }

      bestResult = result;
      firstGuess = result.getScore();
      if (firstGuess < beta) {
        upperBound = firstGuess;
      }
      else {
        lowerBound = firstGuess;
      }
    }

    return bestResult;
  }
}
