package de.bikebean.app.ui.drawer.log;

import androidx.annotation.NonNull;

class GithubGistFiles {

    private final @NonNull GithubGistTsv sms_tsv;
    private final @NonNull GithubGistTsv state_tsv;
    private final @NonNull GithubGistTsv log_tsv;

    GithubGistFiles(final @NonNull String smsTsv, final @NonNull String stateTsv,
                    final @NonNull String logTsv) {
        this.sms_tsv = new GithubGistTsv(smsTsv);
        this.state_tsv = new GithubGistTsv(stateTsv);
        this.log_tsv = new GithubGistTsv(logTsv);
    }
}
