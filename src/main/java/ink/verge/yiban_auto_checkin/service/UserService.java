package ink.verge.yiban_auto_checkin.service;

import ink.verge.yiban_auto_checkin.common.CommonResult;
import ink.verge.yiban_auto_checkin.mbg.mapper.UserMapper;
import ink.verge.yiban_auto_checkin.mbg.model.User;
import ink.verge.yiban_auto_checkin.mbg.model.UserExample;
import ink.verge.yiban_auto_checkin.utils.YibanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserMapper userMapper;
    @Autowired
    public UserService( UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    //获取未完成晨间签到的人
    public CommonResult<List<User>> getMorUndoneUser(){
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIsNotNull()
                .andPasswordIsNotNull()
                .andMorstatusEqualTo(false);
        try {
            List<User> list = userMapper.selectByExample(userExample);
            return CommonResult.success(list,"成功获取未完成晨间签到的用户");
        } catch (Exception e){
            return  CommonResult.failed("获取未完成晨间签到的用户时失败");
        }
    }

    //获取未完成午间签到的人
    public CommonResult<List<User>> getNoonUndoneUser(){
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIsNotNull()
                .andPasswordIsNotNull()
                .andNoonstatusEqualTo(false);
        try {
            List<User> list = userMapper.selectByExample(userExample);
            return CommonResult.success(list,"成功获取未完成午间签到的用户");
        } catch (Exception e){
            return  CommonResult.failed("获取未完成午间签到的用户时失败");
        }
    }

    //增加新用户
    public CommonResult<String> addUser(User user){
        if (userMapper.insertSelective(user) == 1){
            return CommonResult.success("添加成功");
        } else {
            return CommonResult.failed("添加失败");
        }
    }

    //设置cookie
    public CommonResult<String> setCookie(User user){
        CommonResult<String> ck = YibanUtils.getCookieOneStep(user);
        if (ck.getCode() == 200){
            String cookie = ck.getData();
            user.setCookie(cookie);
            if(userMapper.updateByPrimaryKey(user) == 1){
                return CommonResult.success("成功");
            } else {
                return CommonResult.failed("失败");
            }
        } else {
            return CommonResult.failed("失败");
        }
    }
}
