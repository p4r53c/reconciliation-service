package com.msr.rnip.reconciliation.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


//@author p4r53c
public final class DateUtils {

    private final static DatatypeFactory DATATYPE_FACTORY;

    static {
        try {
            DATATYPE_FACTORY = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Gets this Calendar's current time.
     *
     * @param date the xml gregorian calendar
     * @return the current time.
     */
    public static Date toDate(final XMLGregorianCalendar date) {
        if (date != null) {
            return date.toGregorianCalendar().getTime();
        }
        return null;
    }

    /**
     * Create an <code>XMLGregorianCalendar</code> from a {@link String}.
     *
     * @param str     the date to parse
     * @param pattern the date format patterns to use, see SimpleDateFormat, not null
     * @return <code>XMLGregorianCalendar</code> created from
     * <code>java.lang.String</code>.
     */
    public static XMLGregorianCalendar toXMLCalendar(final String str, final String pattern) {
        Assert.notNull(pattern, "Pattern must not be null");
        try {
            if (StringUtils.isNotEmpty(str)) {
                final Date date = org.apache.commons.lang.time.DateUtils.parseDate(str, new String[]{pattern});
                return toXMLCalendar(date);
            }
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Create an <code>XMLGregorianCalendar</code> from a {@link java.util.Date}.
     *
     * @param date the date
     * @return <code>XMLGregorianCalendar</code> created from
     * <code>java.util.Date</code>.
     */
    public static XMLGregorianCalendar toXMLCalendar(final Date date) {
        if (date != null) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return DATATYPE_FACTORY.newXMLGregorianCalendar(calendar);
        }

        return null;
    }

    /**
     * Create an <code>XMLGregorianCalendar</code> from a {@link java.util.Date}.
     *
     * @param date the date
     * @return <code>XMLGregorianCalendar</code> created from
     * <code>java.util.Date</code> without time and time zone.
     */
    public static XMLGregorianCalendar toXMLCalendarDate(final Date date) {
        if (date != null) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return DATATYPE_FACTORY.newXMLGregorianCalendarDate(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
        }

        return null;
    }

    /**
     * Patch for the
     * com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl.
     * The $Revision: 1.7 $, $Date: 2005/11/03 17:54:07 $ formats fractional
     * seconds just as BigDecimal.toString() what leads to exponential nanoseconds
     * representation in some cases and errors on parsing.
     */
    @SuppressWarnings("serial")
    private static class Nanoseconds extends BigDecimal {

        private static final DecimalFormat NANOSECOND_FORMAT = new DecimalFormat("0.0########", new DecimalFormatSymbols(
                Locale.US));

        public Nanoseconds(long val) {
            super(BigInteger.valueOf(val), 9);
        }

        @Override
        public String toString() {
            final BigDecimal val = BigDecimal.valueOf(unscaledValue().longValue(), scale());
            return NANOSECOND_FORMAT.format(val);
        }
    }

    /**
     * Converts {@link java.sql.Timestamp} to
     * {@link javax.xml.datatype.XMLGregorianCalendar}.
     *
     * @param time the time stamp value
     * @return <code>XMLGregorianCalendar</code> or null if input parameter is
     * null
     */
    public static XMLGregorianCalendar toXMLCalendar(final Timestamp time) {
        if (time != null) {
            final GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(time);
            final XMLGregorianCalendar xcal = DATATYPE_FACTORY.newXMLGregorianCalendar(gc);
            xcal.setFractionalSecond(new Nanoseconds(time.getNanos()));
            return xcal;
        }
        return null;
    }

    /**
     * Converts {@link javax.xml.datatype.XMLGregorianCalendar} to
     * {@link java.sql.Timestamp}.
     *
     * @param xmlCal the XML gregorian calendar or null
     * @return <code>Timestamp</code> value or null if input parameter is null
     */
    public static Timestamp toTimestamp(final XMLGregorianCalendar xmlCal) {
        if (xmlCal != null) {
            final GregorianCalendar gc = xmlCal.toGregorianCalendar();
            Timestamp time = new Timestamp(gc.getTimeInMillis());
            final BigDecimal fractSec = xmlCal.getFractionalSecond();
            if (fractSec != null) {
                time.setNanos(fractSec.multiply(new BigDecimal(1000000000)).intValue());
            }

            return time;
        }

        return null;
    }

    /**
     * Converts XML dateTime to {@link java.util.Date} if fractional seconds
     * precision is milliseconds and to {@link java.sql.Timestamp} if more.
     * Precision more than nanoseconds 10E-9 is discarded.
     *
     * @param dateTime the XML dateTime lexical representation
     * @return <code>Date</code> value or null if input parameter is null
     */
    public static Date toTimestamp(final String dateTime) {
        if (dateTime != null && dateTime.trim().length() > 0) {
            final XMLGregorianCalendar xCal = DATATYPE_FACTORY.newXMLGregorianCalendar(dateTime);

            final BigDecimal fractSec = xCal.getFractionalSecond();
            if (fractSec != null && fractSec.multiply(new BigDecimal(1000000000)).intValue() % 1000000 > 0) {
                return toTimestamp(xCal);
            }
            return xCal.toGregorianCalendar().getTime();
        }
        return null;
    }

    /**
     * Converts {@link java.util.Date} and {@link java.sql.Timestamp} to XML
     * dateTime.
     *
     * @param dateTime the value of the {@link java.util.Date} or
     *                 {@link java.sql.Timestamp} type
     * @return the XML dateTime lexical representation. null if the input
     * parameter is null
     */
    public static String toXMLDateTime(final Date dateTime) {
        if (dateTime != null) {
            if (dateTime instanceof Timestamp) {
                return toXMLCalendar((Timestamp) dateTime).toXMLFormat();
            }
            return toXMLCalendar(dateTime).toXMLFormat();
        }

        return null;
    }

    /**
     * Set time part of a timestamp of type {@link java.util.Date} to 0's. Retain
     * only year/month/day part.
     *
     * @param date date in default time zone
     * @return date with zeroed units less of "day"
     */
    public static Date stripTime(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Set time millisecond part of a timestamp of type {@link java.util.Date} to
     * 0's. Retain only year/month/day hours/minutes/second part.
     *
     * @param date date in default time zone
     * @return date with zeroed units less of "day"
     */
    public static Date stripMilliseconds(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Calculate difference between dates.
     *
     * @param start start date in default time zone
     * @param end   end date in default time zone
     * @return difference between end and start
     */
    public static int dateDifference(Date start, Date end) {
        if (start == null || end == null) {
            return 0;
        }
        return (int) ((stripTime(end).getTime() - stripTime(start).getTime()) / (1000 * 60 * 60 * 24));
    }

    public static String format(Date date, String pattern) {
        Assert.notNull(date, "date must not be null");
        Assert.notNull(pattern, "pattern must not be null");
        return new SimpleDateFormat(pattern).format(date);
    }

    public static Date parse(String date, String format) throws ParseException {
        Assert.notNull(date, "date must not be null");
        Assert.notNull(format, "pattern must not be null");
        return new SimpleDateFormat(format).parse(date);
    }

    public static Date parse(String date, String format, TimeZone timeZone) throws ParseException {
        Assert.notNull(date, "date must not be null");
        Assert.notNull(format, "pattern must not be null");
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(timeZone);
        return sdf.parse(date);
    }

    public static Date silentParse(String date, String format) {
        try {
            return parse(date, format);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public static Date setDay(Date date, int countDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, countDay);
        return cal.getTime();
    }


    public static boolean isDateMatchPattern(String date, String pattern) {
        try {
            Assert.notNull(date, "date must not be null");
            Assert.notNull(pattern, "pattern must not be null");
            new SimpleDateFormat(pattern).parse(date);
            return true;
        }
        catch (ParseException e) {
            return false;
        }
    }

    public static Date enforceTime(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 59);
        return cal.getTime();
    }

    public static boolean isDateAfter(Date dateToCheck, Date earlyDate) {
        if (dateToCheck == null || earlyDate == null)
            return false;
        return dateToCheck.compareTo(earlyDate) >= 0;
    }

    public static Date getEndOfDay(Date date) {
        return org.apache.commons.lang3.time.DateUtils.addMilliseconds(
                org.apache.commons.lang3.time.DateUtils.ceiling(date, Calendar.DATE), -1);
    }

    public static Date getEndOfPreviousDay(Date date) {
        return org.apache.commons.lang3.time.DateUtils.addMilliseconds(getStartOfDay(date), -1);
    }

    public static Date nextDay(Date date) {
        return org.apache.commons.lang3.time.DateUtils.ceiling(date, Calendar.DATE);
    }

    public static Date getStartOfDay(Date date) {
        return org.apache.commons.lang3.time.DateUtils.truncate(date, Calendar.DATE);
    }

    public static Date getStartDayOfMonth(Date date) {
        return org.apache.commons.lang3.time.DateUtils.truncate(date, Calendar.MONTH);
    }

    public static Date getEndDayOfMonth(Date date) {
        return org.apache.commons.lang3.time.DateUtils.addMilliseconds(
                org.apache.commons.lang3.time.DateUtils.ceiling(date, Calendar.MONTH), -1);
    }

    public static java.util.Date asDateWithoutTimezone(XMLGregorianCalendar xgc) {
        if (xgc == null) return null;
        xgc.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        return xgc.toGregorianCalendar().getTime();
    }

}
