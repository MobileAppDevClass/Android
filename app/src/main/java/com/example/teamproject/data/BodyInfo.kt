package com.example.teamproject.data

data class BodyInfo(
    val height: Float = 0f,  // cm
    val weight: Float = 0f,  // kg
    val age: Int = 0,
    val gender: String = "남성",  // 남성 or 여성
    val activityLevel: String = "보통"  // 낮음, 보통, 높음
) {
    // 하루 권장 물 섭취량 계산 (ml)
    fun calculateRecommendedWaterIntake(): Int {
        val baseIntake = weight * 30  // 체중 1kg당 30ml
        val activityMultiplier = when (activityLevel) {
            "낮음" -> 1.0f
            "보통" -> 1.2f
            "높음" -> 1.5f
            else -> 1.0f
        }
        return (baseIntake * activityMultiplier).toInt()
    }
}
