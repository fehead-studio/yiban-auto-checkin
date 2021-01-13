package ink.verge.utils.checkin.controller;


import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.fehead.lang.controller.BaseController;
import com.fehead.lang.response.CommonReturnType;
import com.fehead.lang.validation.Create;
import com.fehead.lang.validation.Update;
import ink.verge.utils.checkin.controller.model.UserModel;
import ink.verge.utils.checkin.entity.User;
import ink.verge.utils.checkin.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    public CommonReturnType insertUser(@RequestBody @Validated(Create.class) UserModel user) {
        log.info("PARAM: account " + user.getAccount());
        log.debug("PARAM: password " + user.getPassword());
        log.info("PARAM: account " + user.getMail());

        if(userService.getUserByAccount(user.getAccount()) == null){
            if (userService.verifyAccountByYiBan(user.getAccount(),user.getPassword())){
                String enPassword = aes.encryptBase64(user.getPassword());
                user.setPassword(enPassword);
                User newUser = new User();
                BeanUtils.copyProperties(user,newUser);
                userService.save(newUser);
                log.info("SUCCESS: 插入成功");
                return CommonReturnType.create("添加成功，下次打卡自动打");
            } else {
                log.error("FAIL: 账号或密码错误，插入失败");
                return CommonReturnType.create("账号或密码错误，请检查");
            }
        } else {
            log.error("FAIL: 用户已存在，插入失败");
            return CommonReturnType.create("用户已存在");
        }
    }

    @PutMapping("/update")
    public CommonReturnType updateUser(@RequestBody @Validated(Update.class) UserModel user){
        boolean res = userService.verifyAccount(user.getAccount(),user.getPassword());
        if (res){
            User oldUser = userService.getUserByAccount(user.getAccount());
            BeanUtils.copyProperties(user,oldUser,"password");
            oldUser.setPassword(aes.encryptBase64(user.getPassword()));
            userService.updateById(oldUser);
            return CommonReturnType.create("成功");
        } else {
            return CommonReturnType.create("账号或密码错误");
        }
    }

    @GetMapping("/get")
    private CommonReturnType getUser(String account,String password){
        boolean res = userService.verifyAccount(account,password);
        if (res){
            return CommonReturnType.create(userService.getUserByAccount(account));
        } else {
            return CommonReturnType.create("账号密码不匹配，或账号不存在");
        }
    }
}

