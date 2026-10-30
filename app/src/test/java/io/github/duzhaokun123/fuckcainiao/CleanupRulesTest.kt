package io.github.duzhaokun123.fuckcainiao

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CleanupRulesTest {
    @Test
    fun groupCleanupRemovesPricePromotionAndPreservesParcelSummary() {
        val data = linkedMapOf<String, Any>("title" to "近日签收", "subtitle" to "4件",
            "tagItem" to mapOf("data" to mapOf("text" to "寄件1.6元起")))
        val row = mapOf("data" to data, "decorator" to mapOf("groupId" to "signed"))
        assertTrue(CleanupRules.cleanPackageGroup(row))
        assertFalse(data.containsKey("tagItem"))
        assertEquals("近日签收", data["title"])
        assertEquals("4件", data["subtitle"])
        assertFalse(CleanupRules.cleanPackageGroup(row))
    }

    @Test
    fun groupCleanupPreservesFunctionalLabelsAndAcceptsMissingData() {
        val data = linkedMapOf<String, Any>("tagItem" to mapOf("text" to "寄件订单"))
        assertFalse(CleanupRules.cleanPackageGroup(mapOf("data" to data)))
        assertTrue(data.containsKey("tagItem"))
        assertFalse(CleanupRules.cleanPackageGroup(emptyMap<String, Any>()))
        assertFalse(CleanupRules.isShippingPromotion("共有3件"))
    }
    @Test
    fun aboutRoutingAcceptsReferrerWithoutCapturingOtherLinks() {
        assertTrue(CleanupRules.isAboutUrl(CleanupRules.ABOUT_URL + "?referrer=guoguo%3A%2F%2Fgo"))
        assertTrue(CleanupRules.isAboutUrl(CleanupRules.ABOUT_URL + "#about"))
        assertFalse(CleanupRules.isAboutUrl(CleanupRules.ABOUT_URL + "_other"))
        assertFalse(CleanupRules.isAboutUrl(null))
    }
    @Test
    fun navigationKeepsHomeAndPersonalRegardlessOfOrder() {
        val keys = listOf("personal_center", "im", "homepage", "jijian", "station")
        assertEquals(listOf("personal_center", "homepage"), keys.filter(CleanupRules::keepNavigation))
        assertTrue(CleanupRules.keepNavigation("personal_center_h5"))
    }

    @Test
    fun headerFiltersReorderedPromotionsWithoutRemovingSearchAndTools() {
        val search = mapOf("type" to "native_search_bar")
        val tools = mapOf("type" to "kingkong", "bizData" to mapOf("items" to arrayListOf(
            mapOf("key" to "send"), mapOf("key" to "exchange_old_things"), mapOf("key" to "pickup")
        )))
        val rows = arrayListOf<Any?>(mapOf("type" to "promotion_v2"), search,
            mapOf("type" to "vip3_horizontal_bar"), tools, mapOf("type" to "todo_list_v2"),
            mapOf("type" to "icons"))
        CleanupRules.cleanHeader(rows)
        assertEquals(listOf(search, tools), rows)
        val items = ((tools["bizData"] as Map<*, *>)["items"] as List<*>)
        assertEquals(listOf("send", "pickup"), items.map { (it as Map<*, *>)["key"] })
    }

    @Test
    fun headerAcceptsEmptyAndUnknownDataAndRepeatedRefresh() {
        CleanupRules.cleanHeader(arrayListOf<Any?>())
        val rows = arrayListOf<Any?>(null, mapOf("type" to "unknown_functional_card"))
        CleanupRules.cleanHeader(rows)
        CleanupRules.cleanHeader(rows)
        assertEquals(2, rows.size)
    }

    @Test
    fun personalPromotionUsesFieldsInsteadOfMatchingArbitraryText() {
        assertTrue(CleanupRules.isPersonalPromotion(mapOf("group_id" to "entertainment")))
        assertTrue(CleanupRules.isPersonalPromotion(mapOf("materialContentMapper" to
            mapOf("group_type" to "bottom_place_holder"))))
        assertFalse(CleanupRules.isPersonalPromotion(mapOf("title" to "entertainment")))
        assertFalse(CleanupRules.isPersonalPromotion(null))
    }

    @Test
    fun settingsLookupAcceptsMissingRowsAndDetectsExistingAbout() {
        assertEquals(null, CleanupRules.templateName(null))
        assertEquals("guoguo_new_my_settings_item", CleanupRules.templateName(mapOf(
            "template" to mapOf("name" to "guoguo_new_my_settings_item"))))
        assertTrue(CleanupRules.isAbout(mapOf("materialContentMapper" to
            mapOf("jumpUrl" to CleanupRules.ABOUT_URL))))
        assertFalse(CleanupRules.isAbout(emptyMap<String, Any>()))
    }
}
