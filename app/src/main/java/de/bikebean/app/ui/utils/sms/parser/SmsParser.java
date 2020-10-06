package de.bikebean.app.ui.utils.sms.parser;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.db.type.types.sms_parser_types.CellTowers;
import de.bikebean.app.db.type.types.sms_parser_types.Interval;
import de.bikebean.app.db.type.types.sms_parser_types.LowBattery;
import de.bikebean.app.db.type.types.sms_parser_types.NoWifiList;
import de.bikebean.app.db.type.types.sms_parser_types.Position;
import de.bikebean.app.db.type.types.sms_parser_types.Undefined;
import de.bikebean.app.db.type.types.sms_parser_types.WarningNumber;
import de.bikebean.app.db.type.types.sms_parser_types.WifiList;
import de.bikebean.app.db.type.types.sms_parser_types.WifiOff;
import de.bikebean.app.db.type.types.sms_parser_types.WifiOn;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.main.status.StateViewModel;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;

public class SmsParser extends AsyncTask<String, Void, Boolean> {

    private final @NonNull WeakReference<StateViewModel> statusViewModelReference;
    private final @NonNull WeakReference<SmsViewModel> smsViewModelReference;
    private final @NonNull WeakReference<LogViewModel> logViewModelReference;

    private final @NonNull Sms sms;

    public SmsParser(final @NonNull Sms sms, final StateViewModel st,
                     final SmsViewModel sm, final LogViewModel lv) {
        this.sms = sms;
        statusViewModelReference = new WeakReference<>(st);
        smsViewModelReference = new WeakReference<>(sm);
        logViewModelReference = new WeakReference<>(lv);

        initMatchers(sms.getBody());
    }

    @Override
    protected @NonNull Boolean doInBackground(final @NonNull String... args) {
        /*
         Parse Sms to get which type it is
         */
        final @NonNull SmsParserType type = getType();
        logViewModelReference.get().w(
                String.format(
                        Locale.GERMANY,
                        "Detected Type %d (%s)", type.smsType.ordinal(), type.smsType.name())
        );

        /*
         Add each status entry to the status viewModel ( -> database )
         */
        statusViewModelReference.get().insert(type);

        return true;
    }

    @Override
    protected void onPostExecute(final @NonNull Boolean isDatabaseUpdated) {
        if (isDatabaseUpdated)
            smsViewModelReference.get().markParsed(sms);
    }

    public @NonNull Sms getSms() {
        return sms;
    }

    // MATCHERS
    // position
    private Matcher positionMatcher;

    // status
    private Matcher statusWarningNumberMatcher;
    private Matcher statusIntervalMatcher;
    private Matcher statusWifiStatusMatcher;
    private Matcher statusBatteryStatusMatcher;

    // wifi
    private Matcher wifiStatusOnMatcher;
    private Matcher wifiStatusOffMatcher;

    // warning number
    private Matcher warningNumberMatcher;

    // wapp
    private Matcher wifiMatcher;
    private Matcher noWifiMatcher;
    private Matcher batteryMatcher;

    // interval
    private Matcher intervalChangedMatcher;

    private Matcher lowBatteryMatcher;

