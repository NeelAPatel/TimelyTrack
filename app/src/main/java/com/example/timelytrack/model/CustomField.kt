package com.example.timelytrack.model

// Enables implementation of custom fields in the user's current category as a template
sealed class CustomField(val label: String) {
    data class TextField(val value: String = "") : CustomField("Text")
    data class NumericalField(val value: Int = 0, val range: IntRange) : CustomField("Numerical")
    data class YesNoField(val value: Boolean = false) : CustomField("Yes/No")
    data class SliderField(val value: Int = 0, val max: Int = 10) : CustomField("Slider")
    // Additional field types as needed
}