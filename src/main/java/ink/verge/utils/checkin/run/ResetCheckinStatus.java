package ink.verge.utils.checkin.run;

import ink.verge.utils.checkin.entity.User;
import ink.verge.utils.checkin.service.UserService;
import ink.verge.utils.checkin.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class ResetCheckinStatus {
    @Resource
    private UserService userService;

    /**
     * 重置签到状态为false
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void resetCheckinStatusToFalse(){
        log.info("开始重置签到状态");
        List<User> userList = userService.list();
        for (User user : userList) {
            userService.setCheckinStatus(user.getUid(),false,1);
            userService.setCheckinStatus(user.getUid(),false,2);
        }
        log.info("开始重置完成");
    }
}
