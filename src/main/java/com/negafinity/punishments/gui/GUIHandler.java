package com.negafinity.punishments.gui;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.negafinity.punishments.PunishmentType;
import com.negafinity.punishments.Punishments;
import com.negafinity.punishments.config.PunishmentsGUIConfig;
import com.negafinity.punishments.util.Punishment;
import com.negafinity.punishments.util.Tuple;
import com.negafinity.punishments.util.Utils;

public class GUIHandler
{
	private Punishments plugin;

	public GUIHandler(Punishments plugin)
	{
		this.plugin = plugin;
	}

	public void openPunishmentDialog(Player player, String target, String reason)
	{
		Inventory inv = Bukkit.createInventory(null, 54, "Main Menu:");

		PunishmentsGUIConfig.getMainMenuItems(target, player.getName(), reason).forEach((k, v) -> {
			inv.setItem(v.getFirst(), v.getSecond());
		});

		player.openInventory(inv);
	}

	public void openTimeDialog(Player player, String target, String reason, PunishmentType type)
	{
		Inventory inv = Bukkit.createInventory(null, 27, "Time Selector:");

		PunishmentsGUIConfig.getTimeDialogItems(target, player.getName(), reason).forEach((k, v) -> {
			inv.setItem(v.getFirst(), v.getSecond());
		});

		if (Punishments.punishmentTypes.containsKey(player.getUniqueId()))
		{
			Punishments.punishmentTypes.remove(player.getUniqueId());
		}

		Punishments.punishmentTypes.put(player.getUniqueId(), Tuple.of(Optional.empty(), type));

		player.openInventory(inv);
	}

	public void openConfirmDialog(Player player, String target, String reason, Optional<String> time)
	{
		Inventory inv = Bukkit.createInventory(null, 27, "Confirm:");

		PunishmentsGUIConfig.getConfirmScreenItems(target, player.getName(), reason, time).forEach((k, v) -> {
			inv.setItem(v.getFirst(), v.getSecond());
		});

		player.openInventory(inv);
	}

	public void openHistoryDialog(Player player, String target, UUID uuid)
	{
		Inventory inv = Bukkit.createInventory(null, 54, "History:");
		List<Punishment> punishments = this.plugin.getDatabaseManager().getPlayerPunishments(target, uuid);
		int i = 9;

		for (Punishment p : punishments)
		{
			if (i < 53)
			{
				inv.setItem(i, p.getItemStack());
				i++;
			}
		}
		
		inv.setItem(4, Utils.createPlayerSkull(target));

		player.openInventory(inv);
	}
}
