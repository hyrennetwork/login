package com.redefantasy.login.commands

import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.commands.restriction.CommandRestriction
import com.redefantasy.core.shared.commands.restriction.entities.implementations.GroupCommandRestrictable
import com.redefantasy.core.shared.groups.Group
import com.redefantasy.core.shared.users.data.User
import com.redefantasy.core.shared.world.location.SerializedLocation
import com.redefantasy.core.spigot.command.CustomCommand
import com.redefantasy.login.LoginProvider
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author Gutyerrez
 */
class SetSpawnCommand : CustomCommand("setspawn"), GroupCommandRestrictable {

    override fun getDescription() = "Definir a localização de spawn."

    override fun getCommandRestriction() = CommandRestriction.GAME

    override fun onCommand(
        commandSender: CommandSender,
        user: User?,
        args: Array<out String>
    ): Boolean? {
        commandSender as Player

        LoginProvider.Repositories.Mongo.SPAWN_REPOSITORY.provide().create(
            SerializedLocation(
                CoreProvider.application.name,
                commandSender.location.world.name,
                commandSender.location.x,
                commandSender.location.y,
                commandSender.location.z,
                commandSender.location.yaw,
                commandSender.location.pitch
            )
        )
        commandSender.sendMessage(TextComponent("§aPronto!"))
        return false
    }

    override fun getGroup() = Group.MASTER

}