package ut.veikotiit.checkers.bitboard;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import ut.veikotiit.checkers.moves.Move;
import ut.veikotiit.checkers.moves.MultiJumpMove;
import ut.veikotiit.checkers.moves.SimpleMove;

public class BitUtil {
  public static final long ALL_BITS = 0b11111111111111111111111111111111111111111111111111L; // 50 bits set to 1. Alternative is to write 2^51-1.
  private static final long[] BITS = new long[50];
  static {
    BITS[0] = 0b1L;
    for (int i = 1; i < 50; i++) {
      BITS[i] = BITS[i - 1] * 2;
    }
  }

  private static final List<Long> DIAGONALS = Arrays.asList(
      BITS[3] | BITS[9] | BITS[14],
      BITS[2] | BITS[8] | BITS[13] | BITS[19] | BITS[24],
      BITS[1] | BITS[7] | BITS[12] | BITS[18] | BITS[23] | BITS[29] | BITS[34],
      BITS[0] | BITS[6] | BITS[11] | BITS[17] | BITS[22] | BITS[28] | BITS[33] | BITS[39] | BITS[44],
      BITS[5] | BITS[10] | BITS[16] | BITS[21] | BITS[27] | BITS[32] | BITS[38] | BITS[43] | BITS[49],
      BITS[15] | BITS[20] | BITS[26] | BITS[31] | BITS[37] | BITS[42] | BITS[48],
      BITS[25] | BITS[30] | BITS[36] | BITS[41] | BITS[47],
      BITS[35] | BITS[40] | BITS[46],
      BITS[0] | BITS[5],
      BITS[1] | BITS[6] | BITS[10] | BITS[15],
      BITS[2] | BITS[7] | BITS[11] | BITS[16] | BITS[20] | BITS[25],
      BITS[3] | BITS[8] | BITS[12] | BITS[17] | BITS[21] | BITS[26] | BITS[30] | BITS[35],
      BITS[4] | BITS[9] | BITS[13] | BITS[18] | BITS[22] | BITS[27] | BITS[31] | BITS[36] | BITS[40] | BITS[45],
      BITS[14] | BITS[19] | BITS[23] | BITS[28] | BITS[32] | BITS[37] | BITS[41] | BITS[46],
      BITS[24] | BITS[29] | BITS[33] | BITS[38] | BITS[42] | BITS[47],
      BITS[34] | BITS[49] | BITS[43] | BITS[48],
      BITS[44] | BITS[49]
  );
  
  //squares to where -4 is legal move for white
  public static final long MASK_L4 = BITS[6] | BITS[7] | BITS[8] | BITS[9]
      | BITS[16] | BITS[17] | BITS[18] | BITS[19]
      | BITS[26] | BITS[27] | BITS[28] | BITS[29]
      | BITS[36] | BITS[37] | BITS[38] | BITS[39];

  //squares to where +4 is legal move for black
  public static final long MASK_R4 = BITS[10] | BITS[11] | BITS[12] | BITS[13]
      | BITS[20] | BITS[21] | BITS[22] | BITS[23]
      | BITS[30] | BITS[31] | BITS[32] | BITS[33]
      | BITS[40] | BITS[41] | BITS[42] | BITS[43];

  //squares to where -6 is legal move for white
  public static final long MASK_L6 = MASK_R4
      | BITS[0] | BITS[1] | BITS[2] | BITS[3];

  //squares to where +6 is legal move for white
  public static final long MASK_R6 = MASK_L4
      | BITS[46] | BITS[47] | BITS[48] | BITS[49];

  public static final long MASK_ROW_10 = BITS[45] | BITS[46] | BITS[47] | BITS[48] | BITS[49];
  public static final long MASK_ROW_1 = BITS[0] | BITS[1] | BITS[2] | BITS[3] | BITS[4];

  public static long getRowMask(int row) {
    int rowStartIndex = (row - 1) * 5;
    return BITS[rowStartIndex] | BITS[rowStartIndex + 1] | BITS[rowStartIndex + 2] | BITS[rowStartIndex + 3] | BITS[rowStartIndex + 4];
  }

  
  public static long getBitAt(int i) {
    return BITS[i];
  }

  public static int[] longToBits(long board) {
    return IntStream.range(0, 50)
        .filter(i -> (board & BITS[i]) == BITS[i])
        .toArray();
  }
  
  public static boolean sameDiagonal(Move move1, Move move2) {
    return getDiagonal(move1).equals(getDiagonal(move2));
  }

  public static boolean sameDiagonal(Move move, long diagonal) {
    Optional<Long> diagonalOptional = getDiagonal(move);
    return diagonalOptional.isPresent() && diagonalOptional.get() == diagonal;
  }


  private static Optional<Long> getDiagonal(Move move) {
    return DIAGONALS.stream()
        .filter(d -> Long.bitCount((BITS[move.getOrigin()] | BITS[move.getDestination()]) & d) == 2)
        .findFirst();
  }

  public static boolean sameDirection(Move move1, Move move2) {
    int direction1 = move1.getDestination() - move1.getOrigin();
    int direction2 = move2.getDestination() - move2.getOrigin();
    
    return direction1 * direction2 > 0;
  }
}
