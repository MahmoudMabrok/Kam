package tools.mo3ta.kam.ui

import androidx.compose.runtime.staticCompositionLocalOf

interface KamStrings {
    val appName: String
    val tabCities: String
    val tabConvert: String
    val tabCompare: String
    val tabSettings: String
    val settingsTitle: String
    val settingsDarkMode: String
    val settingsLanguage: String
    val settingsCredits: String
    val settingsNumbeoDesc: String
    val onboardingWelcome: String
    val onboardingWelcomeDesc: String
    val onboardingConverter: String
    val onboardingConverterDesc: String
    val onboardingCompare: String
    val onboardingCompareDesc: String
    val onboardingData: String
    val onboardingDataDesc: String
    val onboardingGetStarted: String
    val visitNumbeo: String
    
    // City List
    val citiesTitle: String
    val citiesCountTemplate: (Int, Int) -> String
    val searchPlaceholder: String
    val singleLabel: String
    val familyLabel: String
    val avgSalaryLabel: String
    val costIndicesTitle: String
    val searchCityPlaceholder: String
    val indexCOL: String
    val indexRent: String
    val indexGroceries: String
    val indexRestaurant: String
    val indexPurchasingPower: String
    
    // Convert
    val convertTitle: String
    val convertDesc: String
    val currentCityLabel: String
    val destinationCityLabel: String
    val salaryLabel: (String) -> String
    val scenarioASameSalary: String
    val scenarioASublabel: (Long, String, String) -> String
    val scenarioAVerdictPositive: String
    val scenarioAVerdictNegative: String
    val scenarioBMaintainLifestyle: String
    val scenarioBSublabel: (String) -> String
    val scenarioBEquivalent: (Long, String) -> String
    val scenarioBNeededIn: (String) -> String
    val costOfLivingGapCheaper: (String, Double) -> String
    val costOfLivingGapExpensive: (String, Double) -> String
    
    // Compare
    val compareTitle: String
    val compareDesc: String
    val optionA: String
    val optionB: String
    val cityLabel: String
    val salaryInputLabel: String
    val comparisonResultTitle: String
    val comparisonVerdictWinner: (String, Double) -> String
    val comparisonVerdictEqual: String
    val powerLabel: (Double) -> String
}

object EnStrings : KamStrings {
    override val appName = "Kam"
    override val tabCities = "Cities"
    override val tabConvert = "Convert"
    override val tabCompare = "Compare"
    override val tabSettings = "Settings"
    override val settingsTitle = "Settings"
    override val settingsDarkMode = "Dark Mode"
    override val settingsLanguage = "Language"
    override val settingsCredits = "Credits"
    override val settingsNumbeoDesc = "Data is manually sourced from Numbeo."
    override val onboardingWelcome = "Welcome to Kam"
    override val onboardingWelcomeDesc = "Understand your real purchasing power across different cities around the world."
    override val onboardingConverter = "Salary Converter"
    override val onboardingConverterDesc = "Calculate the equivalent salary you need in a destination city to maintain your current lifestyle."
    override val onboardingCompare = "Salary Comparison"
    override val onboardingCompareDesc = "Directly compare two different salary offers in different cities to see which one gives you more power."
    override val onboardingData = "Data Source"
    override val onboardingDataDesc = "All cost of living data is provided by Numbeo. Use it to make informed decisions about your next move."
    override val onboardingGetStarted = "Get Started"
    override val visitNumbeo = "Visit Numbeo.com"

    override val citiesTitle = "🌍 Cities"
    override val citiesCountTemplate = { count: Int, countries: Int -> "$count cities across $countries countries" }
    override val searchPlaceholder = "Search cities or countries..."
    override val singleLabel = "Single"
    override val familyLabel = "Family"
    override val avgSalaryLabel = "Avg salary"
    override val costIndicesTitle = "Cost Indices"
    override val searchCityPlaceholder = "Search city..."
    override val indexCOL = "Cost of Living"
    override val indexRent = "Rent"
    override val indexGroceries = "Groceries"
    override val indexRestaurant = "Restaurant"
    override val indexPurchasingPower = "Purchasing Power"

    override val convertTitle = "💱 Salary Converter"
    override val convertDesc = "Understand your real purchasing power across cities"
    override val currentCityLabel = "Your Current City"
    override val destinationCityLabel = "Destination City"
    override val salaryLabel = { currency: String -> "Your Monthly Salary in $currency" }
    override val scenarioASameSalary = "📦 Scenario A — Same salary, you move"
    override val scenarioASublabel = { salary: Long, currency: String, city: String -> "If you keep earning $salary $currency but relocate to $city" }
    override val scenarioAVerdictPositive = "✅ Moving there with the same salary improves your lifestyle."
    override val scenarioAVerdictNegative = "⚠️ Moving there with the same salary reduces your lifestyle."
    override val scenarioBMaintainLifestyle = "🎯 Scenario B — Maintain your lifestyle"
    override val scenarioBSublabel = { city: String -> "The salary you'd need in $city to live exactly as you do now" }
    override val scenarioBEquivalent = { salary: Long, currency: String -> "$salary $currency" }
    override val scenarioBNeededIn = { city: String -> "needed in $city" }
    override val costOfLivingGapCheaper = { city: String, pct: Double -> "💰 $city is cheaper — you'd need ${"%.1f".format(pct)}% of your salary" }
    override val costOfLivingGapExpensive = { city: String, pct: Double -> "📈 $city is more expensive — requires ${"%.1f".format(pct)}% more salary" }

