
/**
 * 
 */
package com.ossel.gamble.gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import com.ossel.gamble.core.data.Block;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.data.enums.CryptoCurrency;
import com.ossel.gamble.core.service.CryptoNetworkService;
import com.ossel.gamble.gui.utils.CryptoUtil;

/**
 * @author ossel
 *
 */
public class PotComponent {

    private static final Logger log = Logger.getLogger(PotComponent.class);

    @Inject
    private CryptoNetworkService service;

    @Property
    @Parameter(required = true)
    private Pot pot;

    @Property
    private Participant participant;

    @Inject
    private ComponentResources _resources;

    public String getExplorerLink() {
        return service.getCryptoNetwork().getExplorerLink();
    }

    public Link getChart3() {
        return _resources.createEventLink("chart", new Object[] {pot.getId(), "600", "400"});
    }

    @SetupRender
    public void setupComponent() {
        if (pot != null) {
            log.info("setup pot component of pot: " + pot.getId() + " is full: " + pot.isFull());
        }
    }

    public StreamResponse onChart(final long potId, final int width, final int height) {
        PieDataset pieDataset = null;
        String chartTitle = "Pot";
        if (pot == null) {
            pot = service.getPotById(potId);
        }
        if (pot == null) {
            // pot not found in the database
            DefaultKeyedValues values = new DefaultKeyedValues();
            values.addValue("Pot not found in the database :(", 1);
            pieDataset = new DefaultPieDataset(values);
        } else {
            log.info("found pot " + potId + " in memory! " + pot.getParticipants());
            pieDataset = CryptoUtil.getPieDataset(pot);
            chartTitle = service.getDisplayableAmount(pot.getExpectedBettingamount()) + " / slot";
        }
        PiePlot plot = new PiePlot(pieDataset);
        String label = Pot.LABEL_OPEN_SLOT;
        for (int i = 1; i <= 10; i++) {
            plot.setSectionPaint(label + i, Color.WHITE);
        }
        plot.setLabelFont(new Font("Dialog", Font.PLAIN, 30));
        plot.setSectionPaint(" 0 ", new Color(239, 154, 154));
        plot.setSectionPaint(" 1 ", new Color(176, 190, 197));
        plot.setSectionPaint(" 2 ", new Color(179, 157, 219));
        plot.setSectionPaint(" 3 ", new Color(144, 202, 249));
        plot.setSectionPaint(" 4 ", new Color(165, 214, 167));
        plot.setSectionPaint(" 5 ", new Color(255, 245, 157));
        plot.setSectionPaint(" 6 ", new Color(185, 246, 202));
        plot.setSectionPaint(" 7 ", new Color(188, 170, 164));
        plot.setSectionPaint(" 8 ", new Color(255, 204, 128));
        plot.setSectionPaint(" 9 ", new Color(128, 222, 234));

        plot.setLabelBackgroundPaint(Color.WHITE);

        plot.setOutlineStroke(null); // no border
        final JFreeChart chart = new JFreeChart(chartTitle, plot);
        chart.removeLegend();
        chart.setBackgroundPaint(Color.WHITE);

        return new StreamResponse() {
            public String getContentType() {
                return "image/jpeg";
            }

            public InputStream getStream() throws IOException {
                BufferedImage image = chart.createBufferedImage(width, height);
                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                ChartUtilities.writeBufferedImageAsJPEG(byteArray, image);
                return new ByteArrayInputStream(byteArray.toByteArray());
            }

            public void prepareResponse(Response response) {}
        };
    }

    public List<Participant> getParticipants() {
        return pot.getParticipants();
    }

    public boolean isNoParticipants() {
        return getParticipants().size() == 0;
    }

    public Integer getParticipantIndex() {
        return participant.getPotIndex();
    }

    public boolean isClosed() {
        return pot.isFull();
    }

    public Block getPayoutBlock() {
        return pot.getPayoutBlock();
    }

    public boolean isWinner() {
        return pot.getWinnerIndex() == participant.getPotIndex();
    }

    public String getPieChartClass() {
        if (pot.isFull()) {
            return "";
        } else {
            return "imgLeftAlign";
        }
    }

    public boolean isNoBankParticipant() {
        return !participant.isBankParticipant();
    }

    public String getExplorerLinkClosingBlockHash() {
        return service.getCryptoNetwork().getExplorerLinkToBlock(pot.getClosingBlockHash());
    }

    public String getExplorerLinkToTxn() {
        return service.getCryptoNetwork().getExplorerLinkToTxn(pot.getPayoutTxnId());
    }

    public String getWinnerSelectionInput() {
        if (pot.getCurrency().equals(CryptoCurrency.ETHEREUM)) {
            return "uint(" + pot.getPayoutBlock().getBlockHash() + ")";
        } else {
            return "" + pot.getPayoutBlock().getWinner();
        }
    }
}
