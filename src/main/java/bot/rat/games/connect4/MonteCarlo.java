package bot.rat.games.connect4;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MonteCarlo {

    public static int[] pureMc(int[][] pos, int my_side, int sims) {
        List<int[]> initialMoves = moves(pos);
        Map<Integer, Double> win_counts = new HashMap<>();
        for (int i = 0; i < initialMoves.size(); i++) {
            win_counts.put(i, 0d);
        }

        int moveCounter = 0;
        for (int[] move : initialMoves) {
            for (int i = 0; i < sims; i++) {
                double res = simulate(pos, move, my_side);
                win_counts.put(moveCounter, win_counts.get(moveCounter) + res);
            }
            moveCounter += 1;
        }

        int bestMove = -1;
        double bestWins = -1d;
        for (int move : win_counts.keySet()) {
//            System.out.println("Placing piece in row " + (move + 1) + " has a win chance of " + win_counts.get(move) * 100 / sims + "%");
            double wins = win_counts.get(move);
            if (wins > bestWins) {
                bestMove = move;
                bestWins = wins;
            }
        }
        return initialMoves.get(bestMove);
    }

    public static double simulate(int[][] pos, int[] move, int my_side) {
        int[][] sim_pos = new int[6][7];
        for (int i = 0; i < sim_pos.length; i++) {
            for (int j = 0; j < sim_pos[i].length; j++) {
                sim_pos[i][j] = pos[i][j];
            }
        }
        if(isDrawn(sim_pos)) {
            return 0.5d;
        }
        if(moveWins(sim_pos, move, my_side)) {
            return 1d;
        }
        Random rand = new Random();
        sim_pos = makeMove(sim_pos, move, my_side);
        boolean my_turn = false;
        while (true) {
            if (my_turn) {
                if (isDrawn(sim_pos)) {
                    return 0.5d;
                }
                List<int[]> poss_moves = moves(sim_pos);
                int[] random_move = poss_moves.get(rand.nextInt(poss_moves.size()));
                if (moveWins(sim_pos, random_move, my_side)) {
                    return 1d;
                }
                sim_pos = makeMove(sim_pos, random_move, my_side);
                my_turn = false;
            } else {
                if (isDrawn(sim_pos)) {
                    return 0.5d;
                }
                List<int[]> poss_moves = moves(sim_pos);
                int[] random_move = poss_moves.get(rand.nextInt(poss_moves.size()));
                if (moveWins(sim_pos, random_move, deOtherSide(my_side))) {
                    return 0d;
                }
                sim_pos = makeMove(sim_pos, random_move, deOtherSide(my_side));
                my_turn = true;
            }
        }
    }

    public static int deOtherSide(int side) {
        if (side == 1) {
            return 2;
        }
        return 1;
    }

    public static boolean moveWins(int[][] pos, int[] move, int move_side) {
        pos[move[0]][move[1]] = move_side;
        List<Integer> range7 = IntStream.rangeClosed(0,6)
                .boxed()
                .collect(Collectors.toList());
        List<Integer> range6 = IntStream.rangeClosed(0,5)
                .boxed()
                .collect(Collectors.toList());
        for (int i = 0; i < 4; i++) {
            if (range7.contains(move[1] - i) && range7.contains(move[1] - i + 3)) {
                if (pos[move[0]][move[1] - i] == move_side && pos[move[0]][move[1] - i + 1] == move_side && pos[move[0]][move[1] - i  + 2] == move_side && pos[move[0]][move[1] - i + 3] == move_side) {
                    return true;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            if (range7.contains(move[1] - i) && range7.contains(move[1] - i + 3) && range6.contains(move[0] - i) && range6.contains(move[0] - i + 3)) {
                if (pos[move[0] - i][move[1] - i] == move_side && pos[move[0] - i + 1][move[1] - i + 1] == move_side && pos[move[0] - i + 2][move[1] - i + 2] == move_side && pos[move[0] - i + 3][move[1] - i + 3] == move_side) {
                    return true;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            if (range7.contains(move[1] - i) && range7.contains(move[1] - i + 3) && range6.contains(move[0] + i) && range6.contains(move[0] + i - 3)) {
                if (pos[move[0] + i][move[1] - i] == move_side && pos[move[0] + i - 1][move[1] - i + 1] == move_side && pos[move[0] + i - 2][move[1] - i + 2] == move_side && pos[move[0] + i - 3][move[1] - i + 3] == move_side) {
                    return true;
                }
            }
        }
        if (move[0] <= 2) {
            if (pos[move[0]][move[1]] == move_side && pos[move[0] + 1][move[1]] == move_side && pos[move[0] + 2][move[1]] == move_side && pos[move[0] + 3][move[1]] == move_side) {
                return true;
            }
        }
        pos[move[0]][move[1]] = 0;
        return false;
    }


    public static boolean isDrawn(int[][] pos) {
        return moves(pos).isEmpty();
    }


    public static List<int[]> moves(int[][] pos) {
        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            if (pos[0][i] == 0) {
                int[] line_move = new int[]{0, i};
                for (int j = 0; j < 6; j++) {
                    if (pos[j][i] == 0) {
                        line_move = new int[]{j, i};
                    }
                }
                moves.add(line_move);
            }
        }
        return moves;
    }


    public static int[][] makeMove(int[][] pos, int[] move, int moveSide) {
        pos[move[0]][move[1]] = moveSide;
        return pos;
    }

    public static int[] parseMove(String moveStr, int[][] pos) {
        int movex = Integer.parseInt(moveStr);
        List<int[]> poss_moves = moves(pos);
        int[] chosen = null;
        for (int[] move : poss_moves) {
            if (move[1] == movex - 1) {
                chosen = move;
            }
        }
        return chosen;
    }

    public static int[][] playRatBotMove(int[][] pos, int sims, GuildMessageReceivedEvent event) {
        int player_side = 2;
        if (isDrawn(pos)) {
            return new int[][]{{126}};
        }
//            if (player_turn) {
//                boolean parsed = false;
//                int[] move = null;
//                while (!parsed) {
//                    try {
//                        String moveStr = scan.nextLine();
//                        move = parseMove(moveStr, pos);
//                        parsed = true;
//                    } catch (Exception e) {
//                        return new int[][]{{123}};
//                    }
//                }
//                if (moveWins(pos, move, player_side)) {
//                    is_over = true;
//                }
//                pos = makeMove(pos, move, player_side);
//                int[] move = pureMc(pos, player_side, 200);
//                System.out.println("AI played move at row " + (move[1] + 1));
//                if (moveWins(pos, move, player_side)) {
//                    System.out.println("Player 1 wins");
//                    is_over = true;
//                }
//                pos = makeMove(pos, move, player_side);

            int[] move = pureMc(pos, player_side, sims);
            if (moveWins(pos, move, player_side)) {
//                    System.out.println("Player 2 wins");
                return new int[][]{{125}};
            }
            pos = makeMove(pos, move, player_side);
            event.getChannel().sendMessage("RatBot put a piece in row " + (move[1] + 1)).queue();
            if (isDrawn(pos)) {
                return new int[][]{{126}};
            }
            return pos;
    }

    public static int doesPlayerMoveWin(int[][] pos, int player_side, String moveString) {
        int[] move;
        try {
            move = parseMove(moveString, pos);
        } catch (Exception e) {
            return 0;
        }
        if (move == null) {
            return 0;
        }
        if (moveWins(pos, move, player_side)) {
            return 1;
        }
        return 2;
    }

    public static int[][] playPlayerMove(int[][] pos, int player_side, String playerMove) {
        int[] move = parseMove(playerMove, pos);
        pos = makeMove(pos, move, player_side);
        return pos;
    }

}
