import java.util.ArrayList;
import java.util.List;

public class Board {

    public List<Integer> getValidMoves() {

        List<Integer> moves = new ArrayList<>();

        for (int col = 0; col < COLS; col++) {

            if (isValidMove(col)) {
              moves.add(col);
            }
        }
        return moves;
    }

    public Board copy() {

        Board newBoard = new Board();

        for (int r = 0; r < ROWS; r++) {

            for (int c = 0; c < COLS; c++) {

                newBoard.board[r][c] = this.board[r][c];
            }
        }
        return newBoard;
    }

    public int getCell(int row, int col) {
        return board[row][col];
    }

    public static final int ROWS = 6;
    public static final int COLS = 7;

    private int[][] board;

    public Board() {
        board = new int[ROWS][COLS];
    }

    public void printBoard() {
        System.out.println();

        for (int r = 0; r < ROWS; r++) {
            System.out.print("| ");
            for (int c = 0; c < COLS; c++) {
                if (board[r][c] == 0) {
                    System.out.print(". ");
                } else if (board[r][c] == 1) {
                    System.out.print("X ");
                } else {
                    System.out.print("O ");
                }
            }
            System.out.println("|");
        }

        System.out.println("  0 1 2 3 4 5 6");
        System.out.println();
    }

    public boolean dropPiece(int col, int player) {
    if (col < 0 || col >= COLS) {
        return false;
    }

    for (int r = ROWS - 1; r >= 0; r--) {
        if (board[r][col] == 0) {
            board[r][col] = player;
            return true;
        }
    }
    return false;
   }

    public boolean isValidMove(int col) {
        if (col < 0 || col >= COLS) {
         return false;
        }

    return board[0][col] == 0;
    }

    public boolean isBoardFull() {
    for (int col = 0; col < COLS; col++) {
        if (board[0][col] == 0) {
            return false;
        }
    }

     return true;
    }

    public boolean checkWin(int player) {

    // Horizontal
    for (int r = 0; r < ROWS; r++) {

        for (int c = 0; c <= COLS - 4; c++) {

            if (board[r][c] == player &&
                board[r][c + 1] == player &&
                board[r][c + 2] == player &&
                board[r][c + 3] == player) {

                return true;
            }
        }
    }
    // Vertical
    for (int c = 0; c < COLS; c++) {

        for (int r = 0; r <= ROWS - 4; r++) {

            if (board[r][c] == player &&
               board[r + 1][c] == player &&
               board[r + 2][c] == player &&
               board[r + 3][c] == player) {

             return true;
            }
        }
    }
    // Diagonal ↘
    for (int r = 0; r <= ROWS - 4; r++) {

        for (int c = 0; c <= COLS - 4; c++) {

            if (board[r][c] == player &&
               board[r + 1][c + 1] == player &&
               board[r + 2][c + 2] == player &&
               board[r + 3][c + 3] == player) {

               return true;
            }
        }
    }
    // Diagonal ↗
    for (int r = 3; r < ROWS; r++) {

        for (int c = 0; c <= COLS - 4; c++) {

            if (board[r][c] == player &&

               board[r - 1][c + 1] == player &&

               board[r - 2][c + 2] == player &&

               board[r - 3][c + 3] == player) {

               return true;
            }
        }
    }

    return false;
    } 
}
