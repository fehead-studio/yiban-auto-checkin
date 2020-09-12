package ink.verge.yiban_auto_checkin.controller;

import ink.verge.yiban_auto_checkin.run.RunCheckin;
import ink.verge.yiban_auto_checkin.run.RunCheckinWithCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CheckinController {
    private final RunCheckin runCheckin;
    private final RunCheckinWithCookie runCheckinWithCookie;

    @Autowired
    public CheckinController(RunCheckin runCheckin, RunCheckinWithCookie runCheckinWithCookie) {
        this.runCheckin = runCheckin;
        this.runCheckinWithCookie = runCheckinWithCookie;
    }

    @RequestMapping(path = "morCheckin")
    @ResponseBody
    public String morCheckin(){
        runCheckin.morCheck();
        return "正在签到中";
    }
    @RequestMapping(path = "noonCheckin")
    @ResponseBody
    public String noonCheckin(){
        runCheckin.noonCheck();
        return "正在签到中";
    }
    @RequestMapping(path = "check")
    @ResponseBody
    public String check(){
        runCheckinWithCookie.runMorCheckin();
        return "签到中";
    }


}
