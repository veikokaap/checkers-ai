package ut.veikotiit.checkers.bitboard;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.moves.Move;
import ut.veikotiit.checkers.moves.MoveVisitor;
import ut.veikotiit.checkers.moves.SimpleMove;
import ut.veikotiit.checkers.moves.SimpleMoveGenerator;

public class BitBoard {
  
  private static final BitBoardMover bitBoardMover = new BitBoardMover();
  
  private final long blacks;
  private final long whites;
  private final Move move;

  public static BitBoard create(long blacks, long whites) {
    return new BitBoard(blacks, whites, null);
  }

  private BitBoard(long blacks, long whites, Move move) {
    this.blacks = blacks;
    this.whites = whites;
    this.move = move;
  }

  public long getBlacks() {
    return blacks;
  }

  public long getWhites() {
    return whites;
  }

  public Move getMove() {
    return move;
  }

  public long getPlayerBitboard(Color color) {
    return color == Color.WHITE ? whites : blacks;
  }
  
  public BitBoard move(Move move) {
    return move.visit(this, bitBoardMover);
  }

  public int getScore(Color color) {
    int score = Long.bitCount(blacks) - Long.bitCount(whites);
    if (color == Color.WHITE) {
      score *= -1;
    }

    return score;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BitBoard)) {
      return false;
    }
    BitBoard bitBoard = (BitBoard) o;
    return getBlacks() == bitBoard.getBlacks() &&
        getWhites() == bitBoard.getWhites();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getBlacks(), getWhites());
  }

  @Override
  public String toString() {
    return "BitBoard{" +
        "blacks=" + blacks +
        ", whites=" + whites +
        ", move=" + move +
        '}';
  }

  public List<BitBoard> getChildBoards(Color color) {
    return Arrays.stream(SimpleMoveGenerator.generate(this, color))
        .map(this::move)
        .collect(Collectors.toList());
  }

  private static class BitBoardMover implements MoveVisitor<BitBoard> {
    @Override
    public BitBoard visit(BitBoard bitBoard, SimpleMove move) {
      if (move.getColor() == Color.WHITE) {
        return new BitBoard(bitBoard.getBlacks(), simpleMove(bitBoard.getWhites(), move), move);
      } else {
        return new BitBoard(simpleMove(bitBoard.getBlacks(), move), bitBoard.getWhites(), move);
      }
    }

    private long simpleMove(long pieces, SimpleMove move) {
      pieces = removePieceAt(pieces, move.getOrigin());
      pieces = addPieceAt(pieces, move.getDestination());
      return pieces;
    }

    private long removePieceAt(long pieces, int location) {
      return pieces & (~BitUtil.getBitAt(location));
    }

    private long addPieceAt(long pieces, int location) {
      return pieces | BitUtil.getBitAt(location);
    }
  }
}
