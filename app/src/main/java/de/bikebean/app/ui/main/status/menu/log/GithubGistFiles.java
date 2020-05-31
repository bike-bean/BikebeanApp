package de.bikebean.app.ui.main.status.menu.log;

class GithubGistFiles {

    private GithubGistTsv sms_tsv;
    private GithubGistTsv state_tsv;
    private GithubGistTsv log_tsv;

    GithubGistFiles(String smsTsv, String stateTsv, String logTsv) {
        this.sms_tsv = new GithubGistTsv(smsTsv);
        this.state_tsv = new GithubGistTsv(stateTsv);
        this.log_tsv = new GithubGistTsv(logTsv);
    }
}
