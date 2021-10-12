package models

import dsl.context

data class Role(val name: String, val type: Type, val context: context) {
    init {
        context.allClasses.add(name)
    }

    var participant: Participant? = null

    enum class Type {
        PARTY,
        DOMAIN,
        `3RD_SYSTEM`,
        EVIDENCE
    }

    infix fun played(participant: Participant): Role = apply { this.participant = participant }

    override fun toString(): String {
        return """
            class $name <<${type.name.lowercase()}>> #orange
        """.trimIndent()
    }
}
