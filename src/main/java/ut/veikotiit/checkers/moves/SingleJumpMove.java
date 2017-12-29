package ut.veikotiit.checkers.moves;

import java.util.Objects;

import ut.veikotiit.checkers.Color;

public class SingleJumpMove implements Move {
  private final Color color;
  private final SimpleMove simpleMove;
  private final int pieceTaken;

  public SingleJumpMove(int origin, int destination, Color color, int pieceTaken) {
    simpleMove = new SimpleMove(origin, destination, color);
    this.color = color;
    this.pieceTaken = pieceTaken;
  }

  @Override
  public <T> T visit(T board, MoveVisitor<T> moveVisitor) {
    return moveVisitor.visit(board, this);
  }

  public SimpleMove getSimpleMove() {
    return simpleMove;
  }

  public Color getColor() {
    return color;
  }

  public int getPieceTaken() {
    return pieceTaken;
  }

  public int getOrigin() {
    return simpleMove.getOrigin();
  }
  
  public int getDestination() {
    return simpleMove.getDestination();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SingleJumpMove that = (SingleJumpMove) o;
    return getPieceTaken() == that.getPieceTaken() &&
        getColor() == that.getColor() &&
        Objects.equals(getSimpleMove(), that.getSimpleMove());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSimpleMove(), getPieceTaken());
  }

  @Override
  public String toString() {
    return "SingleJumpMove{" +
        "color=" + color +
        ", origin=" + getOrigin() +
        ", destination=" + getDestination() +
        ", pieceTaken=" + pieceTaken +
        '}';
  }
}
