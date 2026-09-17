import java.util.concurrent.*;
import java.util.*;

public class AIPlayer {

    private static final int SEARCH_DEPTH = 5;

    private static final int HUMAN = 1;
    private static final int AI = 2;

    private Random random = new Random();

    public int getMove(Board board) {

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<int[]>> futures = new ArrayList<>();

        for (int col : board.getValidMoves()) {
            Future<int[]> future = executor.submit(() -> {
            
                Board copyBoard = board.copy();
                copyBoard.dropPiece(col, AI);
                int score = minimax(copyBoard, SEARCH_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

                return new int[]{col, score};
            });
            futures.add(future);
        } 

        int bestScore = Integer.MIN_VALUE;
        int bestMove = board.getValidMoves().get(0);

        try {
            for (Future<int[]> future : futures) {
                int[] result = future.get();

                int col = result[0];
                int score = result[1];

                if (score > bestScore) {

                   bestScore = score;
                   bestMove = col;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        executor.shutdown();

        return bestMove;
    }

    private int evaluateWindow(int aiCount, int humanCount, int emptyCount) {
        if (aiCount == 4) return 100000;
        if (aiCount == 3 && emptyCount == 1) return 100;
        if (aiCount == 2 && emptyCount == 2) return 10;

        if (humanCount == 3 && emptyCount == 1) return -120;
        if (humanCount == 4) return -100000;

        return 0;
    }

    private int minimax(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {

        if (depth == 0 || board.checkWin(AI) || board.checkWin(HUMAN) || board.isBoardFull()) {
            return evaluateBoard(board);

        }
        if (maximizingPlayer) {

            int bestScore = Integer.MIN_VALUE;

            for (int col : board.getValidMoves()) {

                Board copyBoard = board.copy();

                copyBoard.dropPiece(col, AI);

                int score = minimax(copyBoard, depth - 1, alpha, beta, false);

                bestScore = Math.max(bestScore, score);
                alpha = Math.max(alpha, bestScore);

                if (beta <= alpha) {
                    break;
                }

            }

            return bestScore;
        }
        else {
            int bestScore = Integer.MAX_VALUE;

            for (int col : board.getValidMoves()) {

                Board copyBoard = board.copy();
                copyBoard.dropPiece(col, HUMAN);

                int score = minimax(copyBoard, depth - 1, alpha, beta, true);

                bestScore = Math.min(bestScore, score);
                beta = Math.min(beta, bestScore);

                if (beta <= alpha) {
                    break;
                }

            }
            return bestScore;
        }
    }

    private int evaluateBoard(Board board) {

    int score = 0;

    // Horizontal
    for (int r = 0; r < Board.ROWS; r++) {

        for (int c = 0; c <= Board.COLS - 4; c++) {

            int aiCount = 0;
            int humanCount = 0;
            int emptyCount = 0;

            for (int i = 0; i < 4; i++) {

                int cell = board.getCell(r, c + i);

                if (cell == AI)
                    aiCount++;

                else if (cell == HUMAN)
                    humanCount++;

                    else
                        emptyCount++;
                    }

                score += evaluateWindow(
                    aiCount,
                    humanCount,
                    emptyCount
                );
            }
        }
        // Vertical
        for (int c = 0; c < Board.COLS; c++) {

            for (int r = 0; r <= Board.ROWS - 4; r++) {

            int aiCount = 0;
            int humanCount = 0;
            int emptyCount = 0;

            for (int i = 0; i < 4; i++) {

                int cell = board.getCell(r + i, c);

                if (cell == AI)
                aiCount++;
                else if (cell == HUMAN)
                humanCount++;
                else
                emptyCount++;
            }

            score += evaluateWindow(
                aiCount,
                humanCount,
                emptyCount
            );
            }
        }

        // Diagonal ↘
        for (int r = 0; r <= Board.ROWS - 4; r++) {

            for (int c = 0; c <= Board.COLS - 4; c++) {

                int aiCount = 0;
                int humanCount = 0;
                int emptyCount = 0;

                for (int i = 0; i < 4; i++) {

                    int cell = board.getCell(
                       r + i,
                       c + i
                    );

                    if (cell == AI)
                        aiCount++;
                    else if (cell == HUMAN)
                        humanCount++;
                    else
                        emptyCount++;
                }

                score += evaluateWindow(
                    aiCount,
                    humanCount,
                    emptyCount
                );
            }
        }

        // Diagonal ↗
        for (int r = 3; r < Board.ROWS; r++) {

            for (int c = 0; c <= Board.COLS - 4; c++) {

                int aiCount = 0;
                int humanCount = 0;
                int emptyCount = 0;

                for (int i = 0; i < 4; i++) {

                    int cell = board.getCell(
                        r - i,
                        c + i
                    );

                    if (cell == AI)
                        aiCount++;
                    else if (cell == HUMAN)
                        humanCount++;
                    else
                        emptyCount++;
                }
                score += evaluateWindow(
                    aiCount,
                    humanCount,
                    emptyCount
                );
            }
        }
        return score;
    }
}
