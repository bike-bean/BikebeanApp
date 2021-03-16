package de.bikebean.app.ui.drawer.log

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class GithubGistBody(
        val description: String,
        val files: GithubGistFiles,
        val public: Boolean)

internal class GithubGistBodyBuilder(
        description: String, smsTsv: String,
        stateTsv: String, logTsv: String) {

    private val json = Json.encodeToString(
            GithubGistBody(
                    description,
                    GithubGistFilesBuilder(smsTsv, stateTsv, logTsv).build(),
                    false
            )
    )

    fun build(): String {
        return json
                .replace("sms_tsv", "sms.tsv")
                .replace("state_tsv", "state.tsv")
                .replace("log_tsv", "log.tsv")
    }

}