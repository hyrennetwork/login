package com.redefantasy.login.listeners

import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.groups.Group
import com.redefantasy.core.spigot.CoreSpigotConstants
import com.redefantasy.core.spigot.CoreSpigotProvider
import com.redefantasy.core.spigot.misc.utils.Title
import com.redefantasy.login.LoginPlugin
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
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
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
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

        val spawnSerializedLocation = CoreSpigotProvider.Repositories.Postgres.SPAWN_REPOSITORY.provide().fetch()

        println(spawnSerializedLocation)

        if (spawnSerializedLocation !== null) player.teleport(
            CoreSpigotConstants.BUKKIT_LOCATION_PARSER.apply(spawnSerializedLocation)
        )
    }

    @EventHandler
    fun on(
        event: PlayerMoveEvent
    ) {
        val player = event.player
        val fromLocation = event.from
        val toLocation = event.to
        val user = CoreProvider.Cache.Local.USERS.provide().fetchById(player.uniqueId)

        if (user !== null && user.isLogged() && user.hasGroup(Group.MANAGER)) return

        if (toLocation.x != fromLocation.x || toLocation.y != fromLocation.y || toLocation.z != fromLocation.z) {
            player.teleport(fromLocation)
        }
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

        if ((user === null || !user.isLogged()) && user!!.hasGroup(Group.MANAGER)) {
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
            val player = event.entity

            val spawnSerializedLocation = CoreSpigotProvider.Repositories.Postgres.SPAWN_REPOSITORY.provide().fetch()

            if (spawnSerializedLocation !== null) player.teleport(
                CoreSpigotConstants.BUKKIT_LOCATION_PARSER.apply(spawnSerializedLocation)
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

}