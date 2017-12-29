package ut.veikotiit.checkers;
import ut.veikotiit.checkers.game.TerminalGame;
import ut.veikotiit.checkers.scorer.DefaultBitBoardScorer;
import ut.veikotiit.checkers.game.AutomatedNoUIGame;

public class Checkers {
  
  public static void main(String[] args) {
    TerminalGame game = new TerminalGame();
    game.play();
//    while (true) {
//      new AutomatedNoUIGame(DefaultBitBoardScorer.getInstance(), DefaultBitBoardScorer.getInstance()).play();
//    }
  }
}
