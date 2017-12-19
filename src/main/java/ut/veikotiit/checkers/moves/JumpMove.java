package ut.veikotiit.checkers.moves;

import java.util.Arrays;
import java.util.Objects;

import ut.veikotiit.checkers.Color;

public class JumpMove implements Move {
  private final int origin;
  private final int destination;
  private final Color color;
  private final int[] piecesTaken;

  public JumpMove(int origin, int destination, Color color, int[] piecesTaken) {
    this.origin = origin;
    this.destination = destination;
    this.color = color;
    this.piecesTaken = piecesTaken;
  }

  @Override
  public <T> T visit(T board, MoveVisitor<T> moveVisitor) {
    return moveVisitor.visit(board, this);
  }

  public int getOrigin() {
    return origin;
  }

  public int getDestination() {
    return destination;
  }

  public Color getColor() {
    return color;
  }

  public int[] getPiecesTaken() {
    return piecesTaken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof JumpMove)) {
      return false;
    }
    JumpMove jumpMove = (JumpMove) o;
    return getOrigin() == jumpMove.getOrigin() &&
        getDestination() == jumpMove.getDestination() &&
        getColor() == jumpMove.getColor() &&
        Arrays.equals(getPiecesTaken(), jumpMove.getPiecesTaken());
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(getOrigin(), getDestination(), getColor());
    result = 31 * result + Arrays.hashCode(getPiecesTaken());
    return result;
  }

  @Override
  public String toString() {
    return "JumpMove{" +
        "origin=" + origin +
        ", destination=" + destination +
        ", color=" + color +
        ", piecesTaken=" + Arrays.toString(piecesTaken) +
        '}';
  }
}
