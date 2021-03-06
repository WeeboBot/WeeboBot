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

package io.github.weebobot.weebobot.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Hazard
 */
public class DelayedVoteTask extends TimerTask {

	private Object type;

	private static final Timer timer = new Timer();

	/**
	 * @param time
	 *            - delay before we close the poll
	 * @param p
	 *            - Poll object
	 */
	public DelayedVoteTask(long time, PollUtil p) {
		this.type = p;
		timer.schedule(this, time);
	}

	/**
	 * @param time
	 *            - delay before we close the Raffle
	 * @param r
	 *            - Raffle object
	 */
	public DelayedVoteTask(int time, RaffleUtil r) {
		this.type = r;
		timer.schedule(this, time);
	}

	/**
	 * @param time
	 *            - delay before we close the Time Out Vote
	 * @param v
	 *            - VoteTimeOut object
	 */
	public DelayedVoteTask(int time, VoteTimeOutUtill v) {
		this.type = v;
		timer.schedule(this, time);
	}

	/**
	 * Decides what type of vote this is then counts the votes/chooses a winner
	 */
	public void run() {
		if (type instanceof PollUtil) {
			((PollUtil) type).count();
		} else if (type instanceof RaffleUtil) {
			((RaffleUtil) type).selectWinner();
		} else if (type instanceof VoteTimeOutUtill) {
			((VoteTimeOutUtill) type).count();
		}
	}

}
