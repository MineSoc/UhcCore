package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.exceptions.UhcPlayerDoesNotExistException;
import com.gmail.val59000mc.exceptions.UhcTeamException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForceTeamCommandExecutor implements CommandExecutor {

    private final GameManager gameManager;

    public ForceTeamCommandExecutor(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerManager pm = gameManager.getPlayerManager();
        
        if (args.length != 2) {
            sender.sendMessage("Usage: /forceteam <player> <Team Leader>");
            return true;
        }
        String playerName = args[0], teamLeader = args[1];

        UhcPlayer player;
        UhcTeam team;
        try { player = pm.getUhcPlayer(playerName);
        } catch (UhcPlayerDoesNotExistException e) {
            sender.sendMessage("Player " + playerName + " does not exist");
            return true;
        }

        try { team = pm.getUhcPlayer(teamLeader).getTeam();
        } catch (UhcPlayerDoesNotExistException e) {
            sender.sendMessage("Player " + teamLeader + " does not exist");
            return true;
        }

        try { player.getTeam().leave(player);
        } catch (UhcTeamException ignored) {}

        try {
            team.join(player);
            gameManager.getScoreboardManager().updatePlayerOnTab(player);
        } catch (UhcTeamException e) {
            sender.sendMessage(e.getMessage());
        }

        return true;
    }

}