package ink.verge.yiban_auto_checkin.service.impl;

import cn.hutool.http.HttpUtil;
import com.fehead.lang.error.BusinessException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 微信服务类
 * @Author: lmwis
 * @Date 2020-11-28 15:52
 * @Version 1.0
 */
@Service
public class WXServiceImpl {

    @Value("${fehead.wx.appid}")
    static String appId;
    @Value("${fehead.wx.appsecret}")
    static String appSecret;
    private static final String ACCRESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    private static String accessToken;
    private static Map<String,Object> mapParam = new HashMap<>();
    static {
        mapParam.put("grant_type","client_credential");
        mapParam.put("appid",appId);
        mapParam.put("secret",appSecret);
    }

    /**
     * 获取access_token
     * 一般一个token过期时间为7200秒，即2小时
     * 每日上线2000次
     */
    public void getAccessToken() throws BusinessException {
        String jsonStr = HttpUtil.get(ACCRESS_TOKEN_URL, mapParam);
        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(jsonStr).getAsJsonObject();
        boolean errcode = root.getAsJsonPrimitive("errcode").isJsonNull();
        String errMsg = "";
        // 成功
        if (!errcode) {
            accessToken = root.getAsJsonPrimitive("access_token").getAsString();
        }else {
            errMsg= root.getAsJsonPrimitive("errmsg").getAsString();
//            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL,errMsg);
        }
    }

}
