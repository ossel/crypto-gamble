package com.ossel.gamble.gui.pages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.service.CryptoNetworkService;

public class Payouts {


    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    @Inject
    private CryptoNetworkService service;

    @Component
    private Zone payoutsZone;

    public Block onUpdate() {
        return payoutsZone.getBody();
    }

    @Property
    private Pot pot;

    public List<Pot> getPots() {
        List<Pot> result = new ArrayList<Pot>();
        for (Pot pot : service.getClosedPots()) {
            if (pot.isPayoutStarted()) {
                result.add(pot);
            }
        }
        return result;
    }

    public String getTime() {
        return DATE_FORMAT.format(new Date());
    }

}
