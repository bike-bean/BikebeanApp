package de.bikebean.app.db.sms;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.MainActivity;
import de.bikebean.app.Utils;
import de.bikebean.app.ui.status.StatusViewModel;
import de.bikebean.app.ui.status.preferences.PreferenceUpdater;
import de.bikebean.app.ui.status.sms.parser.SmsParser;

public class Conversation {

    private List<Setting> l = new ArrayList<>();

    private Wifi wifi;
    private Battery battery;
    private Interval interval;
    private WarningNumber warningNumber;
    private Status status;
    private CellTowers cellTowers;
    private WifiAccessPoints wifiAccessPoints;

    private SharedPreferences settings;
    private StatusViewModel statusViewModel;

    private static final int KEY_WIFI = 0;
    private static final int KEY_BATTERY = 1;
    private static final int KEY_INTERVAL = 2;
    private static final int KEY_WARNING_NUMBER = 3;
    private static final int KEY_STATUS = 4;
    private static final int KEY_CELL_TOWERS = 5;
    private static final int KEY_WIFI_ACCESS_POINT = 6;

    public Conversation(SharedPreferences settings, StatusViewModel statusViewModel) {
        this.settings = settings;
        this.statusViewModel = statusViewModel;

        l.add(new Wifi(false, 0, 0));
        l.add(new Battery(0.0, 0, 0));
        l.add(new Interval("", 0, 0));
        l.add(new WarningNumber("", 0, 0));
        l.add(new Status("", 0, 0));
        l.add(new CellTowers("", 0, 0));
        l.add(new WifiAccessPoints("", 0, 0));
    }

    public void add(Sms sms) {
        SmsParser smsParser = new SmsParser(sms);
        int type = smsParser.getType();
        Log.d(MainActivity.TAG, String.format("Detected Type %d", type));
        updateData(smsParser, type, sms.getTimestamp(), sms.getId());
    }

    private void updateData(SmsParser smsParser, int type, long date, int _id) {
        // only update the preferences settings here (which the user can control directly)
        switch (type) {
            case SmsParser.SMS_TYPE_STATUS:
                update(new WarningNumber(smsParser.getStatusWarningNumber(), date, _id));
                update(new Interval(smsParser.getStatusInterval(), date, _id));
                update(new Wifi(smsParser.getStatusWifi(), date, _id));
                update(new Battery(smsParser.getStatusBattery(), date, _id));
                update(new Status("", date, _id));
                break;
            case SmsParser.SMS_TYPE_WIFI_ON:
                update(new Wifi(true, date, _id));
                update(new Status("", date, _id));
                break;
            case SmsParser.SMS_TYPE_WIFI_OFF:
                update(new Wifi(false, date, _id));
                update(new Status("", date, _id));
                break;
            case SmsParser.SMS_TYPE_WARNING_NUMBER:
                update(new WarningNumber(smsParser.getWarningNumber(), date, _id));
                update(new Status("", date, _id));
                break;
            case SmsParser.SMS_TYPE_INT:
                update(new Interval(smsParser.getInterval(), date, _id));
                update(new Status("", date, _id));
                break;
            case SmsParser.SMS_TYPE_CELL_TOWERS:
                update(new CellTowers(smsParser.getWappCellTowers(), date, _id));
                break;
            case SmsParser.SMS_TYPE_WIFI_LIST:
                update(new WifiAccessPoints(smsParser.getWappWifi(), date, _id));
                update(new Battery(smsParser.getBattery(), date, _id));
                break;
        }
    }

    private void update(Setting setting) {
        // update the internal list, but only if the new setting is newer than the saved setting
        for (Setting _l : l)
            if (_l.getKey() == setting.getKey()) {
                if (_l.getDate() < setting.getDate()) {
                    l.add(setting);
                    l.remove(_l);
                }
                break;
            }
    }

