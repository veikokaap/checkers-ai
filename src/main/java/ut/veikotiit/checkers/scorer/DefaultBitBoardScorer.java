package ut.veikotiit.checkers.scorer;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;

public class DefaultBitBoardScorer implements BitBoardScorer {

    private static BitBoardScorer INSTANCE = new DefaultBitBoardScorer();

    @Override
    public int getScore(BitBoard bitBoard) {
        Color color = bitBoard.getNextColor();

        int score = Long.bitCount(bitBoard.getBlacks()) + Long.bitCount(bitBoard.getBlackKings())
                - Long.bitCount(bitBoard.getWhites()) - Long.bitCount(bitBoard.getWhiteKings());
        if (color == Color.WHITE) {
            score *= -1;
        }

        return score;
    }

    public static BitBoardScorer getInstance() {
        return INSTANCE;
    }
}
