package com.supersidor.booratino;

import com.supersidor.booratino.model.Position;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class SimController {

    @RequestMapping(value = "/sim",method = RequestMethod.POST)
    public void post(@RequestBody Position pos){
        log.info("Got position {}",pos);
    }
}
