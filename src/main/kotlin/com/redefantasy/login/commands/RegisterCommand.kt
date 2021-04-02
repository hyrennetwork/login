package com.redefantasy.login.commands

import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.commands.argument.Argument
import com.redefantasy.core.shared.commands.restriction.CommandRestriction
import com.redefantasy.core.shared.misc.utils.EncryptionUtil
import com.redefantasy.core.shared.users.data.User
import com.redefantasy.core.shared.users.passwords.storage.dto.CreateUserPasswordDTO
import com.redefantasy.core.shared.users.passwords.storage.dto.FetchUserPasswordByUserIdDTO
import com.redefantasy.core.shared.users.storage.dto.CreateUserDTO
import com.redefantasy.core.spigot.command.CustomCommand
import com.redefantasy.login.misc.services.LoginService
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author Gutyerrez
 */
class RegisterCommand : CustomCommand("registrar") {

    override fun getDescription() = "Registrar uma conta."

    override fun getCommandRestriction() = CommandRestriction.GAME

    override fun getAliases() = listOf("register")

    override fun getArguments(): List<Argument> = listOf(
        Argument("senha"),
        Argument("confirme a senha")
    )

    override fun canBeExecuteWithoutLogin() = true

    override fun onCommand(
        commandSender: CommandSender,
        user: User?,
        args: Array<out String>
    ): Boolean {
        commandSender as Player

        if (!LoginService.hasStarted(commandSender.uniqueId)) return false

        if (user !== null && CoreProvider.Repositories.Postgres.USERS_PASSWORDS_REPOSITORY.provide().fetchByUserId(
                FetchUserPasswordByUserIdDTO(user.getUniqueId())
            ).isNotEmpty()
        ) {
            commandSender.sendMessage(TextComponent("§cVocê já está registrado. Utilize /logar <senha>."))
            return false
        }

        val address = commandSender.address.address.hostAddress
        val users = CoreProvider.Cache.Local.USERS.provide().fetchByAddress(address)

        if (users !== null && users.size > 1) {
            commandSender.sendMessage(TextComponent("§cVocê já atingiu o limite de cadastros."))
            return false
        }

        var _user: User? = user

        if (_user === null) {
            _user = CoreProvider.Repositories.Postgres.USERS_REPOSITORY.provide().create(
                CreateUserDTO(
                    commandSender.uniqueId,
                    commandSender.name,
                    commandSender.address.address.hostAddress
                )
            )
        }

        if (!args[0].contentEquals(args[1])) {
            commandSender.sendMessage(TextComponent("§cAs senhas não coincidem. (${args[0]}|${args[1]})"))
            return false
        }

        val currentPasswords = CoreProvider.Repositories.Postgres.USERS_PASSWORDS_REPOSITORY.provide().fetchByUserId(
            FetchUserPasswordByUserIdDTO(_user.getUniqueId())
        )

        if (currentPasswords.isNotEmpty() && currentPasswords.stream().anyMatch {
            it.password === EncryptionUtil.hash(EncryptionUtil.Type.SHA256, args[0])
        }) {
            commandSender.sendMessage(TextComponent("§cVocê já usou essa senha anteriormente."))
            return false
        }

        CoreProvider.Repositories.Postgres.USERS_PASSWORDS_REPOSITORY.provide().create(
            CreateUserPasswordDTO(
                _user.getUniqueId(),
                EncryptionUtil.hash(EncryptionUtil.Type.SHA256, args[0])
            )
        )

        LoginService.authenticate(_user)
        return false
    }

}
