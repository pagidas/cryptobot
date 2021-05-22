package me.pysquad.cryptobot.subscriber

import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container

abstract class TestContainersBase {
    companion object {
        private const val containerRethinkDbPort = 28015

        @Container
        @JvmStatic
        val containerRethinkDb = GenericContainer<Nothing>("rethinkdb:2.4.0").apply {
            withExposedPorts(containerRethinkDbPort)
        }

        val exposedContainerRethinkDbPort: Int by lazy { containerRethinkDb.getMappedPort(containerRethinkDbPort) }
    }
}
