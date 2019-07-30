package ru.sokomishalov.memeory.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.sokomishalov.memeory.AbstractMemeoryTest
import kotlin.math.abs

internal class ImageUtilsTest : AbstractMemeoryTest() {

    private val imageWidth = 200
    private val imageHeight = 300
    private val imageUrl = "https://picsum.photos/$imageWidth/$imageHeight"
    private val invalidImageUrl = "https://lol.kek/cheburek"
    private val imageUrl401 = "https://httpstat.us/401"
    private val imageUrl404 = "https://httpstat.us/404"


    @Test
    fun `Get random image by url and check dimensions`() {
        val imageDimensions = getImageDimensions(imageUrl)

        assertEquals(imageWidth, imageDimensions.t1)
        assertEquals(imageHeight, imageDimensions.t2)
    }

    @Test
    fun `Get random image by url and check aspect ratio`() {
        val expected = imageWidth.toDouble().div(imageHeight)
        val result = getImageAspectRatio(imageUrl)

        assertTrue(abs(expected - result) < 0.01)
    }

    @Test
    fun `Check image availability`() {
        assertTrue(checkAttachmentAvailability(imageUrl))
        assertFalse(checkAttachmentAvailability(invalidImageUrl))
        assertFalse(checkAttachmentAvailability(imageUrl401))
        assertFalse(checkAttachmentAvailability(imageUrl404))
    }
}
