package tools.mo3ta.kam.domain

import kotlin.math.roundToLong
import tools.mo3ta.kam.data.City

/**
 * Cost-of-living comparison math, ported from the design handoff `data.js`
 * reference implementation and adapted to the app's [City] model.
 *
 * Differences from the prototype:
 *  - `perUSD` (currency units per 1 USD) comes from live FX [rates] (open.er-api,
 *    USD base) keyed by currency code, instead of a bundled constant.
 *  - The overall cost index is [City.baseIndex] (NYC = 100 scale); rent is
 *    [City.indices].rent.
 *  - Category breakdown uses the three real indices we actually have
 *    (Rent, Groceries, Dining = restaurant) rather than the prototype's
 *    fabricated affine curves for Transport/Leisure.
 */

enum class TrendDirection { CHEAPER, PRICIER }

data class TierInfo(val index: Int, val label: String, val blurb: String)

object Tiers {
    // adjustedUSD (NYC-equivalent monthly buying power) thresholds
    private val maxima = listOf(2500.0, 5000.0, 9000.0, Double.MAX_VALUE)
    private val labels = listOf("Basic", "Middle", "Comfortable", "Luxury")
    private val blurbs = listOf(
        "Covers the essentials with little left over.",
        "Steady living, modest savings and the odd treat.",
        "Room to save, dine out and travel freely.",
        "Premium living with significant disposable income.",
    )

    fun of(adjustedUSD: Double): TierInfo {
        val i = maxima.indexOfFirst { adjustedUSD < it }.let { if (it < 0) maxima.lastIndex else it }
        return TierInfo(i, labels[i], blurbs[i])
    }

    /** Marker position 0..1 along the tier meter. */
    fun position(adjustedUSD: Double): Float =
        (adjustedUSD / 13000.0).coerceIn(0.04, 0.96).toFloat()
}

enum class CostCategory(val label: String) {
    RENT("Rent"),
    GROCERIES("Groceries"),
    DINING("Dining out"),
}

fun City.categoryIndex(cat: CostCategory): Double = when (cat) {
    CostCategory.RENT -> indices.rent
    CostCategory.GROCERIES -> indices.groceries
    CostCategory.DINING -> indices.restaurant
}

data class OfferResult(
    val offerUSD: Double,
    val vsBreakevenPct: Double,
    val equivalentHomeLocal: Double,
    val tier: TierInfo,
    val tierPos: Float,
)

data class CompareResult(
    val ratesReady: Boolean,
    val salaryUSD: Double,
    val breakevenUSD: Double,
    val breakevenLocal: Double,
    val sameSalaryLocal: Double,
    val powerPct: Double,
    val costDeltaPct: Double,
    val adjustedUSD: Double,
    val tier: TierInfo,
    val tierPos: Float,
    val homeTierPos: Float,
    val offer: OfferResult?,
) {
    val cheaper: Boolean get() = costDeltaPct < 0
    val powerUp: Boolean get() = powerPct >= 0
}

/** units of [city]'s currency per 1 USD, from live FX. null if unavailable. */
fun perUSD(city: City, rates: Map<String, Double>): Double? = rates[city.currency]

/**
 * Core comparison. [salaryLocal] is monthly in [origin]'s currency.
 * [offerLocal] (optional, > 0) is a salary offer in [dest]'s currency.
 */
fun compare(
    origin: City,
    dest: City,
    salaryLocal: Double,
    offerLocal: Double,
    rates: Map<String, Double>,
): CompareResult {
    val originPerUSD = perUSD(origin, rates)
    val destPerUSD = perUSD(dest, rates)
    val ratesReady = originPerUSD != null && destPerUSD != null
    val oPer = originPerUSD ?: 1.0
    val dPer = destPerUSD ?: 1.0
    val oIdx = origin.baseIndex
    val dIdx = dest.baseIndex

    val salaryUSD = salaryLocal / oPer
    val breakevenUSD = salaryUSD * (dIdx / oIdx)
    val breakevenLocal = breakevenUSD * dPer
    val sameSalaryLocal = salaryUSD * dPer
    val powerRatio = oIdx / dIdx
    val powerPct = (powerRatio - 1) * 100
    val costDeltaPct = (dIdx - oIdx) / oIdx * 100
    val adjustedUSD = salaryUSD * (100 / dIdx)
    val tier = Tiers.of(adjustedUSD)
    val tierPos = Tiers.position(adjustedUSD)
    val homeAdjUSD = salaryUSD * (100 / oIdx)
    val homeTierPos = Tiers.position(homeAdjUSD)

    val offer = if (offerLocal > 0) {
        val offerUSD = offerLocal / dPer
        val vsBreakevenPct = if (breakevenUSD == 0.0) 0.0 else (offerUSD - breakevenUSD) / breakevenUSD * 100
        val equivalentHomeLocal = offerUSD * (oIdx / dIdx) * oPer
        val offerAdjUSD = offerUSD * (100 / dIdx)
        OfferResult(
            offerUSD = offerUSD,
            vsBreakevenPct = vsBreakevenPct,
            equivalentHomeLocal = equivalentHomeLocal,
            tier = Tiers.of(offerAdjUSD),
            tierPos = Tiers.position(offerAdjUSD),
        )
    } else null

    return CompareResult(
        ratesReady = ratesReady,
        salaryUSD = salaryUSD,
        breakevenUSD = breakevenUSD,
        breakevenLocal = breakevenLocal,
        sameSalaryLocal = sameSalaryLocal,
        powerPct = powerPct,
        costDeltaPct = costDeltaPct,
        adjustedUSD = adjustedUSD,
        tier = tier,
        tierPos = tierPos,
        homeTierPos = homeTierPos,
        offer = offer,
    )
}

/** Conversion rate shown in the footnote: 1 origin-currency ≈ N dest-currency. */
fun fxBetween(origin: City, dest: City, rates: Map<String, Double>): Double? {
    val o = perUSD(origin, rates) ?: return null
    val d = perUSD(dest, rates) ?: return null
    if (o == 0.0) return null
    return d / o
}
