package socketChat

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.server.BroadcastMessageEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

interface MessageListener {
    fun onMessage(message: String)
}

object Singleton {
    var logger: Logger? = null
    var messageFromSocketListener: MessageListener? = null
    var messageFromMinecraftListener: MessageListener? = null

    fun i(message: String) {
        logger?.info(message)
    }
}

class SocketChatPlugin : JavaPlugin() {

    companion object {
        private const val BROADCAST_EVENT_MESSAGE_FILTER = "[WEB]"
        private const val BROADCAST_EVENT_RECIPIENTS_FILTER = "Rcon"
    }

    private var serverThread: Thread? = null

    init {
        Singleton.logger = logger
        Singleton.messageFromSocketListener = object : MessageListener {
            override fun onMessage(message: String) {
                logger.info("try send to minecraft")
                Bukkit.broadcastMessage(message)
            }
        }
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(object : Listener {
            @EventHandler
            fun onAsyncChatEvent(event: AsyncPlayerChatEvent) {
                logger.info("received message from minecraft")
                Singleton.messageFromMinecraftListener?.onMessage(
                    PlayerMessage(
                        data = Message.TextMessageData(
                            event.message,
                            event.player.name
                        )
                    ).toJson()
                )
            }

            @EventHandler
            fun onBroadcastMessageEvent(event: BroadcastMessageEvent) {
                val isRequiredRecipient = event.recipients.find { it.name == BROADCAST_EVENT_RECIPIENTS_FILTER } != null
                val isRequiredMessage = event.message.contains(BROADCAST_EVENT_MESSAGE_FILTER)
                if (isRequiredMessage && isRequiredRecipient) {
                    logger.info("Received broadcast message with $BROADCAST_EVENT_MESSAGE_FILTER from $BROADCAST_EVENT_RECIPIENTS_FILTER")
                    // Без автора, потому, что динмап форматирует текст на своей стороне
                    Singleton.messageFromMinecraftListener?.onMessage(
                        WebmapMessage(
                            data = Message.WebmapMessageData(
                                // §. удаляет информацию о цвете
                                event.message.replace(Regex("§."), "")
                            )
                        ).toJson()
                    )
                }
            }
        }, this)

        try {
            serverThread = Thread {
                val server = Websocket(Websocket.PORT)
                server.start()
                logger.info("ChatServer started on port: " + server.port)
            }
            serverThread?.start()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    override fun onDisable() {
        super.onDisable()
        try {
            if (serverThread?.isAlive == true) serverThread?.stop()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        serverThread = null
    }
}


