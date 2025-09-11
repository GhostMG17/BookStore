package com.example.BookStore.util;

import org.springframework.stereotype.Component;
import java.text.DecimalFormat;

@Component("priceUtil")
public class PriceUtil {
    private static final DecimalFormat df;

    static {
        df = new DecimalFormat("#,##0.000");
        df.setGroupingUsed(true);
        df.setGroupingSize(3);
    }

    public String format(double price) {
        return df.format(price) + " сум";
    }
}
