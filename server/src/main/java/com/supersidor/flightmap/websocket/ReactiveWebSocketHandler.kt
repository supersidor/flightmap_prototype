package com.supersidor.flightmap.websocket

import lombok.extern.slf4j.Slf4j
import com.supersidor.flightmap.service.PositionReceiveService
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.lang.Void
import com.supersidor.flightmap.model.PlanePosition
import reactor.kafka.receiver.ReceiverRecord
import com.supersidor.flightmap.websocket.ReactiveWebSocketHandler
import com.fasterxml.jackson.core.JsonProcessingException
import java.lang.RuntimeException
import reactor.core.publisher.SignalType
import org.springframework.web.reactive.socket.WebSocketMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.supersidor.flightmap.avro.schemas.Position
import com.supersidor.flightmap.model.LatLng
import com.supersidor.flightmap.model.MapBounds
import com.supersidor.flightmap.model.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component("ReactiveWebSocketHandler")
class ReactiveWebSocketHandler(private val receiveService: PositionReceiveService) :
    WebSocketHandler {
     private var bounds:MapBounds = MapBounds(LatLng(),LatLng());

    companion object {
        val log: Logger = LoggerFactory.getLogger(ReactiveWebSocketHandler.javaClass)
        val json = ObjectMapper()
    }
    private fun inside(pos:LatLng):Boolean{
        val latMatch  = (pos.lat>=bounds.northWest.lat && pos.lat<=bounds.southEast.lat) ||
                (pos.lat>=bounds.southEast.lat && pos.lat<=bounds.northWest.lat);
        val longMatch  = (pos.lng>=bounds.northWest.lng && pos.lng<=bounds.southEast.lng) ||
                (pos.lng>=bounds.southEast.lng && pos.lng<=bounds.northWest.lng);
        return latMatch && longMatch;
    }
    override fun handle(webSocketSession: WebSocketSession): Mono<Void> {
        return webSocketSession.send(
            receiveService.receive()
                .filter{
                    val position = it.value();
                    inside(LatLng(position.getLatitude(),position.getLongitude()))
                }
                .map {
                    val position = it.value()
                    Message("pos",PlanePosition(
                        it.key()!!,
                        position.getHeading().toInt(),
                        position.getLongitude(),
                        position.getLatitude()
                    ))
                }
                .map {
                    try {
                        json.writeValueAsString(it)
                    } catch (e: JsonProcessingException) {
                        //TODO incorrect
                        throw RuntimeException(e)
                    }
                }.doFinally { log.info("do on finally {}", it) }
                .map { webSocketSession.textMessage(it) })
            .and(
                webSocketSession.receive()
                    .map { it.payloadAsText }
                    .map { json.readTree(it)}
                    .doOnNext{
                        val nodeType = it.get("type");
                        if (nodeType.textValue()=="bounds"){
                            this.bounds = json.treeToValue(it.get("body"),MapBounds::class.java);
                        }
                        log.debug("{}",nodeType);
                    }

            )
    }

}
//json.readValue( it, ObjectNode);