    public void updatePreferences() {
        PreferenceUpdater preferenceUpdater = new PreferenceUpdater();
        List<de.bikebean.app.db.status.Status> newStatusEntries = new ArrayList<>();

        updateFields();

        if (wifi.getDate() != 0) {
            preferenceUpdater.updateWifi(
                settings,
                wifi.get(),
                Utils.convertToTime(wifi.getDate())
            );
        }
        if (battery.getDate() != 0) {
            newStatusEntries.add(new de.bikebean.app.db.status.Status(
                    battery.getDate(),
                    de.bikebean.app.db.status.Status.KEY_BATTERY,
                    battery.get(),
                    "",
                    de.bikebean.app.db.status.Status.STATUS_CONFIRMED,
                    battery.getId())
            );
        }
        if (interval.getDate() != 0) {
            preferenceUpdater.updateInterval(
                settings,
                interval.get(),
                Utils.convertToTime(interval.getDate())
            );
        }
        if (warningNumber.getDate() != 0) {
            preferenceUpdater.updateWarningNumber(
                    settings,
                    warningNumber.get(),
                    Utils.convertToTime(warningNumber.getDate())
            );
        }
        if (status.getDate() != 0) {
            newStatusEntries.add(new de.bikebean.app.db.status.Status(
                    status.getDate(),
                    de.bikebean.app.db.status.Status.KEY_STATUS,
                    0.0,
                    "",
                    de.bikebean.app.db.status.Status.STATUS_CONFIRMED,
                    status.getId())
            );
        }
        if (cellTowers.getDate() != 0) {
            newStatusEntries.add(new de.bikebean.app.db.status.Status(
                    cellTowers.getDate(),
                    de.bikebean.app.db.status.Status.KEY_CELL_TOWERS,
                    0.0,
                    cellTowers.get(),
                    de.bikebean.app.db.status.Status.STATUS_PENDING,
                    cellTowers.getId())
            );
        }
        if (wifiAccessPoints.getDate() != 0) {
            newStatusEntries.add(new de.bikebean.app.db.status.Status(
                    wifiAccessPoints.getDate(),
                    de.bikebean.app.db.status.Status.KEY_WIFI_ACCESS_POINTS,
                    0.0,
                    wifiAccessPoints.get(),
                    de.bikebean.app.db.status.Status.STATUS_PENDING,
                    wifiAccessPoints.getId())
            );
        }

        for (de.bikebean.app.db.status.Status status : newStatusEntries)
            statusViewModel.insert(status);
    }

    private void updateFields() {
        for (Setting _l : l) {
            switch (_l.getKey()) {
                case KEY_WIFI:
                    wifi = (Wifi) _l;
                    break;
                case KEY_BATTERY:
                    battery = (Battery) _l;
                    break;
                case KEY_INTERVAL:
                    interval = (Interval) _l;
                    break;
                case KEY_WARNING_NUMBER:
                    warningNumber = (WarningNumber) _l;
                    break;
                case KEY_STATUS:
                    status = (Status) _l;
                    break;
                case KEY_CELL_TOWERS:
                    cellTowers = (CellTowers) _l;
                    break;
                case KEY_WIFI_ACCESS_POINT:
                    wifiAccessPoints = (WifiAccessPoints) _l;
                    break;
            }
        }
    }

    abstract class Setting {
        long date;
        int key;
        int smsId;

        long getDate() {
            return date;
        }

        int getKey() {
            return key;
        }

        int getId() {
            return smsId;
        }

        abstract Object get();
    }

    class Wifi extends Setting {
        private boolean wifi;

        Wifi(boolean wifi, long date, int _id) {
            this.date = date;
            this.wifi = wifi;
            this.key = KEY_WIFI;
            this.smsId = _id;
        }

        Boolean get() {
            return wifi;
        }
    }

    class Battery extends Setting {
        private double battery;

        Battery(double battery, long date, int _id) {
            this.date = date;
            this.battery = battery;
            this.key = KEY_BATTERY;
            this.smsId = _id;
        }

        Double get() {
            return battery;
        }
    }

    class Interval extends Setting {
        private String interval;

        Interval(String interval, long date, int _id) {
            this.date = date;
            this.interval = interval;
            this.key = KEY_INTERVAL;
            this.smsId = _id;
        }

        String get() {
            return interval;
        }
    }

    class WarningNumber extends Setting {
        private String warningNumber;

        WarningNumber(String warningNumber, long date, int _id) {
            this.date = date;
            this.warningNumber = warningNumber;
            this.key = KEY_WARNING_NUMBER;
            this.smsId = _id;
        }

        String get() {
            return warningNumber;
        }
    }

    class Status extends Setting {
        private String status;

        Status(String status, long date, int _id) {
            this.date = date;
            this.status = status;
            this.key = KEY_STATUS;
            this.smsId = _id;
        }

        String get() {
            return status;
        }
    }

    class CellTowers extends Setting {
        private String cellTowers;

        CellTowers(String cellTowers, long date, int _id) {
            this.date = date;
            this.cellTowers = cellTowers;
            this.key = KEY_CELL_TOWERS;
            this.smsId = _id;
        }

        String get() {
            return cellTowers;
        }
    }

    class WifiAccessPoints extends Setting {
        private String wifiAccessPoints;

        WifiAccessPoints(String wifiAccessPoints, long date, int _id) {
            this.date = date;
            this.wifiAccessPoints = wifiAccessPoints;
            this.key = KEY_WIFI_ACCESS_POINT;
            this.smsId = _id;
        }

        String get() {
            return wifiAccessPoints;
        }
    }
}
