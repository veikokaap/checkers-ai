package ut.veikotiit.checkers.moves;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.bitboard.BitUtil;

public class KingMoveGenerator {
  public static Set<? extends Move> getMoves(BitBoard bitBoard, Color color) {
    long myKings = bitBoard.getPlayerKings(color);

    Set<Move> moves = generateMovesStartingWithSimpleMove(bitBoard, color, myKings);
    moves.addAll(generateMovesStartingWithJump(bitBoard, color, myKings));
    
    if (moves.stream().anyMatch(m -> m instanceof SingleJumpMove)) {
      return moves.stream()
          .filter(m -> m instanceof SingleJumpMove)
          .collect(Collectors.toSet());
    } else {
      return moves;
    }
  }
  
  private static Set<Move> generateMovesStartingWithJump(BitBoard bitBoard, Color color, long myKings) {
    return JumpMoveGenerator.getSingleJumps(bitBoard, color).stream()
        .filter(m -> (BitUtil.getBitAt(m.getOrigin()) & myKings) != 0)
        .flatMap(m -> nextSimpleMoves(m, bitBoard, color).stream())
        .collect(Collectors.toSet());
  }

  private static Set<Move> generateMovesStartingWithSimpleMove(BitBoard bitBoard, Color color, long myKings) {
    return SimpleMoveGenerator.generate(bitBoard, color).stream()
        .filter(m -> (BitUtil.getBitAt(m.getOrigin()) & myKings) != 0)
        .flatMap(m -> nextSimpleMoves(m, bitBoard, color).stream())
        .flatMap(m -> nextJumps(m, bitBoard, color).stream())
        .collect(Collectors.toSet());
  }

  private static Set<Move> nextSimpleMoves(Move previous, BitBoard bitBoard, Color color) {
    BitBoard movedBoard = bitBoard.move(previous);

    Set<Move> newMoves = SimpleMoveGenerator.generate(movedBoard, color).stream()
        .filter(newMove -> newMove.getOrigin() == previous.getDestination())
        .filter(newMove -> BitUtil.sameDiagonal(previous, newMove))
        .filter(newMove -> BitUtil.sameDirection(previous, newMove))
        .map(newMove -> mergeMove(previous, newMove, color))
        .collect(Collectors.toSet());
    
    if (newMoves.isEmpty()) {
      return Collections.singleton(previous);
    }

    Set<Move> newerMoves = newMoves.stream()
        .map(newMove -> nextSimpleMoves(newMove, bitBoard, color))
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
    
    newerMoves.add(previous);
    return newerMoves;
  }

  private static Move mergeMove(Move oldMove, Move newMove, Color color) {
    if (oldMove instanceof SimpleMove && newMove instanceof SimpleMove) {
      return new SimpleMove(oldMove.getOrigin(), newMove.getDestination(), color);
    } else if (oldMove instanceof SingleJumpMove && newMove instanceof SimpleMove) {
      return new SingleJumpMove(oldMove.getOrigin(), newMove.getDestination(), color, ((SingleJumpMove) oldMove).getPieceTaken());
    } else if (oldMove instanceof SimpleMove && newMove instanceof SingleJumpMove) {
      return new SingleJumpMove(oldMove.getOrigin(), newMove.getDestination(), color, ((SingleJumpMove) newMove).getPieceTaken());
    }
    
    throw new UnsupportedOperationException();
  }

  private static Set<Move> nextJumps(Move previous, BitBoard bitBoard, Color color) {
    BitBoard movedBoard = bitBoard.move(previous);

    Set<Move> jumps = JumpMoveGenerator.getSingleJumps(movedBoard, color).stream()
        .filter(jump -> previous.getDestination() == jump.getOrigin())
        .filter(jump -> BitUtil.sameDiagonal(previous, jump))
        .filter(jump -> BitUtil.sameDirection(previous, jump))
        .map(jump -> mergeMove(previous, jump, color))
        .flatMap(move -> nextSimpleMoves(move, bitBoard, color).stream())
        .collect(Collectors.toSet());
    
    return jumps;
  }
}
