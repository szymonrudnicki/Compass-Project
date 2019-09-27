package com.github.szymonrudnicki.compassproject.validators

import com.github.szymonrudnicki.compassproject.ui.validators.LatitudeValidator
import org.junit.Assert.assertEquals
import org.junit.Test

class LatitudeValidatorTest {

    @Test
    fun `should return true when latitude is in range`() {
        val correctLatitude = "10"
        val result = LatitudeValidator.isValid(correctLatitude)
        assertEquals(true, result)
    }

    @Test
    fun `should return false when latitude contains a letter`() {
        val latitudeWithLetter = "10x"
        val result = LatitudeValidator.isValid(latitudeWithLetter)
        assertEquals(false, result)
    }

    @Test
    fun `should return false when latitude is out of range`() {
        val latitudeOutOfRange = "200"
        val result = LatitudeValidator.isValid(latitudeOutOfRange)
        assertEquals(false, result)
    }
}
