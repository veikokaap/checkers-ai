package ut.veikotiit.checkers.scorer;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;

public interface BitBoardScorer {
    int getScore(BitBoard bitBoard, Color color);
}
