package com.redefantasy.login

import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.providers.databases.mongo.providers.MongoRepositoryProvider
import com.redefantasy.login.storage.repositories.ISpawnRepository
import com.redefantasy.login.storage.repositories.implementations.MongoSpawnRepository

/**
 * @author Gutyerrez
 */
object LoginProvider {

    fun prepare() {
        Repositories.Mongo.SPAWN_REPOSITORY.prepare()
    }

    object Repositories {

        object Mongo {

            val SPAWN_REPOSITORY = MongoRepositoryProvider<ISpawnRepository>(
                CoreProvider.Databases.Mongo.MONGO_MAIN,
                MongoSpawnRepository::class
            )

        }

    }

}