package com.focusclinic.domain.model

data class ProfileAttributes(
    val theme: String,
    val workspaceStyle: String,
    val tools: List<String>,
    val decorations: List<String>,
) {
    companion object {
        val DEFAULT = ProfileAttributes(
            theme = "default",
            workspaceStyle = "basic",
            tools = emptyList(),
            decorations = emptyList(),
        )
    }
}