    override val compareTitle = "⚖️ Compare Salaries"
    override val compareDesc = "Normalize salaries against city cost indices"
    override val optionA = "Option A"
    override val optionB = "Option B"
    override val cityLabel = "City"
    override val salaryInputLabel = "Salary"
    override val comparisonResultTitle = "📊 Purchasing Power Comparison"
    override val comparisonVerdictWinner = { city: String, pct: Double -> "🏆 $city wins with ${"%.1f".format(pct)}% more purchasing power" }
    override val comparisonVerdictEqual = "🤝 Both salaries have equal purchasing power"
    override val powerLabel = { power: Double -> "Power: ${"%.1f".format(power)}" }
}

object ArStrings : KamStrings {
    override val appName = "كم"
    override val tabCities = "المدن"
    override val tabConvert = "تحويل"
    override val tabCompare = "مقارنة"
    override val tabSettings = "الإعدادات"
    override val settingsTitle = "الإعدادات"
    override val settingsDarkMode = "الوضع الداكن"
    override val settingsLanguage = "اللغة"
    override val settingsCredits = "الحقوق"
    override val settingsNumbeoDesc = "يتم جلب البيانات يدوياً من موقع Numbeo."
    override val onboardingWelcome = "مرحباً بك في \"كم\""
    override val onboardingWelcomeDesc = "افهم قوتك الشرائية الحقيقية في مدن مختلفة حول العالم."
    override val onboardingConverter = "محول الرواتب"
    override val onboardingConverterDesc = "احسب الراتب المعادل الذي تحتاجه في مدينة الوجهة للحفاظ على مستوى معيشتك الحالي."
    override val onboardingCompare = "مقارنة الرواتب"
    override val onboardingCompareDesc = "قارن مباشرة بين عرضين وظيفيين مختلفين في مدن مختلفة لمعرفة أيهما يمنحك قوة شرائية أكبر."
    override val onboardingData = "مصدر البيانات"
    override val onboardingDataDesc = "يتم توفير جميع بيانات تكلفة المعيشة من قبل Numbeo. استخدمها لاتخاذ قرارات مدروسة حول خطوتك القادمة."
    override val onboardingGetStarted = "ابدأ الآن"
    override val visitNumbeo = "زيارة Numbeo.com"

    override val citiesTitle = "🌍 المدن"
    override val citiesCountTemplate = { count: Int, countries: Int -> "$count مدينة في $countries دولة" }
    override val searchPlaceholder = "ابحث عن المدن أو الدول..."
    override val singleLabel = "فردي"
    override val familyLabel = "عائلة"
    override val avgSalaryLabel = "متوسط الراتب"
    override val costIndicesTitle = "مؤشرات التكلفة"
    override val searchCityPlaceholder = "ابحث عن المدينة..."
    override val indexCOL = "تكلفة المعيشة"
    override val indexRent = "الإيجار"
    override val indexGroceries = "البقالة"
    override val indexRestaurant = "المطاعم"
    override val indexPurchasingPower = "القوة الشرائية"

    override val convertTitle = "💱 محول الرواتب"
    override val convertDesc = "افهم قوتك الشرائية الحقيقية عبر المدن"
    override val currentCityLabel = "مدينتك الحالية"
    override val destinationCityLabel = "مدينة الوجهة"
    override val salaryLabel = { currency: String -> "راتبك الشهري بـ $currency" }
    override val scenarioASameSalary = "📦 السيناريو أ — نفس الراتب، إذا انتقلت"
    override val scenarioASublabel = { salary: Long, currency: String, city: String -> "إذا استمريت في كسب $salary $currency ولكن انتقلت إلى $city" }
    override val scenarioAVerdictPositive = "✅ الانتقال إلى هناك بنفس الراتب يحسن مستوى معيشتك."
    override val scenarioAVerdictNegative = "⚠️ الانتقال إلى هناك بنفس الراتب يقلل من مستوى معيشتك."
    override val scenarioBMaintainLifestyle = "🎯 السيناريو ب — الحفاظ على مستوى معيشتك"
    override val scenarioBSublabel = { city: String -> "الراتب الذي ستحتاجه في $city للعيش تماماً كما تعيش الآن" }
    override val scenarioBEquivalent = { salary: Long, currency: String -> "$salary $currency" }
    override val scenarioBNeededIn = { city: String -> "مطلوب في $city" }
    override val costOfLivingGapCheaper = { city: String, pct: Double -> "💰 $city أرخص — ستحتاج إلى ${"%.1f".format(pct)}% من راتبك" }
    override val costOfLivingGapExpensive = { city: String, pct: Double -> "📈 $city أغلى — يتطلب ${"%.1f".format(pct)}% زيادة في الراتب" }

    override val compareTitle = "⚖️ مقارنة الرواتب"
    override val compareDesc = "مقارنة الرواتب مقابل مؤشرات تكلفة المدينة"
    override val optionA = "الخيار أ"
    override val optionB = "الخيار ب"
    override val cityLabel = "المدينة"
    override val salaryInputLabel = "الراتب"
    override val comparisonResultTitle = "📊 مقارنة القوة الشرائية"
    override val comparisonVerdictWinner = { city: String, pct: Double -> "🏆 $city يتفوق بـ ${"%.1f".format(pct)}% قوة شرائية أكثر" }
    override val comparisonVerdictEqual = "🤝 كلا الراتبين لهما نفس القوة الشرائية"
    override val powerLabel = { power: Double -> "القوة: ${"%.1f".format(power)}" }
}

val LocalKamStrings = staticCompositionLocalOf<KamStrings> { EnStrings }
