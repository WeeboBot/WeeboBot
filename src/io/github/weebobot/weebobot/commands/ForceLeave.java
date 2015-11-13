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

package io.github.weebobot.weebobot.commands;

import io.github.weebobot.weebobot.Main;
import io.github.weebobot.weebobot.util.CLevel;

public class ForceLeave extends Command {

	@Override
	public CLevel getCommandLevel() {
		return CLevel.Mod;
	}

	@Override
	public String getCommandText() {
		return "forceleave";
	}

	@Override
	public String execute(String channel, String sender, String... parameters) {
		if(channel.equalsIgnoreCase(Main.getBotChannel())) {
			if(parameters[0].startsWith("#")) {
				Main.getBot().sendMessage(parameters[0], "I am leaving the channel!");
				Main.partChannel(parameters[0]);
			} else {

				Main.getBot().sendMessage("#"+parameters[0], "I am leaving the channel!");
				Main.partChannel("#"+parameters[0]);
			}
			return "Forcefully leaving %channel%".replace("%channel%", parameters[0]);
		}
		return "You can only preform this command from the main bot channel!";
	}

}
