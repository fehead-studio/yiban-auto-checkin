package ink.verge.yiban_auto_checkin.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.fehead.lang.controller.BaseController;
import com.fehead.lang.error.BusinessException;
import com.fehead.lang.error.EmBusinessError;
import com.fehead.lang.properties.FeheadProperties;
import com.fehead.lang.response.CommonReturnType;
import com.fehead.lang.response.FeheadResponse;
import com.fehead.lang.util.CheckEmailAndTelphoneUtil;
import ink.verge.yiban_auto_checkin.controller.model.ValidateCodeModel;
import ink.verge.yiban_auto_checkin.service.RedisService;
import ink.verge.yiban_auto_checkin.service.SmsService;
import ink.verge.yiban_auto_checkin.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author weirwei 2020/12/23 14:43
 */
@RestController
@RequestMapping("/sys/sms")
public class SmsController extends BaseController {
    enum SmsAction {
        REGISTER("register"),
        LOGIN("login"),
        RESET("reset");
        private String actionStr;

        SmsAction(String actionStr) {
            this.actionStr = actionStr;
        }

        public String value() {
            return actionStr;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SmsController.class);

    @Resource
    private RedisService redisService;
    @Resource
    private FeheadProperties feheadProperties;
    @Resource
    private SmsService smsService;
    @Resource
    private UserService userService;

    /**
     * 提供手机号和当前行为，根据行为发送相应短信
     *
     * @param request 请求
     * @param response 响应
     * @return FeheadResponse
     * @throws BusinessException fehead 标准异常
     */
    @PostMapping(value = "/send")
    public FeheadResponse sendSms(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
        String telephone = request.getParameter("tel");
        logger.info("手机号：" + telephone);
        String action = request.getParameter("action");

        // 检查手机号是否合法
        if (!CheckEmailAndTelphoneUtil.checkTelphone(telephone)) {
            logger.info("手机号不合法");
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "手机号不合法");
        }


        // 检查验证码在60秒内是否已经发送
        if (action.equals(SmsAction.REGISTER.actionStr)) {
            if (smsService.check(feheadProperties.getSmsProperties().getRegisterPreKeyInRedis() + telephone)) {
                ValidateCodeModel code = (ValidateCodeModel) redisService.get(feheadProperties.getSmsProperties().getRegisterPreKeyInRedis() + telephone);
                if (!code.isExpired(60)) {
                    logger.info("验证码已发送");
                    throw new BusinessException(EmBusinessError.SMS_ALREADY_SEND);
                } else {
                    redisService.remove(feheadProperties.getSmsProperties().getRegisterPreKeyInRedis() + telephone);
                }
            }
        } else if (action.equals(SmsAction.LOGIN.actionStr)) {
            if (smsService.check(feheadProperties.getSmsProperties().getLoginPreKeyInRedis() + telephone)) {
                ValidateCodeModel code = (ValidateCodeModel) redisService.get(feheadProperties.getSmsProperties().getLoginPreKeyInRedis() + telephone);
                if (!code.isExpired(60)) {
                    logger.info("验证码已发送");
                    throw new BusinessException(EmBusinessError.SMS_ALREADY_SEND);
                } else {
                    redisService.remove(feheadProperties.getSmsProperties().getLoginPreKeyInRedis() + telephone);
                }
            }
        } else if (action.equals(SmsAction.RESET.actionStr)) {
            if (smsService.check(feheadProperties.getSmsProperties().getResetPreKeyInRedis() + telephone)) {
                ValidateCodeModel code = (ValidateCodeModel) redisService.get(feheadProperties.getSmsProperties().getResetPreKeyInRedis() + telephone);
                if (!code.isExpired(60)) {
                    logger.info("验证码已发送");
                    throw new BusinessException(EmBusinessError.SMS_ALREADY_SEND);
                } else {
                    redisService.remove(feheadProperties.getSmsProperties().getResetPreKeyInRedis() + telephone);
                }
            }
        } else {
            logger.info("action异常");
            throw new BusinessException(EmBusinessError.OPERATION_ILLEGAL, "action异常");
        }

