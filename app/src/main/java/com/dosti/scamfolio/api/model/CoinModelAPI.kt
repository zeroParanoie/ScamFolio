package com.dosti.scamfolio.api.model

data class Sparkline(
    val price: List<Double>
)
data class CoinModelAPI(
    val id : String,
    val name: String,
    val symbol: String,
    val price: String?,
    val image: String,
    val price_change_percentage_24h : String,
    val current_price : String,
    var time_fetched: Long?,
    val sparkline_in_7d: Sparkline
)