package ink.verge.yiban_auto_checkin.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.fehead.lang.error.BusinessException;
import com.fehead.lang.error.EmBusinessError;
import com.fehead.lang.properties.FeheadProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 写代码 敲快乐
 * だからよ...止まるんじゃねぇぞ
 * ▏n
 * █▏　､⺍
 * █▏ ⺰ʷʷｨ
 * █◣▄██◣
 * ◥██████▋
 * 　◥████ █▎
 * 　　███▉ █▎
 * 　◢████◣⌠ₘ℩
 * 　　██◥█◣\≫
 * 　　██　◥█◣
 * 　　█▉　　█▊
 * 　　█▊　　█▊
 * 　　█▊　　█▋
 * 　　 █▏　　█▙
 * 　　 █
 *
 * @author Nightnessss 2019/7/20 17:03
 */
@Component
public class SmsUtil {

    private static final String ORDINARY_ERROR = "CLIENT_SEND_FAILED";
    @Resource
    private FeheadProperties feheadProperties;


    /**
     * 新版本：阿里云云通信发送短信通知
     *
     * @param modelName  短信模板名，传入的模板必须是在阿里大于“管理中心-短信模板管理”中的可用模板。示例：sms.TemplateNotice
     * @param modelParam 模板内容里面的变量
     * @param phone      用户手机号码
     * @return String    Code
     */
    public String sendSms(String modelName, Map<String, String> modelParam, String phone) {

        String appKey = feheadProperties.getSmsProperties().getAppKey();
        String secret = feheadProperties.getSmsProperties().getSecret();
        String regionId = feheadProperties.getSmsProperties().getRegionId();
        String signName = feheadProperties.getSmsProperties().getSignName();

        DefaultProfile profile = DefaultProfile.getProfile(regionId, appKey, secret);
        IAcsClient client = new DefaultAcsClient(profile);


        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", modelName);
        request.putQueryParameter("TemplateParam", JSONUtils.toJSONString(modelParam));
        System.out.println("modelParam: " + JSONUtils.toJSONString(modelParam));
        JSONObject data;
        try {
            CommonResponse response = client.getCommonResponse(request);
            data = JSONUtil.parseObj(response.getData());
        } catch (ClientException e) {
            e.printStackTrace();
            return ORDINARY_ERROR;
        }
        System.out.println(data);
        return (String) data.get("Code");
    }
}
