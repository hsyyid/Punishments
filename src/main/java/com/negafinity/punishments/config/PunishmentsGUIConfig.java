package com.negafinity.punishments.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.negafinity.punishments.PunishmentType;
import com.negafinity.punishments.Punishments;
import com.negafinity.punishments.util.Tuple;
import com.negafinity.punishments.util.Utils;

public class PunishmentsGUIConfig
{
	private Punishments plugin;

	private static File configFile;
	private static FileConfiguration config;

	public PunishmentsGUIConfig(Punishments plugin)
	{
		this.plugin = plugin;
		this.loadConfig();
	}

	private void loadConfig()
	{
		configFile = new File(plugin.getDataFolder(), "gui.yml");

		if (!plugin.getDataFolder().exists())
		{
			plugin.getDataFolder().mkdir();
		}

		if (!configFile.exists())
		{
			try
			{
				configFile.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			config = YamlConfiguration.loadConfiguration(configFile);

			// Main menu
			config.set("Punishments.gui.mainmenu.head.slot", 31);

			config.set("Punishments.gui.mainmenu.history.item", "STRING");
			config.set("Punishments.gui.mainmenu.history.name", "History");
			config.set("Punishments.gui.mainmenu.history.lore", "View History");
			config.set("Punishments.gui.mainmenu.history.slot", 0);

			config.set("Punishments.gui.mainmenu.kick.item", "STRING");
			config.set("Punishments.gui.mainmenu.kick.name", "Kick");
			config.set("Punishments.gui.mainmenu.kick.lore", "Kick Player");
			config.set("Punishments.gui.mainmenu.kick.slot", 38);

			config.set("Punishments.gui.mainmenu.ban.item", "STRING");
			config.set("Punishments.gui.mainmenu.ban.name", "Ban");
			config.set("Punishments.gui.mainmenu.ban.lore", "Ban Player");
			config.set("Punishments.gui.mainmenu.ban.slot", 42);

			config.set("Punishments.gui.mainmenu.mute.item", "STRING");
			config.set("Punishments.gui.mainmenu.mute.name", "Mute");
			config.set("Punishments.gui.mainmenu.mute.lore", "Mute Player");
			config.set("Punishments.gui.mainmenu.mute.slot", 24);

			config.set("Punishments.gui.mainmenu.warn.item", "STRING");
			config.set("Punishments.gui.mainmenu.warn.name", "Warn");
			config.set("Punishments.gui.mainmenu.warn.lore", "Warn Player");
			config.set("Punishments.gui.mainmenu.warn.slot", 20);

			// Time selector menu
			config.set("Punishments.gui.timeselector.head.slot", 4);

			config.set("Punishments.gui.timeselector.00:00:01.item", "STRING");
			config.set("Punishments.gui.timeselector.00:00:01.name", "1 Minute");
			config.set("Punishments.gui.timeselector.00:00:01.lore", "");
			config.set("Punishments.gui.timeselector.00:00:01.slot", 10);

			config.set("Punishments.gui.timeselector.00:00:05.item", "STRING");
			config.set("Punishments.gui.timeselector.00:00:05.name", "5 Minutes");
			config.set("Punishments.gui.timeselector.00:00:05.lore", "");
			config.set("Punishments.gui.timeselector.00:00:05.slot", 11);

			config.set("Punishments.gui.timeselector.00:00:10.item", "STRING");
			config.set("Punishments.gui.timeselector.00:00:10.name", "10 Minutes");
			config.set("Punishments.gui.timeselector.00:00:10.lore", "");
			config.set("Punishments.gui.timeselector.00:00:10.slot", 12);

			config.set("Punishments.gui.timeselector.00:00:30.item", "STRING");
			config.set("Punishments.gui.timeselector.00:00:30.name", "30 Minutes");
			config.set("Punishments.gui.timeselector.00:00:30.lore", "");
			config.set("Punishments.gui.timeselector.00:00:30.slot", 13);

			config.set("Punishments.gui.timeselector.00:01:00.item", "STRING");
			config.set("Punishments.gui.timeselector.00:01:00.name", "1 Hour");
			config.set("Punishments.gui.timeselector.00:01:00.lore", "");
			config.set("Punishments.gui.timeselector.00:01:00.slot", 14);

			config.set("Punishments.gui.timeselector.00:06:00.item", "STRING");
			config.set("Punishments.gui.timeselector.00:06:00.name", "6 Hours");
			config.set("Punishments.gui.timeselector.00:06:00.lore", "");
			config.set("Punishments.gui.timeselector.00:06:00.slot", 15);

			config.set("Punishments.gui.timeselector.00:12:00.item", "STRING");
			config.set("Punishments.gui.timeselector.00:12:00.name", "12 Hours");
			config.set("Punishments.gui.timeselector.00:12:00.lore", "");
			config.set("Punishments.gui.timeselector.00:12:00.slot", 16);

			config.set("Punishments.gui.timeselector.01:00:00.item", "STRING");
			config.set("Punishments.gui.timeselector.01:00:00.name", "1 Day");
			config.set("Punishments.gui.timeselector.01:00:00.lore", "");
			config.set("Punishments.gui.timeselector.01:00:00.slot", 19);

			config.set("Punishments.gui.timeselector.07:00:00.item", "STRING");
			config.set("Punishments.gui.timeselector.07:00:00.name", "1 Week");
			config.set("Punishments.gui.timeselector.07:00:00.lore", "");
			config.set("Punishments.gui.timeselector.07:00:00.slot", 20);

			config.set("Punishments.gui.timeselector.perm.item", "STRING");
			config.set("Punishments.gui.timeselector.perm.name", "Permanent");
			config.set("Punishments.gui.timeselector.perm.lore", "");
			config.set("Punishments.gui.timeselector.perm.slot", 22);

			config.set("Punishments.gui.timeselector.00:14:00.item", "STRING");
			config.set("Punishments.gui.timeselector.00:14:00.name", "2 Weeks");
			config.set("Punishments.gui.timeselector.00:14:00.lore", "");
			config.set("Punishments.gui.timeselector.00:14:00.slot", 24);

			config.set("Punishments.gui.timeselector.31:00:00.item", "STRING");
			config.set("Punishments.gui.timeselector.31:00:00.name", "1 Month");
			config.set("Punishments.gui.timeselector.31:00:00.lore", "");
			config.set("Punishments.gui.timeselector.31:00:00.slot", 25);

			// Confirm Screen
			config.set("Punishments.gui.confirm.head.slot", 13);

			config.set("Punishments.gui.confirm.confirm.item", "STRING");
			config.set("Punishments.gui.confirm.confirm.name", "Confirm");
			config.set("Punishments.gui.confirm.confirm.lore", "");
			config.set("Punishments.gui.confirm.confirm.slot", 11);

			config.set("Punishments.gui.confirm.decline.item", "STRING");
			config.set("Punishments.gui.confirm.decline.name", "Decline");
			config.set("Punishments.gui.confirm.decline.lore", "");
			config.set("Punishments.gui.confirm.decline.slot", 15);

			try
			{
				config.save(configFile);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			config = YamlConfiguration.loadConfiguration(configFile);
		}
	}

	public static Map<String, Tuple<Integer, ItemStack>> getConfirmScreenItems(String target, String punisher, String reason, Optional<String> time)
	{
		Map<String, Tuple<Integer, ItemStack>> items = Maps.newHashMap();

		for (String option : config.getConfigurationSection("Punishments.gui.confirm").getKeys(false))
		{
			if (option.equals("head"))
				continue;

			items.put(option, PunishmentsGUIConfig.getItemStack("confirm", option));
		}

		items.put(null, Tuple.of(config.getInt("Punishments.gui.confirm.head.slot"), Utils.createPlayerSkull(target, punisher, reason, time)));

		return items;
	}

	public static Map<String, Tuple<Integer, ItemStack>> getTimeDialogItems(String target, String punisher, String reason)
	{
		Map<String, Tuple<Integer, ItemStack>> items = Maps.newHashMap();

		for (String time : config.getConfigurationSection("Punishments.gui.timeselector").getKeys(false))
		{
			if (time.equals("head"))
				continue;

			items.put(time, PunishmentsGUIConfig.getItemStack("timeselector", time));
		}

		items.put(null, Tuple.of(config.getInt("Punishments.gui.timeselector.head.slot"), Utils.createPlayerSkull(target, punisher, reason, Optional.empty())));

		return items;
	}

	public static Map<PunishmentType, Tuple<Integer, ItemStack>> getMainMenuItems(String target, String punisher, String reason)
	{
		Map<PunishmentType, Tuple<Integer, ItemStack>> items = Maps.newHashMap();

		List<PunishmentType> punishments = Lists.newArrayList(PunishmentType.values());
		punishments.removeIf(p -> p == PunishmentType.UNBAN || p == PunishmentType.TEMPBAN || p == PunishmentType.UNMUTE);

		for (PunishmentType type : punishments)
		{
			items.put(type, PunishmentsGUIConfig.getItemStack("mainmenu", type.name()));
		}

		items.put(null, Tuple.of(config.getInt("Punishments.gui.mainmenu.head.slot"), Utils.createPlayerSkull(target, punisher, reason, Optional.empty())));

		return items;
	}

	@SuppressWarnings("deprecation")
	public static Tuple<Integer, ItemStack> getItemStack(String menu, String type)
	{
		String[] item = config.getString("Punishments.gui." + menu + "." + type.toLowerCase() + ".item").split(":");
		ItemStack stack = new ItemStack(Material.valueOf(item[0]));
		ItemMeta meta = stack.getItemMeta();

		if (item.length > 1)
		{
			MaterialData data = stack.getData();
			data.setData((byte) Integer.parseInt(item[1]));
			stack.setData(data);
		}

		meta.setDisplayName(PunishmentsGUIConfig.getItemName(menu, type));
		List<String> lore = Lists.newArrayList();
		lore.addAll(Lists.newArrayList(PunishmentsGUIConfig.getItemLore(menu, type)));
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return Tuple.of(config.getInt("Punishments.gui." + menu + "." + type.toLowerCase() + ".slot"), stack);
	}

	public static String getItemName(String menu, String type)
	{
		return ChatColor.translateAlternateColorCodes('&', config.getString("Punishments.gui." + menu + "." + type.toLowerCase() + ".name"));
	}

	public static String[] getItemLore(String menu, String type)
	{
		return ChatColor.translateAlternateColorCodes('&', config.getString("Punishments.gui." + menu + "." + type.toLowerCase() + ".lore")).split(";");
	}

	public FileConfiguration getConfig()
	{
		return config;
	}
}
