package ut.veikotiit.checkers.moves;

import ut.veikotiit.checkers.Color;

public interface Move {
  
  int getOrigin();
  
  int getDestination();
  
  Color getColor();
  
  <T> T visit(T board, MoveVisitor<T> moveVisitor);
}
