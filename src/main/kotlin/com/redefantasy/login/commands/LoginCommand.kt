package com.redefantasy.login.commands

import com.redefantasy.core.shared.CoreConstants
import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.commands.argument.Argument
import com.redefantasy.core.shared.commands.restriction.CommandRestriction
import com.redefantasy.core.shared.echo.packets.TitlePacket
import com.redefantasy.core.shared.users.data.User
import com.redefantasy.core.shared.users.passwords.storage.dto.FetchUserPasswordByUserIdDTO
import com.redefantasy.core.spigot.command.CustomCommand
import com.redefantasy.core.spigot.misc.utils.Title
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.abs

/**
 * @author Gutyerrez
 */
class LoginCommand : CustomCommand("logar") {

    override fun getDescription() = "Autenticar sua conta."

    override fun getCommandRestriction() = CommandRestriction.GAME

    override fun getAliases() = arrayOf("login")

    override fun getArguments() = listOf(
        Argument("senha")
    )

    override fun canBeExecuteWithoutLogin() = true

    override fun onCommand(
        commandSender: CommandSender,
        user: User?,
        args: Array<out String>
    ): Boolean {
        commandSender as Player

        if (user === null) {
            commandSender.sendMessage(TextComponent("§cVocê não está registrado."))
            return false
        }

        if (user.isLogged()) {
            return false
        }

        val currentPassword = CoreProvider.Repositories.Postgres.USERS_PASSWORDS_REPOSITORY.provide().fetchByUserId(
            FetchUserPasswordByUserIdDTO(user.getUniqueId())
        ).stream()
            .filter { it.enabled }
            .findFirst()
            .orElse(null)

        if (currentPassword === null) {
            commandSender.sendMessage(TextComponent("§cVocê não está registrado."))
            return false
        }

        val successfully = user.attemptLogin(args[0])

        if (!successfully && user.loginAttempts.get() >= CoreConstants.MAX_LOGIN_ATTEMPTS) {
            commandSender.kick(
                ComponentBuilder("§c§lREDE FANTASY")
                    .append("\n\n")
                    .append("§cVocê excedeu o número limite de ${CoreConstants.MAX_LOGIN_ATTEMPTS} tentativas de login, reconecte e tente novamente.")
                    .create()
            )

            user.loginAttempts.set(0)
            return false
        } else if (!successfully) {
            commandSender.sendMessage(TextComponent("§cSenha incorreta! Você tem mais ${abs(user.loginAttempts.get() - CoreConstants.MAX_LOGIN_ATTEMPTS)} ${if (abs(user.loginAttempts.get() - CoreConstants.MAX_LOGIN_ATTEMPTS) > 1) "tentativas" else "tentativa"}."))
            return false
        }

        user.setLogged(successfully)

        val title = Title(
            "§a§lAutenticado!",
            "§fRedirecionando...",
            0,
            0,
            60
        )

        commandSender.playSound(
            commandSender.location,
            Sound.LEVEL_UP,
            1.0F,
            1.0F
        )
        title.sendToPlayer(commandSender)
        return true
    }

}