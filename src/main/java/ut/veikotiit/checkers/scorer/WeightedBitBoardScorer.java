package ut.veikotiit.checkers.scorer;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;

public class WeightedBitBoardScorer implements BitBoardScorer {

  // TODO: 28.12.17 Consider refactoring score values to double type. Having 2x weight for kings might be too much.
  private static final int KING_WEIGHT = 2;

  @Override
  public int getScore(BitBoard bitBoard) {
    Color color = bitBoard.getNextColor();

    int score = Long.bitCount(bitBoard.getBlacks()) + KING_WEIGHT * Long.bitCount(bitBoard.getBlackKings())
            - Long.bitCount(bitBoard.getWhites()) - KING_WEIGHT * Long.bitCount(bitBoard.getWhiteKings());
    if (color == Color.WHITE) {
      score *= -1;
    }

    return score;
  }

  private static BitBoardScorer INSTANCE = new WeightedBitBoardScorer();

  public static BitBoardScorer getInstance() {
    return INSTANCE;
  }
}
