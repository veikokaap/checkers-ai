package ut.veikotiit.checkers.moves;

public interface MoveVisitor<T> {
  T visit(T board, SimpleMove simpleMove);
}
