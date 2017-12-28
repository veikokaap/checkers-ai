package ut.veikotiit.checkers.bitboard;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.moves.JumpMoveGenerator;
import ut.veikotiit.checkers.moves.KingMoveGenerator;
import ut.veikotiit.checkers.moves.Move;
import ut.veikotiit.checkers.moves.MoveGenerator;
import ut.veikotiit.checkers.moves.MoveVisitor;
import ut.veikotiit.checkers.moves.MultiJumpMove;
import ut.veikotiit.checkers.moves.SimpleMove;
import ut.veikotiit.checkers.moves.SimpleMoveGenerator;
import ut.veikotiit.checkers.moves.SingleJumpMove;

public class BitBoard {

  /*
      0   1   2   3   4
    5   6   7   8   9
     10  11  12  13  14
   15  16  17  18  19
     20  21  22  23  24
   25  26  27  28  29
     30  31  32  33  34
   35  36  37  38  39
     40  41  42  43  44
   45  46  47  48  49
   */
  
  private static final BitBoardMover bitBoardMover = new BitBoardMover();

  private final long blacks;
  private final long whites;
  private final long kings;
  private final Move move;

  public static BitBoard create(long blacks, long whites, long kings) {
    return new BitBoard(blacks, whites, kings, null);
  }

  private BitBoard(long blacks, long whites, long kings, Move move) {
    this.blacks = blacks;
    this.whites = whites;
    this.kings = kings;
    this.move = move;
  }

  public long getBlacks() {
    return blacks;
  }

  public long getWhites() {
    return whites;
  }

  public long getWhiteKings() {
    return whites & kings;
  }

  public long getBlackKings() {
    return blacks & kings;
  }

  public Move getMove() {
    return move;
  }

  public long getPlayerPieces(Color color) {
    return color == Color.WHITE ? whites : blacks;
  }

  public long getPlayerKings(Color color) {
    return color == Color.WHITE ? getWhiteKings() : getBlackKings();
  }
  
  public BitBoard move(Move move) {
    return move.visit(this, bitBoardMover);
  }

  public int getScore(Color color) {
    int score = Long.bitCount(blacks) + 5 * Long.bitCount(getBlackKings()) - Long.bitCount(whites) - 5 * Long.bitCount(getWhiteKings());
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
      return MoveGenerator.getAllMoves(this, color).stream()
          .map(this::move)
          .collect(Collectors.toList());
  }

  private static class BitBoardMover implements MoveVisitor<BitBoard> {
    @Override
    public BitBoard visit(BitBoard bitBoard, SimpleMove move) {
      if (move.getColor() == Color.WHITE) {
        return new BitBoard(bitBoard.getBlacks(), simpleMove(bitBoard.getWhites(), move), moveKings(bitBoard, move), move);
      }
      else {
        return new BitBoard(simpleMove(bitBoard.getBlacks(), move), bitBoard.getWhites(), moveKings(bitBoard, move), move);
      }
    }

    private long moveKings(BitBoard bitBoard, Move move) {
      long kings = bitBoard.kings;
      if (moveDestinationInLastRow(move)) {
        kings |= BitUtil.getBitAt(move.getDestination());
      }

      if (pieceWasKing(bitBoard, move.getOrigin())) {
        kings &= (~BitUtil.getBitAt(move.getOrigin()));
        kings |= BitUtil.getBitAt(move.getDestination());
      }

      if (move instanceof MultiJumpMove) {
        for (SingleJumpMove singleJumpMove : ((MultiJumpMove) move).getJumps()) {
          kings = jumpKing(bitBoard, kings, singleJumpMove);
        }
      }
      else if (move instanceof SingleJumpMove) {
        kings = jumpKing(bitBoard, kings, (SingleJumpMove) move);
      }

      return kings;
    }

    private long jumpKing(BitBoard bitBoard, long kings, SingleJumpMove singleJumpMove) {
      if (pieceWasKing(bitBoard, singleJumpMove.getPieceTaken())) {
        kings &= (~BitUtil.getBitAt(singleJumpMove.getPieceTaken()));
      }
      return kings;
    }

    private boolean pieceWasKing(BitBoard bitBoard, int piece) {
      return (BitUtil.getBitAt(piece) & bitBoard.kings) != 0;
    }

    private boolean moveDestinationInLastRow(Move move) {
      if (move.getColor() == Color.WHITE && (BitUtil.getBitAt(move.getDestination()) & BitUtil.MASK_ROW_1) != 0) {
        return true;
      }
      if (move.getColor() == Color.BLACK && (BitUtil.getBitAt(move.getDestination()) & BitUtil.MASK_ROW_10) != 0) {
        return true;
      }
      return false;
    }

    private long simpleMove(long pieces, SimpleMove move) {
      pieces = removePieceAt(pieces, move.getOrigin());
      pieces = addPieceAt(pieces, move.getDestination());
      return pieces;
    }

    @Override
    public BitBoard visit(BitBoard board, SingleJumpMove jumpMove) {
      if (jumpMove.getColor() == Color.WHITE) {
        long whites = removePieceAt(board.getWhites(), jumpMove.getOrigin());
        whites = addPieceAt(whites, jumpMove.getDestination());
        long blacks = board.getBlacks();
        blacks = removePieceAt(blacks, jumpMove.getPieceTaken());
        return new BitBoard(blacks, whites, moveKings(board, jumpMove), jumpMove);
      }
      else {
        long blacks = removePieceAt(board.getBlacks(), jumpMove.getOrigin());
        blacks = addPieceAt(blacks, jumpMove.getDestination());
        long whites = board.getWhites();
        whites = removePieceAt(whites, jumpMove.getPieceTaken());
        return new BitBoard(blacks, whites, moveKings(board, jumpMove), jumpMove);
      }
    }

    @Override
    public BitBoard visit(BitBoard board, MultiJumpMove multiJumpMove) {
      BitBoard newBoard = board;
      for (SingleJumpMove singleJumpMove : multiJumpMove.getJumps()) {
        newBoard = newBoard.move(singleJumpMove);
      }
      return new BitBoard(newBoard.getBlacks(), newBoard.getWhites(), moveKings(board, multiJumpMove), multiJumpMove);
    }

    private long removePieceAt(long pieces, int location) {
      return pieces & (~BitUtil.getBitAt(location));
    }

    private long addPieceAt(long pieces, int location) {
      return pieces | BitUtil.getBitAt(location);
    }
  }
}
