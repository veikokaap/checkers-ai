package ut.veikotiit.checkers.transposition;

import java.util.HashMap;
import java.util.Map;

import ut.veikotiit.checkers.bitboard.BitBoard;

public class TranspositionTable {
  private final Map<BitBoard, CachedValue> map = new HashMap<>();

  public void put(BitBoard board, CachedValue cachedValue) {
    map.put(board, cachedValue);
  }

  public CachedValue get(BitBoard bitBoard) {
    return map.get(bitBoard);
  }
}
