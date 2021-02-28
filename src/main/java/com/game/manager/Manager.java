package com.game.manager;

import com.game.utils.SortByPoints;
import com.game.utils.SortByWonPositions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Manager {

    private static int N;
    private static int M;

    public static void gameParameters() {
        Scanner in = new Scanner(System.in);
        System.out.println("Welcome to the dice game.");
        System.out.println("Please enter the number of players.");
        N = in.nextInt();
        System.out.println("No. of players are: "+N);
        System.out.println("Please enter points of accumulate.");
        M = in.nextInt();
        System.out.println("As soon as a player accumulates " + M + " points he/she will finish the game.");
    }

    public static ArrayList<ArrayList<Integer>> composeGame() {
        ArrayList<Integer> order = new ArrayList<>();
        for (int i=0;i<N;i++) {
            order.add(i+1);
        }
        Collections.shuffle(order);
        ArrayList<ArrayList<Integer>> list = new ArrayList<>();
        for (int i=0;i<N;i++) {
            ArrayList<Integer> listInner = new ArrayList<>();
            listInner.add(order.get(i)); // player identifier
            listInner.add(1); // Flag
            // Flag info -> 0: Blocked(2 consecutive 1s) | 1: Can play clean | 2: Got 1 in last chance | >2: Player has won.
            listInner.add(0); // Total points of player
            list.add(listInner);
        }
        return list;
    }

    public static void game(ArrayList<ArrayList<Integer>> list) {
        int d = 0,playersRemaining = N;
        while (playersRemaining > 1) {
            for (int i=0;i<N;i++) {
                if (playersRemaining < 2) {
                    System.out.println("Game is finished.");
                    break;
                }
                if(list.get(i).get(1) == 0) {
                    System.out.println("Player-"+list.get(i).get(0)+", you are blocked for this turn as you rolled 2 consecutive 1s");
                    list.get(i).set(1, 1);
                    continue;
                }
                else if(list.get(i).get(1) > 2) {
                    continue;
                }
                System.out.println("Player-"+list.get(i).get(0)+" its your turn (press ‘r’ to roll the dice)");
                d= diceRoll(i, list);
                if(list.get(i).get(2) >= M) {
                    playersRemaining = win(list, i, playersRemaining);
                    continue;
                }
                else if(d==6) {
                    list.get(i).set(1, 1);
                    System.out.println("Please roll the dice again, Player-"+list.get(i).get(0)+" as you have got 6.");
                    d = diceRoll(i, list);
                    if(list.get(i).get(2) >= M) {
                        playersRemaining = win(list, i, playersRemaining);
                        continue;
                    }
                }
                if(d==1) {
                    list.get(i).set(1, (list.get(i).get(1) == 2) ? 0 : 2);
                }
                else {
                    list.get(i).set(1, 1);
                }
                printRanks(list);
            }
        }
    }

    private static int diceRoll(int i, ArrayList<ArrayList<Integer>> list) {
        int d;
        Random rand = new Random();
        Scanner in = new Scanner(System.in);
        in.next();
        do {
            d = rand.nextInt(7);
        } while (d == 0);
        System.out.println("Dice rolled is: "+d);
        list.get(i).set(2, list.get(i).get(2) + d);
        return d;
    }

    private static int win(ArrayList<ArrayList<Integer>> list, int i, int playersRemaining) {
        System.out.println("You have won Player-"+list.get(i).get(0)+" and your rank is "+(N-playersRemaining+1));
        playersRemaining--;
        list.get(i).set(1, 2+playersRemaining);
        printRanks(list);
        return playersRemaining;
    }

    private static void printRanks(ArrayList<ArrayList<Integer>> list) {
        ArrayList<ArrayList<Integer>> listRemainingPlayers = new ArrayList<>();
        ArrayList<ArrayList<Integer>> listWonPlayers = new ArrayList<>();
        int pos = 0;
        for(int i=0; i<N; i++) {
            if(list.get(i).get(1) < 3) {
                listRemainingPlayers.add(list.get(i));
            } else {
                listWonPlayers.add(list.get(i));
                pos++;
            }
        }
        listWonPlayers.sort(new SortByWonPositions());
        for(int i=0; i<listWonPlayers.size(); i++) {
            System.out.println("Rank:"+(i+1)+" is Player-"+listWonPlayers.get(i).get(0));
        }
        listRemainingPlayers.sort(new SortByPoints());
        for(int i=0; i<listRemainingPlayers.size(); i++) {
            System.out.println("Rank:"+(i+pos+1)+" is Player-"+listRemainingPlayers.get(i).get(0)+" with points: "+listRemainingPlayers.get(i).get(2));
        }
    }
}
