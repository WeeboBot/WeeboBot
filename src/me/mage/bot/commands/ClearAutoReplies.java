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

package me.mage.bot.commands;

import me.mage.bot.database.Database;
import me.mage.bot.util.CLevel;

public class ClearAutoReplies extends Command {

	@Override
	public CLevel getCommandLevel() {
		return CLevel.Owner;
	}
	
	@Override
	public String getCommandText() {
		return "clearautoreplies";
	}
	
	@Override
	public String execute(String channel, String sender, String...parameters) {
		Database.clearAutoRepliesTable(channel.substring(1));
		return sender + " has cleared the auto replies.";
	}

}
