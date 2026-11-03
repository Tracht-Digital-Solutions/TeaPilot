package de.trachtdigital.teapilot

import de.trachtdigital.teapilot.core.ApiTransport
import org.json.JSONArray
import org.json.JSONObject

data class Repository(val id: Long, val fullName: String, val description: String, val isPrivate: Boolean)
data class RepositoryPage(val repositories: List<Repository>, val hasNext: Boolean)

class GiteaApi(private val transport: ApiTransport) {
    fun version(): String = JSONObject(transport.get("/version", null).body).getString("version").also {
        require(it.isNotBlank())
    }

    fun username(token: String): String = JSONObject(transport.get("/user", token).body).getString("login").also {
        require(it.isNotBlank())
    }

    fun repositories(token: String, page: Int): RepositoryPage {
        require(page > 0)
        val response = transport.get("/user/repos?page=$page&limit=50", token)
        val array = JSONArray(response.body)
        val repositories = (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            Repository(item.getLong("id"), item.getString("full_name"), item.optString("description", ""), item.getBoolean("private"))
        }
        return RepositoryPage(repositories, response.hasNext && repositories.isNotEmpty())
    }
}
