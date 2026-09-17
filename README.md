🎮 Connect 4 AI

A desktop-based Connect Four game built with Java and JavaFX, featuring an intelligent AI opponent powered by Minimax with Alpha-Beta Pruning.

The AI evaluates possible moves using a depth-5 search and processes candidate moves concurrently using multiple threads.

✨ Features

* 🎮 Human vs AI gameplay
* 🤖 Minimax-based AI
* ⚡ Alpha-Beta Pruning for optimized search
* 🧠 Depth-5 game-tree search
* 🔄 Multithreaded AI move evaluation
* 🏆 Win and draw detection
* 📊 Player and AI score tracking
* 🎨 Modern JavaFX user interface
* 🔁 New Game option
* 🚫 Prevents invalid moves when a column is full
* 🖥️ 6 × 7 standard Connect Four board

🛠️ Tech Stack

* Java 21
* JavaFX 21.0.2
* Maven
* Minimax Algorithm
* Alpha-Beta Pruning
* Multithreading / ExecutorService

🧠 AI Logic

The AI uses the Minimax algorithm to select moves by exploring possible future game states.

Alpha-Beta Pruning

Alpha-Beta Pruning reduces unnecessary branches of the Minimax search tree, allowing the AI to evaluate moves more efficiently.

Search Depth

The current AI searches up to depth 5.

Multithreading

Each valid AI move is evaluated independently using an ExecutorService with multiple worker threads. This allows different candidate moves to be processed concurrently.

📂 Project Structure

Connect4AI/
├── pom.xml
└── src/
    └── main/
        └── java/
            ├── Main.java
            ├── Board.java
            ├── AIPlayer.java
            └── Connect4UI.java

Main Components

Board.java

* Maintains the 6 × 7 game board
* Handles piece placement
* Validates moves
* Detects wins
* Detects a full board
* Creates board copies for AI simulation

AIPlayer.java

* Implements the AI
* Uses Minimax
* Uses Alpha-Beta Pruning
* Evaluates board positions
* Uses multithreading for move evaluation

Connect4UI.java

* JavaFX graphical interface
* Handles player interaction
* Displays the game board
* Shows scores and game status
* Runs AI calculations without blocking the UI

Main.java

* Provides the console-based game entry point

🚀 How to Run

Prerequisites

Install:

* Java 21 or later
* Maven

Run with Maven

Clone the repository:

git clone <YOUR_REPOSITORY_URL>
cd Connect4AI

Then run:

mvn javafx:run

The JavaFX application will launch.

🎯 How to Play

1. Start the application.
2. Choose a column using the arrow buttons.
3. Your piece is placed in the selected column.
4. The AI calculates its move.
5. The AI places its piece automatically.
6. Connect four pieces horizontally, vertically, or diagonally to win.

🏗️ Architecture

Player Move
     ↓
Board
     ↓
AIPlayer
     ↓
Valid Moves
     ↓
Parallel Move Evaluation
     ↓
Minimax + Alpha-Beta Pruning
     ↓
Board Evaluation
     ↓
Best Move
     ↓
JavaFX UI

📌 Key Learning Outcomes

This project demonstrates practical implementation of:

* Object-Oriented Programming in Java
* Game development fundamentals
* Artificial Intelligence
* Minimax decision making
* Alpha-Beta optimization
* Multithreading
* JavaFX GUI development
* Maven project management
* Game-state simulation and evaluation

🔮 Future Improvements

* Adjustable AI difficulty
* Configurable search depth
* Move history
* Sound effects
* Improved animations
* AI statistics and search visualization
* Transposition tables for faster AI search

👨‍💻 Author

Sumit Kumar

B.Tech CSE (AI & ML)

⸻

⭐ If you find this project interesting, consider giving the repository a star!
