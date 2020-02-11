package fr.robar.android.recipe

enum class BOTTLES(name: String) {
    RHUM("Rhum"),
    COCA("Coca-Cola"),
    ANANAS("Ananas"),
    ORANGE("Orange"),
}

enum class Mode {
    TIME,
    START,
    STOP
}

class Recipe(
    val rhum: Byte, // rhum
    val coca: Byte, // coca
    val ananas: Byte, // ananas
    val orange: Byte, // orange
    val trigger: Mode = Mode.TIME
) {
    fun encodeToByteArray() = byteArrayOf(
        when (trigger) {
            Mode.TIME -> 0xf
            Mode.START -> 0x1
            Mode.STOP -> 0x0
        }, rhum, coca, ananas, orange
    )

    companion object {
        val RECIPES = mapOf(
            "Rhum Coca-Cola" to Recipe(2, 8, 0, 0),
            "Planteur" to Recipe(2, 0, 4, 4),
            "Rhum Ananas" to Recipe(2, 0, 8, 0),
            "Rhum Orange" to Recipe(2, 0, 0, 8)
        )
    }
}