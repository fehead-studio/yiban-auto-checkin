package ink.verge.yiban_auto_checkin.controller;

import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.fehead.lang.controller.BaseController;
import com.fehead.lang.validation.Create;
import com.fehead.lang.validation.Update;
import ink.verge.yiban_auto_checkin.common.CommonResult;
import ink.verge.yiban_auto_checkin.mbg.model.User;
import ink.verge.yiban_auto_checkin.run.RunCheckin;
import ink.verge.yiban_auto_checkin.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private SymmetricCrypto aes;

    @PostMapping(path = "/insert")
    public CommonResult insertUser(@RequestBody @Validated(Create.class) User user) {
        log.info("PARAM: account " + user.getAccount());
        log.debug("PARAM: password " + user.getPassword());
        log.info("PARAM: account " + user.getMail());

        if(userService.getUserByAccount(user.getAccount()) == null){
            if (userService.verifyAccount(user.getAccount(),user.getPassword())){
                String enPassword = aes.encryptBase64(user.getPassword());
                user.setPassword(enPassword);
                userService.insertUser(user);
                log.info("SUCCESS: 插入成功");
                return CommonResult.success("添加成功，下次打卡自动打");
            } else {
                log.error("FAIL: 账号或密码错误，插入失败");
                return CommonResult.failed("账号或密码错误，请检查");
            }
        } else {
            log.error("FAIL: 用户已存在，插入失败");
            return CommonResult.failed("用户已存在");
        }
    }

    @PutMapping("/update")
    public CommonResult updateUser(@RequestBody @Validated(Update.class) User user){
        User oldUser = userService.getUserByAccount(user.getAccount());
        boolean res = userService.verifyAccount(user.getAccount(),user.getPassword());
        if (oldUser != null && res){
            user.setUid(oldUser.getUid());
            userService.changeUserInfo(user);
            return CommonResult.success();
        } else {
            return CommonResult.failed("账号或密码错误");
        }
    }
}
