# 🎮 Connect 4 AI

An AI-powered Connect Four game with a Minimax + Alpha-Beta Pruning opponent, available in two forms:

- **Web version** (`index.html`) — play instantly in any browser, desktop or mobile, no install needed.
- **Desktop version** (`src/`) — the original Java + JavaFX build.

Both versions share the exact same AI: a depth-5 minimax search with alpha-beta pruning.

## ▶️ Play the web version

Open `index.html` in any browser, or (once GitHub Pages is enabled on this repo) visit:
`https://S12DD.github.io/Connect-4-Ai/`

## 🖥️ Run the desktop version

Prerequisites: Java 21+, Maven

```bash
git clone https://github.com/S12DD/Connect-4-Ai.git
cd Connect-4-Ai
mvn javafx:run
```

## ✨ Features

- 🎮 Human vs AI gameplay
- 🤖 Minimax-based AI with Alpha-Beta Pruning
- 🧠 Depth-5 game-tree search
- 🏆 Win and draw detection
- 📊 Player and AI score tracking
- 🔁 New Game option
- 🖥️ 6 × 7 standard Connect Four board

## 🧠 AI Logic

The AI explores future game states with Minimax, using Alpha-Beta Pruning to cut unnecessary branches and search up to depth 5 efficiently. Each window of four cells is scored, rewarding near-wins for the AI and penalizing near-wins for the opponent.

## 🛠️ Tech Stack

- Web: HTML, CSS, vanilla JavaScript
- Desktop: Java 21, JavaFX 21.0.2, Maven

## 📂 Project Structure

```
Connect-4-Ai/
├── index.html              # Web version
├── pom.xml
└── src/
    └── main/
        └── java/
            ├── Main.java
            ├── Board.java
            ├── AIPlayer.java
            └── Connect4UI.java
```

## 👨‍💻 Author

Sumit Kumar
B.Tech CSE (AI & ML)

---
⭐ If you find this project interesting, consider giving the repository a star!
