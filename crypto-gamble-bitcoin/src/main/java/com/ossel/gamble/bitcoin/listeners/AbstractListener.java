package com.ossel.gamble.bitcoin.listeners;

import java.util.Date;

public abstract class AbstractListener {

	private Date lastTriggered;

	public Date getLastTriggeredTime() {
		return lastTriggered;
	}

	public void listenerTriggered() {
		lastTriggered = new Date();
	}

}
