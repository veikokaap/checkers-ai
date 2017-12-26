package ut.veikotiit.checkers.terminal;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.bitboard.BitUtil;
import ut.veikotiit.checkers.minimax.MtdF;
import ut.veikotiit.checkers.moves.Move;
import ut.veikotiit.checkers.moves.MultiJumpMove;
import ut.veikotiit.checkers.moves.SingleJumpMove;

public class TerminalGame {

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  public static final String ANSI_BLUE = "\u001B[34m";

  private BitBoard bitBoard = BitBoard.create(0b11111111111111111111L, 0b11111111111111111111000000000000000000000000000000L, 0L);

  public void play() {
    System.out.println("Welcome");
    try {
      startGame();
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void startGame() throws InterruptedException {
    MtdF mtdF = new MtdF();
    while (true) {
      print();
      Move blackMove = mtdF.search(bitBoard, Color.BLACK, 10);
      if (blackMove == null) {
        System.out.println("No legal moves for black");
        break;
      }
      bitBoard = bitBoard.move(blackMove);
//      Thread.sleep(2000);
      print();
      Move whiteMove = mtdF.search(bitBoard, Color.WHITE, 10);
      if (whiteMove == null) {
        System.out.println("No legal moves for white");
        break;
      }
      bitBoard = bitBoard.move(whiteMove);
//      Thread.sleep(2000);
    }
  }

  private void print() {
    List<Integer> blacks = Arrays.stream(BitUtil.longToBits(bitBoard.getBlacks())).boxed().collect(Collectors.toList());
    List<Integer> blackKings = Arrays.stream(BitUtil.longToBits(bitBoard.getBlackKings())).boxed().collect(Collectors.toList());
    List<Integer> whites = Arrays.stream(BitUtil.longToBits(bitBoard.getWhites())).boxed().collect(Collectors.toList());
    List<Integer> whiteKings = Arrays.stream(BitUtil.longToBits(bitBoard.getWhiteKings())).boxed().collect(Collectors.toList());

    System.out.println("+------------------------------+");
    for (int i = 0; i < 10; i++) {
      System.out.print("|");
      for (int j = 0; j < 10; j++) {
        if ((i + j) % 2 == 1) {
          int index = (5 * i) + (j / 2);
          if (blackKings.contains(index)) {
            System.out.print(ANSI_BLUE + " B " + ANSI_RESET);
          }
          else if (blacks.contains(index) ) {
            System.out.print(ANSI_BLUE + " b " + ANSI_RESET);
          }
          else if (whiteKings.contains(index)) {
            System.out.print(ANSI_GREEN + " W " + ANSI_RESET);
          }
          else if (whites.contains(index)) {
            System.out.print(ANSI_GREEN + " w " + ANSI_RESET);
          }
          else {
            printEmpty(index);
          }
        }
        else {
          System.out.print("   ");
        }
      }
      System.out.println("|");
    }
    System.out.println("+------------------------------+");
    if (bitBoard.getMove() != null) {
      System.out.println("Move: " + bitBoard.getMove());
      System.out.println("Score (" + bitBoard.getMove().getColor() + "): " + bitBoard.getScore(bitBoard.getMove().getColor()));
    }
    System.out.println();
  }

  private void printEmpty(int index) {
    if (bitBoard.getMove() instanceof SingleJumpMove) {
      if (((SingleJumpMove) bitBoard.getMove()).getPieceTaken() == index) {
        System.out.print(ANSI_RED + " x " + ANSI_RESET);
        return;
      }
    }
    else if (bitBoard.getMove() instanceof MultiJumpMove) {
      if (((MultiJumpMove) bitBoard.getMove()).takesPiece(index)) {
        System.out.print(ANSI_RED + " x " + ANSI_RESET);
        return;
      }
    }

    System.out.print(" · ");
  }
}
