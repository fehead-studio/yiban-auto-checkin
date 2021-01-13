package ink.verge.utils.checkin.service.impl;

import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fehead.lang.error.BusinessException;
import ink.verge.utils.checkin.entity.User;
import ink.verge.utils.checkin.mapper.UserMapper;
import ink.verge.utils.checkin.service.UserService;
import ink.verge.utils.checkin.utils.YibanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Verge
 * @since 2021-01-12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private YibanUtils yibanUtils;

    @Resource
    private UserMapper userMapper;

    @Autowired
    private SymmetricCrypto aes;


    @Override
    public List<User> getNoonUncheckUser() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("is_enable_noon_check",true)
                .eq("noon_check_status",false);
        return userMapper.selectList(wrapper);
    }

    @Override
    public List<User> getMornUncheckUser() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("is_enable_morn_check",true)
                .eq("morn_check_status",false);
        return userMapper.selectList(wrapper);
    }

    @Override
    public int setCheckinStatus(int id,boolean status, int type) {
        User user = new User();
        user.setUid(id);
        if (type == 1){
            user.setMornCheckStatus(status);
        } else if (type == 2){
            user.setNoonCheckStatus(status);
        } else {
            user.setMornCheckStatus(status);
            user.setNoonCheckStatus(status);
        }
        return userMapper.updateById(user);
    }

    @Override
    public User getUserByAccount(String account) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account",account);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User getUserByOpenId(String openId) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openId);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public int updateOpenIdByAccount(String account, String openId) throws BusinessException {
        User user = new User();
        user.setOpenid(openId);
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("account",account);
        userMapper.update(user,wrapper);
        return userMapper.update(user,wrapper);
    }

    @Override
    public boolean verifyAccountByYiBan(String username, String password) {
        return yibanUtils.verifyAccount(username,password);
    }

    @Override
    public boolean verifyAccount(String username, String password) {
        User user = getUserByAccount(username);
        if (user == null) return false;
        return aes.decryptStr(user.getPassword()).equals(password);
    }
}
