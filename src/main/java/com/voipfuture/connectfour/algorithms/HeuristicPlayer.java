package com.voipfuture.connectfour.algorithms;

import com.voipfuture.connectfour.Board;
import com.voipfuture.connectfour.GameState;
import com.voipfuture.connectfour.IInputProvider;
import com.voipfuture.connectfour.Player;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class HeuristicPlayer implements IInputProvider {

    @Override
    public Optional<InputEvent> readInput(GameState gameState) {
        //first move on the start of a game is always random
        if (gameState.board.isEmpty()) {
            int column = new Random(System.currentTimeMillis()).nextInt(gameState.board.width);
            return Optional.of(new MoveEvent(gameState.currentPlayer(), column));
        }

        Player currentPlayer = gameState.currentPlayer();
        Player nextPlayer = gameState.nextPlayer();

        GamePrediction gamePrediction = new GamePrediction(currentPlayer, nextPlayer, currentPlayer.maxThinkDepth());
        return Optional.of(gamePrediction.getNextMove(gameState.board));
    }

    protected static final class GamePrediction {

        final Player maxPlayer;
        final Player minPlayer;
        private final int maxThinkDepth;

        GamePrediction(Player maxPlayer, Player minPlayer, int maxThinkDepth) {
            this.maxPlayer = maxPlayer;
            this.minPlayer = minPlayer;
            this.maxThinkDepth = maxThinkDepth;
        }

        /**
         * Predict next possible move according to the maximizing strategy
         * */
        MoveEvent getNextMove(Board board) {
            int bestMove = -1;
            int bestScore = 0;

            ArrayList<Integer> possibleColumns = getPossibleMoves(board);
            Board updatedBoard = board.createCopy();

            for (Integer x : possibleColumns) {
                int y = updatedBoard.move(x, maxPlayer);
                int score = -getScore(updatedBoard, minPlayer, 0);
                updatedBoard.clear(x, y);
                if ((bestMove == -1) || (score > bestScore)) {
                    bestMove = x;
                    bestScore = score;
                }
            }
            return new MoveEvent(maxPlayer, bestMove);
        }

        /**
         * straightforward implementation of nega-max algorithm
         * */
        private int getScore(Board board, Player player, int depth) {
            Optional<Board.WinningCondition> state = board.getState();
            if (state.isPresent()) {
                return state.get().isDraw ? Heuristic.DRAW_SCORE :
                        (state.get().player().equals(player) ? Heuristic.WIN_SCORE : -Heuristic.WIN_SCORE);
            }
            if (depth > maxThinkDepth) {
                return Heuristic.getScore(board, player);
            }

            ArrayList<Integer> possibleColumns = getPossibleMoves(board);
            Board updatedBoard = board.createCopy();
            int bestScore = Integer.MIN_VALUE;
            for (Integer x : possibleColumns) {
                int y = updatedBoard.move(x, player);
                int score = -getScore(updatedBoard, player == maxPlayer ? minPlayer : maxPlayer, depth + 1);
                updatedBoard.clear(x, y);
                bestScore = Math.max(bestScore, score);
            }
            return bestScore;
        }

        /**
         * return a list of columns, where a new tile can be placed
         * */
        private ArrayList<Integer> getPossibleMoves(Board board) {
            ArrayList<Integer> moves = new ArrayList<>();
            for (int x = 0; x < board.width; x++) {
                if (board.hasSpaceInColumn(x)) {
                    moves.add(x);
                }
            }
            return moves;
        }
    }
}
