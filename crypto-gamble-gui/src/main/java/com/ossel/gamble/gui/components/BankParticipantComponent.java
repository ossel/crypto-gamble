/**
 * 
 */
package com.ossel.gamble.gui.components;

/**
 * @author ossel
 *
 */
public class BankParticipantComponent extends ParticipantComponent {
	// the difference comes from the .tml file.

	public String getPseudonymWithSpaces() {
		return getParticipant().getPseudonym()
				.concat("                                                                 ");
	}
}