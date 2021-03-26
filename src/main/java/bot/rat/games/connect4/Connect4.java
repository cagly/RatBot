package bot.rat.games.connect4;

import bot.rat.messages.MessageHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Connect4 {

    // Discord ID - Board position
    Map<String, int[][]> ongoingGames;
    Set<String> blockedFromCommands;
    Random rand;
    String selfId;

    public Connect4(MessageHandler handler) {
        ongoingGames = new HashMap<>();
        blockedFromCommands = new HashSet<>();
        rand = new Random();
    }

    public void connect4Commands(GuildMessageReceivedEvent event, MessageHandler handler, String message) {
        if (selfId == null) {
            try {
                selfId = event.getJDA().getSelfUser().getId();
                getRatBotAvatar(event.getJDA());
            } catch (Exception e) {
                event.getChannel().sendMessage("Unnnff, major fucky wucky, good luck brosef.").queue();
                return;
            }
        }
        if (!blockedFromCommands.contains(event.getAuthor().getId())
                && (event.getChannel().getName().equals("rat-games")
                || event.getChannel().getName().equals("rat-hole"))) {
            blockedFromCommands.add(event.getAuthor().getId());
            if (message.equals("new game")) {
                if (!ongoingGames.keySet().contains(event.getAuthor().getId())) {
                    forceGetAvatar(event.getAuthor().getId(), event);
                    startNewBoard(event.getAuthor().getId());
                    try {
                        event.getChannel().sendMessage("A new board has been set, " + event.getMember().getEffectiveName()).queue();
                    } catch (NullPointerException e) {
                        event.getChannel().sendMessage("A new board has been set, " + event.getAuthor().getName()).queue();
                    }
                    int starts = rand.nextInt(2);
                    // 0, bot starts, 1, player starts
                    if (starts == 0) {
                        ratPlayMove(event.getAuthor().getId(), event);
                    } else {
                        printBoard(event.getAuthor().getId(), event);
                        event.getChannel().sendMessage("Play your move... if you dare.").queue();
                    }
                } else {
                    try {
                        event.getChannel().sendMessage("You already have an ongoing game, " + event.getMember().getEffectiveName()).queue();
                    } catch (NullPointerException e) {
                        event.getChannel().sendMessage("You already have an ongoing game, " + event.getAuthor().getName()).queue();
                    }
                }
            } else if (message.equals("my board")) {
                if (ongoingGames.keySet().contains(event.getAuthor().getId())) {
                    event.getChannel().sendMessage("Here is your current board: ").queue();
                    printBoard(event.getAuthor().getId(), event);
                    event.getChannel().sendMessage("Where would you like to play a piece?").queue();
                } else {
                    event.getChannel().sendMessage("You dont have a board, bro.").queue();
                }
            } else if (message.equals("end game")) {
                if (ongoingGames.keySet().contains(event.getAuthor().getId())) {
                    ongoingGames.remove(event.getAuthor().getId());
                    event.getChannel().sendMessage("Your current game has been scrapped.").queue();
                } else {
                    event.getChannel().sendMessage("You don't have an active game, friendo.").queue();
                }
            } else if (message.length() > 5 && message.substring(0, 4).equals("play")) {
                if (ongoingGames.keySet().contains(event.getAuthor().getId())) {
                    String desiredRow = message.substring(5);
                    // 0 = error, 1 = winning move, 2 = not winning move
                    int ans = MonteCarlo.doesPlayerMoveWin(ongoingGames.get(event.getAuthor().getId()), 1, desiredRow);
                    if (ans == 0) {
                        event.getChannel().sendMessage("Illegal moves are illegal.").queue();
                    } else if (ans == 1) {
                        ratBotLost(event.getAuthor().getId(), event);
                    } else {
                        int[][] newPos = MonteCarlo.playPlayerMove(ongoingGames.get(event.getAuthor().getId()), 1, desiredRow);
                        ongoingGames.put(event.getAuthor().getId(), newPos);
                        ratPlayMove(event.getAuthor().getId(), event);
                    }
                }
            } else if (message.equals("tutorial")) {
                event.getChannel().sendMessage("Here are the commands for the Connect 4 game: \n" +
                        "new game - Start a new game against RatBot\n" +
                        "play [1-7] - Play a piece in the specified column. Columns are enumerated from left to right.\n" +
                        "my board - Shows you your board.\n" +
                        "end game - End your current game against RatBot.\n" +
                        "tutorial - Display this message.\n\n" +
                        "Rules of the game: \n" +
                        "https://en.wikipedia.org/wiki/Connect_Four").queue();
            }
        }
        blockedFromCommands.remove(event.getAuthor().getId());
    }

    void printBoard(String id, GuildMessageReceivedEvent event) {
        try {
            getAvatar(id, event);
        } catch (IOException e) {
            event.getChannel().sendMessage("Your avatar sucks so I won't show you the board.").queue();
            return;
        }
        try {
            int[][] pos = ongoingGames.get(id);
            int drawingWidth = 1400;
            int drawingHeight = 1200;
            int circleSize = 200;
            BufferedImage newImg = new BufferedImage(drawingWidth, drawingHeight, BufferedImage.TYPE_INT_ARGB);
            File playerAvatarFile = new File("/home/ubuntu/RatPics/" + event.getAuthor().getId() + ".png");
//            File playerAvatarFile = new File("C:\\Users\\Hans\\IdeaProjects\\ratbot\\resources\\" + event.getAuthor().getId() + ".png");
            BufferedImage playerAvatar = ImageIO.read(playerAvatarFile);
            File ratAvatarFile = new File("/home/ubuntu/RatPics/" + selfId + ".png");
//            File ratAvatarFile = new File("C:\\Users\\Hans\\IdeaProjects\\ratbot\\resources\\" + selfId + ".png");
            BufferedImage ratAvatar = ImageIO.read(ratAvatarFile);
            Graphics2D g2d = newImg.createGraphics();
            g2d.setColor(Color.WHITE);
            for (int i = 0; i < drawingWidth / circleSize; i++) {
                for (int j = 0; j < drawingHeight / circleSize; j++) {
                    g2d.setColor(Color.WHITE);
                    if (pos[j][i] == 1) {
                        g2d.setClip(new Ellipse2D.Float(i * circleSize, j * circleSize, circleSize, circleSize));
                        g2d.drawImage(playerAvatar, i * circleSize, j * circleSize, circleSize, circleSize, null);
                    } else if (pos[j][i] == 2) {
                        g2d.setClip(new Ellipse2D.Float(i * circleSize, j * circleSize, circleSize, circleSize));
                        g2d.drawImage(ratAvatar, i * circleSize, j * circleSize, circleSize, circleSize, null);
                    } else {
                        g2d.setClip(new Ellipse2D.Float(i * circleSize, j * circleSize, circleSize, circleSize));
                        g2d.setColor(Color.DARK_GRAY);
                        g2d.fillOval(i * circleSize, j * circleSize, circleSize, circleSize);
                    }
                }
            }
            File drawing = new File("/home/ubuntu/RatPics/board" + id + ".png");
//            File drawing = new File("C:\\Users\\Hans\\IdeaProjects\\ratbot\\resources\\board " + id + ".png");
            ImageIO.write(newImg, "png", drawing);
            event.getChannel().sendFile(drawing).queue();
        } catch (IOException e) {
            event.getChannel().sendMessage("Ungghhh, uff, error while drawing board for you, sowwy >W< *nuzzles*").queue();
        }
    }

    void getAvatar(String id, GuildMessageReceivedEvent event) throws IOException {
//        File file = new File("C:\\Users\\Hans\\IdeaProjects\\ratbot\\resources\\" + event.getAuthor().getId() + ".png");
        File file = new File("/home/ubuntu/RatPics/" + event.getAuthor().getId() + ".png");
        if (!file.exists()) {
            URL url = new URL(event.getAuthor().getAvatarUrl());
            URLConnection uc = url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            BufferedImage img = ImageIO.read(uc.getInputStream());
            ImageIO.write(img, "png", file);
        }
    }

    void forceGetAvatar(String id, GuildMessageReceivedEvent event) {
//        File file = new File("C:\\Users\\Hans\\IdeaProjects\\ratbot\\resources\\" + event.getAuthor().getId() + ".png");
        try {
            File file = new File("/home/ubuntu/RatPics/" + event.getAuthor().getId() + ".png");
            URL url = new URL(event.getAuthor().getAvatarUrl());
            URLConnection uc = url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            BufferedImage img = ImageIO.read(uc.getInputStream());
            ImageIO.write(img, "png", file);
        } catch (IOException e) {
        }
    }

    void ratPlayMove(String id, GuildMessageReceivedEvent event) {
        int[][] newPos = MonteCarlo.playRatBotMove(ongoingGames.get(id), 500, event);
        if (newPos[0][0] == 126) {
            gameDraw(id, event);
        } else if (newPos[0][0] == 125) {
            ratBotWon(id, event);
        } else {
            ongoingGames.put(id, newPos);
            printBoard(id, event);
            event.getChannel().sendMessage("Your move, pal. Which column would you like to play a piece in?").queue();
        }
    }

    void gameDraw(String id, GuildMessageReceivedEvent event) {
        printBoard(id, event);
        try {
            event.getChannel().sendMessage(event.getMember().getEffectiveName() + " drew their game against RatBot.").queue();
        } catch (NullPointerException e) {
            event.getChannel().sendMessage(event.getAuthor().getName() + " drew their game against RatBot.").queue();
        }
        ongoingGames.remove(id);
    }

    void ratBotWon(String id, GuildMessageReceivedEvent event) {
        printBoard(id, event);
        try {
            event.getChannel().sendMessage(event.getMember().getEffectiveName() + " lost their game against RatBot.").queue();
        } catch (NullPointerException e) {
            event.getChannel().sendMessage(event.getAuthor().getName() + " lost their game against RatBot.").queue();
        }
        ongoingGames.remove(id);
    }

    void ratBotLost(String id, GuildMessageReceivedEvent event) {
        printBoard(id, event);
        try {
            event.getChannel().sendMessage(event.getMember().getEffectiveName() + " won their game against RatBot! Not bad, kid.").queue();
        } catch (NullPointerException e) {
            event.getChannel().sendMessage(event.getAuthor().getName() + " won their game against RatBot! Not bad, kid.").queue();
        }
        ongoingGames.remove(id);
    }

    public void startNewBoard(String id) {
        int[][] startingPos = new int[6][7];
        for (int i = 0; i < startingPos.length; i++) {
            startingPos[i] = new int[]{0,0,0,0,0,0,0};
        }
        ongoingGames.put(id, startingPos);
    }


    void getRatBotAvatar(JDA jda) throws IOException {
        jda.getSelfUser().getAvatarUrl();
        File file = new File("/home/ubuntu/RatPics/" + jda.getSelfUser().getId() + ".png");
//        File file = new File("C:\\Users\\Hans\\IdeaProjects\\ratbot\\resources\\" + jda.getSelfUser().getId() + ".png");
        if (!file.exists()) {
            URL url = new URL(jda.getSelfUser().getAvatarUrl());
            URLConnection uc = url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            BufferedImage img = ImageIO.read(uc.getInputStream());
            ImageIO.write(img, "png", file);
        }
    }

}
