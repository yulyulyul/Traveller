package jso.kpl.traveller.util;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class Money {

    public static String moneyFormatToWon(long money) {
        NumberFormat nFormat = NumberFormat.getCurrencyInstance(Locale.KOREA);
        nFormat.setCurrency(Currency.getInstance(Locale.KOREA));
        String result = nFormat.format(money);

        return result;
    }



}
