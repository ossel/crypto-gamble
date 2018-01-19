/**
 * 
 */
package com.ossel.gamble.gui.components;

/**
 * @author ossel
 *
 */
public class DepositComponent extends ParticipantComponent {

	public boolean isPending() {
		return getParticipant().getReceivedAmount() == 0;
	}
}