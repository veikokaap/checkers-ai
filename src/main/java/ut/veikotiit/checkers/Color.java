package ut.veikotiit.checkers;

public enum Color {
  BLACK,
  WHITE;

  public Color getOpponent() {
    return this == BLACK ? WHITE : BLACK;
  }
}
