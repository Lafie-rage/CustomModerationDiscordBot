package net.lafierage.cmdb.model

enum class AbusiveBan(val message: String) {
    NOT_AT_ALL("Pas le moins du monde"),
    LEGIT("Grave pas, c'est legit"),
    ALWAYS_DESERVED("C'est toujours mérité"),
    NEVER_SEEN_A_NON_LEGIT("On a jamais vu une telle action pas legit");

    override fun toString(): String {
        return message
    }

    companion object {
        private const val SIZE = 4

        /**
         * A very optimized function to retrieve a random reason to say the ban wasn't abusive.
         *
         * @return A reason why the ban wasn't abusive
         */
        fun getRandomAbusiveBan(): AbusiveBan {
            return when ((Math.random() * SIZE).toInt()) {
                0 -> NOT_AT_ALL
                1 -> ALWAYS_DESERVED
                2 -> LEGIT
                else -> NEVER_SEEN_A_NON_LEGIT
            }
        }
    }
}