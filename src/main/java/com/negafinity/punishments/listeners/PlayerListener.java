package com.negafinity.punishments.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.negafinity.punishments.Punishments;

public class PlayerListener implements Listener
{
	private Punishments plugin;

	public PlayerListener(Punishments plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		String ip = event.getAddress().getHostAddress();
		Player player = event.getPlayer();
		if (this.plugin.getDatabaseManager().isPlayerBanned(ip))
		{
			event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
			this.plugin.getLogger().info(player.getName() + " tried to join the server.");
		}
		else if (this.plugin.getDatabaseManager().isPlayerTempBanned(ip))
		{
			event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
			this.plugin.getLogger().info(player.getName() + " tried to join the server.");
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();

		if (this.plugin.getDatabaseManager().isPlayerMuted(player.getUniqueId()))
		{
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are muted.");
		}
	}
}
