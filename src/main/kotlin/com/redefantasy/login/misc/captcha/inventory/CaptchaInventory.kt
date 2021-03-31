package com.redefantasy.login.misc.captcha.inventory

import com.redefantasy.core.spigot.inventory.CustomInventory
import com.redefantasy.core.spigot.misc.utils.ItemBuilder
import com.redefantasy.login.LoginPlugin
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import java.util.function.Consumer

/**
 * @author Gutyerrez
 */
class CaptchaInventory : CustomInventory(
    "Validação",
    3 * 9
) {

    private val ITEMS_SLOTS = arrayOf(
        10, 11, 12, 13, 14, 15, 16
    )

    private val FILL_ITEM = ItemBuilder(Material.SKULL_ITEM)
        .build()

    init {
        ITEMS_SLOTS.forEach { slot ->
            this.setItem(
                slot,
                FILL_ITEM,
                Consumer {
                    val player = it.whoClicked as Player

                    player.kick(
                        TextComponent("§cValidação informada incorreta!")
                    )
                }
            )
        }
    }

    override fun on(
        event: InventoryCloseEvent
    ) {
        val player = event.player

        Bukkit.getScheduler().runTaskLater(
            LoginPlugin.instance,
            {
                player.openInventory(this@CaptchaInventory)
            },
            1L
        )
    }

}