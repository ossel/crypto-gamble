/**
 * 
 */
package com.ossel.gamble.gui.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import com.ossel.gamble.core.data.Block;
import com.ossel.gamble.core.data.ExtendedBlock;
import com.ossel.gamble.core.data.enums.CryptoCurrency;
import com.ossel.gamble.core.service.CryptoNetworkService;

/**
 * @author ossel
 *
 */
public class BlockComponent {

    @Inject
    private CryptoNetworkService service;

    @Property
    @Parameter(required = false)
    private Block block;


    public String getExplorerLink() {
        return service.getCryptoNetwork().getExplorerLinkToBlock(block.getBlockHash());
    }

    public boolean isShowPayoutHash() {
        return block != null;
    }

    public boolean isExtendedBlock() {
        return block instanceof ExtendedBlock;
    }

    public String getSuffix() {
        return ((ExtendedBlock) block).getSuffix();
    }

    public String getPrefix() {
        return ((ExtendedBlock) block).getPrefix();
    }

    public boolean isEthereum() {
        return CryptoCurrency.ETHEREUM.equals(service.getCryptoNetwork().getCryptoCurrency());
    }

    public String getToBeDecidedClass() {
        return isEthereum() ? "tbd_eth" : "tbd";
    }

}
