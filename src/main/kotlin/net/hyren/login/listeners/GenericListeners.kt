package net.hyren.login.listeners

import net.hyren.login.misc.captcha.inventory.CaptchaInventory
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

/**
 * @author Gutyerrez
 */
class GenericListeners : Listener {


    @EventHandler
    fun on(
        event: PlayerJoinEvent
    ) {
        val player = event.player

        Bukkit.getOnlinePlayers().forEach {
            player.hidePlayer(it); it.hidePlayer(player)
        }

        player.maxHealth = 2.0

        val scoreboard = player.scoreboard

        val team = scoreboard.getTeam("zzz") ?: scoreboard.registerNewTeam("zzz")

        team.prefix = "§7"
        team.addEntry(player.displayName)

        Thread {
            Thread.sleep(100)

            player.openInventory(CaptchaInventory())
        }.start()
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
        event: BlockBreakEvent
    ) {
        event.isCancelled = true
    }

    @EventHandler
    fun on(
        event: BlockPlaceEvent
    ) {
        event.isCancelled = true
    }

    @EventHandler
    fun on(
        event: PlayerInteractEvent
    ) {
        event.isCancelled = true
    }

    @EventHandler
    fun on(
        event: InventoryClickEvent
    ) {
        event.isCancelled = true
    }

    @EventHandler
    fun on(
        event: BlockPhysicsEvent
    ) {
        event.isCancelled = true
    }

    @EventHandler
    fun on(
        event: PlayerInteractEntityEvent
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