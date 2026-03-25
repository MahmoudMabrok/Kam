package tools.mo3ta.kam

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform