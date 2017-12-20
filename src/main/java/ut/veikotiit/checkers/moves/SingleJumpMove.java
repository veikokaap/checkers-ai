package ut.veikotiit.checkers.moves;

import java.util.Objects;

import ut.veikotiit.checkers.Color;

public class SingleJumpMove implements Move {
  private final int origin;
  private final int destination;
  private final Color color;
  private final int pieceTaken;

  public SingleJumpMove(int origin, int destination, Color color, int pieceTaken) {
    this.origin = origin;
    this.destination = destination;
    this.color = color;
    this.pieceTaken = pieceTaken;
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

  public int getPieceTaken() {
    return pieceTaken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SingleJumpMove)) {
      return false;
    }
    SingleJumpMove singleJumpMove = (SingleJumpMove) o;
    return getOrigin() == singleJumpMove.getOrigin() &&
        getDestination() == singleJumpMove.getDestination() &&
        getPieceTaken() == singleJumpMove.getPieceTaken() &&
        getColor() == singleJumpMove.getColor();
  }

  @Override
  public int hashCode() {

    return Objects.hash(getOrigin(), getDestination(), getColor(), getPieceTaken());
  }

  @Override
  public String toString() {
    return "SingleJumpMove{" +
        "origin=" + origin +
        ", destination=" + destination +
        ", color=" + color +
        ", pieceTaken=" + pieceTaken +
        '}';
  }
}
