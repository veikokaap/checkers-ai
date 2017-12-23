package ut.veikotiit.checkers.moves;

public interface MoveVisitor<T> {
  T visit(T board, SimpleMove simpleMove);
  
  T visit(T board, SingleJumpMove singleJumpMove);

  T visit(T board, MultiJumpMove multiJumpMove);
}
