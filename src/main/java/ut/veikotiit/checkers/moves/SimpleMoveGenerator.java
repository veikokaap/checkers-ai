package ut.veikotiit.checkers.moves;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.bitboard.BitUtil;


public class SimpleMoveGenerator {

  public static Set<SimpleMove> generate(BitBoard board, Color color) {
    if (color == Color.WHITE) {
      return new HashSet<>(Arrays.asList(getWhiteMoves(board)));
    } else {
      return new HashSet<>(Arrays.asList(getBlackMoves(board)));
    }
  }

  private static SimpleMove[] getBlackMoves(BitBoard board) {
    long whites = board.getWhites();
    long blacks = board.getBlacks();
    long blackKings = board.getBlackKings();

    SimpleMove[][] blackRegularPieceMoves = getBlackRegularPieceMoves(whites, blacks);
    SimpleMove[][] blackKingPieceMoves = getBlackKingPieceMoves(whites, blacks, blackKings);
    
    return Stream.of(blackRegularPieceMoves, blackKingPieceMoves)
        .flatMap(Stream::of)
        .flatMap(Stream::of)
        .toArray(SimpleMove[]::new);
  }

  private static SimpleMove[][] getBlackRegularPieceMoves(long whites, long blacks) {
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

    return new SimpleMove[][]{fourMoves, fiveMoves, sixMoves};
  }

  private static SimpleMove[][] getBlackKingPieceMoves(long whites, long blacks, long blackKings) {
    long unoccupied = BitUtil.ALL_BITS ^ (whites | blacks);

    long fiveMovers = (unoccupied << 5) & blackKings;
    long fourMovers = ((unoccupied & BitUtil.MASK_L4) << 4) & blackKings;
    long sixMovers = ((unoccupied & BitUtil.MASK_L6) << 6) & blackKings;

    SimpleMove[] fourMoves = Arrays.stream(BitUtil.longToBits(fourMovers))
        .mapToObj(i -> new SimpleMove(i, i - 4, Color.BLACK))
        .toArray(SimpleMove[]::new);

    SimpleMove[] fiveMoves = Arrays.stream(BitUtil.longToBits(fiveMovers))
        .mapToObj(i -> new SimpleMove(i, i - 5, Color.BLACK))
        .toArray(SimpleMove[]::new);

    SimpleMove[] sixMoves = Arrays.stream(BitUtil.longToBits(sixMovers))
        .mapToObj(i -> new SimpleMove(i, i - 6, Color.BLACK))
        .toArray(SimpleMove[]::new);
    
    return new SimpleMove[][]{fourMoves, fiveMoves, sixMoves};
  }

  private static SimpleMove[] getWhiteMoves(BitBoard board) {
    long whites = board.getWhites();
    long blacks = board.getBlacks();
    long whiteKings = board.getWhiteKings();

    SimpleMove[][] whiteRegularPieceMoves = getWhiteRegularPieceMoves(whites, blacks);
    SimpleMove[][] whiteKingPieceMoves = getWhiteKingPieceMoves(whites, blacks, whiteKings);

    return Stream.of(whiteRegularPieceMoves, whiteKingPieceMoves)
        .flatMap(Stream::of)
        .flatMap(Stream::of)
        .toArray(SimpleMove[]::new);
  }

  private static SimpleMove[][] getWhiteKingPieceMoves(long whites, long blacks, long whiteKings) {
    long unoccupied = BitUtil.ALL_BITS ^ (whites | blacks);

    long fiveMovers = (unoccupied >> 5) & whiteKings;
    long fourMovers = ((unoccupied & BitUtil.MASK_R4) >> 4) & whiteKings;
    long sixMovers = ((unoccupied & BitUtil.MASK_R6) >> 6) & whiteKings;

    SimpleMove[] fourMoves = Arrays.stream(BitUtil.longToBits(fourMovers))
        .mapToObj(i -> new SimpleMove(i, i + 4, Color.WHITE))
        .toArray(SimpleMove[]::new);

    SimpleMove[] fiveMoves = Arrays.stream(BitUtil.longToBits(fiveMovers))
        .mapToObj(i -> new SimpleMove(i, i + 5, Color.WHITE))
        .toArray(SimpleMove[]::new);

    SimpleMove[] sixMoves = Arrays.stream(BitUtil.longToBits(sixMovers))
        .mapToObj(i -> new SimpleMove(i, i + 6, Color.WHITE))
        .toArray(SimpleMove[]::new);

    return new SimpleMove[][]{fourMoves, fiveMoves, sixMoves};
  }

  private static SimpleMove[][] getWhiteRegularPieceMoves(long whites, long blacks) {
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

    return new SimpleMove[][]{fourMoves, fiveMoves, sixMoves};
  }
}