    private void initMatchers(final @NonNull String smsText) {
        Pattern statusWarningNumberPattern = Pattern.compile("(Warningnumber: )([+0-9]{8,})");
        Pattern statusIntervalPattern = Pattern.compile("(Interval: )(1|2|4|8|12|24)(h)");
        Pattern statusWifiStatusPattern = Pattern.compile("(Wifi Status: )(on|off)");
        Pattern statusBatteryStatusPattern = Pattern.compile("(Battery Status: )([0-9]{1,3})(%)");
        Pattern warningNumberPattern = Pattern.compile(
                "(Warningnumber has been changed to )([+0-9]{8,})"
        );
        Pattern wifiStatusOnPattern = Pattern.compile("(Wifi is on!)");
        Pattern wifiStatusOffPattern = Pattern.compile("(Wifi Off)");
        Pattern intervalChangedPattern = Pattern.compile(
                "(GSM will be switched on every )(1|2|4|8|12|24)( hour)(s)*([.])"
        );
        Pattern positionPattern = Pattern.compile(
                "([0-9]{3},[0-9]{2},[0-9a-fA-F]+,[0-9a-fA-F]+,[0-9]+)"
        );
        Pattern wifiPattern = Pattern.compile("([0-9a-fA-F]{14})");
        Pattern noWifiPattern = Pattern.compile("(no wifi available)([0-9]){1,3}");
        Pattern batteryPattern = Pattern.compile("^([0-9]){1,3}$", Pattern.MULTILINE);
        Pattern lowBatteryPattern = Pattern.compile("(BATTERY LOW!\\nBATTERY STATUS: )([0-9]{1,3})(%)");

        statusWarningNumberMatcher = statusWarningNumberPattern.matcher(smsText);
        statusIntervalMatcher = statusIntervalPattern.matcher(smsText);
        statusWifiStatusMatcher = statusWifiStatusPattern.matcher(smsText);
        statusBatteryStatusMatcher = statusBatteryStatusPattern.matcher(smsText);
        warningNumberMatcher = warningNumberPattern.matcher(smsText);
        wifiStatusOnMatcher = wifiStatusOnPattern.matcher(smsText);
        wifiStatusOffMatcher = wifiStatusOffPattern.matcher(smsText);
        intervalChangedMatcher = intervalChangedPattern.matcher(smsText);
        positionMatcher = positionPattern.matcher(smsText);
        wifiMatcher = wifiPattern.matcher(smsText);
        noWifiMatcher = noWifiPattern.matcher(smsText);
        batteryMatcher = batteryPattern.matcher(smsText);
        lowBatteryMatcher = lowBatteryPattern.matcher(smsText);
    }

    public @NonNull SmsParserType getType() {
        if (positionMatcher.find(0) && statusBatteryStatusMatcher.find(0))
            return new Position(this);
        else if (statusWarningNumberMatcher.find(0) && statusIntervalMatcher.find(0) &&
                statusWifiStatusMatcher.find(0) && statusBatteryStatusMatcher.find(0))
            return new de.bikebean.app.db.type.types.sms_parser_types.Status(this, null);
        else if (statusIntervalMatcher.find(0) && statusWifiStatusMatcher.find(0) &&
                statusBatteryStatusMatcher.find(0))
            return new de.bikebean.app.db.type.types.sms_parser_types.Status(this, logViewModelReference);
        else if (wifiStatusOnMatcher.find(0) && statusBatteryStatusMatcher.find(0))
            return new WifiOn(this);
        else if (wifiStatusOffMatcher.find(0) && statusBatteryStatusMatcher.find(0))
            return new WifiOff(this);
        else if (warningNumberMatcher.find(0) && statusBatteryStatusMatcher.find(0))
            return new WarningNumber(this);
        else if (positionMatcher.find(0))
            return new CellTowers(this);
        else if (wifiMatcher.find(0) && batteryMatcher.find(0))
            return new WifiList(this);
        else if (noWifiMatcher.find(0))
            return new NoWifiList(this);
        else if (intervalChangedMatcher.find(0) && statusBatteryStatusMatcher.find(0))
            return new Interval(this);
        else if (lowBatteryMatcher.find(0))
            return new LowBattery(this);
        else
            return new Undefined(this, logViewModelReference);
    }

    /*
    Get results of Sms Parser
    */
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

    public double getLowBattery() {
        final @NonNull String result = getMatcherResult(lowBatteryMatcher);

        if (!result.isEmpty())
            return Double.parseDouble(result);
        else return 0.0;
    }

    public int getInterval() {
        return Integer.parseInt(getMatcherResult(intervalChangedMatcher));
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

