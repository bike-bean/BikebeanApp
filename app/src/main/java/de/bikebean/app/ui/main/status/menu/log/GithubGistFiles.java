package de.bikebean.app.ui.main.status.menu.log;

class GithubGistFiles {

    private final GithubGistTsv sms_tsv;
    private final GithubGistTsv state_tsv;
    private final GithubGistTsv log_tsv;

    GithubGistFiles(String smsTsv, String stateTsv, String logTsv) {
        this.sms_tsv = new GithubGistTsv(smsTsv);
        this.state_tsv = new GithubGistTsv(stateTsv);
        this.log_tsv = new GithubGistTsv(logTsv);
    }
}
