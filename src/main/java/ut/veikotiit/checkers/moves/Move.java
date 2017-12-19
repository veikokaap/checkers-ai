package ut.veikotiit.checkers.moves;

public interface Move {
  <T> T visit(T board, MoveVisitor<T> moveVisitor);
}
