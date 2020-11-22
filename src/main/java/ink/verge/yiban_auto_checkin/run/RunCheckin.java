package ink.verge.yiban_auto_checkin.run;

import ink.verge.yiban_auto_checkin.mbg.model.User;
import ink.verge.yiban_auto_checkin.service.impl.UserServiceImpl;
import ink.verge.yiban_auto_checkin.utils.YibanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RunCheckin {
    private final UserServiceImpl userService;
    private final YibanUtils yibanUtils;

    @Autowired
    public RunCheckin(UserServiceImpl userService, YibanUtils yibanUtils) {
        this.userService = userService;
        this.yibanUtils = yibanUtils;
    }

    /**
     * 晨间签到
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void morCheck(){
        log.info("开始执行晨间签到");

        List<User> morUndoneUser = userService.getMorUndoneUser();
        for (User user : morUndoneUser) {
            if (yibanUtils.checkin(user,1)) userService.setCheckinStatus(user,1);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 午间签到
     */
    @Scheduled(cron = "0 0 12 * * *")
    public void noonCheck(){
        log.info("开始执行午间签到");

        List<User> noonUndoneUserList = userService.getNoonUndoneUser();
        for (User user : noonUndoneUserList) {
            if (yibanUtils.checkin(user,2)) userService.setCheckinStatus(user,2);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
