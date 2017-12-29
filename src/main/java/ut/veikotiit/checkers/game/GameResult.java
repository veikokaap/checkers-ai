package ut.veikotiit.checkers.game;

import ut.veikotiit.checkers.Color;

public enum GameResult {
  DRAW(null),
  BLACK_WIN(Color.BLACK),
  WHITE_WIN(Color.WHITE);

  private final Color winner;

  GameResult(Color winner) {
    this.winner = winner;
  }

  public Color getWinner() {
    return winner;
  }
}
