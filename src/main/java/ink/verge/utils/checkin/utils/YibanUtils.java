package ink.verge.utils.checkin.utils;

import cn.hutool.crypto.symmetric.SymmetricCrypto;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.fehead.lang.error.BusinessException;
import com.fehead.lang.error.EmBusinessError;
import com.fehead.lang.response.CommonReturnType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ink.verge.utils.checkin.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
@Slf4j
public class YibanUtils {

    @Resource
    private SymmetricCrypto aes;

    private final JsonParser parser = new JsonParser();

    private static final Map<String,Object> mornMap = new LinkedHashMap<>();
    private static final Map<String,Object> noonMap = new LinkedHashMap<>();
    private static final String mornUrl = "http://yiban.sust.edu.cn/v4/public/index.php/Index/formflow/add.html?desgin_id=24&list_id=12";
    private static final String noonUrl = "http://yiban.sust.edu.cn/v4/public/index.php/Index/formflow/add.html?desgin_id=25&list_id=12";
    static {
        mornMap.put("24[0][0][name]","form[24][field_1588749561_2922][]");
        mornMap.put("24[0][0][value]","36.5");

        mornMap.put("24[0][1][name]","form[24][field_1588749738_1026][]");
        mornMap.put("24[0][1][value]","陕西省 西安市 未央区 111县道 111县 靠近陕西科技大学学生生活区");

        mornMap.put("24[0][2][name]","form[24][field_1588749759_6865][]");
        mornMap.put("24[0][2][value]","是");

        mornMap.put("24[0][3][name]","form[24][field_1588749842_2715][]");
        mornMap.put("24[0][3][value]","否");

        mornMap.put("24[0][4][name]","form[24][field_1588749886_2103][]");
        mornMap.put("24[0][4][value]","");

        noonMap.put("25[0][0][name]","form[25][field_1588750276_2934][]");
        noonMap.put("25[0][0][value]","36.5");

        noonMap.put("25[0][1][name]","form[25][field_1588750304_5363][]");
        noonMap.put("25[0][1][value]","陕西省 西安市 未央区 111县道 111县 靠近陕西科技大学学生生活区");

        noonMap.put("25[0][2][name]","form[25][field_1588750323_2500][]");
        noonMap.put("25[0][2][value]","是");

        noonMap.put("25[0][3][name]","form[25][field_1588750343_3510][]");
        noonMap.put("25[0][3][value]","否");

        noonMap.put("25[0][4][name]","form[25][field_1588750363_5268][]");
        noonMap.put("25[0][4][value]","");
    }


    public String getAccessToken(String username, String password) throws BusinessException {
        log.info("-----------------------------------------------------------------------");
        log.info("PARAM: username "+ username);
        log.debug("PARAM: password "+ password);

        String dePassword = aes.decryptStr(password);

        Map<String,Object> map = new HashMap<>();
        map.put("mobile",username);
        map.put("password",dePassword);
        map.put("imei",IMEIUtils.getIMEI());

        String jsonStr = HttpUtil.get("https://mobile.yiban.cn/api/v3/passport/login",map);

        JsonElement element = parser.parse(jsonStr);
        JsonObject root = element.getAsJsonObject();
        String code = root.getAsJsonPrimitive("response").getAsString();

        if (code.equals("100")) {
            JsonObject userObject = root.getAsJsonObject("data").getAsJsonObject("user");
            String nick = userObject.getAsJsonPrimitive("nick").getAsString();
            log.info("nick: "+nick);

            return userObject.getAsJsonPrimitive("access_token").getAsString();
        }else {
            String errMsg = root.getAsJsonPrimitive("message").getAsString();
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL,errMsg);
        }

    }

    public String getCookie(String accesstoken) throws BusinessException{
        log.debug("PARAM: access_token " + accesstoken);

        if (accesstoken == null) throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);

        HttpResponse response = HttpRequest
                .get("http://f.yiban.cn/iapp/index?act=iapp610661&access_token="+accesstoken)
                .setFollowRedirects(true)
                .execute();
        String res = response.header("Set-Cookie");
        log.debug("SUCCESS: Cookie " + res);
        return res;
    }

    public CommonReturnType submit(String cookie,User user,int checkinType){
        log.debug("PARAM: cookie " + cookie);
        log.info("PARAM: type " + checkinType);

        double temperature = (new Random().nextInt(4)+4)/10.0+36;
        Map<String,Object> map;
        String url;

        if (checkinType == 1){
            map = mornMap;
            url = mornUrl;
            map.replace("24[0][0][value]",temperature);
            if (!user.getIsUseDefaultAddress()){
                map.replace("24[0][1][value]",user.getAddress());
                map.replace("24[0][2][value]","否");
            }
        } else {
            map = noonMap;
            url = noonUrl;
            map.replace("25[0][0][value]",temperature);
            if (!user.getIsUseDefaultAddress()){
                map.replace("25[0][1][value]",user.getAddress());
                map.replace("25[0][2][value]","否");
            }
        }



        try {
            if (cookie == null) throw new Exception();

            HttpResponse response = HttpRequest.post(url)
                    .contentType("application/x-www-form-urlencoded")
                    .form(map)
                    .cookie(cookie)
                    .execute();

            JsonElement element = parser.parse(response.body());
            JsonObject root = element.getAsJsonObject();
            String code = root.getAsJsonPrimitive("code").getAsString();
            String msg = root.getAsJsonPrimitive("msg").getAsString();

            if (code.equals("1") || msg.contains("请勿多次提交") || msg.contains("SU")){
                log.info("SUCCESS: "+msg);
            } else {
                log.info("FAILED: " + msg);
            }
            return CommonReturnType.create(msg);
        } catch (Exception e){
            e.printStackTrace();
            return CommonReturnType.create("提交信息时发生异常");
        }
    }


    public boolean checkin(User user, int type) {
        log.info("PARAM: user " + user);
        log.info("PARAM: type " + type);

        boolean flag = false;
        StringBuilder message = new StringBuilder();
        message.append("日期: ").append(new Date());
        try {
            String accessToken = getAccessToken(user.getAccount(),user.getPassword());
            String cookie = getCookie(accessToken);

            CommonReturnType info = submit(cookie,user,type);

            message.append("\n易班返回消息: ").append(info.toString());

            flag = info.getStatus().equals("success");
        }catch (Exception e){
            message.append("失败信息: ").append(e.getMessage());
            log.error("签到失败");
        }finally {
            if (user.getIsEnableEmailAlert() && user.getMail()!=null){
                try {
                    String email = user.getMail();
                    if (flag) MailUtil.send(email,"今日打卡成功", message.toString(),false);
                    else MailUtil.send(email,"！！！今日打卡失败！！！", message.toString(),false);
                } catch (Exception e){
                    log.info("邮件发送失败");
                }
            }
        }
        return flag;
    }

    public boolean verifyAccount(String username, String password){
        log.info("PARAM: username "+ username);
        log.debug("PARAM: password "+ password);

        Map<String,Object> map = new HashMap<>();
        map.put("mobile",username);
        map.put("password",password);
        map.put("imei",IMEIUtils.getIMEI());


        String jsonStr = HttpUtil.get("https://mobile.yiban.cn/api/v3/passport/login",map);

        JsonElement element = parser.parse(jsonStr);
        JsonObject root = element.getAsJsonObject();
        String code = root.getAsJsonPrimitive("response").getAsString();

        return code.equals("100");
    }
}
