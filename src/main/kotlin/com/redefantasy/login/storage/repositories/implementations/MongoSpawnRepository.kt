package com.redefantasy.login.storage.repositories.implementations

import com.mongodb.client.model.Filters
import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.providers.databases.mongo.repositories.MongoRepository
import com.redefantasy.core.shared.world.location.SerializedLocation
import com.redefantasy.login.storage.repositories.ISpawnRepository

/**
 * @author Gutyerrez
 */
class MongoSpawnRepository : MongoRepository<SerializedLocation>(
    CoreProvider.Databases.Mongo.MONGO_MAIN,
    "login_server_spawn",
    SerializedLocation::class
), ISpawnRepository {

    override fun create(
        serializedLocation: SerializedLocation
    ) = this.mongoCollection.insertOne(serializedLocation)

    override fun fetch(): SerializedLocation? = this.mongoCollection.find(
        Filters.eq("application_name", CoreProvider.application.name)
    ).first()

    override fun delete(
        serializedLocation: SerializedLocation
    ) {
        this.mongoCollection.deleteOne(
            Filters.and(
                Filters.eq("application_name", serializedLocation.applicationName),
                Filters.eq("world_name", serializedLocation.worldName),
                Filters.eq("x", serializedLocation.x),
                Filters.eq("y", serializedLocation.y),
                Filters.eq("z", serializedLocation.z),
                Filters.eq("yaw", serializedLocation.yaw),
                Filters.eq("pitch", serializedLocation.pitch),
            )
        )
    }

}