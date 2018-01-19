package com.ossel.gamble.dash.listeners;

import java.util.List;

import org.apache.log4j.Logger;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.core.listeners.ReorganizeListener;

public class ReorganizationListener extends AbstractListener implements ReorganizeListener {
	private static final Logger log = Logger.getLogger(ReorganizationListener.class);

	@Override
	public void reorganize(StoredBlock splitPoint, List<StoredBlock> oldBlocks, List<StoredBlock> newBlocks)
			throws VerificationException {
		listenerTriggered();
		log.info(
				"Reorganize because of block: " + splitPoint.getHeight() + " hash=" + splitPoint.getHeader().getHash());
		log.info("Old blocks:");
		for (StoredBlock storedBlock : oldBlocks) {
			log.info(storedBlock.getHeight() + " hash=" + storedBlock.getHeader().getHash());
		}

		log.info("New blocks:");
		for (StoredBlock storedBlock : newBlocks) {
			log.info(storedBlock.getHeight() + " hash=" + storedBlock.getHeader().getHash());
		}

	}

}
