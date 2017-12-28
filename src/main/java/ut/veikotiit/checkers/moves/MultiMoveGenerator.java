package ut.veikotiit.checkers.moves;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;

public class MultiMoveGenerator {

  public static Set<MultiJumpMove> getJumps(BitBoard board, Color color, BiFunction<BitBoard, Color, Set<SingleJumpMove>> singleJumpFunction) {
    Set<SingleJumpMove> singleJumps = singleJumpFunction.apply(board, color);
    Set<MultiJumpMove> multiJumpMoves = singleJumps.stream()
        .map(MultiJumpMove::new)
        .collect(Collectors.toSet());

    return findMultiJumps(multiJumpMoves, board, color, singleJumpFunction);
  }
  
  private static Set<MultiJumpMove> findMultiJumps(Set<MultiJumpMove> previousJumps, BitBoard board, Color color, BiFunction<BitBoard, Color, Set<SingleJumpMove>> singleJumpFunction) {
    while (true) {
      Set<MultiJumpMove> newJumps = new HashSet<>();

      for (MultiJumpMove multiJump : previousJumps) {
        BitBoard newBoard = moveBoardWithoutTakingPieces(board, multiJump);

        Set<SingleJumpMove> singleJumps = singleJumpFunction.apply(newBoard, color);
        for (SingleJumpMove newJump : singleJumps) {
          if (multiJump.getDestination() == newJump.getOrigin() && !multiJump.takesPiece(newJump.getPieceTaken())) {
            newJumps.add(new MultiJumpMove(multiJump, newJump));
          }
        }
      }

      if (newJumps.isEmpty()) {
        return previousJumps;
      }
      else {
        previousJumps = newJumps;
      }
    }
  }

  private static BitBoard moveBoardWithoutTakingPieces(BitBoard board, MultiJumpMove multiJump) {
    BitBoard newBoard = board;
    for (SingleJumpMove jump : multiJump.getJumps()) {
      newBoard = newBoard.move(jump.getSimpleMove());
    }
    return newBoard;
  }
}