        // 根据行为选择模板发送短信  0为注册模板，1为登录模板，2为重置模版
        if (action.equals(SmsAction.LOGIN.actionStr)) {
            smsService.send(telephone, SmsService.LOGIN);
        } else if (action.equals(SmsAction.REGISTER.actionStr)) {
            smsService.send(telephone, SmsService.REGISTER);
        } else if (action.equals(SmsAction.RESET.actionStr)) {
            smsService.send(telephone, SmsService.RESET);
        } else {
            logger.info("action异常");
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR, "action异常");
        }

        return CommonReturnType.create(telephone);
    }

    /**
     * 对手机号和验证码进行校验
     *
     * @param request 请求
     * @param response 响应
     * @return FeheadResponse
     * @throws BusinessException fehead 标准异常
     */
    @PutMapping(value = "/validate")
    public FeheadResponse validateSms(HttpServletRequest request, HttpServletResponse response)
            throws BusinessException {

        String telephoneInRequest = request.getParameter("tel");
        String codeInRequest = request.getParameter("code");
        String openId = request.getParameter("open_id");
        String smsKey = "";
        logger.info("手机号：" + telephoneInRequest);
        logger.info("验证码：" + codeInRequest);
        if (!CheckEmailAndTelphoneUtil.checkTelphone(telephoneInRequest)) {
            logger.info("手机号不合法");
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "手机号不合法");
        }
        if (codeInRequest.isEmpty()) {
            logger.info("验证码为空");
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "验证码为空");
        }
        if (registerValidate(telephoneInRequest, codeInRequest)) {
            userService.updateOpenIdByAccount(telephoneInRequest, openId);
        }
//        if (registerValidate(telephoneInRequest, codeInRequest)) {
//            smsKey = DigestUtil.bcrypt(telephoneInRequest);
//            logger.info("密钥：" + smsKey);
//            redisService.set("sms_key_" + telephoneInRequest, smsKey, (long) (30 * 60));
//        }

        return CommonReturnType.create(smsKey);
    }

    private boolean registerValidate(String telephoneInRequest, String codeInRequest) throws BusinessException {
        ValidateCodeModel smsCode;

        // 检查redis中是否存有该手机号验证码
        if (!redisService.exists(feheadProperties
                .getSmsProperties().getRegisterPreKeyInRedis() + telephoneInRequest)) {
            if (!redisService.exists(feheadProperties
                    .getSmsProperties().getResetPreKeyInRedis() + telephoneInRequest)) {
                logger.info("验证码不存在");
                throw new BusinessException(EmBusinessError.SMS_ISNULL);
            } else {
                smsCode = (ValidateCodeModel) redisService
                        .get(feheadProperties.getSmsProperties().getResetPreKeyInRedis() + telephoneInRequest);
            }
        } else {
            smsCode = (ValidateCodeModel) redisService
                    .get(feheadProperties.getSmsProperties().getRegisterPreKeyInRedis() + telephoneInRequest);
        }


        if (StringUtils.isBlank(codeInRequest)) {
            logger.info("验证码不能为空");
            throw new BusinessException(EmBusinessError.SMS_BLANK);
        }

        if (smsCode == null) {
            logger.info("验证码不存在");
            throw new BusinessException(EmBusinessError.SMS_ISNULL);
        }


        if (!DigestUtil.bcryptCheck(codeInRequest, smsCode.getCode())) {
            logger.info("验证码不匹配");
            throw new BusinessException(EmBusinessError.SMS_ILLEGAL);
        }

        redisService.remove(feheadProperties.getSmsProperties().getRegisterPreKeyInRedis() + telephoneInRequest);
        redisService.remove(feheadProperties.getSmsProperties().getResetPreKeyInRedis() + telephoneInRequest);


        return true;
    }


}
