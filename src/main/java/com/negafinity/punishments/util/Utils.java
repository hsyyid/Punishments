package com.negafinity.punishments.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.collect.Lists;

public class Utils
{
	public static String getDateString(String time) throws IOException
	{
		String[] tokens = time.split(":");
		int days = Integer.parseInt(tokens[0]);
		int hours = Integer.parseInt(tokens[1]);
		int minutes = Integer.parseInt(tokens[2]);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, days);
		cal.add(Calendar.HOUR_OF_DAY, hours);
		cal.add(Calendar.MINUTE, minutes);

		Date date = cal.getTime();
		SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		return format.format(date);
	}

	public static Date getDateFromString(String s)
	{
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
			format.setTimeZone(TimeZone.getTimeZone("GMT"));
			return format.parse(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static ItemStack createPlayerSkull(String target)
	{
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(target);
		meta.setDisplayName(ChatColor.LIGHT_PURPLE + target);
		skull.setItemMeta(meta);

		return skull;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack createPlayerSkull(String target, String punisher, String reason, Optional<String> expiration)
	{
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(target);
		meta.setDisplayName(ChatColor.LIGHT_PURPLE + target);
		List<String> lore = Lists.newArrayList("Name: " + target, "Punisher: " + punisher, "Time: " + Calendar.getInstance().getTime().toGMTString(), "Reason: " + ChatColor.translateAlternateColorCodes('&', reason));

		if (expiration.isPresent())
		{
			try
			{
				lore.add("Expiration: " + Utils.getDateString(expiration.get()));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		meta.setLore(lore);
		skull.setItemMeta(meta);

		return skull;
	}
}
