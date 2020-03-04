package mc.z0ne.moe.forge1installer

import net.minecraftforge.installer.OptionalLibrary


internal class OptionalListEntry internal constructor(var lib: OptionalLibrary) {
    var isEnabled = false

    init {
        isEnabled = lib.default
    }
}