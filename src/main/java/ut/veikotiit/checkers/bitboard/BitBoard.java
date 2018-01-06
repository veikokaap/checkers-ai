package ut.veikotiit.checkers.bitboard;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.moves.Move;
import ut.veikotiit.checkers.moves.MoveGenerator;
import ut.veikotiit.checkers.moves.MoveVisitor;
import ut.veikotiit.checkers.moves.MultiJumpMove;
import ut.veikotiit.checkers.moves.SimpleMove;
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
  private static final Color STARTING_COLOR = Color.BLACK;

  private final long blacks;
  private final long whites;
  private final long kings;
  private final Move previousMove;
  private List<BitBoard> childBoards;

  public static BitBoard create(long blacks, long whites, long kings) {
    return new BitBoard(blacks, whites, kings, null);
  }

  private BitBoard(long blacks, long whites, long kings, Move previousMove) {
    this.blacks = blacks;
    this.whites = whites;
    this.kings = kings;
    this.previousMove = previousMove;
  }

  public long getAllBlackPieces() {
    return blacks;
  }

  public long getAllWhitePieces() {
    return whites;
  }

  public long getWhiteRegularPieces() {
    return whites & ~kings;
  }

  public long getBlackRegularPieces() {
    return whites & ~kings;
  }

  public long getWhiteKings() {
    return whites & kings;
  }

  public long getBlackKings() {
    return blacks & kings;
  }

  public Move getPreviousMove() {
    return previousMove;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BitBoard bitBoard = (BitBoard) o;

    if (blacks != bitBoard.blacks) {
      return false;
    }
    if (whites != bitBoard.whites) {
      return false;
    }
    return kings == bitBoard.kings;
  }

  @Override
  public int hashCode() {
    int result = (int) (blacks ^ (blacks >>> 32));
    result = 31 * result + (int) (whites ^ (whites >>> 32));
    result = 31 * result + (int) (kings ^ (kings >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "BitBoard{" +
        "blacks=" + blacks +
        ", whites=" + whites +
        ", previousMove=" + previousMove +
        '}';
  }

  public String toPrettyString() {
    StringBuilder stringBuilder = new StringBuilder();

    List<Integer> blacks = Arrays.stream(BitUtil.longToBits(getAllBlackPieces())).boxed().collect(Collectors.toList());
    List<Integer> blackKings = Arrays.stream(BitUtil.longToBits(getBlackKings())).boxed().collect(Collectors.toList());
    List<Integer> whites = Arrays.stream(BitUtil.longToBits(getAllWhitePieces())).boxed().collect(Collectors.toList());
    List<Integer> whiteKings = Arrays.stream(BitUtil.longToBits(getWhiteKings())).boxed().collect(Collectors.toList());

    stringBuilder.append("+------------------------------+").append(System.lineSeparator());
    for (int i = 0; i < 10; i++) {
      stringBuilder.append("|");
      for (int j = 0; j < 10; j++) {
        stringBuilder.append(" ");
        if ((i + j) % 2 == 1) {
          int index = (5 * i) + (j / 2);
          if (blackKings.contains(index)) {
            stringBuilder.append("B");
          }
          else if (blacks.contains(index)) {
            stringBuilder.append("b");
          }
          else if (whiteKings.contains(index)) {
            stringBuilder.append("W");
          }
          else if (whites.contains(index)) {
            stringBuilder.append("w");
          }
          else {
            stringBuilder.append(" ");
          }
        }
        else {
          stringBuilder.append(" ");
        }
        stringBuilder.append(" ");
      }
      stringBuilder.append("|").append(System.lineSeparator());
    }
    stringBuilder.append("+------------------------------+");

    return stringBuilder.toString();
  }

  public Color getNextColor() {
    if (previousMove == null) {
      return STARTING_COLOR;
    } else {
      return previousMove.getColor().getOpponent();
    }
  }

  public List<BitBoard> getChildBoards() {
    if (childBoards == null) {
      childBoards = MoveGenerator.getAllMoves(this, getNextColor()).stream()
          .map(this::move)
          .collect(Collectors.toList());
    }

    return childBoards;
  }

  private static class BitBoardMover implements MoveVisitor<BitBoard> {
    @Override
    public BitBoard visit(BitBoard bitBoard, SimpleMove move) {
      if (move.getColor() == Color.WHITE) {
        return new BitBoard(bitBoard.getAllBlackPieces(), simpleMove(bitBoard.getAllWhitePieces(), move), moveKings(bitBoard, move), move);
      }
      else {
        return new BitBoard(simpleMove(bitBoard.getAllBlackPieces(), move), bitBoard.getAllWhitePieces(), moveKings(bitBoard, move), move);
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
        long whites = removePieceAt(board.getAllWhitePieces(), jumpMove.getOrigin());
        whites = addPieceAt(whites, jumpMove.getDestination());
        long blacks = board.getAllBlackPieces();
        blacks = removePieceAt(blacks, jumpMove.getPieceTaken());
        return new BitBoard(blacks, whites, moveKings(board, jumpMove), jumpMove);
      }
      else {
        long blacks = removePieceAt(board.getAllBlackPieces(), jumpMove.getOrigin());
        blacks = addPieceAt(blacks, jumpMove.getDestination());
        long whites = board.getAllWhitePieces();
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
      return new BitBoard(newBoard.getAllBlackPieces(), newBoard.getAllWhitePieces(), moveKings(board, multiJumpMove), multiJumpMove);
    }

    private long removePieceAt(long pieces, int location) {
      return pieces & (~BitUtil.getBitAt(location));
    }

    private long addPieceAt(long pieces, int location) {
      return pieces | BitUtil.getBitAt(location);
    }
  }
}
