package com.voipfuture.connectfour;

import com.voipfuture.connectfour.algorithms.Heuristic;

public class HeuristicTest extends AbstractTestHelper {

    public void testScoreCompleteBoard() {
        final String s = "1.121\n" +
                         "2.222\n" +
                         "1.211\n" +
                         "1.112\n" +
                         "2.212";
        setupBoard(s);

        assertEquals(165, Heuristic.getScore(state.board, player1));
        assertEquals(165, Heuristic.getScore(state.board, player2));
    }
}
