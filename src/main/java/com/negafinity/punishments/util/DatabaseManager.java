package com.negafinity.punishments.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.google.common.collect.Lists;
import com.negafinity.punishments.PunishmentType;
import com.negafinity.punishments.Punishments;

public class DatabaseManager
{
	private Punishments plugin;
	private Connection conn;

	public DatabaseManager(Punishments plugin)
	{
		this.plugin = plugin;
	}

	public Connection getConnection()
	{
		if (this.conn == null)
		{
			String db = this.plugin.getConfig().getString("mysql.mysqldb");
			String ip = this.plugin.getConfig().getString("mysql.mysqlip");
			String port = this.plugin.getConfig().getString("mysql.mysqlport");
			String user = this.plugin.getConfig().getString("mysql.mysqluser");
			String pass = this.plugin.getConfig().getString("mysql.mysqlpass");
			try
			{
				this.conn = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + db, user, pass);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return this.conn;
	}

	public void checkDatabase()
	{
		try
		{
			Connection con = getConnection();
			Statement stmt = con.createStatement();

			String sql = "CREATE TABLE IF NOT EXISTS MUTED (" + "player    VARCHAR(255) NOT NULL," + "punisher  TEXT         NOT NULL," + "issued    TEXT         NOT NULL," + "reason    TEXT         NOT NULL," + "expired   BOOLEAN      NOT NULL)";

			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS TEMPMUTED (" + "player    VARCHAR(255) NOT NULL," + "time      TEXT         NOT NULL," + "punisher  TEXT         NOT NULL," + "issued    TEXT         NOT NULL," + "reason    TEXT         NOT NULL)";

			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS KICKS (" + "player    VARCHAR(255) NOT NULL," + "punisher  TEXT         NOT NULL," + "issued    TEXT         NOT NULL," + "reason    TEXT         NOT NULL)";

			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS BANNED (" + "player    VARCHAR(255) NOT NULL, " + "ip        TEXT         NOT NULL," + "punisher  TEXT         NOT NULL," + "issued    TEXT         NOT NULL," + "reason    TEXT         NOT NULL," + "expired   BOOLEAN      NOT NULL)";

			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS TEMPBANNED (" + "player    VARCHAR(255)   NOT NULL," + "ip        VARCHAR(255)   NOT NULL," + "time      VARCHAR(255)   NOT NULL," + "punisher  TEXT           NOT NULL," + "issued    TEXT           NOT NULL," + "reason    TEXT           NOT NULL)";

			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS WARNS (" + "player    VARCHAR(255)  NOT NULL," + "punisher  TEXT          NOT NULL," + "issued    TEXT          NOT NULL," + "reason    TEXT          NOT NULL)";

			stmt.executeUpdate(sql);

			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void addKick(UUID uuid, String punisher, String reason)
	{
		try
		{
			Connection con = this.getConnection();
			PreparedStatement stmt = con.prepareStatement("INSERT INTO KICKS VALUES(?,?,?,?)");
			stmt.setString(1, uuid.toString());
			stmt.setString(2, punisher);
			stmt.setString(3, Calendar.getInstance().getTime().toGMTString());
			stmt.setString(4, reason);
			stmt.executeUpdate();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void mutePlayer(UUID uuid, String punisher, String reason, Optional<String> time)
	{
		try
		{
			Connection con = this.getConnection();

			if (!time.isPresent())
			{
				PreparedStatement stmt = con.prepareStatement("INSERT INTO MUTED VALUES(?,?,?,?,?)");
				stmt.setString(1, uuid.toString());
				stmt.setString(2, punisher);
				stmt.setString(3, Calendar.getInstance().getTime().toGMTString());
				stmt.setString(4, reason);
				stmt.setBoolean(5, false);
				stmt.executeUpdate();
				stmt.close();
			}
			else
			{
				PreparedStatement stmt = con.prepareStatement("INSERT INTO TEMPMUTED VALUES(?,?,?,?,?)");
				stmt.setString(1, uuid.toString());
				stmt.setString(2, Utils.getDateString(time.get()));
				stmt.setString(3, punisher);
				stmt.setString(4, Calendar.getInstance().getTime().toGMTString());
				stmt.setString(5, reason);
				stmt.executeUpdate();
				stmt.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public boolean isPlayerMuted(UUID uuid)
	{
		boolean muted = false;

		try
		{
			Connection con = this.getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM MUTED WHERE player=?");
			stmt.setString(1, uuid.toString());
			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				boolean expired = rs.getBoolean("expired");

				if (expired)
				{
					muted = true;
					break;
				}
			}

			rs.close();
			stmt.close();

			if (!muted)
			{
				stmt = con.prepareStatement("SELECT * FROM TEMPMUTED WHERE player=?");
				stmt.setString(1, uuid.toString());
				rs = stmt.executeQuery();

				while (rs.next())
				{
					Calendar cal = Calendar.getInstance();
					Date date = cal.getTime();
					Date endDate = Utils.getDateFromString(rs.getString("time"));

					if (date.before(endDate))
					{
						muted = true;
						break;
					}
				}

				rs.close();
				stmt.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return muted;
	}

	public boolean isPlayerBanned(String ip)
	{
		boolean banned = false;

		try
		{
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM BANNED WHERE ip=?");
			stmt.setString(1, ip);
			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				boolean expired = rs.getBoolean("expired");

				if (expired)
				{
					banned = true;
					break;
				}
			}

			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return banned;
	}

	public boolean isPlayerTempBanned(String ip)
	{
		boolean banned = false;

		try
		{
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM TEMPBANNED WHERE ip=?");
			stmt.setString(1, ip);
			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				Calendar cal = Calendar.getInstance();
				Date date = cal.getTime();
				Date endDate = Utils.getDateFromString(rs.getString("time"));

				if (date.before(endDate))
				{
					banned = true;
					break;
				}
			}

			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return banned;
	}

	public boolean isPlayerBanned(String database, UUID uuid)
	{
		boolean banned = false;

		try
		{
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM " + database + " WHERE player=?");
			stmt.setString(1, uuid.toString());
			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				if (database.equals("TEMPBANNED"))
				{
					Calendar cal = Calendar.getInstance();
					Date date = cal.getTime();
					Date endDate = Utils.getDateFromString(rs.getString("time"));

					if (date.before(endDate))
					{
						banned = true;
						break;
					}
				}
				else
				{
					boolean expired = rs.getBoolean("expired");

					if (expired)
					{
						banned = true;
						break;
					}
				}
			}

			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return banned;
	}

	public List<Punishment> getPlayerPunishments(String player, UUID uuid)
	{
		List<Punishment> punishments = Lists.newArrayList();

		try
		{
			Connection con = this.getConnection();

			// Warnings
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM WARNS WHERE player=?");
			stmt.setString(1, uuid.toString());
			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				String punisher = rs.getString("punisher");
				String reason = rs.getString("reason");
				Date issued = Utils.getDateFromString(rs.getString("issued").replace(" GMT", ""));
				punishments.add(new Punishment(PunishmentType.WARN, player, uuid, punisher, reason, issued));
			}

			rs.close();
			stmt.close();

			// Kicks
			stmt = con.prepareStatement("SELECT * FROM KICKS WHERE player=?");
			stmt.setString(1, uuid.toString());
			rs = stmt.executeQuery();

			while (rs.next())
			{
				String punisher = rs.getString("punisher");
				String reason = rs.getString("reason");
				Date issued = Utils.getDateFromString(rs.getString("issued").replace(" GMT", ""));
				punishments.add(new Punishment(PunishmentType.KICK, player, uuid, punisher, reason, issued));
			}

			rs.close();
			stmt.close();

			// BANS
			stmt = con.prepareStatement("SELECT * FROM BANNED WHERE player=?");
			stmt.setString(1, uuid.toString());
			rs = stmt.executeQuery();

			while (rs.next())
			{
				String punisher = rs.getString("punisher");
				String reason = rs.getString("reason");
				Date issued = Utils.getDateFromString(rs.getString("issued").replace(" GMT", ""));
				punishments.add(new Punishment(PunishmentType.BAN, player, uuid, punisher, reason, issued));
			}

			rs.close();
			stmt.close();

			// TEMPBANS
			stmt = con.prepareStatement("SELECT * FROM TEMPBANNED WHERE player=?");
			stmt.setString(1, uuid.toString());
			rs = stmt.executeQuery();

			while (rs.next())
			{
				String punisher = rs.getString("punisher");
				String reason = rs.getString("reason");
				Date issued = Utils.getDateFromString(rs.getString("issued").replace(" GMT", ""));
				Date expiration = Utils.getDateFromString(rs.getString("time"));
				punishments.add(new Punishment(PunishmentType.BAN, player, uuid, punisher, reason, issued, Optional.of(expiration)));
			}

			rs.close();
			stmt.close();

			// MUTES
			stmt = con.prepareStatement("SELECT * FROM MUTED WHERE player=?");
			stmt.setString(1, uuid.toString());
			rs = stmt.executeQuery();

			while (rs.next())
			{
				String punisher = rs.getString("punisher");
				String reason = rs.getString("reason");
				Date issued = Utils.getDateFromString(rs.getString("issued").replace(" GMT", ""));
				punishments.add(new Punishment(PunishmentType.MUTE, player, uuid, punisher, reason, issued));
			}

			rs.close();
			stmt.close();

			// TEMPMUTES
			stmt = con.prepareStatement("SELECT * FROM TEMPMUTED WHERE player=?");
			stmt.setString(1, uuid.toString());
			rs = stmt.executeQuery();

			while (rs.next())
			{
				String punisher = rs.getString("punisher");
				String reason = rs.getString("reason");
				Date issued = Utils.getDateFromString(rs.getString("issued").replace(" GMT", ""));
				Date expiration = Utils.getDateFromString(rs.getString("time"));
				System.out.println(uuid);
				punishments.add(new Punishment(PunishmentType.MUTE, player, uuid, punisher, reason, issued, Optional.of(expiration)));
			}

			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		punishments.sort(new Comparator<Punishment>()
		{
			@Override
			public int compare(Punishment o1, Punishment o2)
			{
				return o1.getIssued().compareTo(o2.getIssued());
			}
		});

		return punishments;
	}

	@SuppressWarnings("deprecation")
	public void addWarning(UUID uuid, String punisher, String reason)
	{
		try
		{
			Connection con = this.getConnection();
			PreparedStatement stmt = con.prepareStatement("INSERT INTO WARNS VALUES(?,?,?,?)");
			stmt.setString(1, uuid.toString());
			stmt.setString(2, punisher);
			stmt.setString(3, Calendar.getInstance().getTime().toGMTString());
			stmt.setString(4, reason);
			stmt.executeUpdate();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void unbanPlayer(String playerName, UUID uuid)
	{
		if (isPlayerBanned("BANNED", uuid))
		{
			try
			{
				Connection con = getConnection();
				PreparedStatement stmt = con.prepareStatement("UPDATE BANNED SET expired=? WHERE player=?");
				stmt.setBoolean(1, true);
				stmt.setString(2, uuid.toString());
				stmt.executeUpdate();
				this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:pardon " + playerName);
				stmt.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				Date date = Calendar.getInstance().getTime();
				SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
				format.setTimeZone(TimeZone.getTimeZone("GMT"));
				Connection con = getConnection();
				PreparedStatement stmt = con.prepareStatement("UPDATE TEMPBANNED SET time=? WHERE player=?");
				stmt.setString(1, format.format(date));
				stmt.setString(2, uuid.toString());
				stmt.executeUpdate();
				stmt.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void unmutePlayer(UUID uuid)
	{
		try
		{
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM TEMPMUTED WHERE player=?");
			stmt.setString(1, uuid.toString());
			ResultSet rs = stmt.executeQuery();

			if (rs.next())
			{
				rs.close();
				stmt.close();
				stmt = con.prepareStatement("UPDATE TEMPMUTED SET time=? WHERE player=?");
				stmt.setBoolean(1, true);
				stmt.setString(2, uuid.toString());
				stmt.executeUpdate();
				stmt.close();
			}
			else
			{
				rs.close();
				stmt.close();
				stmt = con.prepareStatement("UPDATE MUTED SET expired=? WHERE player=?");
				stmt.setBoolean(1, true);
				stmt.setString(2, uuid.toString());
				stmt.executeUpdate();
				stmt.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void banPlayer(UUID uuid, String ip, String name, String reason)
	{
		try
		{
			Connection con = this.plugin.databaseManager.getConnection();
			PreparedStatement stmt = con.prepareStatement("INSERT INTO BANNED VALUES(?,?,?,?,?,?)");
			stmt.setString(1, uuid.toString());
			stmt.setString(2, ip);
			stmt.setString(3, name);
			stmt.setString(4, Calendar.getInstance().getTime().toGMTString());
			stmt.setString(5, reason);
			stmt.setBoolean(6, false);
			stmt.executeUpdate();

			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void tempbanPlayer(UUID uuid, String ip, String date, String punisher, String reason)
	{
		try
		{
			Connection con = this.plugin.getDatabaseManager().getConnection();
			PreparedStatement stmt = con.prepareStatement("INSERT INTO TEMPBANNED VALUES(?,?,?,?,?,?)");
			stmt.setString(1, uuid.toString());
			stmt.setString(2, ip);
			stmt.setString(3, date);
			stmt.setString(4, punisher);
			stmt.setString(5, Calendar.getInstance().getTime().toGMTString());
			stmt.setString(6, reason);
			stmt.executeUpdate();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "incomplete-switch", "deprecation" })
	public void removePunishment(Punishment punishment)
	{
		try
		{
			switch (punishment.getType())
			{
				case BAN: {
					if (punishment.getExpiration().isPresent())
					{
						Connection con = this.getConnection();
						PreparedStatement stmt = con.prepareStatement("DELETE FROM TEMPBANNED WHERE player=? AND issued=?");
						stmt.setString(1, punishment.getUniqueId().toString());
						stmt.setString(2, punishment.getIssued().toGMTString());
						stmt.execute();
						stmt.close();
					}
					else
					{
						Connection con = this.getConnection();
						PreparedStatement stmt = con.prepareStatement("DELETE FROM BANNED WHERE player=? AND issued=?");
						stmt.setString(1, punishment.getUniqueId().toString());
						stmt.setString(2, punishment.getIssued().toGMTString());
						stmt.execute();
						stmt.close();
					}

					break;
				}
				case MUTE: {
					if (punishment.getExpiration().isPresent())
					{
						Connection con = this.getConnection();
						PreparedStatement stmt = con.prepareStatement("DELETE FROM TEMPMUTED WHERE player=? AND issued=?");
						stmt.setString(1, punishment.getUniqueId().toString());
						stmt.setString(2, punishment.getIssued().toGMTString());
						stmt.execute();
						stmt.close();
					}
					else
					{
						Connection con = this.getConnection();
						PreparedStatement stmt = con.prepareStatement("DELETE FROM MUTED WHERE player=? AND issued=?");
						stmt.setString(1, punishment.getUniqueId().toString());
						stmt.setString(2, punishment.getIssued().toGMTString());
						stmt.execute();
						stmt.close();
					}

					break;
				}
				case KICK: {
					Connection con = this.getConnection();
					PreparedStatement stmt = con.prepareStatement("DELETE FROM KICKS WHERE player=? AND issued=?");
					stmt.setString(1, punishment.getUniqueId().toString());
					stmt.setString(2, punishment.getIssued().toGMTString());
					stmt.execute();
					stmt.close();
					break;
				}
				case WARN: {
					Connection con = this.getConnection();
					PreparedStatement stmt = con.prepareStatement("DELETE FROM WARNS WHERE player=? AND issued=?");
					stmt.setString(1, punishment.getUniqueId().toString());
					stmt.setString(2, punishment.getIssued().toGMTString());
					stmt.execute();
					stmt.close();
					break;
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
