/**
 * 
 */
package com.ossel.gamble.gui.components;

import org.apache.log4j.Logger;
import com.ossel.gamble.gui.pages.Donate;

/**
 * @author ossel
 *
 */
public class DonationButton {
	private static final Logger log = Logger.getLogger(DonationButton.class);

	Object onActionFromDonateButton() {
		log.info(">onActionFromDonateButton");
		return Donate.class;
	}

}