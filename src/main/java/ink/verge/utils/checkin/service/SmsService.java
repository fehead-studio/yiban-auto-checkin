package ink.verge.utils.checkin.service;


import com.fehead.lang.error.BusinessException;

/**
 * @author Nightnessss 2019/8/11 17:37
 */
public interface SmsService {
    /**
     * 根据行为选择模板发送短信  0为注册模板，1为登录模板，2为重置模版
     */
    public static final int REGISTER = 0;
    public static final int LOGIN = 1;
    public static final int RESET = 3;

    public boolean check(String key) throws BusinessException;

    public void send(String telphone, Integer modelId) throws BusinessException;
}
