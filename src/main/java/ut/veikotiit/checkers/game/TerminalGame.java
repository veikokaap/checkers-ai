package ut.veikotiit.checkers.game;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialog;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.bitboard.BitUtil;
import ut.veikotiit.checkers.minimax.MtdF;
import ut.veikotiit.checkers.moves.Move;
import ut.veikotiit.checkers.moves.MultiJumpMove;
import ut.veikotiit.checkers.moves.SingleJumpMove;
import ut.veikotiit.checkers.scorer.DefaultBitBoardScorer;

public class TerminalGame implements Game {

  private final HashMap<BitBoard, AtomicInteger> whiteBitboardStateCounters = new HashMap<>();
  private final HashMap<BitBoard, AtomicInteger> blackBitboardStateCounters = new HashMap<>();

  private BitBoard bitBoard = BitBoard.create(0b11111111111111111111L, 0b11111111111111111111000000000000000000000000000000L, 0L);

  private Color player = null;
  private Terminal terminal;
  private int selection = -1;
  private Map<Integer, Move> rowMoveMap = new TreeMap<>();

  public TerminalGame() {
    DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
  }

  private void print(String s) {
    s.chars().forEach(c -> {
      try {
        terminal.putCharacter((char) c);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  private void println() {
    try {
      terminal.putCharacter('\n');
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void println(String s) {
    try {
      s.chars().forEach(c -> {
        try {
          terminal.putCharacter((char) c);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      });
      terminal.putCharacter('\n');
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void askToPlay() throws IOException {
    Screen screen = new TerminalScreen(terminal);
    screen.startScreen();

    // Setup WindowBasedTextGUI for dialogs
    final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

    ActionListDialog playerQuestion = new ActionListDialogBuilder()
        .setTitle("Do you want to play yourself or let AI play?")
        .addAction("Play with Whites", () -> player = Color.WHITE)
        .addAction("Play with Blacks", () -> player = Color.BLACK)
        .addAction("Let AI play", () -> player = null)
        .build();

    playerQuestion.showDialog(textGUI);
    terminal.flush();
  }

  @Override
  public GameResult play() {
    DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
    defaultTerminalFactory.setTerminalEmulatorFontConfiguration(SwingTerminalFontConfiguration.newInstance(new Font("Monospaced", Font.PLAIN, 18)));
    GameResult result;
    try {
      terminal = defaultTerminalFactory.createTerminal();
      if (terminal instanceof SwingTerminalFrame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        ((SwingTerminalFrame) terminal).setSize((int)(screenSize.getWidth() * 0.8), (int)(screenSize.getHeight() * 0.8));
      }
//      terminal.enterPrivateMode();
      askToPlay();
      result = startGame();
      println("Press Enter key to exit");
      terminal.flush();
      while (true) {
        KeyStroke keyStroke = terminal.readInput();
        if (keyStroke.getKeyType().equals(KeyType.Enter)) {
          terminal.close();
          return result;
        }
      }
//      terminal.exitPrivateMode();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private GameResult startGame() throws InterruptedException, IOException {
    MtdF mtdF = new MtdF(50);
    while (true) {
      if (move(mtdF, Color.WHITE, "Black(green) won!")) {
        return GameResult.BLACK_WIN;
      }
      if (sameBoardThirdTime(whiteBitboardStateCounters)) {
        return GameResult.DRAW;
      }

      if (move(mtdF, Color.BLACK, "White(blue) won!")) {
        return GameResult.WHITE_WIN;
      }
      if (sameBoardThirdTime(blackBitboardStateCounters)) {
        return GameResult.DRAW;
      }
    }
  }

  private boolean sameBoardThirdTime(HashMap<BitBoard, AtomicInteger> boardStateCounters) {
    if (!boardStateCounters.containsKey(bitBoard)) {
      boardStateCounters.put(bitBoard, new AtomicInteger(0));
    }
    int counter = boardStateCounters.get(bitBoard).incrementAndGet();
    if (counter >= 3) {
      println("DRAW!");
      return true;
    }

    return false;
  }

  private boolean move(MtdF mtdF, Color color, String gameOverMessage) throws IOException {
    printBoard(color);
    if (color == player) {
      List<BitBoard> childBoards = bitBoard.getChildBoards();
      if (childBoards.isEmpty()) {
        println(gameOverMessage);
        terminal.flush();
        return true;
      }

      while (true) {
        KeyStroke keyStroke = terminal.readInput();
        switch (keyStroke.getKeyType()) {
          case ArrowUp:
            selectionUp();
            printBoard(color);
            break;
          case ArrowDown:
            selectionDown();
            printBoard(color);
            break;
          case Enter:
            TerminalPosition cursorPosition = terminal.getCursorPosition();
            if (rowMoveMap.containsKey(cursorPosition.getRow())) {
              bitBoard = bitBoard.move(rowMoveMap.get(cursorPosition.getRow()));
              rowMoveMap.clear();
              selection = -1;
              return false;
            }
        }
      }
    }
    else {
      Move whiteMove = mtdF.search(bitBoard, color, 100, DefaultBitBoardScorer.getInstance());
      if (whiteMove == null) {
        println(gameOverMessage);
        terminal.flush();
        return true;
      }
      bitBoard = bitBoard.move(whiteMove);
      return false;
    }
  }


  private void selectionUp() {
    Optional<Integer> up = rowMoveMap.keySet().stream()
        .sorted(Collections.reverseOrder())
        .filter(i -> i < selection)
        .findFirst();

    up.ifPresent(i -> selection = i);
    
    if (!up.isPresent()) {
      selection = rowMoveMap.keySet().stream().sorted().collect(Collectors.toList()).get(0) - 1;
    }
  }

  private void selectionDown() {
    Optional<Integer> down = rowMoveMap.keySet().stream()
        .sorted()
        .filter(i -> i > selection)
        .findFirst();
    
    down.ifPresent(i -> selection = i);
  }

  private void clearScreen() throws IOException {
    terminal.clearScreen();
    terminal.setCursorPosition(0, 0);
  }

  private void printBoard(Color color) throws IOException {
    BitBoard shownBoard = bitBoard;

    if (color == player && rowMoveMap.containsKey(selection)) {
      shownBoard = bitBoard.move(rowMoveMap.get(selection));
    }
    
    List<Integer> blacks = Arrays.stream(BitUtil.longToBits(shownBoard.getBlacks())).boxed().collect(Collectors.toList());
    List<Integer> blackKings = Arrays.stream(BitUtil.longToBits(shownBoard.getBlackKings())).boxed().collect(Collectors.toList());
    List<Integer> whites = Arrays.stream(BitUtil.longToBits(shownBoard.getWhites())).boxed().collect(Collectors.toList());
    List<Integer> whiteKings = Arrays.stream(BitUtil.longToBits(shownBoard.getWhiteKings())).boxed().collect(Collectors.toList());

    clearScreen();
    
    println("+------------------------------+");
    for (int i = 0; i < 10; i++) {
      print("|");
      for (int j = 0; j < 10; j++) {
        print(" ");
        if ((i + j) % 2 == 1) {
          int index = (5 * i) + (j / 2);
          if (shownBoard.getPreviousMove() != null) {
            if (shownBoard.getPreviousMove().getDestination() == index) {
              terminal.enableSGR(SGR.UNDERLINE);
            }
          }
          if (blackKings.contains(index)) {
            terminal.setForegroundColor(TextColor.ANSI.GREEN);
            print("\u2605");
            terminal.resetColorAndSGR();
          }
          else if (blacks.contains(index)) {
            terminal.setForegroundColor(TextColor.ANSI.GREEN);
            print("\u25CF");
            terminal.resetColorAndSGR();
          }
          else if (whiteKings.contains(index)) {
            terminal.setForegroundColor(TextColor.ANSI.BLUE);
            print("\u2605");
            terminal.resetColorAndSGR();
          }
          else if (whites.contains(index)) {
            terminal.setForegroundColor(TextColor.ANSI.BLUE);
            print("\u25CF");
            terminal.resetColorAndSGR();
          }
          else {
            printEmpty(shownBoard, index);
          }
        }
        else {
          print(" ");
        }
        print(" ");
      }
      println("|");
    }
    println("+------------------------------+");
    println();

    if (color == player) {
      List<BitBoard> childBoards = bitBoard.getChildBoards();

      if (selection == -1) {
        selection = terminal.getCursorPosition().getRow() - 1;
      }

      for (BitBoard childBoard : childBoards) {
        rowMoveMap.put(terminal.getCursorPosition().getRow(), childBoard.getPreviousMove());
        println(" " + childBoard.getPreviousMove());
      }

      terminal.setCursorPosition(0, selection);
      print(">");
    }
    terminal.flush();
  }

  private void printMove() {
    if (bitBoard.getPreviousMove() != null) {
      if (bitBoard.getPreviousMove() instanceof MultiJumpMove && ((MultiJumpMove) bitBoard.getPreviousMove()).getJumps().size() == 1) {
        println("Move: " + ((MultiJumpMove) bitBoard.getPreviousMove()).getJumps().get(0));
      }
      else {
        println("Move: " + bitBoard.getPreviousMove());
      }
//      println("Score (" + bitBoard.getPreviousMove().getColor() + "): " + bitBoard.getScore(bitBoard.getPreviousMove().getColor()));
    }
  }

  private void printEmpty(BitBoard shownBoard, int index) throws IOException {
    if (shownBoard.getPreviousMove() instanceof SingleJumpMove) {
      if (((SingleJumpMove) shownBoard.getPreviousMove()).getPieceTaken() == index) {
        terminal.setForegroundColor(TextColor.ANSI.RED);
        print("x");
        terminal.resetColorAndSGR();
        return;
      }
    }
    else if (shownBoard.getPreviousMove() instanceof MultiJumpMove) {
      if (((MultiJumpMove) shownBoard.getPreviousMove()).takesPiece(index)) {
        terminal.setForegroundColor(TextColor.ANSI.RED);
        print("x");
        terminal.resetColorAndSGR();
        return;
      }
    }

    if (shownBoard.getPreviousMove() != null) {
      if (shownBoard.getPreviousMove().getOrigin() == index) {
        if (shownBoard.getPreviousMove().getColor() == Color.BLACK) {
          terminal.setForegroundColor(TextColor.ANSI.GREEN);
        }
        else {
          terminal.setForegroundColor(TextColor.ANSI.BLUE);
        }
        print("\u25CC");
        terminal.resetColorAndSGR();
      }
      else {
        if (shownBoard.getPreviousMove() instanceof MultiJumpMove) {
          for (SingleJumpMove singleJumpMove : ((MultiJumpMove) shownBoard.getPreviousMove()).getJumps()) {
            if (singleJumpMove.getDestination() == index) {
              terminal.enableSGR(SGR.UNDERLINE);
            }
          }
        }
        print("·");
        terminal.resetColorAndSGR();
      }
    }
    else {
      print("·");
      terminal.resetColorAndSGR();
    }
  }
}
