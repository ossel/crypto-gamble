/**
 * 
 */
package com.ossel.gamble.gui.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.enums.CryptoCurrency;
import com.ossel.gamble.core.service.CryptoNetworkService;

/**
 * @author ossel
 *
 */
public class ParticipantComponent {

    @Inject
    private CryptoNetworkService service;

    @Parameter(required = true)
    private Participant participant;

    @Parameter(required = false)
    private boolean winner;

    public String getExplorerLinkToDepositAddress() {
        return service.getCryptoNetwork().getExplorerLinkToAddress(participant.getDepositAddress());
    }

    public String getExplorerLinkToPayoutAddress() {
        return service.getCryptoNetwork().getExplorerLinkToAddress(participant.getPayoutAddress());
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Integer getIndex() {
        return participant.getPotIndex();
    }

    public String getCssClass() {
        if (winner) {
            return "win";
        }
        return "lose";
    }

    public boolean isEthereum() {
        return CryptoCurrency.ETHEREUM.equals(service.getCryptoNetwork().getCryptoCurrency());
    }

    public String getCurrencyCode() {
        return service.getCryptoNetwork().getCryptoCurrency().getSmallestDenomination();
    }


}
