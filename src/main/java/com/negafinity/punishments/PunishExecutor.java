package com.negafinity.punishments;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.negafinity.punishments.util.UUIDFetcher;
import com.negafinity.punishments.util.Utils;

public class PunishExecutor
{
	private Punishments plugin;

	public PunishExecutor(Punishments plugin)
	{
		this.plugin = plugin;
	}

	@SuppressWarnings({ "deprecation", "incomplete-switch" })
	public void executePunishment(Player sender, PunishmentType type, String player, String reason, Optional<String> time)
	{
		switch (type)
		{
			case TEMPBAN: {
				if (!time.isPresent())
				{
					sender.sendMessage(ChatColor.RED + "Incorrect usage: /tempban <player> <reason> <time>");
					return;
				}

				Player target = this.plugin.getServer().getPlayer(player);

				if (target == null)
				{
					sender.sendMessage(ChatColor.RED + "Could not find Player!");
					return;
				}

				try
				{
					if (this.plugin.getDatabaseManager().isPlayerTempBanned(target.getAddress().getAddress().getHostAddress()))
					{
						sender.sendMessage(ChatColor.RED + target.getName() + " is already banned!");
					}
					else
					{
						String date = Utils.getDateString(time.get());
						this.plugin.getDatabaseManager().tempbanPlayer(target.getUniqueId(), target.getAddress().getAddress().getHostAddress(), date, sender.getName(), reason);
						target.kickPlayer(ChatColor.translateAlternateColorCodes('&', MessageFormat.format("&7You are now banned!" + '\n' + "&cReason&f:&7 {0}" + '\n' + "&cBanned By&f:&7 {1}" + '\n' + "&cBanned Until&f:&7 {2}" + '\n' + '\n' + "&7To appeal, please visit us on our website." + '\n' + ChatColor.RED + this.plugin.getConfig().getString("ban.appeal-website"), reason, sender.getName(), date)));
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				break;
			}
			case KICK: {
				Player target = this.plugin.getServer().getPlayer(player);

				if (target == null)
				{
					sender.sendMessage("Player does not exist!");
					return;
				}

				this.plugin.getDatabaseManager().addKick(target.getUniqueId(), sender.getName(), reason);
				target.kickPlayer(ChatColor.translateAlternateColorCodes('&', reason));
				break;
			}
			case BAN: {
				Player target = this.plugin.getServer().getPlayer(player);

				if (target == null)
				{
					sender.sendMessage("Player does not exist!");
					return;
				}

				this.plugin.getDatabaseManager().banPlayer(target.getUniqueId(), target.getAddress().getAddress().getHostAddress(), sender.getName(), reason);

				target.kickPlayer(ChatColor.translateAlternateColorCodes('&', MessageFormat.format("&7You are now banned!" + '\n' + "&cReason&f:&7 {0}" + '\n' + "&cBanned By&f:&7 {1}" + '\n' + "&7You are permanently banned!" + '\n' + '\n' + "&7To appeal, please visit us on our website." + '\n' + ChatColor.RED + this.plugin.getConfig().getString("ban.appeal-website"), reason, sender.getName())));
				target.setBanned(true);

				break;
			}
			case MUTE: {
				Player target = this.plugin.getServer().getPlayer(player);

				if (target != null)
				{
					if (this.plugin.getDatabaseManager().isPlayerMuted(target.getUniqueId()))
					{
						sender.sendMessage(ChatColor.RED + target.getName() + " is already muted!");
					}
					else
					{
						this.plugin.getDatabaseManager().mutePlayer(target.getUniqueId(), sender.getName(), reason, time);
						sender.sendMessage(ChatColor.RED + "You have muted " + target.getName());
						String date = "";

						try
						{
							date = time.isPresent() ? " until " + ChatColor.WHITE + Utils.getDateString(time.get()) : "";
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}

						target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have been muted for " + reason + date));
					}
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "Player does not exist");
					return;
				}

				break;
			}
			case WARN: {
				Player target = this.plugin.getServer().getPlayer(player);

				if (target == null)
				{
					sender.sendMessage("Player does not exist!");
					return;
				}

				sender.sendMessage(ChatColor.GREEN + "Warned " + target.getName());
				target.sendMessage(ChatColor.RED + "You have been warned by " + sender.getName() + " for " + reason);
				this.plugin.getDatabaseManager().addWarning(target.getUniqueId(), sender.getName(), reason);
				break;
			}
			case UNMUTE: {
				Player target = this.plugin.getServer().getPlayer(player);

				if (target == null)
				{
					sender.sendMessage("Player does not exist!");
					return;
				}

				if (this.plugin.getDatabaseManager().isPlayerMuted(target.getUniqueId()))
				{
					this.plugin.getDatabaseManager().unmutePlayer(target.getUniqueId());
					sender.sendMessage("You have unmuted " + target.getName());
					target.sendMessage("You have been unmuted!");
				}
				else
				{
					sender.sendMessage(ChatColor.RED + target.getName() + " is not muted!");
				}

				break;
			}
			case UNBAN: {
				try
				{
					UUID uuid = UUIDFetcher.getUUIDOf(player);

					if (uuid != null && (this.plugin.getDatabaseManager().isPlayerBanned("BANNED", uuid) || this.plugin.getDatabaseManager().isPlayerBanned("TEMPBANNED", uuid)))
					{
						this.plugin.getDatabaseManager().unbanPlayer(player, uuid);
						sender.sendMessage("You have unbanned " + player);
					}
					else if (uuid == null)
					{
						sender.sendMessage(ChatColor.RED + "Player not found!");
					}
					else
					{
						sender.sendMessage(ChatColor.RED + "Player is not banned!");
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				break;
			}
		}
	}
}
