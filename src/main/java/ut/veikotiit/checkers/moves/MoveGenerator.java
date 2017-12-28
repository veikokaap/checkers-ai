package ut.veikotiit.checkers.moves;

import java.util.Set;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;

public class MoveGenerator {
  public static Set<? extends Move> getAllMoves(BitBoard bitBoard, Color color) {
    Set<MultiJumpMove> jumps = MultiMoveGenerator.getJumps(bitBoard, color, JumpMoveGenerator::getSingleJumps);
    jumps.addAll(MultiMoveGenerator.getJumps(bitBoard, color, KingMoveGenerator::getJumps));

    if (!jumps.isEmpty()) {
      return jumps;
    }
    else {
      Set<SimpleMove> moves = SimpleMoveGenerator.generate(bitBoard, color);
      moves.addAll(KingMoveGenerator.getSimpleMoves(bitBoard, color));

      return moves;
    }
  }
}
