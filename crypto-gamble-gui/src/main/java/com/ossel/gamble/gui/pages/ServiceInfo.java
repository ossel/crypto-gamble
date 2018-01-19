package com.ossel.gamble.gui.pages;

import java.util.ArrayList;
import java.util.List;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import com.ossel.gamble.core.data.ServiceInformation;
import com.ossel.gamble.core.service.CryptoNetworkService;

public class ServiceInfo {

    @Inject
    private CryptoNetworkService service;

    @Property
    private String info;

    public List<String> getInfoList() {
        List<String> result = new ArrayList<String>();
        for (ServiceInformation infoItem : service.getServiceInformations()) {
            result.add(infoItem.getKey() + ": " + infoItem.getValue());
        }
        return result;
    }

}
