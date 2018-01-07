package ut.veikotiit.checkers.game;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.minimax.IterativeDeepeningSearcher;
import ut.veikotiit.checkers.minimax.MtdF;
import ut.veikotiit.checkers.moves.Move;
import ut.veikotiit.checkers.scorer.BitBoardScorer;
import ut.veikotiit.checkers.transposition.TranspositionTable;

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
    this.bitBoard = BitBoard.createStartingBoard();
  }

  @Override
  public GameResult play() {
    IterativeDeepeningSearcher searcher = new IterativeDeepeningSearcher(100, 100);
    try {
      while (true) {
        if (move(searcher)) {
          System.out.println(bitBoard.toPrettyString());
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
          System.out.println(bitBoard.toPrettyString());
          System.out.println("White has same board thrice! Game draw!");
          return GameResult.DRAW;
        }
        if (sameBoardThirdTime(blackBitboardStateCounters)) {
          System.out.println(bitBoard.toPrettyString());
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

  private boolean move(IterativeDeepeningSearcher searcher) throws IOException {
    Move move = searcher.findBestMove(bitBoard, bitBoard.getNextColor() == Color.WHITE ? whiteScorer : blackScorer);
    if (move == null) {
      return true;
    }
    bitBoard = bitBoard.move(move);
    return false;
  }
}
