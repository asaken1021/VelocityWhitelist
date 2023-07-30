package net.asaken1021.velocitywhitelist.util.dependency

import com.velocitypowered.api.proxy.ProxyServer
import net.asaken1021.velocitywhitelist.VelocityWhitelist
import org.slf4j.Logger
import java.io.*
import java.net.URLConnection
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.*

class VWDependency(
    private val plugin: VelocityWhitelist,
    private val server: ProxyServer,
    private val logger: Logger,
    dataDirectory: Path
) {
    private val libraryDirectory: Path = Path.of(dataDirectory.absolutePathString() + File.separatorChar + "libs")
    private val dependencies: VWDependencies = VWDependencies()

    init {
        prepareDirectory(dataDirectory)
        prepareDirectory(libraryDirectory)
    }

    @Throws(DirectoryNotFoundException::class)
    private fun prepareDirectory(path: Path) {
        if (path.notExists()) {
            try {
                path.createDirectory()
            } catch (e: Exception) {
                throw DirectoryNotFoundException("Trying to create directory ${path.name}, Caused: $e")
            }
        }
    }

    fun getDependencies() {
        dependencies.getDependencies().forEach { dependency ->
            val jarFileName: String = dependencies.getDependencyJarFileName(dependency)
            val jarFilePath: String = libraryDirectory.absolutePathString() + File.separatorChar + jarFileName

            if (Path.of(jarFilePath).notExists()) {
                try {
                    logger.info("Downloading dependency: $jarFileName")
                    val connection: URLConnection = dependencies.getDependencyURL(dependency).openConnection()
                    val inputStream: InputStream = BufferedInputStream(connection.getInputStream())
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    val fileOutputStream = FileOutputStream(jarFilePath)

                    do {
                        val readData = inputStream.read()
                        byteArrayOutputStream.write(readData)
                    } while (readData != -1)

                    inputStream.close()

                    fileOutputStream.write(byteArrayOutputStream.toByteArray())
                    fileOutputStream.close()
                } catch (e: Exception) {
                    throw GetPluginFailedException("Trying to download plugin: $jarFileName, Caused: $e")
                }
            }
        }
    }

    fun loadDependencies() {
        dependencies.getDependencies().forEach { dependency ->
            val jarFileName: String = dependencies.getDependencyJarFileName(dependency)
            val jarFilePath: String = libraryDirectory.absolutePathString() + File.separatorChar + jarFileName

            try {
                logger.info("Loading dependency: $jarFileName")
                server.pluginManager.addToClasspath(plugin, Paths.get(jarFilePath))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}