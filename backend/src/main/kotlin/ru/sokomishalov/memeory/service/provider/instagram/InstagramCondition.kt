package ru.sokomishalov.memeory.service.provider.instagram

import ru.sokomishalov.memeory.condition.BaseBooleanPropertyCondition


/**
 * @author sokomishalov
 */
class InstagramCondition : BaseBooleanPropertyCondition() {
    override val propertyName: String = "provider.instagram.enabled"
}