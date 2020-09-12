package ink.verge.yiban_auto_checkin.service;

import ink.verge.yiban_auto_checkin.common.CommonResult;
import ink.verge.yiban_auto_checkin.mbg.mapper.UserMapper;
import ink.verge.yiban_auto_checkin.mbg.model.User;
import ink.verge.yiban_auto_checkin.mbg.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckinService {
    private final UserMapper userMapper;
    @Autowired
    public CheckinService( UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    //重置签到状态
    public CommonResult<String> resetCheckinStatus(){
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andNoonstatusEqualTo(true);
        userExample.or()
                .andMorstatusEqualTo(true);

        User user = new User();
        user.setMorstatus(false);
        user.setNoonstatus(false);
        try {
            userMapper.updateByExampleSelective(user,userExample);
            return CommonResult.success("成功重置签到状态");
        } catch (Exception e){
            return CommonResult.failed("重置签到状态时出现错误");
        }
    }

    //设置签到状态为true
    public CommonResult<String> setCheckinStatus(User user,int type){
        if (type == 1){
            user.setMorstatus(true);
        } else {
            user.setNoonstatus(true);
        }
        if (userMapper.updateByPrimaryKey(user) == 1){
            return CommonResult.success("成功");
        } else {
            return CommonResult.failed("失败");
        }
    }
}
