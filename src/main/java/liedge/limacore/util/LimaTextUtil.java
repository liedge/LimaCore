package liedge.limacore.util;

import com.ibm.icu.number.LocalizedNumberFormatter;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.Precision;
import com.ibm.icu.util.NoUnit;
import com.ibm.icu.util.ULocale;

import java.math.RoundingMode;

public final class LimaTextUtil
{
    private LimaTextUtil() {}

    private static final LocalizedNumberFormatter PERCENT = NumberFormatter.withLocale(ULocale.US).unit(NoUnit.PERCENT).precision(Precision.integer());
    private static final LocalizedNumberFormatter PERCENT_1PLACE = NumberFormatter.withLocale(ULocale.US).unit(NoUnit.PERCENT).precision(Precision.maxFraction(1)).roundingMode(RoundingMode.FLOOR);
    private static final LocalizedNumberFormatter WHOLE_NUMBER_COMMA = NumberFormatter.withLocale(ULocale.US).grouping(NumberFormatter.GroupingStrategy.AUTO).precision(Precision.integer());
    private static final LocalizedNumberFormatter DECIMAL_2PLACE = NumberFormatter.withLocale(ULocale.US).precision(Precision.maxFraction(2)).roundingMode(RoundingMode.FLOOR);

    // Decimal formatting
    public static String formatPercentage(double value)
    {
        return PERCENT.format(value * 100d).toString();
    }

    public static String format1PlacePercentage(double value)
    {
        return PERCENT_1PLACE.format(value * 100d).toString();
    }

    public static String formatWholeNumber(int value)
    {
        return WHOLE_NUMBER_COMMA.format(value).toString();
    }

    public static String formatWholeNumber(double value)
    {
        return WHOLE_NUMBER_COMMA.format(value).toString();
    }

    public static String format2PlaceDecimal(double value)
    {
        return DECIMAL_2PLACE.format(value).toString();
    }
}