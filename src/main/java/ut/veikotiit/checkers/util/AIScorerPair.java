package ut.veikotiit.checkers.util;

import ut.veikotiit.checkers.scorer.BitBoardScorer;

public class AIScorerPair {
  private final BitBoardScorer white;
  private final BitBoardScorer black;

  private AIScorerPair(BitBoardScorer white, BitBoardScorer black) {
    this.white = white;
    this.black = black;
  }

  public static AIScorerPair of(BitBoardScorer white, BitBoardScorer black) {
    return new AIScorerPair(white, black);
  }

  public static AIScorerPair flip(AIScorerPair aiScorerPair) {
    return new AIScorerPair(aiScorerPair.black, aiScorerPair.white);
  }

  public BitBoardScorer getWhite() {
    return white;
  }

  public BitBoardScorer getBlack() {
    return black;
  }

  @Override
  public String toString() {
    return "AIScorerPair{" +
            "white=" + white +
            ", black=" + black +
            '}';
  }
}
