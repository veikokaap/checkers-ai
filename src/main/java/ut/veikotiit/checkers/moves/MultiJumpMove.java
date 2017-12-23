package ut.veikotiit.checkers.moves;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ut.veikotiit.checkers.Color;

public class MultiJumpMove implements Move {
  private final List<SingleJumpMove> jumps;
  
  public MultiJumpMove(SingleJumpMove singleJumpMove) {
    jumps = Collections.singletonList(singleJumpMove);
  }

  public MultiJumpMove(MultiJumpMove previousMove, SingleJumpMove singleJumpMove) {
    jumps = new ArrayList<>(previousMove.getJumps());
    jumps.add(singleJumpMove);
  }

  public List<SingleJumpMove> getJumps() {
    return jumps;
  }
  
  public int getOrigin() {
    return getJumps().get(0).getOrigin();
  }
  
  public int getDestination() {
    return getJumps().get(getJumps().size() - 1).getDestination();
  }

  public Color getColor() {
    return jumps.get(0).getColor();
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MultiJumpMove that = (MultiJumpMove) o;
    return Objects.equals(getJumps(), that.getJumps());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getJumps());
  }

  @Override
  public String toString() {
    return "MultiJumpMove{" +
        "jumps=" + jumps +
        '}';
  }

  @Override
  public <T> T visit(T board, MoveVisitor<T> moveVisitor) {
    return moveVisitor.visit(board, this);
  }

  public boolean takesPiece(int pieceTaken) {
    for (SingleJumpMove jump : getJumps()) {
      if (jump.getPieceTaken() == pieceTaken) {
        return true;
      }
    }
    
    return false;
  }
}
