package de.bikebean.app.ui.main.status.menu.log;

class GithubGistFiles {

    private GithubGistCsv sms_csv;
    private GithubGistCsv state_csv;
    private GithubGistCsv log_csv;

    GithubGistFiles(String smsCsv, String stateCsv, String logCsv) {
        this.sms_csv = new GithubGistCsv(smsCsv);
        this.state_csv = new GithubGistCsv(stateCsv);
        this.log_csv = new GithubGistCsv(logCsv);
    }
}
