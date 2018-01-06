package ut.veikotiit.checkers.moves;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.bitboard.BitUtil;

public class JumpMoveGenerator {
  
  public static Set<SingleJumpMove> getSingleJumps(BitBoard board, Color color) {
    long myPieces, opponentPieces;
    if (color == Color.WHITE) {
      myPieces = board.getAllWhitePieces();
      opponentPieces = board.getAllBlackPieces();
    }
    else {
      myPieces = board.getAllBlackPieces();
      opponentPieces = board.getAllWhitePieces();
    }

    return new HashSet<>(Arrays.asList(getSingleJumps(color, myPieces, opponentPieces)));
  }

  private static SingleJumpMove[] getSingleJumps(Color color, long myPieces, long opponentPieces) {
    SingleJumpMove[] forwardJumps = getForwardSingleJumps(color, myPieces, opponentPieces);
    SingleJumpMove[] backwardJumps = getBackwardSingleJumps(color, myPieces, opponentPieces);

    SingleJumpMove[] jumps = new SingleJumpMove[forwardJumps.length + backwardJumps.length];
    System.arraycopy(forwardJumps, 0, jumps, 0, forwardJumps.length);
    System.arraycopy(backwardJumps, 0, jumps, forwardJumps.length, backwardJumps.length);

    return jumps;
  }

  private static SingleJumpMove[] getForwardSingleJumps(Color color, long myPieces, long opponentPieces) {
    List<SingleJumpMove> moves = new ArrayList<>();

    long unoccupied = BitUtil.ALL_BITS ^ (myPieces | opponentPieces);

    long temp = (unoccupied >> 5) & opponentPieces;

    if (temp != 0) {
      long nineMovers = ((temp & BitUtil.MASK_R4) >> 4) & myPieces;
      long elevenMovers = ((temp & BitUtil.MASK_R6) >> 6) & myPieces;

      for (int i : BitUtil.longToBits(nineMovers)) {
        moves.add(new SingleJumpMove(i, i + 9, color, i + 4));
      }
      for (int i : BitUtil.longToBits(elevenMovers)) {
        moves.add(new SingleJumpMove(i, i + 11, color, i + 6));
      }
    }
    long tempNine = ((unoccupied & BitUtil.MASK_R4) >> 4) & opponentPieces;
    long tempEleven = ((unoccupied & BitUtil.MASK_R6) >> 6) & opponentPieces;

    long nineMovers = (tempNine >> 5) & myPieces;
    long elevenMovers = (tempEleven >> 5) & myPieces;

    for (int i : BitUtil.longToBits(nineMovers)) {
      moves.add(new SingleJumpMove(i, i + 9, color, i + 5));
    }
    for (int i : BitUtil.longToBits(elevenMovers)) {
      moves.add(new SingleJumpMove(i, i + 11, color, i + 5));
    }

    return moves.toArray(new SingleJumpMove[0]);
  }

  private static SingleJumpMove[] getBackwardSingleJumps(Color color, long myPieces, long opponentPieces) {
    List<SingleJumpMove> moves = new ArrayList<>();

    long unoccupied = BitUtil.ALL_BITS ^ (myPieces | opponentPieces);

    long temp = (unoccupied << 5) & opponentPieces;

    if (temp != 0) {
      long nineMovers = ((temp & BitUtil.MASK_L4) << 4) & myPieces;
      long elevenMovers = ((temp & BitUtil.MASK_L6) << 6) & myPieces;

      for (int i : BitUtil.longToBits(nineMovers)) {
        moves.add(new SingleJumpMove(i, i - 9, color, i - 4));
      }
      for (int i : BitUtil.longToBits(elevenMovers)) {
        moves.add(new SingleJumpMove(i, i - 11, color, i - 6));
      }
    }
    long tempNine = ((unoccupied & BitUtil.MASK_L4) << 4) & opponentPieces;
    long tempEleven = ((unoccupied & BitUtil.MASK_L6) << 6) & opponentPieces;

    long nineMovers = (tempNine << 5) & myPieces;
    long elevenMovers = (tempEleven << 5) & myPieces;

    for (int i : BitUtil.longToBits(nineMovers)) {
      moves.add(new SingleJumpMove(i, i - 9, color, i - 5));
    }
    for (int i : BitUtil.longToBits(elevenMovers)) {
      moves.add(new SingleJumpMove(i, i - 11, color, i - 5));
    }

    return moves.toArray(new SingleJumpMove[0]);
  }
}
