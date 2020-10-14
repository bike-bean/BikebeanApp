package de.bikebean.app.ui.main.status.menu.log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

class GithubGistBody {

    private final @NonNull String description;
    private final @NonNull GithubGistFiles files;

    GithubGistBody(final @NonNull String description, final @NonNull String smsTsv,
                   final @NonNull String stateTsv, final @NonNull String logTsv) {
        this.description = description;
        this.files = new GithubGistFiles(smsTsv, stateTsv, logTsv);
    }

    @NonNull String createJsonApiBody() {
        final @NonNull Gson gson = new Gson();

        final @NonNull String json = gson.toJson(this);

        return json
                .replace("sms_tsv", "sms.tsv")
                .replace("state_tsv", "state.tsv")
                .replace("log_tsv", "log.tsv");
    }
}
