package ut.veikotiit.checkers.bitboard;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.moves.MultiJumpMove;
import ut.veikotiit.checkers.moves.SingleJumpMove;
import ut.veikotiit.checkers.moves.JumpMoveGenerator;
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
    MultiJumpMove[] jumps = JumpMoveGenerator.getJumps(this, color);

    // Mandatory taking
    if (jumps.length > 0) {
      return Arrays.stream(jumps)
          .map(this::move)
          .collect(Collectors.toList());
    }
    else {
      return Arrays.stream(SimpleMoveGenerator.generate(this, color))
          .map(this::move)
          .collect(Collectors.toList());
    }
  }

  private static class BitBoardMover implements MoveVisitor<BitBoard> {
    @Override
    public BitBoard visit(BitBoard bitBoard, SimpleMove move) {
      if (move.getColor() == Color.WHITE) {
        return new BitBoard(bitBoard.getBlacks(), simpleMove(bitBoard.getWhites(), move), move);
      }
      else {
        return new BitBoard(simpleMove(bitBoard.getBlacks(), move), bitBoard.getWhites(), move);
      }
    }

    private long simpleMove(long pieces, SimpleMove move) {
      pieces = removePieceAt(pieces, move.getOrigin());
      pieces = addPieceAt(pieces, move.getDestination());
      return pieces;
    }

    @Override
    public BitBoard visit(BitBoard board, SingleJumpMove move) {
      if (move.getColor() == Color.WHITE) {
        long whites = removePieceAt(board.getWhites(), move.getOrigin());
        whites = addPieceAt(whites, move.getDestination());
        long blacks = board.getBlacks();
        blacks = removePieceAt(blacks, move.getPieceTaken());
        return new BitBoard(blacks, whites, move);
      }
      else {
        long blacks = removePieceAt(board.getBlacks(), move.getOrigin());
        blacks = addPieceAt(blacks, move.getDestination());
        long whites = board.getWhites();
        whites = removePieceAt(whites, move.getPieceTaken());
        return new BitBoard(blacks, whites, move);
      }
    }

    @Override
    public BitBoard visit(BitBoard board, MultiJumpMove multiJumpMove) {
      BitBoard newBoard = board;
      for (SingleJumpMove singleJumpMove : multiJumpMove.getJumps()) {
        newBoard = newBoard.move(singleJumpMove);
      }
      return newBoard;
    }

    private long removePieceAt(long pieces, int location) {
      return pieces & (~BitUtil.getBitAt(location));
    }

    private long addPieceAt(long pieces, int location) {
      return pieces | BitUtil.getBitAt(location);
    }
  }
}
