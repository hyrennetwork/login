package com.redefantasy.login.misc.captcha.inventory

import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.spigot.inventory.CustomInventory
import com.redefantasy.core.spigot.misc.utils.ItemBuilder
import com.redefantasy.login.LoginPlugin
import com.redefantasy.login.misc.services.LoginService
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import java.util.*
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

    private val MATERIALS = arrayOf(
        Material.CARROT_ITEM,
        Material.PUMPKIN_PIE,
        Material.APPLE,
        Material.RAW_FISH
    )

    private var remainingItems = 2

    init {
        ITEMS_SLOTS.shuffle()

        val filledSlots = mutableListOf<Int>()
        var index = 0

        for (remainingItem in remainingItems downTo 0) {
            val slot = ITEMS_SLOTS[index]
            val material = MATERIALS[index]

            this.setItem(
                slot,
                ItemBuilder(material)
                    .name("§eClique aqui")
                    .build(),
                Consumer {
                    val player = it.whoClicked as Player

                    this@CaptchaInventory.remainingItems--

                    this@CaptchaInventory.setItem(
                        slot,
                        ItemBuilder(Material.BARRIER).build()
                    )

                    if (this@CaptchaInventory.remainingItems < 0) {
                        val user = CoreProvider.Cache.Local.USERS.provide().fetchById(player.uniqueId)

                        player.closeInventory()

                        when (user === null) {
                            true -> LoginService.start(player.uniqueId)
                            false -> LoginService.start(user)
                        }
                    }
                }
            )

            filledSlots.add(slot)

            index++
        }

        Arrays.stream(
            ITEMS_SLOTS
        ).filter {
            !filledSlots.contains(it)
        }.forEach { slot ->
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

        if (this.remainingItems < 0) return

        Bukkit.getScheduler().runTaskLater(
            LoginPlugin.instance,
            {
                player.openInventory(this@CaptchaInventory)
            },
            1L
        )
    }

}