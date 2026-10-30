package io.github.duzhaokun123.fuckcainiao

internal object CleanupRules {
    const val ABOUT_URL = "guoguo://go/about_fuck_cainiao"

    fun isAboutUrl(value: String?): Boolean =
        value?.substringBefore('?')?.substringBefore('#') == ABOUT_URL

    fun isShippingPromotion(value: Any?): Boolean = when (value) {
        is String -> value.contains("寄件") &&
            listOf("元", "优惠", "领券", "折").any(value::contains)
        is Map<*, *> -> value.values.any(::isShippingPromotion)
        is List<*> -> value.any(::isShippingPromotion)
        else -> false
    }

    fun cleanPackageGroup(row: Map<*, *>): Boolean {
        val content = row["data"] as? MutableMap<*, *> ?: return false
        if (!isShippingPromotion(content["tagItem"])) return false
        content.remove("tagItem")
        return true
    }

    fun keepNavigation(key: String): Boolean =
        key == "homepage" || key == "personal_center" || key == "personal_center_h5"

    fun isPersonalPromotion(value: Any?): Boolean {
        val row = value as? Map<*, *> ?: return false
        val content = row["materialContentMapper"] as? Map<*, *>
        return row["group_id"] == "entertainment" ||
            row["group_type"] == "bottom_place_holder" ||
            content?.get("group_id") == "entertainment" ||
            content?.get("group_type") == "bottom_place_holder"
    }

    fun templateName(value: Any?): String? =
        ((value as? Map<*, *>)?.get("template") as? Map<*, *>)?.get("name") as? String

    fun isAbout(value: Any?): Boolean =
        ((value as? Map<*, *>)?.get("materialContentMapper") as? Map<*, *>)
            ?.get("jumpUrl") == ABOUT_URL

    fun cleanHeader(rows: MutableList<*>) {
        // Header cards can be reordered by the server. Keep functional cards by type.
        rows.removeAll { value ->
            val type = (value as? Map<*, *>)?.get("type") as? String ?: return@removeAll false
            type.startsWith("promotion") || type.startsWith("vip3_horizontal_bar") ||
                type.startsWith("new_user_award") || type.startsWith("todo_list") || type == "icons"
        }
        rows.forEach { value ->
            val row = value as? Map<*, *> ?: return@forEach
            for (key in listOf("bizData", "data")) {
                val content = row[key] as? Map<*, *> ?: continue
                val items = content["items"] as? MutableList<*> ?: continue
                items.removeAll { (it as? Map<*, *>)?.get("key") == "exchange_old_things" }
            }
        }
    }
}
