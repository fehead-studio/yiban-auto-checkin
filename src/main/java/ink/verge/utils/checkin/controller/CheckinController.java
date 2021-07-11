package ink.verge.utils.checkin.controller;

import ink.verge.utils.checkin.run.RunCheckin;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Verge
 * @Date 2021/7/11 17:18
 * @Version 1.0
 */
@RestController
@RequestMapping("/checkin")
public class CheckinController {
    @Autowired
    private RunCheckin runCheckin;

    @RequestMapping("/1")
    public String mornCheckin(){
        runCheckin.morCheck();
        return "ok";
    }

    @RequestMapping("/2")
    public String noonCheckin(){
        runCheckin.noonCheck();
        return "ok";
    }
}