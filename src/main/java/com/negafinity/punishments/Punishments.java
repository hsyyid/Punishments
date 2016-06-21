package com.negafinity.punishments;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.negafinity.punishments.config.PunishmentsGUIConfig;
import com.negafinity.punishments.gui.GUIHandler;
import com.negafinity.punishments.listeners.InventoryListener;
import com.negafinity.punishments.listeners.PlayerListener;
import com.negafinity.punishments.util.DatabaseManager;
import com.negafinity.punishments.util.Tuple;

public class Punishments extends JavaPlugin implements Listener
{
	public DatabaseManager databaseManager;
	public PunishExecutor exec;
	public GUIHandler handler;
	public static Map<UUID, Tuple<String, String>> punishmentArgs = Maps.newHashMap();
	public static Map<UUID, Tuple<Optional<String>, PunishmentType>> punishmentTypes = Maps.newHashMap();

	@Override
	public void onEnable()
	{
		new PunishmentsGUIConfig(this);

		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		this.getServer().getPluginManager().registerEvents(new InventoryListener(this), this);

		this.exec = new PunishExecutor(this);
		this.handler = new GUIHandler(this);

		this.getCommand("p").setExecutor(new CommandExecutor()
		{

			@Override
			public boolean onCommand(CommandSender src, Command cmd, String arg2, String[] args)
			{
				if (src instanceof Player)
				{
					Player player = (Player) src;

					if (player.hasPermission("punishments.punish.use"))
					{
						if (args.length >= 2)
						{
							String target = args[0];
							String reason = "";

							List<String> arguments = Lists.newArrayList(args);
							arguments.remove(0);

							for (String a : arguments)
							{
								reason += reason.isEmpty() ? a : " " + a;
							}

							if (punishmentArgs.containsKey(player.getUniqueId()))
							{
								punishmentArgs.remove(player.getUniqueId());
							}

							punishmentArgs.put(player.getUniqueId(), Tuple.of(target, reason));
							handler.openPunishmentDialog(player, target, reason);
						}
						else
						{
							src.sendMessage(ChatColor.RED + "Invalid arguments: /p <player> <reason>");
						}
					}
					else
					{
						src.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
					}
				}
				else
				{
					src.sendMessage(ChatColor.RED + "You must be a player to use this command!");
				}

				return true;
			}
		});

		this.getConfig().options().copyDefaults(true);
		this.saveConfig();

		this.databaseManager = new DatabaseManager(this);
		this.databaseManager.checkDatabase();

		this.getLogger().info("Punishments Enabled");
	}

	@Override
	public void onDisable()
	{
		this.getLogger().info("Punishments Disabled");
	}

	public DatabaseManager getDatabaseManager()
	{
		return this.databaseManager;
	}

	public PunishExecutor getExec()
	{
		return exec;
	}

	public GUIHandler getHandler()
	{
		return handler;
	}
}
