package ut.veikotiit.checkers.moves;

import java.util.ArrayList;
import java.util.List;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.bitboard.BitUtil;

public class JumpMoveGenerator {
  public static JumpMove[] generate(BitBoard board, Color color) {
    if (color == Color.WHITE) {
      return getWhiteSingleJumps(board);
    } else {
      return getBlackSingleJumps(board);
    }
  }

  private static JumpMove[] getBlackSingleJumps(BitBoard board) {
    List<JumpMove> moves = new ArrayList<>();

    long whites = board.getWhites();
    long blacks = board.getBlacks();

    long unoccupied = BitUtil.ALL_BITS ^ (whites | blacks);

    long temp = (unoccupied >> 5) & whites;

    if (temp != 0) {
      long nineMovers = ((temp & BitUtil.MASK_R4) >> 4) & blacks;
      long elevenMovers = ((temp & BitUtil.MASK_R6) >> 6) & blacks;

      for (int i : BitUtil.longToBits(nineMovers)) {
        moves.add(new JumpMove(i, i + 9, Color.BLACK, new int[]{i + 4}));
      }
      for (int i : BitUtil.longToBits(elevenMovers)) {
        moves.add(new JumpMove(i, i + 11, Color.BLACK, new int[]{i + 6}));
      }
    }
    long tempNine = ((unoccupied & BitUtil.MASK_R4) >> 4) & whites;
    long tempEleven = ((unoccupied & BitUtil.MASK_R6) >> 6) & whites;

    long nineMovers = (tempNine >> 5) & blacks;
    long elevenMovers = (tempEleven >> 5) & blacks;

    for (int i : BitUtil.longToBits(nineMovers)) {
      moves.add(new JumpMove(i, i + 9, Color.BLACK, new int[]{i + 5}));
    }
    for (int i : BitUtil.longToBits(elevenMovers)) {
      moves.add(new JumpMove(i, i + 11, Color.BLACK, new int[]{i + 5}));
    }

    return moves.toArray(new JumpMove[0]);
  }

  private static JumpMove[] getWhiteSingleJumps(BitBoard board) {
    List<JumpMove> moves = new ArrayList<>();

    long whites = board.getWhites();
    long blacks = board.getBlacks();

    long unoccupied = BitUtil.ALL_BITS ^ (whites | blacks);

    long temp = (unoccupied << 5) & blacks;

    if (temp != 0) {
      long nineMovers = ((temp & BitUtil.MASK_L4) << 4) & whites;
      long elevenMovers = ((temp & BitUtil.MASK_L6) << 6) & whites;

      for (int i : BitUtil.longToBits(nineMovers)) {
        moves.add(new JumpMove(i, i - 9, Color.WHITE, new int[]{i - 4}));
      }
      for (int i : BitUtil.longToBits(elevenMovers)) {
        moves.add(new JumpMove(i, i - 11, Color.WHITE, new int[]{i - 6}));
      }
    }
    long tempNine = ((unoccupied & BitUtil.MASK_L4) << 4) & blacks;
    long tempEleven = ((unoccupied & BitUtil.MASK_L6) << 6) & blacks;

    long nineMovers = (tempNine << 5) & whites;
    long elevenMovers = (tempEleven << 5) & whites;

    for (int i : BitUtil.longToBits(nineMovers)) {
      moves.add(new JumpMove(i, i - 9, Color.WHITE, new int[]{i - 5}));
    }
    for (int i : BitUtil.longToBits(elevenMovers)) {
      moves.add(new JumpMove(i, i - 11, Color.WHITE, new int[]{i - 5}));
    }

    return moves.toArray(new JumpMove[0]);
  }
}
