package tools.mo3ta.kam.domain

import kotlin.math.abs
import kotlin.math.roundToLong
import tools.mo3ta.kam.data.City

/** Minimal currency-code -> symbol map (City has no symbol field). Falls back to code. */
object CurrencySymbols {
    private val map = mapOf(
        "USD" to "$", "EUR" to "€", "GBP" to "£", "JPY" to "¥", "CNY" to "¥",
        "AUD" to "A$", "CAD" to "C$", "SGD" to "S$", "NZD" to "NZ$", "HKD" to "HK$",
        "CHF" to "CHF", "SEK" to "kr", "NOK" to "kr", "DKK" to "kr", "PLN" to "zł",
        "AED" to "د.إ", "SAR" to "﷼", "TRY" to "₺", "EGP" to "E£", "INR" to "₹",
        "MXN" to "$", "BRL" to "R$", "THB" to "฿", "IDR" to "Rp", "MYR" to "RM",
        "PHP" to "₱", "VND" to "₫", "ZAR" to "R", "RUB" to "₽", "KRW" to "₩",
        "QAR" to "﷼", "KWD" to "د.ك", "BHD" to ".د.ب", "JOD" to "د.ا", "MAD" to "د.م.",
        "CZK" to "Kč", "HUF" to "Ft", "ILS" to "₪", "RON" to "lei", "NGN" to "₦",
    )

    fun of(code: String): String = map[code] ?: code
}

/** Group integer part with thousands separators (locale-agnostic, en grouping). */
fun groupThousands(value: Long): String {
    val neg = value < 0
    val s = abs(value).toString()
    val sb = StringBuilder()
    val rem = s.length % 3
    for (i in s.indices) {
        if (i != 0 && (i - rem) % 3 == 0) sb.append(',')
        sb.append(s[i])
    }
    return if (neg) "-$sb" else sb.toString()
}

/** Format an amount with the city's currency symbol, e.g. "E£12,300". */
fun money(amount: Double, city: City, decimals: Int = 0): String {
    val sym = CurrencySymbols.of(city.currency)
    return sym + amountString(amount, decimals)
}

fun amountString(amount: Double, decimals: Int = 0): String {
    if (decimals <= 0) return groupThousands(amount.roundToLong())
    val rounded = amount
    val whole = rounded.toLong()
    val frac = ((abs(rounded - whole)) * pow10(decimals)).roundToLong()
    return groupThousands(whole) + "." + frac.toString().padStart(decimals, '0')
}

private fun pow10(n: Int): Double {
    var r = 1.0
    repeat(n) { r *= 10 }
    return r
}
