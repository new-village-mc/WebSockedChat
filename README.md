# WebSockedChat

Это просто мост между чатом майнкрафта и чем-либо еще. Работает на WebSocket

```
ws://127.0.0.1:8080
```

формат данных 
```
{
    "type": "message", // пока только текстовые сообщения
    "data": {
        "text": "test text", // текст
        "author": "Evgeniy1357" // отправитель
    }
}
```
## TODO
* Конфигурационный yml файл (задать порт и префикс. Возможно чтото еще кастомизируемое)
* Текст с форматированием
* Научиться перехватывать сообщения из веб-карты
