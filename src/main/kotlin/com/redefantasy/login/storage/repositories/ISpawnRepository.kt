package com.redefantasy.login.storage.repositories

import com.redefantasy.core.shared.storage.repositories.IRepository
import com.redefantasy.core.shared.world.location.SerializedLocation

/**
 * @author Gutyerrez
 */
interface ISpawnRepository : IRepository {

    fun create(
        serializedLocation: SerializedLocation
    )

    fun fetch(): SerializedLocation?

    fun delete(
        serializedLocation: SerializedLocation
    )

}