package de.bikebean.app.ui.main.status.menu.log;

import com.google.gson.Gson;

class GithubGistBody {

    private final String description;
    private final GithubGistFiles files;

    GithubGistBody(String description, String smsCsv, String stateCsv, String logCsv) {
        this.description = description;
        this.files = new GithubGistFiles(smsCsv, stateCsv, logCsv);
    }

    String createJsonApiBody() {
        final Gson gson = new Gson();

        String json = gson.toJson(this);

        return json
                .replace("sms_csv", "sms.csv")
                .replace("state_csv", "state.csv")
                .replace("log_csv", "log.csv");
    }
}
