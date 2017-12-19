package ut.veikotiit.checkers.terminal;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.bitboard.BitUtil;
import ut.veikotiit.checkers.minimax.MtdF;
import ut.veikotiit.checkers.moves.Move;

public class TerminalGame {

  private BitBoard bitBoard = BitBoard.create(0b11111111111111111111L, 0b11111111111111111111000000000000000000000000000000L);
  private final Scanner scanner = new Scanner(System.in);

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
      Move blackMove = mtdF.search(bitBoard, Color.BLACK, 13);
      if (blackMove == null) {
        System.out.println("No legal moves for black");
        break;
      }
      bitBoard = bitBoard.move(blackMove);
      Thread.sleep(20);
      print();
      Move whiteMove = mtdF.search(bitBoard, Color.WHITE, 13);
      if (whiteMove == null) {
        System.out.println("No legal moves for white");
        break;
      }
      bitBoard = bitBoard.move(whiteMove);
      Thread.sleep(200);
    }
  }

  private void print() {
    List<Integer> blacks = Arrays.stream(BitUtil.longToBits(bitBoard.getBlacks())).boxed().collect(Collectors.toList());
    List<Integer> whites = Arrays.stream(BitUtil.longToBits(bitBoard.getWhites())).boxed().collect(Collectors.toList());

    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        if ((i + j) % 2 == 1) {
          int index = (5 * i) + (j / 2);
          if (blacks.contains(index)) {
            System.out.print(" B ");
          }
          else if (whites.contains(index)) {
            System.out.print(" W ");
          }
          else {
            System.out.print(" x ");
          }
        }
        else {
          System.out.print("   ");
        }
      }
      System.out.println();
    }
    System.out.println();
    System.out.println();
  }
}
