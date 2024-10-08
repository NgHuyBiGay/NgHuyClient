/*
 * MinusBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/MinusMC/MinusBounce
 */
package net.minusmc.minusbounce.features.module.modules.client

import net.minecraft.util.ResourceLocation
import net.minusmc.minusbounce.features.module.Module
import net.minusmc.minusbounce.features.module.ModuleCategory
import net.minusmc.minusbounce.features.module.ModuleInfo
import net.minusmc.minusbounce.utils.ClassUtils
import net.minusmc.minusbounce.value.ListValue


/*
TASK: online cape add cape from online
 */
@ModuleInfo(name = "Cape", description = "capes.", category = ModuleCategory.CLIENT)
class Cape : Module() {
    private val styleValue = ListValue("Style", arrayOf("MinusBounce"), "MinusBounce")
    private val capeCache = hashMapOf<String, ResourceLocation>()

    override fun onInitialize() {
        ClassUtils.capeFiles.forEach {
            val name = it.split("/").last().replace(".png", "")
            capeCache[name] = ResourceLocation(it)
        }
        if (capeCache.isEmpty()) return
        styleValue.changeListValues(capeCache.keys.toTypedArray())
    }

    val cape: ResourceLocation?
        get() = capeCache[styleValue.get()] ?: capeCache["MinusBounce"]

    override val tag: String
        get() = styleValue.get()

}
