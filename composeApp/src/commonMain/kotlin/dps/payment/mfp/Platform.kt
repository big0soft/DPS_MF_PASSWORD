package dps.payment.mfp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform