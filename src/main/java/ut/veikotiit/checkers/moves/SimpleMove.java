package ut.veikotiit.checkers.moves;

import java.util.Objects;


import ut.veikotiit.checkers.Color;

public class SimpleMove implements Move {
  private final int origin;
  private final int destination;
  private final Color color;

  public SimpleMove(int origin, int to, Color color) {
    this.origin = origin;
    this.destination = to;
    this.color = color;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimpleMove move = (SimpleMove) o;
    return getOrigin() == move.getOrigin() &&
        getDestination() == move.getDestination() &&
        getColor() == move.getColor();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getOrigin(), getDestination(), getColor());
  }

  @Override
  public String toString() {
    return "pkg.moves.SimpleMove{" +
        "origin=" + origin +
        ", destination=" + destination +
        ", color=" + color +
        '}';
  }
}
