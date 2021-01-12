package ink.verge.utils.checkin.run;

import ink.verge.utils.checkin.entity.User;
import ink.verge.utils.checkin.service.impl.UserServiceImpl;

import ink.verge.utils.checkin.utils.YibanUtils;
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
    @Scheduled(cron = "0 0 6-8 * * *")
    public void morCheck() throws InterruptedException {
        log.info("开始执行晨间签到");

        List<User> morUndoneUser = userService.getMornUncheckUser();
        for (User user : morUndoneUser) {
            if (yibanUtils.checkin(user,1)) userService.setCheckinStatus(user.getUid(),true,1);
            Thread.sleep(1000);
        }

    }

    /**
     * 午间签到
     */
    @Scheduled(cron = "0 0 12-14 * * *")
    public void noonCheck() throws InterruptedException{
        log.info("开始执行午间签到");

        List<User> noonUndoneUserList = userService.getNoonUncheckUser();
        for (User user : noonUndoneUserList) {
            if (yibanUtils.checkin(user,2)) userService.setCheckinStatus(user.getUid(),true,2);
            Thread.sleep(1000);
        }
    }


}
