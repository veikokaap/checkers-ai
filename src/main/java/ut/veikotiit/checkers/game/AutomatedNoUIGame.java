package ut.veikotiit.checkers.game;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.minimax.MtdF;
import ut.veikotiit.checkers.moves.Move;
import ut.veikotiit.checkers.scorer.BitBoardScorer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulates AI vs AI game without any user interface.
 */
public class AutomatedNoUIGame implements Game {

  private final Map<BitBoard, AtomicInteger> whiteBitboardStateCounters = new HashMap<>();
  private final Map<BitBoard, AtomicInteger> blackBitboardStateCounters = new HashMap<>();
  private final BitBoardScorer whiteScorer;
  private final BitBoardScorer blackScorer;
  private BitBoard bitBoard;

  public AutomatedNoUIGame(BitBoardScorer whiteScorer, BitBoardScorer blackScorer) {
    this.whiteScorer = whiteScorer;
    this.blackScorer = blackScorer;
    this.bitBoard = BitBoard.create(0b11111111111111111111L, 0b11111111111111111111000000000000000000000000000000L, 0L);
  }

  @Override
  public GameResult play() {
    MtdF mtdF = new MtdF(10);
    try {
      while (true) {
        if (move(mtdF)) {
          if (bitBoard.getNextColor() == Color.WHITE) {
            System.out.println("White has no more moves! Black(green) won!");
            return GameResult.BLACK_WIN;
          }
          else {
            System.out.println("Black has no more moves! White(blue) won!");
            return GameResult.WHITE_WIN;
          }
        }
        if (sameBoardThirdTime(whiteBitboardStateCounters)) {
          System.out.println("White has same board thrice! Game draw!");
          return GameResult.DRAW;
        }
        if (sameBoardThirdTime(blackBitboardStateCounters)) {
          System.out.println("Black has same board thrice! Game draw!");
          return GameResult.DRAW;
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private boolean sameBoardThirdTime(Map<BitBoard, AtomicInteger> boardStateCounters) {
    if (!boardStateCounters.containsKey(bitBoard)) {
      boardStateCounters.put(bitBoard, new AtomicInteger(0));
    }
    return boardStateCounters.get(bitBoard).incrementAndGet() >= 3;
  }

  private boolean move(MtdF mtdF) throws IOException {
    Move move = mtdF.search(bitBoard, 100, bitBoard.getNextColor() == Color.WHITE ? whiteScorer : blackScorer);
    if (move == null) {
      return true;
    }
    bitBoard = bitBoard.move(move);
    return false;
  }
}
