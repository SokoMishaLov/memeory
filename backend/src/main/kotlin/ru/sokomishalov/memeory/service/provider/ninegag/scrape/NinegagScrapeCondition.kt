package ru.sokomishalov.memeory.service.provider.ninegag.scrape

import ru.sokomishalov.memeory.condition.BaseBooleanPropertyCondition


/**
 * @author sokomishalov
 */
class NinegagScrapeCondition : BaseBooleanPropertyCondition() {
    override val propertyName: String = "provider.ninegag.scrape-enabled"
}