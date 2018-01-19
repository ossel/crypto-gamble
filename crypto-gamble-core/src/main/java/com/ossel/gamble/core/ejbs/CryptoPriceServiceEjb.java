package com.ossel.gamble.core.ejbs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Singleton;
import org.json.JSONObject;
import com.ossel.gamble.core.data.enums.CryptoCurrency;


@Singleton
public class CryptoPriceServiceEjb {

    public static final String PRICE_EUR = "price_eur";
    public static final String PRICE_USD = "price_usd";
    public static final String PERCENT_1H = "percent_change_1h";
    public static final String PERCENT_24H = "percent_change_24h";
    public static final String PERCENT_7D = "percent_change_7d";

    private final String USER_AGENT = "Mozilla/5.0";

    public final static long REFRESH_THRESHOLD = 60000; // msec

    private Map<CryptoCurrency, Long> lastRefresh;
    private Map<CryptoCurrency, String> priceCash;

    public CryptoPriceServiceEjb() {
        lastRefresh = new HashMap<>();
        priceCash = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        for (CryptoCurrency c : CryptoCurrency.values()) {
            lastRefresh.put(c, cal.getTime().getTime());
            priceCash.put(c, "");
        }
    }

    /**
     * sends http request to coinmarketcap.com
     * 
     * @param currency
     * @return
     * @throws IOException
     */
    private JSONObject fetchPriceData(CryptoCurrency currency) throws IOException {
        String url = currency.getPriceUrl();

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        con.setRequestProperty("User-Agent", USER_AGENT);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String resp = response.toString();
        resp = (String) resp.subSequence(1, resp.length() - 1); // remove brackets [ data ]
        return new JSONObject(resp);
    }

    public String getPrice(CryptoCurrency currency) {
        if (System.currentTimeMillis() - lastRefresh.get(currency) > REFRESH_THRESHOLD) {
            String price = "error";
            try {
                price = fetchPriceData(currency).getString(PRICE_EUR);
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            priceCash.put(currency, price);
        }

        return priceCash.get(currency);

    }

}
