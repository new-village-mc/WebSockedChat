package socketChat

import com.google.gson.Gson
import socketChat.Message.Companion.TYPE_TEXT
import socketChat.Message.Companion.TYPE_WEBMAP

interface Message {

    companion object {
        const val TYPE_TEXT = "message"
        const val TYPE_WEBMAP = "webmap"
    }

    var type: String

    fun toJson(): String = Gson().toJson(this)

    class TextMessageData (
        val text: String,
        val author: String
    )

    class WebmapMessageData (
        val text: String
    )
}

class TextMessage(
    override var type: String = TYPE_TEXT,
    var data: Message.TextMessageData
): Message

class PlayerMessage(
    override var type: String = TYPE_TEXT,
    var data: Message.TextMessageData
): Message

class WebmapMessage(
    override var type: String = TYPE_WEBMAP,
    var data: Message.WebmapMessageData
): Message

