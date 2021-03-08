package com.redefantasy.login.listeners

import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.spigot.misc.utils.Title
import com.redefantasy.login.LoginPlugin
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.scheduler.BukkitTask
import java.util.*

/**
 * @author Gutyerrez
 */
class GeneralListeners : Listener {

    companion object {

        val LOGIN_IN = mutableMapOf<UUID, BukkitTask>()

    }

    @EventHandler
    fun on(
        event: PlayerJoinEvent
    ) {
        val player = event.player
        val user = CoreProvider.Cache.Local.USERS.provide().fetchById(player.uniqueId)
        val title = Title(
            "§6§lREDE FANTASY",
            "§fUtilize ${
                if (user === null) {
                    "/registrar <senha> <senha>"
                } else "/logar <senha>"
            }",
            0,
            0,
            20 * 10
        )

        LOGIN_IN[player.uniqueId] = Bukkit.getScheduler().runTaskLater(
            LoginPlugin.instance,
            {
                player.kick(
                    ComponentBuilder()
                        .append("§c§lREDE FANTASY")
                        .append("\n")
                        .append("§cVocê excedeu o tempo limite para efetuar o login, tente novamente.")
                        .create()
                )
            },
            20 * 10
        )

        title.sendToPlayer(player)

        Bukkit.getOnlinePlayers().forEach {
            player.hidePlayer(it); it.hidePlayer(player)
        }
    }

    @EventHandler
    fun on(
        event: PlayerMoveEvent
    ) {
        val player = event.player
        val fromLocation = event.from
        val toLocation = event.to

        if (toLocation.x != fromLocation.x || toLocation.y != fromLocation.y || toLocation.z != fromLocation.z) {
            player.teleport(fromLocation)
        }
    }

}