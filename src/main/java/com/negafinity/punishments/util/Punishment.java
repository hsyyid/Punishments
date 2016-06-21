package com.negafinity.punishments.util;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.negafinity.punishments.PunishmentType;
import com.negafinity.punishments.config.PunishmentsGUIConfig;

public class Punishment
{
	private PunishmentType type;
	private String player;
	private UUID uuid;
	private String punisher;
	private String reason;
	private Date issued;
	private Optional<Date> expiration;

	public Punishment(PunishmentType type, String player, UUID uuid, String punisher, String reason, Date issued)
	{
		this.type = type;
		this.player = player;
		this.uuid = uuid;
		this.punisher = punisher;
		this.reason = reason;
		this.issued = issued;
		this.expiration = Optional.empty();
	}

	public Punishment(PunishmentType type, String player, UUID uuid, String punisher, String reason, Date issued, Optional<Date> expiration)
	{
		this.type = type;
		this.player = player;
		this.uuid = uuid;
		this.punisher = punisher;
		this.reason = reason;
		this.issued = issued;
		this.expiration = expiration;
	}

	public PunishmentType getType()
	{
		return type;
	}

	public String getPlayer()
	{
		return player;
	}

	public UUID getUniqueId()
	{
		return uuid;
	}

	public String getPunisher()
	{
		return punisher;
	}

	public String getReason()
	{
		return reason;
	}

	public Date getIssued()
	{
		return issued;
	}

	public Optional<Date> getExpiration()
	{
		return expiration;
	}

	@SuppressWarnings("deprecation")
	public ItemStack getItemStack()
	{
		ItemStack stack = PunishmentsGUIConfig.getItemStack("mainmenu", this.type.name()).getSecond();
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(this.type.name().substring(0, 1).toUpperCase() + this.type.name().substring(1).toLowerCase());
		List<String> lore = Lists.newArrayList("Issued By: " + this.punisher, "Reason: " + ChatColor.translateAlternateColorCodes('&', this.reason), "Issued On: " + this.getIssued().toGMTString());

		if (this.expiration.isPresent())
		{
			lore.add("Expiration: " + this.expiration.get().toGMTString());
		}

		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}
}
