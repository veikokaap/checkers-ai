package ut.veikotiit.checkers.scorer;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;

public class DefaultBitBoardScorer implements BitBoardScorer {

  private static BitBoardScorer INSTANCE = new DefaultBitBoardScorer();

    @Override
    public int getScore(BitBoard bitBoard, Color color) {
      int score = Long.bitCount(bitBoard.getPlayerPieces(color)) + Long.bitCount(bitBoard.getPlayerKings(color))
          - Long.bitCount(bitBoard.getPlayerPieces(color.getOpponent())) - Long.bitCount(bitBoard.getPlayerKings(color.getOpponent()));

      return score * 1000;
    }

  public static BitBoardScorer getInstance() {
    return INSTANCE;
  }
}
