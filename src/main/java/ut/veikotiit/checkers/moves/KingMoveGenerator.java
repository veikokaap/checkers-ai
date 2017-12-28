package ut.veikotiit.checkers.moves;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.bitboard.BitUtil;

public class KingMoveGenerator {
    
  public static Set<SimpleMove> getSimpleMoves(BitBoard bitBoard, Color color) {
    return generateSimpleMoves(bitBoard, color, bitBoard.getPlayerKings(color));
  }
  
  public static Set<SingleJumpMove> getJumps(BitBoard bitBoard, Color color) {
    long myKings = bitBoard.getPlayerKings(color);

    Set<SingleJumpMove> moves = generateMovesStartingWithSimpleMove(bitBoard, color, myKings);
    moves.addAll(generateMovesStartingWithJump(bitBoard, color, myKings));

    return moves;
  }

  private static Set<SingleJumpMove> generateMovesStartingWithJump(BitBoard bitBoard, Color color, long myKings) {
    return JumpMoveGenerator.getSingleJumps(bitBoard, color).stream()
        .filter(m -> (BitUtil.getBitAt(m.getOrigin()) & myKings) != 0)
        .flatMap(m -> nextSimpleMoves(m, bitBoard, color).stream())
        .map(m -> (SingleJumpMove) m)
        .collect(Collectors.toSet());
  }
  
  private static Set<SimpleMove> generateSimpleMoves(BitBoard bitBoard, Color color, long myKings) {
    return SimpleMoveGenerator.generate(bitBoard, color).stream()
        .filter(m -> (BitUtil.getBitAt(m.getOrigin()) & myKings) != 0)
        .flatMap(m -> nextSimpleMoves(m, bitBoard, color).stream())
        .map(move -> (SimpleMove) move)
        .collect(Collectors.toSet());
  }

  private static Set<SingleJumpMove> generateMovesStartingWithSimpleMove(BitBoard bitBoard, Color color, long myKings) {
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
    }
    else if (oldMove instanceof SingleJumpMove && newMove instanceof SimpleMove) {
      return new SingleJumpMove(oldMove.getOrigin(), newMove.getDestination(), color, ((SingleJumpMove) oldMove).getPieceTaken());
    }
    else if (oldMove instanceof SimpleMove && newMove instanceof SingleJumpMove) {
      return new SingleJumpMove(oldMove.getOrigin(), newMove.getDestination(), color, ((SingleJumpMove) newMove).getPieceTaken());
    }

    throw new UnsupportedOperationException();
  }

  private static Set<SingleJumpMove> nextJumps(Move previous, BitBoard bitBoard, Color color) {
    BitBoard movedBoard = bitBoard.move(previous);

    return JumpMoveGenerator.getSingleJumps(movedBoard, color).stream()
        .filter(jump -> previous.getDestination() == jump.getOrigin())
        .filter(jump -> BitUtil.sameDiagonal(previous, jump))
        .filter(jump -> BitUtil.sameDirection(previous, jump))
        .map(jump -> mergeMove(previous, jump, color))
        .flatMap(move -> nextSimpleMoves(move, bitBoard, color).stream())
        .map(move -> (SingleJumpMove) move)
        .collect(Collectors.toSet());
  }
}
