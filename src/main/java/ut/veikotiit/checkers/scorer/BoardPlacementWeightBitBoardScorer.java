package ut.veikotiit.checkers.scorer;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.bitboard.BitUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Takes into account the position of pieces, giving weights for rows.
 */
public class BoardPlacementWeightBitBoardScorer implements BitBoardScorer {
  private static BitBoardScorer INSTANCE = new BoardPlacementWeightBitBoardScorer();

  private static final Map<Integer, Integer> rowScoreMap = new HashMap<>();

  static {
    rowScoreMap.put(1, 5);
    rowScoreMap.put(2, 4);
    rowScoreMap.put(3, 3);
    rowScoreMap.put(4, 2);
    rowScoreMap.put(5, 1);
    rowScoreMap.put(6, 1);
    rowScoreMap.put(7, 2);
    rowScoreMap.put(8, 3);
    rowScoreMap.put(9, 4);
    rowScoreMap.put(10, 5);
  }

  @Override
  public double getScore(BitBoard bitBoard, Color color) {
    double score = 0.0d;
    long allColorPieces = color == Color.WHITE ? bitBoard.getAllWhitePieces() : bitBoard.getAllBlackPieces();

    for (int i = 1; i <= 10; i++) {
      score += (Long.bitCount(allColorPieces & BitUtil.getRowMask(i)) * rowScoreMap.get(i));
    }

    if (color == Color.WHITE) {
      score *= -1;
    }
    return score + DefaultBitBoardScorer.getInstance().getScore(bitBoard, color);
  }

  public static BitBoardScorer getInstance() {
    return INSTANCE;
  }
}
