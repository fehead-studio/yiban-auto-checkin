package ink.verge.yiban_auto_checkin.controller;

import ink.verge.yiban_auto_checkin.common.CommonResult;
import ink.verge.yiban_auto_checkin.mbg.model.User;
import ink.verge.yiban_auto_checkin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "/addUser")
    public @ResponseBody
    CommonResult<String> addUser(@RequestBody User user){
        System.out.println(user);
        if (user.getAccount().length() < 11 || user.getPassword().length() < 6){
            return CommonResult.failed("数据有误，请检查");
        } else {
            return userService.addUser(user);
        }
    }
}
