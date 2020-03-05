package moe.z0ne.mc.forge1installer

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import com.google.common.base.Predicate
import net.minecraftforge.installer.InstallerAction
import net.minecraftforge.installer.ServerInstall
import net.minecraftforge.installer.VersionInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File


fun newPattern(lc: LoggerContext, colored: Boolean = false): PatternLayoutEncoder {
    val pattern = PatternLayoutEncoder()
    pattern.context = lc
    pattern.pattern = if (colored) {
        "%highlight(%.-1p) %gray([%d{HH:mm:ss.SSS}]) %cyan(\\(%t\\)) %magenta(%c{20}): %m%n"
    } else {
        "%.-1p [%d{HH:mm:ss.SSS}] \\(%t\\) %c{20}: %m%n"
    }
    pattern.start()

    return pattern
}

fun configureLogger(): Logger {
    val lc = LoggerFactory.getILoggerFactory() as LoggerContext

    val consoleAppender = ConsoleAppender<ILoggingEvent>()
    consoleAppender.name = "console"
    consoleAppender.target = "System.out"
    consoleAppender.encoder = newPattern(lc, true)
    consoleAppender.context = lc
    consoleAppender.start()
    val log = lc.getLogger("root")
    log.isAdditive = false
    log.detachAndStopAllAppenders()
    log.addAppender(consoleAppender)
    log.level = Level.TRACE

    return log
}

fun main(args: Array<String>) {
    val logger = configureLogger()
    logger.info("Starting forge 1 installer")
    val target = File(args[0])

    logger.debug("fetching optionals")
    val optionals: List<OptionalListEntry>? = if (VersionInfo.hasOptionals()) {
        VersionInfo.getOptionals().map { lib -> OptionalListEntry(lib) }
    } else {
        null
    }

    val optPred: Predicate<String> = object : Predicate<String> {
        override fun apply(input: String?): Boolean {
            if (optionals == null) return true
            return optionals.find { it.lib.artifact == input }?.isEnabled ?: false
        }
    }

    // disable download progress bar
    ServerInstall.headless = true
    InstallerAction.CLIENT.run(target, optPred)
}
