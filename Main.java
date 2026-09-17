import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Board board = new Board();
        AIPlayer ai = new AIPlayer();

        // System.out.println(board.getValidMoves());
        Scanner sc = new Scanner(System.in);

        int currentPlayer = 1;

        while (true) {

            board.printBoard();

            System.out.println("Player " + currentPlayer +
                    " Enter Column (0-6): ");

            int col;
            if (currentPlayer == 1) {
                System.out.println("Enter Column (0-6): ");
                col = sc.nextInt();
            } else {
                col = ai.getMove(board);
                System.out.println(
                    "AI chose column: " + col
                );
            }

            if (!board.isValidMove(col)) {

                System.out.println("Invalid Move!");
                continue;
            }

            board.dropPiece(col, currentPlayer);

            if (board.checkWin(currentPlayer)) {

                board.printBoard();

                System.out.println(
                        "Player " + currentPlayer + " Wins!"
                );

                break;
            }

            if (board.isBoardFull()) {

                board.printBoard();

                System.out.println("Game Draw!");

                break;
            }

            currentPlayer = (currentPlayer == 1) ? 2 : 1;
        }

        sc.close();
    }
}