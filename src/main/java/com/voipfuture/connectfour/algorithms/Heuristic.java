package com.voipfuture.connectfour.algorithms;

import com.voipfuture.connectfour.Board;
import com.voipfuture.connectfour.Player;

public class Heuristic {

    private static final int PLAYER_TILE_SCORE = 10;
    private static final int FREE_TILE_SCORE = 5;
    static final int WIN_SCORE = 1000000;
    static final int DRAW_SCORE = 50000;

    /**
     * The logic of calculating score is similar to Board.getState() in terms of checking the board,
     * only returns total score instead of the boards state
     * TODO: is it possible to generalise?
     */
    public static int getScore(Board board, Player player) {
        PlayerCounter counter = new PlayerCounter(player);

        // check rows
        for (int y = 0; y < board.height; y++) {
            Player firstTile = board.get(0, y);
            counter.reset(firstTile);
            for (int x = 1; x < board.width; x++) {
                final Player currentTile = board.get(x, y);
                if (counter.hasWon(currentTile)) {
                    return counter.getTotalScore();
                }
            }
        }

        // check columns
        for (int x = 0; x < board.width; x++) {
            counter.reset(board.get(x, 0));
            for (int y = 1; y < board.height; y++) {
                final Player currentTile = board.get(x, y);
                if (counter.hasWon(currentTile)) {
                    return counter.getTotalScore();
                }
            }
        }

        // check diagonals right-down
        for (int y = 0; y < board.height; y++) {
            counter.reset(board.get(0, y));
            for (int y0 = y + 1, x0 = 1; y0 < board.height && x0 < board.width; y0++, x0++) {
                final Player currentTile = board.get(x0, y0);
                if (counter.hasWon(currentTile)) {
                    return counter.getTotalScore();
                }
            }
        }

        for (int x = 1; x < board.width; x++) {
            counter.reset(board.get(x, 0));
            for (int y0 = 1, x0 = x + 1; y0 < board.height && x0 < board.width; y0++, x0++) {
                final Player currentTile = board.get(x0, y0);
                if (counter.hasWon(currentTile)) {
                    return counter.getTotalScore();
                }
            }
        }

        // check diagonals left-down
        for (int y = 0; y < board.height; y++) {
            counter.reset(board.get(board.width - 1, y));
            for (int y0 = y + 1, x0 = board.width - 2; y0 < board.height && x0 >= 0; y0++, x0--) {
                final Player currentTile = board.get(x0, y0);
                if (counter.hasWon(currentTile)) {
                    return counter.getTotalScore();
                }
            }
        }

        for (int x = board.width - 2; x >= 0; x--) {
            counter.reset(board.get(x, 0));
            for (int y0 = 1, x0 = x - 1; y0 < board.height && x0 >= 0; y0++, x0--) {
                final Player currentTile = board.get(x0, y0);
                if (counter.hasWon(currentTile)) {
                    return counter.getTotalScore();
                }
            }
        }

        if (board.isFull()) {
            return DRAW_SCORE;
        }

        //it's neither a win no a draw, return current player score then
        counter.reset(null);
        return counter.getTotalScore();
    }

    /**
     * Helper to check the board state, similar to Board.Counter
     */
    private static final class PlayerCounter {

        private final Player player;
        private int count;
        private int totalScore;
        private int currentScore;

        PlayerCounter(Player currentPlayer) {
            this.player = currentPlayer;
        }

        void reset(Player currentTile) {
            if (count >= 2) {
                totalScore += currentScore;
            }
            count = 0;
            currentScore = 0;
            calcTileScore(currentTile);
        }

        private void calcTileScore(Player currentTile) {
            if (currentTile == null) {
                currentScore = count == 0 ? FREE_TILE_SCORE : (currentScore += FREE_TILE_SCORE);
            } else if (player.equals(currentTile)) { //found a new sequence
                count++;
                currentScore += PLAYER_TILE_SCORE;
            } else { //enemy tile broke the sequence
                if (count >= 2) {
                    totalScore += currentScore;
                }
                count = 0;
                currentScore = 0;
            }
        }

        boolean hasWon(Player currentTile) {
            calcTileScore(currentTile);

            if (count >= 4) {
                totalScore = WIN_SCORE;
                return true;
            }

            if (currentTile == null) {
                reset(null);
            }
            return false;
        }

        int getTotalScore() {
            return totalScore;
        }
    }
}
