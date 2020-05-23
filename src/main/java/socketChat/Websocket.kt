package socketChat

import com.google.gson.Gson
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.io.IOException
import java.net.InetSocketAddress


class Websocket(port: Int) : WebSocketServer(InetSocketAddress(port)) {

    companion object {
        const val PORT = 25564
    }

    init {
        Singleton.messageFromMinecraftListener = object : MessageListener {
            override fun onMessage(message: String) {
                Singleton.i("try send to socket")
                try {
                    broadcast(message)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        try {
            val data = Gson().fromJson(message ?: "", TextMessage::class.java)
            if (data.type != Message.TYPE_TEXT) return
            Singleton.i("received message from socket")
            Singleton.messageFromSocketListener?.onMessage("[Telegram] ${data.data.author}:  ${data.data.text}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        conn?.send("Welcome to the server!")
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {

    }

    override fun onStart() {
        connectionLostTimeout = 0
        connectionLostTimeout = 100
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        ex?.printStackTrace()
        Singleton.i("еблысь")
    }
}
