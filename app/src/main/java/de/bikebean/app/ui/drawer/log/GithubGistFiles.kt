package de.bikebean.app.ui.drawer.log

import kotlinx.serialization.Serializable

@Serializable
data class GithubGistFiles(
        val sms_tsv: GithubGistTsv,
        val state_tsv: GithubGistTsv,
        val log_tsv: GithubGistTsv
)

internal class GithubGistFilesBuilder(
        smsTsv: String,
        stateTsv: String,
        logTsv: String) {

    private val data = GithubGistFiles(
            GithubGistTsv(smsTsv),
            GithubGistTsv(stateTsv),
            GithubGistTsv(logTsv),
    )

    fun build() = data

}