package ut.veikotiit.checkers;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import ut.veikotiit.checkers.game.AutomatedNoUIGame;
import ut.veikotiit.checkers.game.GameResult;
import ut.veikotiit.checkers.scorer.BitBoardScorer;
import ut.veikotiit.checkers.scorer.BoardPlacementWeightBitBoardScorer;
import ut.veikotiit.checkers.scorer.DefaultBitBoardScorer;
import ut.veikotiit.checkers.scorer.WeightedBitBoardScorer;
import ut.veikotiit.checkers.util.AIScorerPair;

import java.util.*;

public class CheckersAITournament {

  /**
   * Determine how many games each AI scorer combination will play
   */
  private static final int GAME_COUNT = 100;

  // TODO: 2.01.18 Dependency injection?
  private static final List<BitBoardScorer> scorers = Arrays.asList(
          DefaultBitBoardScorer.getInstance(),
          WeightedBitBoardScorer.getInstance(),
          BoardPlacementWeightBitBoardScorer.getInstance()
  );

  private final Map<AIScorerPair, MultiSet<GameResult>> results = new HashMap<>();

  public static void main(String[] args) {
    CheckersAITournament checkersAITournament = new CheckersAITournament();

    List<AIScorerPair> list = checkersAITournament.getAllScorerPairCombinations();
    for (AIScorerPair aiScorerPair : list) {
      System.out.println(aiScorerPair);
    }
    for (AIScorerPair aiScorerPair : list) {
      for (int i = 0; i < GAME_COUNT; i++) {
        checkersAITournament.results.computeIfAbsent(aiScorerPair, x -> new HashMultiSet<>())
                .add(new AutomatedNoUIGame(aiScorerPair.getWhite(), aiScorerPair.getBlack()).play());
      }
    }
    checkersAITournament.prettyPrintResults();
  }

  private List<AIScorerPair> getAllScorerPairCombinations() {
    List<AIScorerPair> combinations = new ArrayList<>();

    for (int i = 0; i < scorers.size(); i++) {
      for (int j = i; j < scorers.size(); j++) {
        BitBoardScorer blackScorer = scorers.get(j);
        BitBoardScorer whiteScorer = scorers.get(i);
        AIScorerPair aiScorerPair = AIScorerPair.of(whiteScorer, blackScorer);
        combinations.add(aiScorerPair);
        if (whiteScorer != blackScorer)
          combinations.add(AIScorerPair.flip(aiScorerPair));
      }
    }
    return combinations;
  }

  private void prettyPrintResults() {
    for (Map.Entry<AIScorerPair, MultiSet<GameResult>> resultPair : results.entrySet()) {
      AIScorerPair aiScorerPair = resultPair.getKey();
      MultiSet<GameResult> results = resultPair.getValue();
      System.out.format("Black: %s - White: %s %10s\n",
              aiScorerPair.getBlack().getClass().getSimpleName(),
              aiScorerPair.getWhite().getClass().getSimpleName(),
              results
      );
    }
  }


}
