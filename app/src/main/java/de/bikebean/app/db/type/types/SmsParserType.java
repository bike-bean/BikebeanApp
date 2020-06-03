package de.bikebean.app.db.type.types;

import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.type.Type;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public abstract class SmsParserType extends Type {

    protected SmsParser mSmsParser;

    public SmsParserType(Type.SMSTYPE t) {
        super(t);
    }

    public void addToConversationList(List<Setting> conversationList) {
        // update the conversationList, checking if the information from the SMS
        // is newer than already stored information.
        for (Setting smsSetting : getSettings())
            smsSetting.getConversationListAdder().addToConversationList(conversationList);
    }
}
