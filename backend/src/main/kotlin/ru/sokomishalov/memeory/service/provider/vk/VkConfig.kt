package ru.sokomishalov.memeory.service.provider.vk

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.ServiceActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration


/**
 * @author sokomishalov
 */
@Configuration
class VkConfig(
        private val props: VkConfigurationProperties
) {
    @Bean
    @Conditional(VkCondition::class)
    fun fixedGsonForVk(): Gson = GsonBuilder()
            .addDeserializationExclusionStrategy(VkBadFieldsExclusions())
            .create()

    @Bean
    @Conditional(VkCondition::class)
    fun apiClient(): VkApiClient = VkApiClient(HttpTransportClient(), fixedGsonForVk(), 3)

    @Bean
    @Conditional(VkCondition::class)
    fun serviceActor(vkApiClient: VkApiClient): ServiceActor = ServiceActor(props.appId, props.accessToken)
}