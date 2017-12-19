package ut.veikotiit.checkers.moves;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.bitboard.BitUtil;


public class SimpleMoveGenerator {

  public static SimpleMove[] generate(BitBoard board, Color color) {
    if (color == Color.WHITE) {
      return getWhiteMoves(board);
    } else {
      return getBlackMoves(board);
    }
  }

  private static SimpleMove[] getBlackMoves(BitBoard board) {
    long whites = board.getWhites();
    long blacks = board.getBlacks();

    long unoccupied = BitUtil.ALL_BITS ^ (whites | blacks);

    long fiveMovers = (unoccupied >> 5) & blacks;
    long fourMovers = ((unoccupied & BitUtil.MASK_R4) >> 4) & blacks;
    long sixMovers = ((unoccupied & BitUtil.MASK_R6) >> 6) & blacks;


    SimpleMove[] fourMoves = Arrays.stream(BitUtil.longToBits(fourMovers))
        .mapToObj(i -> new SimpleMove(i, i + 4, Color.BLACK))
        .toArray(SimpleMove[]::new);

    SimpleMove[] fiveMoves = Arrays.stream(BitUtil.longToBits(fiveMovers))
        .mapToObj(i -> new SimpleMove(i, i + 5, Color.BLACK))
        .toArray(SimpleMove[]::new);

    SimpleMove[] sixMoves = Arrays.stream(BitUtil.longToBits(sixMovers))
        .mapToObj(i -> new SimpleMove(i, i + 6, Color.BLACK))
        .toArray(SimpleMove[]::new);

    return Stream.of(fourMoves, fiveMoves, sixMoves)
        .flatMap(Arrays::stream)
        .toArray(SimpleMove[]::new);
  }

  private static SimpleMove[] getWhiteMoves(BitBoard board) {
    long whites = board.getWhites();
    long blacks = board.getBlacks();

    long unoccupied = BitUtil.ALL_BITS ^ (whites | blacks);

    long fiveMovers = (unoccupied << 5) & whites;
    long fourMovers = ((unoccupied & BitUtil.MASK_L4) << 4) & whites;
    long sixMovers = ((unoccupied & BitUtil.MASK_L6) << 6) & whites;


    SimpleMove[] fourMoves = Arrays.stream(BitUtil.longToBits(fourMovers))
        .mapToObj(i -> new SimpleMove(i, i - 4, Color.WHITE))
        .toArray(SimpleMove[]::new);

    SimpleMove[] fiveMoves = Arrays.stream(BitUtil.longToBits(fiveMovers))
        .mapToObj(i -> new SimpleMove(i, i - 5, Color.WHITE))
        .toArray(SimpleMove[]::new);

    SimpleMove[] sixMoves = Arrays.stream(BitUtil.longToBits(sixMovers))
        .mapToObj(i -> new SimpleMove(i, i - 6, Color.WHITE))
        .toArray(SimpleMove[]::new);

    return Stream.of(fourMoves, fiveMoves, sixMoves)
        .flatMap(Arrays::stream)
        .toArray(SimpleMove[]::new);
  }
}
