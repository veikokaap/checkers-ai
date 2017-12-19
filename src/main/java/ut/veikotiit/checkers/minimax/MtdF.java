package ut.veikotiit.checkers.minimax;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.moves.Move;
import ut.veikotiit.checkers.transposition.TranspositionTable;

public class MtdF {

  public Move search(BitBoard board, Color color, int max_depth) {
    TranspositionTable transpositionTable = new TranspositionTable();

    int depth = 0;
    Negamax.Result bestResult = null;
    int firstGuess;
    while (depth < max_depth) {
      depth += 1;
      if (bestResult == null) {
        firstGuess = board.getScore(color);
      }
      else{
        firstGuess = bestResult.getScore();
      }

      Negamax.Result newResult = internal(board, color, depth, transpositionTable, firstGuess);

      if (newResult == null) {
        break;
      } else {
        bestResult = newResult;
      }
    }

    if (bestResult == null || bestResult.getBoard() == null) {
      return null;
    }

    return bestResult.getBoard().getMove();
  }

  private Negamax.Result internal(BitBoard board, Color color, int depth, TranspositionTable transpositionTable, int firstGuess) {
    Negamax negamax = new Negamax();

    int upperBound = Integer.MAX_VALUE;
    int lowerBound = Integer.MIN_VALUE;

    Negamax.Result bestResult = null;
    while (lowerBound < upperBound) {
      int beta = Math.max(firstGuess, lowerBound + 1);
      Negamax.Result result = negamax.recursive(board, color, beta - 1, beta, depth, transpositionTable);

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
