package de.bikebean.app.ui.main.status.menu.log;

import com.google.gson.Gson;

class GithubGistBody {

    private final String description;
    private final GithubGistFiles files;

    GithubGistBody(String description, String smsTsv, String stateTsv, String logTsv) {
        this.description = description;
        this.files = new GithubGistFiles(smsTsv, stateTsv, logTsv);
    }

    String createJsonApiBody() {
        final Gson gson = new Gson();

        String json = gson.toJson(this);

        return json
                .replace("sms_tsv", "sms.tsv")
                .replace("state_tsv", "state.tsv")
                .replace("log_tsv", "log.tsv");
    }
}
