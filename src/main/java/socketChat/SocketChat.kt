package socketChat

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
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

class SocketChat : JavaPlugin() {

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
                    Message(
                        data = Message.TextMessageMessageData(
                            event.message,
                            event.player.name
                        )
                    ).toJson()
                )
            }

        }, this)

        try {
            serverThread = Thread {
                val server = Websocket(Websocket.PORT)
                server.start()
                logger.info( "ChatServer started on port: " + server.port)
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


