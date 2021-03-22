package com.redefantasy.login.listeners

import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.groups.Group
import com.redefantasy.core.spigot.misc.utils.Title
import com.redefantasy.login.LoginPlugin
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityCombustEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.scheduler.BukkitTask
import java.util.*

/**
 * @author Gutyerrez
 */
class GeneralListeners : Listener {

    private val SECONDS = 30L

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
            20 * SECONDS.toInt()
        )

        LOGIN_IN[player.uniqueId] = Bukkit.getScheduler().runTaskLater(
            LoginPlugin.instance,
            {
                player.kick(
                    ComponentBuilder()
                        .append("§c§lREDE FANTASY")
                        .append("\n\n")
                        .append("§cVocê excedeu o tempo limite para efetuar o login, tente novamente.")
                        .create()
                )
            },
            20 * SECONDS
        )

        title.sendToPlayer(player)

        Bukkit.getOnlinePlayers().forEach {
            player.hidePlayer(it); it.hidePlayer(player)
        }

        player.maxHealth = 2.0

        player.sendMessage(
            ComponentBuilder()
                .append("\n")
                .append("§e§l AVISO: §r§eNão utilize sua senha em outros servidores.")
                .append("\n")
                .create()
        )

        val scoreboard = player.scoreboard

        val team = scoreboard.getTeam("zzz") ?: scoreboard.registerNewTeam("zzz")

        team.prefix = "§7"
        team.addEntry(player.displayName)
    }

    @EventHandler
    fun on(
        event: BlockBreakEvent
    ) {
        val player = event.player
        val user = CoreProvider.Cache.Local.USERS.provide().fetchById(player.uniqueId)

        if ((user === null || !user.isLogged()) && user!!.hasGroup(Group.MANAGER)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun on(
        event: BlockPlaceEvent
    ) {
        val player = event.player
        val user = CoreProvider.Cache.Local.USERS.provide().fetchById(player.uniqueId)

        if ((user === null || !user.isLogged()) && user!!.hasGroup(Group.MANAGER)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun on(
        event: PlayerInteractEvent
    ) {
        val player = event.player
        val user = CoreProvider.Cache.Local.USERS.provide().fetchById(player.uniqueId)

        if (user === null) {
            event.isCancelled = true

            return
        }

        if (!user.isLogged() && user.hasGroup(Group.MANAGER)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun on(
        event: InventoryClickEvent
    ) {
        if (event.whoClicked !is Player) return

        val player = event.whoClicked as Player
        val user = CoreProvider.Cache.Local.USERS.provide().fetchById(player.uniqueId)

        if ((user === null || !user.isLogged()) && user!!.hasGroup(Group.MANAGER)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun on(
        event: EntityDamageEvent
    ) {
        if (event.entity !is Player) return

        event.isCancelled = true

        if (event.cause === EntityDamageEvent.DamageCause.VOID && event.entity is Player) {
            val world = Bukkit.getWorld("world")
            val player = event.entity

            player.teleport(
                Location(
                    world,
                    0.5,
                    78.0,
                    -0.5,
                    180F,
                    0F
                )
            )
        }
    }

    @EventHandler
    fun on(
        event: EntityChangeBlockEvent
    ) {
        val entity = event.entity
        val block = event.block

        if (entity.type == EntityType.FALLING_BLOCK) {
            val blockState = block.state

            blockState.update()

            entity.remove()

            blockState.update()

            event.isCancelled = true

            blockState.update()
        }
    }

    @EventHandler
    fun on(
        event: PlayerInitialSpawnEvent
    ) {
        val world = Bukkit.getWorld("world")

        event.spawnLocation = Location(
            world,
            0.5,
            78.0,
            -0.5,
            180F,
            0F
        )
    }

    @EventHandler
    fun on(
        event: BlockPhysicsEvent
    ) {
        event.isCancelled = true
    }

    @EventHandler
    fun on(
        event: PlayerInteractAtEntityEvent
    ) {
        event.isCancelled = true
    }

    @EventHandler
    fun on(
        event: EntityCombustEvent
    ) {
        event.isCancelled = true
    }

    @EventHandler
    fun on(
        event: BlockIgniteEvent
    ) {
        event.isCancelled = true
    }

    @EventHandler
    fun on(
        event: EntityDamageByEntityEvent
    ) {
        event.isCancelled = true
    }

    @EventHandler
    fun on(
        event: BlockFromToEvent
    ) {
        event.isCancelled = true
    }

    @EventHandler
    fun on(
        event: BlockFadeEvent
    ) {
        event.isCancelled = true
    }

    @EventHandler
    fun on(
        event: AsyncPlayerChatEvent
    ) {
        event.isCancelled = true
    }

    @EventHandler
    fun on(
        event: WeatherChangeEvent
    ) {
        if (event.toWeatherState())
            event.isCancelled = true
    }

}