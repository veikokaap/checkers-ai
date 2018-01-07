package ut.veikotiit.checkers.scorer;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;

public class WeightedBitBoardScorer implements BitBoardScorer {

  private static final int ADDITIONAL_KING_WEIGHT = 200;

  @Override
  public int getScore(BitBoard bitBoard, Color color) {
    int score = (1000 * Long.bitCount(bitBoard.getAllBlackPieces())) + ADDITIONAL_KING_WEIGHT * Long.bitCount(bitBoard.getBlackKings())
            - (1000 * Long.bitCount(bitBoard.getAllWhitePieces())) - ADDITIONAL_KING_WEIGHT * Long.bitCount(bitBoard.getWhiteKings());
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
