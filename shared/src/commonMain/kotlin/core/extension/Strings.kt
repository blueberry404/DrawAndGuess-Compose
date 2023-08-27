package core.extension

fun String.getInitials(): String {
    val parts = this.split(" ")
    val first = parts[0][0].uppercaseChar()
    val second = parts[1][0].uppercaseChar()
    return "$first$second"
}