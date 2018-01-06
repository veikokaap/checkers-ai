package ut.veikotiit.checkers.scorer;

import ut.veikotiit.checkers.bitboard.BitBoard;

public interface BitBoardScorer {
    double getScore(BitBoard bitBoard);
}
