package fr.sorbonne.gutenberg.core.utils.regex;

import java.util.ArrayList;

import static fr.sorbonne.gutenberg.core.utils.regex.RegEx.*;

class EpsilonTransition {

    // private static final int DOT = 259;
    int[][][] transition = new int[100][260][2];
    private int state = 1;

    EpsilonTransition(RegExTree tree) throws Exception {
        treeToTab(tree);
    }

    private int getState(){
        return state++;
    }

    private int[] treeToTab(RegExTree tree) throws Exception {
        if (tree.subTrees.isEmpty()) {
            // feuille
            int initState = getState();
            int finalState = getState();
            transition[initState][tree.root][0] = finalState;
            transition[initState][INIT][0] = 1;
            transition[finalState][FINAL][0] = 1;
            return new int[]{initState, finalState};
        } else {
            // noeud
            int[][] tr = new int[2][2];
            int i = 0;
            for (RegExTree sub : tree.subTrees) {
                tr[i] = treeToTab(sub);
                i++;
            }
            switch (tree.root) {
                case ALTERN : {
                    int newState = getState();
                    int newState2 = getState();
                    int firstChildBegin = tr[0][0];
                    int firstChildEnd = tr[0][1];
                    int secondChildBegin = tr[1][0];
                    int secondChildEnd = tr[1][1];
                    transition[newState][EPSILONE][0] = firstChildBegin;
                    transition[newState][EPSILONE][1] = secondChildBegin;
                    transition[newState][INIT][0] = 1;
                    transition[firstChildBegin][INIT][0] = 0;
                    transition[secondChildBegin][INIT][0] = 0;
                    transition[firstChildEnd][FINAL][0] = 0;
                    transition[firstChildEnd][EPSILONE][0] = newState2;
                    transition[secondChildEnd][FINAL][0] = 0;
                    transition[secondChildEnd][EPSILONE][0] = newState2;
                    transition[newState2][FINAL][0] = 1;
                    return new int[]{newState, newState2};
                }
                case ETOILE : {
                    int childBegin = tr[0][0];
                    int childEnd = tr[0][1];
                    int initState = getState();
                    int finalState = getState();
                    transition[initState][EPSILONE][0] = childBegin;
                    transition[initState][EPSILONE][1] = finalState;
                    transition[initState][INIT][0] = 1;
                    transition[childBegin][INIT][0] = 0;
                    transition[childEnd][FINAL][0] = 0;
                    transition[childEnd][EPSILONE][0] = childBegin;
                    transition[childEnd][EPSILONE][1] = finalState;
                    transition[finalState][FINAL][0] = 1;
                    return new int[]{initState, finalState};
                }
                case CONCAT :{
                    int firstChildBegin = tr[0][0];
                    int firstChildEnd = tr[0][1];
                    int secondChildBegin = tr[1][0];
                    int secondChildEnd = tr[1][1];
                    transition[firstChildEnd][FINAL][0] = 0;
                    transition[secondChildBegin][INIT][0] = 0;
                    transition[firstChildEnd][EPSILONE][0] = secondChildBegin;
                    return new int[]{firstChildBegin, secondChildEnd};
                }
                default:
                    throw new Exception("invalid noeud");
            }
        }
    }

    int getInitState(){
        for (int i = 0; i < state; i++)
            if (transition[i][INIT][0] == 1)
                return i;
        return 0;
    }

    int getFinalState(){
        for (int i = 0; i < state; i++)
            if (transition[i][FINAL][0] == 1)
                return i;
        return 0;
    }

    ArrayList<Integer> getClosure(int state){
        ArrayList<Integer> fermeture = new ArrayList<>();
        fermeture.add(state);
        if(transition[state][EPSILONE][0] != 0)
            fermeture.addAll(getClosure(transition[state][EPSILONE][0]));
        if (transition[state][EPSILONE][1] != 0)
            fermeture.addAll(getClosure(transition[state][EPSILONE][1]));
        return fermeture;
    }

    void affich() {
        for (int i = 1; i < state; i++) {
            String initState = transition[i][INIT][0] == 1 ? "Initial state" : "";
            String finalState = transition[i][FINAL][0] == 1 ? "Final state" : "";
            StringBuilder values = new StringBuilder();
            for (int j = 0; j < 255; j++)
                if (transition[i][j][0] != 0)
                    values.append(i).append(" -> ").append((char) j).append(" -> ").append(transition[i][j][0]).append(" ,");

            int j = -1;
            while (++j < 2 && transition[i][EPSILONE][j] != 0)
                values.append(i).append(" -> ").append("epsilone").append(" -> ").append(transition[i][EPSILONE][j]).append(" ,");

            System.out.println("state (" + i + ") : ( " + initState + ", " + finalState + " ){" + values + "}");
        }
    }
}
