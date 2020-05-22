package socketChat

import com.google.gson.Gson

class Message (
    var type: String = TYPE_TEXT,
    var data: TextMessageMessageData
) {

    companion object {
        const val TYPE_TEXT = "message"
    }

    class TextMessageMessageData (
        val text: String,
        val author: String
    )

    fun toJson(): String = Gson().toJson(this)
}