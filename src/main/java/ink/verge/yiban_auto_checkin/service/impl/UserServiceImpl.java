package ink.verge.yiban_auto_checkin.service.impl;

import ink.verge.yiban_auto_checkin.mbg.mapper.UserMapper;
import ink.verge.yiban_auto_checkin.mbg.model.User;
import ink.verge.yiban_auto_checkin.mbg.model.UserExample;
import ink.verge.yiban_auto_checkin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getMorUndoneUser(){
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIsNotNull()
                .andPasswordIsNotNull()
                .andMorstatusEqualTo(false);
        return userMapper.selectByExample(userExample);
    }


    @Override
    public List<User> getNoonUndoneUser(){
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIsNotNull()
                .andPasswordIsNotNull()
                .andNoonstatusEqualTo(false);
        return userMapper.selectByExample(userExample);
    }


    @Override
    public int insertUser(User user){
        return userMapper.insertSelective(user);
    }

    @Override
    public int deleteUser(int uid) {
        return userMapper.deleteByPrimaryKey(uid);
    }

    @Override
    public int changeUserInfo(User user) {
        return userMapper.updateByPrimaryKeySelective(user);

    }

    @Override
    public int resetCheckinStatus(){
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andNoonstatusEqualTo(true);
        userExample.or()
                .andMorstatusEqualTo(true);

        User user = new User();
        user.setMorstatus(false);
        user.setNoonstatus(false);
        return userMapper.updateByExampleSelective(user,userExample);
    }

    @Override
    public int setCheckinStatus(User user,int type){
        if (type == 1){
            user.setMorstatus(true);
        } else {
            user.setNoonstatus(true);
        }
        return userMapper.updateByPrimaryKey(user);
    }

    @Override
    public User getUserByUID(int uid) {
        return userMapper.selectByPrimaryKey(uid);
    }

    @Override
    public User getUserByAccount(String account) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andAccountEqualTo(account);
        List<User> list = userMapper.selectByExample(userExample);
        return list.get(0);
    }

    @Override
    public User getUserByOpenId(String openId) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andAccountEqualTo(openId);
        List<User> list = userMapper.selectByExample(userExample);
        return list.get(0);
    }
}
