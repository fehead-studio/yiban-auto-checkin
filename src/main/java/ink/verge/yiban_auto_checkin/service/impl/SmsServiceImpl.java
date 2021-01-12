package ink.verge.yiban_auto_checkin.service.impl;

import com.fehead.lang.error.BusinessException;
import com.fehead.lang.error.EmBusinessError;
import com.fehead.lang.properties.FeheadProperties;
import ink.verge.yiban_auto_checkin.controller.model.ValidateCodeModel;
import ink.verge.yiban_auto_checkin.service.RedisService;
import ink.verge.yiban_auto_checkin.service.SmsService;
import ink.verge.yiban_auto_checkin.utils.CreateCodeUtil;
import ink.verge.yiban_auto_checkin.utils.SmsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nightnessss 2019/8/11 17:38
 */
@Service
public class SmsServiceImpl implements SmsService {

    @Resource
    SmsUtil smsUtil;

    @Resource
    private FeheadProperties feheadProperties;

    @Resource
    private RedisService redisService;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean check(String key) {

        boolean result = false;
        // 检查验证码在60秒内是否已经发送
        if (redisService.exists(key)) {
            result = true;
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public void send(String telephone, Integer modelId) throws BusinessException {
        Map<String, String> paramMap = new HashMap<>();
        ValidateCodeModel smsCode = CreateCodeUtil.createCode(telephone, 6);
        paramMap.put("code", smsCode.getCode());
        String modelName;
        try {
            modelName = feheadProperties.getSmsProperties().getSmsModel().get(modelId).getName();
        } catch (Exception e) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        logger.info("action：" + feheadProperties.getSmsProperties().getSmsModel().get(modelId).getDes());
        logger.info("模板：" + modelName);
        logger.info("验证码：" + smsCode.getCode());
        logger.info("encode:" + smsCode.getCode());
        smsCode.encode();
        String sendCode = smsUtil.sendSms(modelName, paramMap, telephone);
        if (!StringUtils.equals(sendCode, "OK")) {
            throw new BusinessException(EmBusinessError.SMS_SEND_FAILED,
                    "短信发送失败，CODE: " + sendCode);
        }
        switch (modelId) {
            case 0:
//                String key = passwordEncoder.encode(telephone);
//                logger.info("sms_key: " + key);
//                redisService.set("sms_key_" + telephone, key, new Long(300));
                redisService.set(
                        feheadProperties.getSmsProperties().getRegisterPreKeyInRedis() + smsCode.getTelephone(),
                        smsCode, (long) (30 * 60));
                break;
            case 1:
                redisService.set(
                        feheadProperties.getSmsProperties().getLoginPreKeyInRedis() + smsCode.getTelephone(),
                        smsCode, (long) (30 * 60));
                break;
            case 2:
                redisService.set(
                        feheadProperties.getSmsProperties().getResetPreKeyInRedis() + smsCode.getTelephone(),
                        smsCode, (long) (30 * 60));
                break;
            default:
                break;
        }
    }
}
