package com.supersidor.flightmap;

import com.supersidor.flightmap.CurrentPositionService;
import com.supersidor.flightmap.avro.Position;
import com.supersidor.flightmap.websocket.WebSocketPosition;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PeriodicSendPositionTask {

    private static final String WS_MESSAGE_TRANSFER_DESTINATION = "/topic/position";
    private CurrentPositionService currentPositionService;
    private SimpMessagingTemplate simpMessagingTemplate;

    public PeriodicSendPositionTask(CurrentPositionService currentPositionService, SimpMessagingTemplate simpMessagingTemplate){
        this.currentPositionService = currentPositionService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Scheduled(fixedRate=1000)
    public void work() {
        Optional<Position> position = currentPositionService.getPosition();
        position.ifPresent( (pos)->{
            simpMessagingTemplate.convertAndSend(
                    WS_MESSAGE_TRANSFER_DESTINATION,
                    new WebSocketPosition(
                            pos.getAltitude(),
                            pos.getLatitude(),
                            pos.getLongitude(),
                            pos.getHeading()));
        });
        //simpMessagingTemplate.convertAndSend(WS_MESSAGE_TRANSFER_DESTINATION,
        //        new Greeting("Hello, all!"));
    }
}
