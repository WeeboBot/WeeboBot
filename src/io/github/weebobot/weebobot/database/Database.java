/*	  It's a Twitch bot, because we can.
 *    Copyright (C) 2015  Timothy Chandler, James Wolff
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.weebobot.weebobot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.weebobot.weebobot.Main;
import io.github.weebobot.weebobot.twitch.TwitchUtilities;
import io.github.weebobot.weebobot.util.TOptions;
import io.github.weebobot.weebobot.util.ULevel;
import io.github.weebobot.weebobot.util.WLogger;

public class Database {

	private static Connection conn;

	private static final String URL = "jdbc:mysql://localhost:3306/weebo?";

	public static final String DATABASE = "weebo";

	static final Logger logger = Logger.getLogger(Database.class + "");

	/**
	 * Creates a connection to the database.
	 * 
	 * @return - true if connection is successful
	 */
	public static boolean initDBConnection(String pass) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			logger.log(
					Level.SEVERE,
					"Unable to find Driver in classpath!"
							,e);
			WLogger.logError(e);
		}
		try {
			conn = DriverManager.getConnection(String.format("%suser=bot&password=%s", URL, pass));
		} catch (SQLException e) {
			logger.log(
					Level.SEVERE,
					"Unable to connect to the database!!"
							,e);
			WLogger.logError(e);
			return false;
		}
		return true;
	}

	/**
	 * Creates the tables for the provided channel
	 * 
	 * @param channelNoHash - the channel we are connecting to.
	 * @return - true if it has to create the tables
	 */
	public static boolean getChannelTables(String channelNoHash) {
		Statement stmt;
		Statement stmt1;
		Statement stmt2;
		Statement stmt3;
		Statement stmt4;
		Statement stmt5;
		try {
			stmt = conn.createStatement();
			stmt.closeOnCompletion();
			stmt.executeQuery(String.format("SELECT * FROM %s.%sOptions", DATABASE, channelNoHash));
			return false;
		} catch (SQLException e) {
			try {
				stmt1 = conn.createStatement();
				stmt1.closeOnCompletion();
				stmt1.executeUpdate(String.format("CREATE TABLE %s.%sOptions(optionID varchar(50), value varchar(4000), PRIMARY KEY (optionID))", DATABASE, channelNoHash));
			} catch (SQLException ex) {
				logger.log(Level.SEVERE, String.format("Unable to create table %sOptions!", channelNoHash), ex );
				WLogger.logError(e);
			}
			try {
				stmt2 = conn.createStatement();
				stmt2.closeOnCompletion();
				stmt2.executeUpdate(String.format("CREATE TABLE %s.%sSpam(emote BOOLEAN, word varchar(25), PRIMARY KEY (word))", DATABASE, channelNoHash));
			} catch (SQLException ex) {
				logger.log(Level.SEVERE, String.format("Unable to create table %sSpam!", channelNoHash), ex);
				WLogger.logError(e);
			}
			try{
                stmt3=conn.createStatement();
                stmt3.closeOnCompletion();
                stmt3.executeUpdate(String.format("CREATE TABLE %s.%sPoints(userID varchar(25), points INTEGER, visibility BOOLEAN, PRIMARY KEY (userID))", DATABASE, channelNoHash));
            }catch(SQLException ex){
                logger.log(Level.SEVERE, "Unable to create table Points!", ex);
    			WLogger.logError(e);
            }
            try{
                stmt4=conn.createStatement();
                stmt4.closeOnCompletion();
                stmt4.executeUpdate(String.format("CREATE TABLE %s.%sUsers(userID varchar(25), userLevel varchar(25), PRIMARY KEY (userID))", DATABASE, channelNoHash));
            }catch(SQLException ex){
                logger.log(Level.SEVERE, "Unable to create table Users!", ex);
    			WLogger.logError(e);
            }
            try{
                stmt5=conn.createStatement();
                stmt5.closeOnCompletion();
                stmt5.executeUpdate(String.format("CREATE TABLE %s.%sCommands(command varchar(25), parameters varchar(255), reply varchar(4000), PRIMARY KEY (command))", DATABASE, channelNoHash));
            }catch(SQLException ex){
                logger.log(Level.SEVERE, "Unable to create table Commands!", ex);
    			WLogger.logError(e);
            }
			return true;
		}
	}

	/**
	 * Sends an update to the database (eg. INSERT, DELETE, etc.)
	 * 
	 * @param sqlCommand
	 * @return - true if it successfully executes the update
	 */
	protected static boolean executeUpdate(String sqlCommand) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.closeOnCompletion();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, String.format("Unable to create connection for SQLCommand: %s", sqlCommand), e);
			WLogger.logError(e);
			return false;
		}
		try {
			stmt.executeUpdate(sqlCommand);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, String.format("Unable to execute statment: %s", sqlCommand), e);
			WLogger.logError(e);
			return false;
		}
		return true;
	}

	/**
	 * Sends a query to the database (eg. SELECT, etc.)
	 * @param sqlQuery
	 * @return
	 */
	protected static ResultSet executeQuery(String sqlQuery) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt=conn.createStatement();
			stmt.closeOnCompletion();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, String.format("Unable to create connection for SQLQuery: %s", sqlQuery), e);
			WLogger.logError(e);
		}
		try {
			rs = stmt.executeQuery(sqlQuery);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, String.format("Unable to execute query: %s", sqlQuery), e);
			WLogger.logError(e);
		}
		return rs;
	}

	/**
	 * Sends an update to the database (eg. INSERT, DELETE, etc.)
	 * 
	 * @param stmt
	 * @return - true if it successfully executes the update
	 */
	protected static boolean executeUpdate(PreparedStatement stmt) {
		try {
			stmt.closeOnCompletion();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, String.format("Unable to create connection for SQLCommand"), e);
			WLogger.logError(e);
			return false;
		}
		try {
			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, String.format("Unable to execute statment"), e);
			WLogger.logError(e);
			return false;
		}
		return true;
	}

	/**
	 * Sends a query to the database (eg. SELECT, etc.)
	 * @param stmt
	 * @return
	 */
	protected static ResultSet executeQuery(PreparedStatement stmt) {
		ResultSet rs = null;
		try {
			stmt.closeOnCompletion();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, String.format("Unable to create connection for SQLQuery"), e);
			WLogger.logError(e);
		}
		try {
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, String.format("Unable to execute query"), e);
			WLogger.logError(e);
		}
		return rs;
	}
	
	/**
	 * @param user - user to get the oauth for
	 * @return oauth code for the specified user
	 */
	public static String getUserOAuth(String user) {
		ResultSet rs=executeQuery(String.format("SELECT * FROM %s.userOAuth WHERE userID=\'%s\'", DATABASE, user));
		try {
			if(rs.next()) {
				return rs.getString("oAuth");
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, String.format("An error occurred getting %s\'s OAuth from the database", user), e);
			WLogger.logError(e);
		}
		return null;
	}

	/**
	 * @param channelNoHash - channel to get the option for without the leading #
	 * @param option - Timeout Option
	 * @return value if the option
	 */
	public static String getOption(String channelNoHash, String option) {
		ResultSet rs=executeQuery(String.format("SELECT * FROM %s.%sOptions WHERE optionID=\'%s\'", DATABASE, channelNoHash, option));
		try {
			if(rs.next()) {
				return rs.getString(2);
			}
			return null;
		} catch (SQLException | NumberFormatException e) {
			logger.log(Level.SEVERE, String.format("Unable to get welcome message for %s", channelNoHash), e);
			WLogger.logError(e);
		}
		return null;
	}
	
	/**
	 * @param channelNoHash - channel to get the welcome message for, without the leading #
	 * @return The welcome message
	 */
	public static String getWelcomeMessage(String channelNoHash) {
		ResultSet rs=executeQuery(String.format("SELECT * FROM %s.%sOptions WHERE optionID=\'%s\'", DATABASE, channelNoHash, TOptions.welcomeMessage));
		try {
			if(rs.next()) {
				return rs.getString(2);
			}
			return null;
		} catch (SQLException | NumberFormatException e) {
			logger.log(Level.SEVERE, String.format("Unable to get welcome message for %s", channelNoHash), e);
			WLogger.logError(e);
		}
		return null;
	}

	/**
	 * @param channelNoHash - channel to set the welcome message for, without the leading #
	 * @param option - timeout option
	 * @param value - new welcome message
	 * @return true if the message is set successfully
	 */
	public static boolean setWelcomeMessage(String channelNoHash, TOptions option, String value) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(String.format("UPDATE %s.%sOptions SET optionID=?,value=? WHERE optionID=?", DATABASE, channelNoHash));
			stmt.setString(1, option.getOptionID());
			stmt.setString(2, value);
			stmt.setString(3, option.getOptionID());
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "An error occurred setting the welcome message", e);
			WLogger.logError(e);
		}
		return executeUpdate(stmt);
	}

	/**
	 * @param channelNoHash - channel to set the option for, without the leading #
	 * @param option - timeout option
	 * @param value - value to set for the option
	 * @return true if the message is set successfully
	 */
	public static boolean setOption(String channelNoHash, String option, String value) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(String.format("UPDATE %s.%sOptions SET optionID=?,value=? WHERE optionID=?", DATABASE, channelNoHash));
			stmt.setString(1, option.toLowerCase());
			stmt.setString(2, value+"");
			stmt.setString(3, option.toLowerCase());
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Unable to set option", e);
			WLogger.logError(e);
		}
		return executeUpdate(stmt);
	}
	
	/**
	 * @param channelNoHash - channel to add the option for, without the leading #
	 * @param option - timeout option
	 * @param value - value to set the option to
	 * @return true if the option is added successfully
	 */
	public static boolean addOption(String channelNoHash, String option, String value) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(String.format("INSERT INTO %s.%sOptions VALUES(? , ?)", DATABASE, channelNoHash));
			stmt.setString(1, option.toLowerCase());
			stmt.setString(2, value+"");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Unable to add option", e);
			WLogger.logError(e);
		}
		return executeUpdate(stmt);
	}

	/**
	 * @param moderator - person to check if their a moderator
	 * @param channelNoHash - channel to check if their a moderator in, without the leading #
	 * @return true if user is a moderator in channel
	 */
	public static boolean isMod(String moderator, String channelNoHash) {
		ResultSet rs = executeQuery(String.format("SELECT * FROM %s.%sUsers WHERE userID=\'%s\'", DATABASE, channelNoHash, moderator));
		try {
			if(rs.next()){
				return rs.getString(2).equalsIgnoreCase("moderator");
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, String.format("An error occurred checking if %s is in %s's Mod List.", moderator, channelNoHash), e);
			WLogger.logError(e);
		}
		return false;
	}
	
	/**
	 * @param moderator - moderator to add
	 * @param channelNoHash - channel to add the mod to, without the #
	 */
	public static void addMod(String moderator, String channelNoHash) {
		executeUpdate(String.format("UPDATE %s.%sUsers SET userID=\'%s\',userLevel=\'moderator\' WHERE userID=\'%s\'", DATABASE, channelNoHash, moderator, moderator));
	}
	
	/**
	 * @param channelNoHash - channel to add the auto reply to
	 * @param keywords - keywords to trigger the auto reply
	 * @param reply - auto reply to be sent on trigger
	 */
	public static void addAutoReply(String channelNoHash, String keywords, String reply) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(String.format("INSERT INTO %s.%sCommands VALUES(? , ?, ?)", DATABASE, channelNoHash));
			stmt.setString(1, keywords);
			stmt.setString(2, "");
			stmt.setString(3, reply);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Unable to set option", e);
			WLogger.logError(e);
		}
		executeUpdate(stmt);
	}

	/**
	 * @param channelNoHash - channel to get the auto replies for, without the leading #
	 * @return a result set of the auto replies
	 */
	public static ResultSet getAutoReplies(String channelNoHash) {
		return executeQuery(String.format("SELECT * FROM %s.%sCommands WHERE commands NOT LIKE !%%", DATABASE, channelNoHash));
	}
	
	/**
	 * @param channelNoHash - channel to add the command for, without the leading #
	 * @param command - command to be added
	 * @param parameters - parameters that should be passed
	 * @param reply - reply to be sent on command
	 */
	public static void addCommand(String channelNoHash, String command, String parameters, String reply) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(String.format("INSERT INTO %s.%sCommands VALUES(? , ?, ?)", DATABASE, channelNoHash));
			stmt.setString(1, command);
			stmt.setString(2, parameters);
			stmt.setString(3, reply);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Unable to add command", e);
			WLogger.logError(e);
		}
		executeUpdate(stmt);
	}
	
	/**
	 * @param channelNoHash - channel to get spam for
	 * @return result set of spam words
	 */
	public static ResultSet getSpam(String channelNoHash) {
		return executeQuery(String.format("SELECT * FROM %s.%sSpam", DATABASE, channelNoHash));
	}

	/**
	 * @param moderator - moderator to remove
	 * @param channelNoHash - channel to remove the moderator from, without the leading #
	 * @return true if the moderator is removed
	 */
	public static boolean delModerator(String moderator, String channelNoHash) {
		if(!Main.isDefaultMod(moderator, channelNoHash)) {
			String uLevel=TwitchUtilities.getUserLevelNoMod(channelNoHash, moderator);
			return executeUpdate(String.format("UPDATE %s.%sUsers SET userID=\'%s\',userLevel=\'%s\' WHERE userID=\'%s\'", DATABASE, channelNoHash, moderator, uLevel, moderator));
		}
		return false;
	}

	/**
	 * @param channelNoHash - channel to delete the auto reply from, without the leading #
	 * @param keywords - keywords of the auto reply
	 * @return true if the auto reply is removed
	 */
	public static boolean delAutoReply(String channelNoHash, String keywords) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(String.format("DELETE FROM %s.%sCommands WHERE command=?", DATABASE, channelNoHash));
			stmt.setString(1, keywords);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Unable to set option", e);
			WLogger.logError(e);
		}
		return executeUpdate(stmt);
	}

	/**
	 * @param channelNoHash - channel to get the custom commands for, without the leading #
	 * @return result set of custom commands
	 */
	public static ResultSet getCustomCommands(String channelNoHash) {
		return executeQuery(String.format("SELECT * FROM %s.%sCommands", DATABASE, channelNoHash));
	}

	/**
	 * @param channelNoHash - channel to add spam to, without the leading #
	 * @param word - word to add to the table
	 * @return true if the word is added
	 */
	public static boolean addSpam(String channelNoHash, boolean emote, String word) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(String.format("INSERT INTO %s.%sSpam VALUES(?,?)", DATABASE, channelNoHash));
			stmt.setBoolean(1, emote);
			stmt.setString(2, word);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Unable to set option", e);
			WLogger.logError(e);
		}
		return executeUpdate(stmt);
	}
	
	/**
	 * @param channelNoHash - channel to delete the spam from, without the leading #
	 * @param word - word to delete
	 * @return true if the word is deleted
	 */
	public static boolean delSpam(String channelNoHash, String word) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(String.format("DELETE FROM %s.%sSpam WHERE word=?", DATABASE, channelNoHash));
			stmt.setString(1, word);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Unable to set option", e);
			WLogger.logError(e);
		}
		return executeUpdate(stmt);
	}

	/**
	 * @param nick - person to add points to
	 * @param channelNoHash - channel the user is in, without the leading #
	 * @param ammount - the number of points to add
	 */
	public static void addPoints(String nick, String channelNoHash, int ammount) {
		ResultSet rs = Database.executeQuery(String.format("SELECT * FROM %s.%sUsers WHERE userID=\'%s\'", DATABASE, channelNoHash, nick));
		try {
			String userLevel = rs.getString(2);
			int points = rs.getInt(3);
			boolean visible = rs.getBoolean(4);
			boolean regular = rs.getBoolean(5);
			Database.executeUpdate(String.format("UPDATE %s.%sUsers SET userID=\'%s\', userLevel=\'%s\', points=%d, visibility=%b regular=%d", DATABASE, channelNoHash, nick, userLevel, points, visible, regular));
			return;
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "An Error occured updating "+nick+"'s points!\n", e);
			WLogger.logError(e);
		}
	}

	/**
	 * @param sender - person to get points for
	 * @param channelNoHash - channel the user is in, without the leading #
	 * @return number of points the user has
	 */
	public static String getPoints(String sender, String channelNoHash) {
		ResultSet rs = executeQuery(String.format("SELECT * FROM %s.%sUsers WHERE userID=\'%s\'", DATABASE, channelNoHash, sender));
		try {
			if(rs.next()) {
				return rs.getInt(3)+"";
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "An error occurred getting a user's points.", e);
			WLogger.logError(e);
		}
		return null;
	}

	/**
	 * @param ammount - number of players to get
	 * @param channelNoHash - channel the people are in
	 * @return formatted string of top x players
	 */
	public static String topPlayers(int ammount, String channelNoHash) {
		StringBuilder output = new StringBuilder();
		output.append("The top " + ammount + " points holder(s) are: ");
		ResultSet rs=executeQuery(String.format("SELECT * FROM %s.%sPoints ORDER BY points DESC", DATABASE, channelNoHash));
		try {
			while(rs.next()&&ammount>1){
				if(rs.getBoolean(3)) {
					output.append(rs.getString(1)+": "+rs.getInt(2) + ", ");
					ammount--;
				}
			}
			output.append(rs.getString(1)+": "+rs.getInt(2));
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error occurred creating Top list!", e);
			WLogger.logError(e);
		}
		return output.toString();
	}
	
	public static boolean topExemption(String channelNoHash, String username, boolean visible){
		String points = getPoints(username, channelNoHash);
		return executeUpdate(String.format("UPDATE %s.%sPoints SET userID=\'%s\', points=%s, visibility=%b WHERE userID=\'%s\'", DATABASE, channelNoHash, username, points, visible, username));
	}

	/**
	 * @param sender - person to check if is regular
	 * @param channelNoHash - channel the person is in, without the leading #
	 * @return true if {@link sender} is a regular in {@link channelNoHash}
	 */
	public static boolean isRegular(String sender, String channelNoHash) {
		ResultSet currentPoints=Database.executeQuery(String.format("SELECT * FROM %s.%sUsers WHERE userID=\'%s\'", DATABASE, channelNoHash, sender));
		try {
			return currentPoints.next() && currentPoints.getBoolean(5);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "User is not a regular!", e);
			WLogger.logError(e);
		}
		return false;
	}

	public static boolean delCommand(String channelNoHash, String command) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(String.format("DELETE FROM %s.%sCommands WHERE command=?", DATABASE, channelNoHash));
			stmt.setString(1, command);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Unable to set option", e);
			WLogger.logError(e);
		}
		return executeUpdate(stmt);
	}

	public static ResultSet getMods(String channelNoHash) {
		return executeQuery(String.format("SELECT * FROM %s.%sUsers WHERE userLevel=\'%s\'", DATABASE, channelNoHash, ULevel.Moderator.getName()));
	}
	
	public static boolean[] getImmunities(String channelNoHash, String level){
		String immunity = getOption(channelNoHash, level.toLowerCase()+"Immunities");
		boolean[] immunities = new boolean[6];
		immunities[0] = immunity.charAt(0) == '1';
		immunities[1] = immunity.charAt(1) == '1';
		immunities[2] = immunity.charAt(2) == '1';
		immunities[3] = immunity.charAt(3) == '1';
		immunities[4] = immunity.charAt(4) == '1';
		immunities[5] = immunity.charAt(5) == '1';
		return immunities;
	}
	
	public static String getUserLevel(String channelNoHash, String user){
		ResultSet rs=executeQuery(String.format("SELECT * FROM %s.%sUsers WHERE userID=\'%s\'", DATABASE, channelNoHash, user));
		try {
			if(rs.next()) {
				return rs.getString(2);
			}
			return null;
		} catch (SQLException | NumberFormatException e) {
			logger.log(Level.SEVERE, String.format("Unable to get welcome message for %s", channelNoHash), e);
			WLogger.logError(e);
		}
		return ULevel.Normal.getName();
	}

	public static String getEmoteList(String channelNoHash) {
		ResultSet rs = executeQuery(String.format("SELECT * FROM %s.%sSpam WHERE emote=true", DATABASE, channelNoHash));
		StringBuilder sb = new StringBuilder();
		try {
			while(rs.next()) {
				sb.append(rs.getString(2));
				sb.append("|");
			}
			return sb.toString();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Unable to get the emote list for: " + channelNoHash, e);
			WLogger.logError(e);
		}
		return null;
	}

	public static boolean updateUser(String channelNoHash, String sender) {
		ResultSet rs = executeQuery(String.format("SELECT * FROM %s.%sUsers WHERE userID=\'%s\'", DATABASE, channelNoHash, sender));
		try {
			if(!rs.next()) {
				boolean visible = true;
				if(sender.toLowerCase().matches(String.format("(%s|%s)", channelNoHash.toLowerCase(), Main.getBotChannel().substring(1)))){
					visible = false;
				}
				String uLevel = TwitchUtilities.getUserLevelNoMod(channelNoHash, sender);
				return executeUpdate(String.format("INSERT INTO %s.%sUsers VALUES (\'%s\',\'%s\',1,%b,%b)", DATABASE, channelNoHash, sender, uLevel, visible, false));
			} else {
				String userLevel = TwitchUtilities.getUserLevelNoMod(channelNoHash, sender);
				int points = rs.getInt(3);
				boolean visible = rs.getBoolean(4);
				boolean regular = rs.getBoolean(5);
				Database.executeUpdate(String.format("UPDATE %s.%sUsers SET userID=\'%s\', userLevel=\'%s\', points=%d, visibility=%b regular=%b", DATABASE, channelNoHash, sender, userLevel, points, visible, regular));
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "There was an issue adding the user to the table", e);
			WLogger.logError(e);
		}
		return false;
	}
}
