package com.negafinity.punishments.listeners;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.negafinity.punishments.PunishmentType;
import com.negafinity.punishments.Punishments;
import com.negafinity.punishments.config.PunishmentsGUIConfig;
import com.negafinity.punishments.util.Punishment;
import com.negafinity.punishments.util.Tuple;
import com.negafinity.punishments.util.UUIDFetcher;

public class InventoryListener implements Listener
{
	private Punishments plugin;

	public InventoryListener(Punishments plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();
		int slot = event.getRawSlot();

		if (slot != -999 && event.getInventory().getSize() > slot && event.getInventory().getItem(slot) != null)
		{
			Inventory inventory = event.getInventory();

			if (inventory.getTitle().equalsIgnoreCase("Main Menu:"))
			{
				event.setCancelled(true);

				Map.Entry<PunishmentType, Tuple<Integer, ItemStack>> entry = PunishmentsGUIConfig.getMainMenuItems(player.getName(), "", "").entrySet().stream().filter(k -> {
					if (k.getValue().getFirst() == slot)
					{
						return true;
					}

					return false;
				}).findAny().orElse(null);

				if (entry == null)
				{
					return;
				}

				PunishmentType type = entry.getKey();

				if (type == null)
				{
					return;
				}
				else if (type == PunishmentType.BAN && event.getClick() == ClickType.SHIFT_RIGHT)
				{
					type = PunishmentType.UNBAN;
				}
				else if (type == PunishmentType.MUTE && event.getClick() == ClickType.SHIFT_RIGHT)
				{
					type = PunishmentType.UNMUTE;
				}
				else if (type == PunishmentType.BAN || type == PunishmentType.TEMPBAN || type == PunishmentType.MUTE)
				{
					Tuple<String, String> args = Punishments.punishmentArgs.get(player.getUniqueId());
					this.plugin.getHandler().openTimeDialog(player, args.getFirst(), args.getSecond(), type);
					return;
				}
				else if (type == PunishmentType.HISTORY)
				{
					try
					{
						Tuple<String, String> args = Punishments.punishmentArgs.get(player.getUniqueId());
						UUID uuid = UUIDFetcher.getUUIDOf(args.getFirst());

						if (uuid != null)
							this.plugin.getHandler().openHistoryDialog(player, args.getFirst(), uuid);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}

					return;
				}

				Tuple<String, String> args = Punishments.punishmentArgs.get(player.getUniqueId());

				if (Punishments.punishmentTypes.containsKey(player.getUniqueId()))
				{
					Punishments.punishmentTypes.remove(player.getUniqueId());
				}

				Punishments.punishmentTypes.put(player.getUniqueId(), Tuple.of(Optional.empty(), type));
				this.plugin.getHandler().openConfirmDialog(player, args.getFirst(), args.getSecond(), Optional.empty());
			}
			else if (inventory.getTitle().equals("Time Selector:"))
			{
				event.setCancelled(true);

				Map.Entry<String, Tuple<Integer, ItemStack>> entry = PunishmentsGUIConfig.getTimeDialogItems(player.getName(), "", "").entrySet().stream().filter(k -> {
					if (k.getValue().getFirst() == slot)
					{
						return true;
					}

					return false;
				}).findAny().orElse(null);

				if (entry == null)
				{
					return;
				}

				Tuple<String, String> args = Punishments.punishmentArgs.get(player.getUniqueId());
				Tuple<Optional<String>, PunishmentType> type = Punishments.punishmentTypes.get(player.getUniqueId());

				if (type.getSecond() == PunishmentType.BAN && !entry.getKey().equals("perm"))
				{
					type = Tuple.of(Optional.of(entry.getKey()), PunishmentType.TEMPBAN);
				}
				else if (entry.getKey().equals("perm"))
				{
					type = Tuple.of(Optional.empty(), type.getSecond());
				}
				else
				{
					type = Tuple.of(Optional.of(entry.getKey()), type.getSecond());
				}

				if (Punishments.punishmentTypes.containsKey(player.getUniqueId()))
				{
					Punishments.punishmentTypes.remove(player.getUniqueId());
				}

				Punishments.punishmentTypes.put(player.getUniqueId(), type);
				this.plugin.getHandler().openConfirmDialog(player, args.getFirst(), args.getSecond(), type.getFirst());
			}
			else if (inventory.getTitle().equals("Confirm:"))
			{
				event.setCancelled(true);

				Map.Entry<String, Tuple<Integer, ItemStack>> entry = PunishmentsGUIConfig.getConfirmScreenItems(player.getName(), "", "", Optional.empty()).entrySet().stream().filter(k -> {
					if (k.getValue().getFirst() == slot)
					{
						return true;
					}

					return false;
				}).findAny().orElse(null);

				if (entry == null)
				{
					return;
				}

				if (entry.getKey().equals("confirm"))
				{
					Tuple<String, String> args = Punishments.punishmentArgs.get(player.getUniqueId());
					Tuple<Optional<String>, PunishmentType> type = Punishments.punishmentTypes.get(player.getUniqueId());
					plugin.exec.executePunishment(player, type.getSecond(), args.getFirst(), args.getSecond(), type.getFirst());
					player.closeInventory();
				}
				else
				{
					player.closeInventory();
				}
			}
			else if (inventory.getTitle().equals("History:"))
			{
				event.setCancelled(true);

				if (event.getClick() == ClickType.SHIFT_RIGHT)
				{
					try
					{
						Tuple<String, String> args = Punishments.punishmentArgs.get(player.getUniqueId());
						UUID uuid = UUIDFetcher.getUUIDOf(args.getFirst());

						if (uuid != null)
						{
							List<Punishment> punishments = this.plugin.getDatabaseManager().getPlayerPunishments(args.getFirst(), uuid);

							if ((slot - 9) < punishments.size())
							{
								Punishment punishment = punishments.get(slot - 9);
								this.plugin.getDatabaseManager().removePunishment(punishment);
								this.plugin.getHandler().openHistoryDialog(player, args.getFirst(), uuid);
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
}
