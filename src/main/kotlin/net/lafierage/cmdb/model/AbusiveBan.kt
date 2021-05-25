package net.lafierage.cmdb.model

enum class AbusiveBan(val message: String) {
    NOT_AT_ALL("Pas le moins du monde "),
    LEGIT("Grave pas, c'est legit "),
    ALWAYS_DESERVED("C'est toujours mérité"),
    NEVER_SEEN_A_NON_LEGIT("On a jamais vu une telle action pas legit");

    override fun toString(): String {
        return message
    }
}