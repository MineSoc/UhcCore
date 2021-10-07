package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.exceptions.UhcPlayerDoesNotExistException;
import com.gmail.val59000mc.exceptions.UhcTeamException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomizeTeamsCommandExecutor implements CommandExecutor{

    private final GameManager gameManager;

    public RandomizeTeamsCommandExecutor(GameManager gameManager){
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (args.length != 1) {
            sender.sendMessage("Provide team size integer");
            return true;
        }

        if (gameManager.getGameState() != GameState.WAITING)
            return true;
        
        int team_size = Integer.parseInt(args[0]);
        PlayerManager pm = gameManager.getPlayerManager();
        // Randomize player list
        List<UhcPlayer> players = new ArrayList<>(pm.getPlayersList());
        Collections.shuffle(players);

        // Pick leaders for each team
        int team_count = (players.size() + team_size - 1) / team_size;
        List<UhcPlayer> teamLeaders = players.subList(0, team_count);
        List<UhcTeam> teams = new ArrayList<>();

        // Construct list of teams (each leader leaving leaves them in a team of them only)
        for (UhcPlayer player : teamLeaders) {
            try { player.getTeam().leave(player);
            } catch (UhcTeamException ignored) {}
            teams.add(player.getTeam());
            gameManager.getScoreboardManager().updatePlayerOnTab(player);
        }

        // Add each player to the leaders teams in a round-robin fashion
        for (int i=team_count; i < players.size(); i++) {
            int team_index = i % team_count;
            UhcPlayer player = players.get(i);

            try { player.getTeam().leave(player);
            } catch (UhcTeamException ignored) {}

            try {
                teams.get(team_index).join(player);
                gameManager.getScoreboardManager().updatePlayerOnTab(player);
            } catch (UhcTeamException ignored) {}
        }
        return true;
    }

}