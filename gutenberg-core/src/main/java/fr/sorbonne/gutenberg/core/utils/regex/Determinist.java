package fr.sorbonne.gutenberg.core.utils.regex;

import java.util.ArrayList;
import java.util.Collections;

import static fr.sorbonne.gutenberg.core.utils.regex.RegEx.*;

class Determinist {

    private EpsilonTransition epsilonTransition;

    private int[][] deterministe = new int[100][260];
    private String[] saveStates = new String[100];
    private int state = 1;

    private int initState;
    private int finalState;

    Determinist(EpsilonTransition epsilonTransition) {
        this.epsilonTransition = epsilonTransition;
        initState = epsilonTransition.getInitState();
        finalState = epsilonTransition.getFinalState();
    }

    private int getState() {
        return state++;
    }

    void determinaze() {
        determinaze(initState);
    }

    private int determinaze(int state) {
        ArrayList<Integer> closure = epsilonTransition.getClosure(state);
        StringBuilder saveState = new StringBuilder();
        Collections.sort(closure);
        closure.forEach(e -> saveState.append(e).append(","));
        int newState = getState();
        for (int i = 1; i < state; i++)
            if (saveStates[i] != null && saveStates[i].equals(saveState.toString()))
                return i;
        saveStates[newState] = saveState.toString();
        for (int s : closure)
            for (int i = 0; i < 255; i++)
                if (epsilonTransition.transition[s][i][0] != 0)
                    deterministe[newState][i] = determinaze(epsilonTransition.transition[s][i][0]);
        if (closure.contains(initState)) deterministe[newState][INIT] = 1;
        if (closure.contains(finalState)) deterministe[newState][FINAL] = 1;
        return newState;
    }

    ArrayList<Integer> matchAll(char[] text) {
        ArrayList<Integer> result = new ArrayList<>();
        int initState = getInitState();
        for (int i = 0, j = i; i < text.length; i++, j = i) {
            int state = initState;
            int nextState;
            while (j < text.length) {
                if (text[j] > 255)
                    nextState = 0;
                else
                    nextState = deterministe[state][text[j]];

                if (nextState == 0) {
                    if (deterministe[state][FINAL] == 1 && j != i)
                        result.add(i);
                    break;
                }
                state = nextState;
                j++;
            }
        }
        return result;
    }

    private int getInitState() {
        for (int i = 1; i < state; i++)
            if (deterministe[i][INIT] == 1) return i;
        return -1;
    }

    void affich() {
        for (int i = 1; i < state; i++) {
            StringBuilder values = new StringBuilder();
            String initState = deterministe[i][INIT] == 1 ? "Initial state" : "";
            String finalState = deterministe[i][FINAL] == 1 ? "Final state" : "";
            for (int j = 0; j < 255; j++)
                if (deterministe[i][j] != 0)
                    values.append(i).append(" -> ").append((char) j).append(" -> ").append(deterministe[i][j]).append(" ,");

            System.out.println("state (" + i + ") : (" + initState + "," + finalState + "){" + values + "}");
        }
    }
}
