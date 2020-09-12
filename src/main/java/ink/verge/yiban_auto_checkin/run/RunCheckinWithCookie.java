package ink.verge.yiban_auto_checkin.run;

import ink.verge.yiban_auto_checkin.common.CommonResult;
import ink.verge.yiban_auto_checkin.mbg.model.User;
import ink.verge.yiban_auto_checkin.service.UserService;
import ink.verge.yiban_auto_checkin.utils.YibanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RunCheckinWithCookie {
    private UserService userService;
    private YibanUtils yibanUtils;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setYibanUtils(YibanUtils yibanUtils) {
        this.yibanUtils = yibanUtils;
    }


    public void runMorCheckin(){
        CommonResult<List<User>> resultInfo = userService.getMorUndoneUser();
        if (resultInfo.getCode() == 200 ){
            List<User> undoneUserList = resultInfo.getData();
            for (User user : undoneUserList) {
                CommonResult<String> info = yibanUtils.checkinWithCookie(user,1);
                System.out.println(info);
            }

        }

    }
}
