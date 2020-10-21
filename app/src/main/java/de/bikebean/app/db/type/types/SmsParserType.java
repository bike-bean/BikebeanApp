package de.bikebean.app.db.type.types;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.type.SmsType;
import de.bikebean.app.ui.drawer.log.LogViewModel;

public abstract class SmsParserType extends SmsType {

    private final @NonNull WeakReference<LogViewModel> logViewModelReference;
    private final @NonNull Sms sms;

    /* Regex patterns */
    protected static @NonNull Pattern positionPattern = Pattern.compile(
            "([0-9]{3},[0-9]{2},[0-9a-fA-F]+,[0-9a-fA-F]+,[0-9]+)"
    );
    protected static @NonNull Pattern statusWarningNumberPattern = Pattern.compile(
            "(Warningnumber: )([+0-9]{8,})"
    );
    protected static @NonNull Pattern statusIntervalPattern = Pattern.compile(
            "(Interval: )(1|2|4|8|12|24)(h)"
    );
    protected static @NonNull Pattern statusWifiStatusPattern = Pattern.compile(
            "(Wifi Status: )(on|off)"
    );
    protected static @NonNull Pattern statusBatteryStatusPattern = Pattern.compile(
            "(Battery Status: )([0-9]{1,3})(%)"
    );
    protected static Pattern warningNumberPattern = Pattern.compile(
            "(Warningnumber has been changed to )([+0-9]{8,})"
    );
    protected static Pattern wifiStatusOnPattern = Pattern.compile(
            "(Wifi is on!)"
    );
    protected static Pattern wifiStatusOffPattern = Pattern.compile(
            "(Wifi Off)"
    );
    protected static Pattern intervalChangedPattern = Pattern.compile(
            "(GSM will be switched on every )(1|2|4|8|12|24)( hour)(s)*([.])"
    );
    protected static Pattern wifiPattern = Pattern.compile(
            "([0-9a-fA-F]{14})"
    );
    protected static Pattern noWifiPattern = Pattern.compile(
            "(no wifi available)([0-9]){1,3}"
    );
    protected static Pattern batteryPattern = Pattern.compile(
            "^([0-9]){1,3}$", Pattern.MULTILINE
    );
    protected static Pattern lowBatteryPattern = Pattern.compile(
            "(BATTERY LOW!\\nBATTERY STATUS: )([0-9]{1,3})(%)"
    );

    /* MATCHERS */
    protected static Matcher positionMatcher;
    protected static Matcher statusWarningNumberMatcher;
    protected static Matcher statusBatteryStatusMatcher;
    protected static Matcher statusIntervalMatcher;
    protected static Matcher statusWifiStatusMatcher;
    protected static Matcher warningNumberMatcher;
    protected static Matcher wifiStatusOnMatcher;
    protected static Matcher wifiStatusOffMatcher;
    protected static Matcher wifiMatcher;
    protected static Matcher batteryMatcher;
    protected static Matcher noWifiMatcher;
    protected static Matcher intervalChangedMatcher;
    protected static Matcher lowBatteryMatcher;

    public SmsParserType(final @NonNull TYPE type, final @NonNull Sms sms,
                         final @NonNull WeakReference<LogViewModel> logViewModelReference) {
        super(type);

        this.sms = sms;
        this.logViewModelReference = logViewModelReference;
    }

    public final void addToConversationList(final @NonNull List<Setting> conversationList) {
        /* update the conversationList, checking if the information from the SMS
           is newer than already stored information. */
        for (final @NonNull Setting smsSetting : getSettings())
            smsSetting.getConversationListAdder().add(conversationList, smsSetting);
    }

    /* Get results of Sms Parser */
    public @NonNull String getStatusWarningNumber() {
        return getMatcherResult(statusWarningNumberMatcher);
    }

    public final int getStatusInterval() {
        return Integer.parseInt(getMatcherResult(statusIntervalMatcher));
    }

    public boolean getStatusWifi() {
        final @NonNull String result = getMatcherResult(statusWifiStatusMatcher);
        return result.equals("on");
    }

    public double getStatusBattery() {
        final @NonNull String result = getMatcherResult(statusBatteryStatusMatcher);

        if (!result.isEmpty())
            return Double.parseDouble(result);
        else return 0.0;
    }

    public @NonNull String getWarningNumber() {
        return getMatcherResult(warningNumberMatcher);
    }

    public @NonNull String getWappCellTowers() {
        final @NonNull StringBuilder result = new StringBuilder();
        positionMatcher.reset();

        while (positionMatcher.find()) {
            result.append(positionMatcher.group());
            result.append("\n");
        }

        return result.toString();
    }

    public @NonNull String getWappWifiAccessPoints() {
        final @NonNull StringBuilder result = new StringBuilder();
        wifiMatcher.reset();

        while (wifiMatcher.find()) {
            result.append(wifiMatcher.group());
            result.append("\n");
        }

        return result.toString();
    }

    public double getBattery() {
        @NonNull String result = "";
        batteryMatcher.reset();

        while (batteryMatcher.find()) {
            // use the last entry that matches battery specs
            result = batteryMatcher.group();
        }

        if (!result.isEmpty())
            return Double.parseDouble(result);
        else return 0.0;
    }

    public double getBatteryNoWifi() {
        final @NonNull String result = getMatcherResult(noWifiMatcher);

        if (!result.isEmpty())
            return Double.parseDouble(result);
        else return 0.0;
    }

    public int getInterval() {
        return Integer.parseInt(getMatcherResult(intervalChangedMatcher));
    }

    public double getLowBattery() {
        final @NonNull String result = getMatcherResult(lowBatteryMatcher);

        if (!result.isEmpty())
            return Double.parseDouble(result);
        else return 0.0;
    }

    private @NonNull String getMatcherResult(final @NonNull Matcher m) {
        int count = 0;
        @Nullable String result = "";
        m.reset();

        while (m.find()) {
            count++;
            result = m.group(2);
            if (count > 1)
                logViewModelReference.get().e("There should only be one instance per message!");
            if (result == null) {
                logViewModelReference.get().e("Failed to parse SMS. Matcher: " + m.toString() +
                        " SMS: " + sms.getBody());
                result = "";
            }
        }

        return result;
    }
}
