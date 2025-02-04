package me.arasple.mc.trmenu.util.bukkit

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import me.arasple.mc.trmenu.module.display.MenuSettings
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.buildItem
import kotlin.math.min

/**
 * @author Arasple
 * @date 2021/2/4 9:56
 */
object ItemHelper {

    fun serializeColor(color: String): Color {
        val rgb = color.split(",").toTypedArray()
        if (rgb.size == 3) {
            val r = min(rgb[0].toIntOrNull() ?: 0, 255)
            val g = min(rgb[1].toIntOrNull() ?: 0, 255)
            val b = min(rgb[2].toIntOrNull() ?: 0, 255)
            return Color.fromRGB(r, g, b)
        }
        return Color.BLACK
    }

    fun deserializeColor(color: Color): String {
        return "${color.red},${color.green},${color.blue}"
    }

    fun defColorize(string: String, isLore: Boolean = false): String {
        return if (string.isNotBlank() && !string.startsWith(ChatColor.COLOR_CHAR) && !string.startsWith('&')) {
            val defColor = if (isLore) MenuSettings.DEFAULT_LORE_COLOR else MenuSettings.DEFAULT_NAME_COLOR
            defColor + string
        } else string
    }

    fun isJson(json: String): Boolean {
        return try {
            JsonParser().parse(json)
            true
        } catch (e: Throwable) {
            false
        }
    }

    fun fromJson(json: String): ItemStack? {
        try {
            val parse = JsonParser().parse(json)
            if (parse is JsonObject) {
                val itemBuild = buildItem(parse["type"].let { it ?: XMaterial.STONE; XMaterial.valueOf(it.asString) }) {
                    parse["data"].let {
                        it ?: return@let
                        damage = it.asInt
                    }
                    parse["amount"].let {
                        it ?: return@let
                        amount = it.asInt
                    }
                }
                val meta = parse["meta"]
                return if (meta != null) itemBuild.also { ItemTag.fromJson(meta.toString()).saveTo(it) }
                else itemBuild
            }
            return null
        } catch (e: Throwable) {
            return null
        }
    }

}