package de.bikebean.app.db.type.types

import java.util.regex.Pattern

/* Regex patterns for Sms Parser */
object ParserPatterns {

    var positionPattern: Pattern = Pattern.compile(
            "([0-9]{3},[0-9]{2},[0-9a-fA-F]+,[0-9a-fA-F]+,[0-9]+)"
    )
    var statusWarningNumberPattern: Pattern = Pattern.compile(
            "(Warningnumber: )([+0-9]{8,})"
    )
    var statusNoWarningNumberPattern: Pattern = Pattern.compile(
            "(Warningnumber: )"
    )
    var statusIntervalPattern: Pattern = Pattern.compile(
            "(Interval: )(1|2|4|8|12|24)(h)"
    )
    var statusWifiStatusPattern: Pattern = Pattern.compile(
            "(Wifi Status: )(on|off)"
    )
    var statusBatteryStatusPattern: Pattern = Pattern.compile(
            "(Battery Status: )([0-9]{1,3})(%)"
    )
    var statusBatteryStatusPatternShort: Pattern = Pattern.compile(
            "([0-9]{1,3})(%)"
    )
    var warningNumberPattern: Pattern = Pattern.compile(
            "(Warningnumber has been changed to )([+0-9]{8,})"
    )
    var wifiStatusOnPattern: Pattern = Pattern.compile(
            "(Wifi is on!)"
    )
    var wifiStatusOffPattern: Pattern = Pattern.compile(
            "(Wifi Off)"
    )
    var intervalChangedPattern: Pattern = Pattern.compile(
            "(GSM will be switched on every )(1|2|4|8|12|24)( hour)(s)*([.])"
    )
    var wifiPattern: Pattern = Pattern.compile(
            "([0-9a-fA-F]{14})"
    )
    var noWifiPattern: Pattern = Pattern.compile(
            "(no wifi available)([0-9]{1,3})"
    )
    var noWifiPatternAlt: Pattern = Pattern.compile(
            "(no wifi available\n)([0-9]{1,3})"
    )
    var batteryPattern: Pattern = Pattern.compile(
            "^([0-9]{1,3})$", Pattern.MULTILINE
    )
    var lowBatteryPattern: Pattern = Pattern.compile(
            "(BATTERY LOW!\\nBATTERY STATUS: )([0-9]{1,3})(%)"
    )
    var veryLowBatteryPattern: Pattern = Pattern.compile(
            "(Interval set to 24h)"
    )

}