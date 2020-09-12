package ink.verge.yiban_auto_checkin.run;

import ink.verge.yiban_auto_checkin.common.CommonResult;
import ink.verge.yiban_auto_checkin.mbg.model.User;
import ink.verge.yiban_auto_checkin.service.CheckinService;
import ink.verge.yiban_auto_checkin.service.UserService;
import ink.verge.yiban_auto_checkin.utils.YibanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RunCheckin {
    private final CheckinService checkinService;
    private final UserService userService;
    private final YibanUtils yibanUtils;

    @Autowired
    public RunCheckin(CheckinService checkinService, UserService userService, YibanUtils yibanUtils) {
        this.checkinService = checkinService;
        this.userService = userService;
        this.yibanUtils = yibanUtils;
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void morCheck(){
        System.out.println("-----------------------");
        System.out.println("开始晨间签到");
        System.out.println("-----------------------\n");
        CommonResult<List<User>> morUndoneUser = userService.getMorUndoneUser();
        if (morUndoneUser.getCode() == 200){
            List<User> list = morUndoneUser.getData();
            System.out.println(list);
            CommonResult<List<User>> commonResult = yibanUtils.checkin(list,1);
            List<User> doneUserList = commonResult.getData();
            for (User user : doneUserList) {
                checkinService.setCheckinStatus(user,1);
            }
        }
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void noonCheck(){
        System.out.println("-----------------------");
        System.out.println("开始午间签到");
        System.out.println("-----------------------");
        CommonResult<List<User>> noonUndoneUser = userService.getNoonUndoneUser();
        if (noonUndoneUser.getCode() == 200){
            List<User> list = noonUndoneUser.getData();
            System.out.println(list);
            CommonResult<List<User>> commonResult = yibanUtils.checkin(list,2);
            List<User> doneUserList = commonResult.getData();
            for (User user : doneUserList) {
                checkinService.setCheckinStatus(user,2);
            }
        }
    }
}
