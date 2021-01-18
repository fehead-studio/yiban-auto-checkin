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
    private static final Map<String,Object> holidayMap = new LinkedHashMap<>();
    private static final String mornUrl = "http://yiban.sust.edu.cn/v4/public/index.php/Index/formflow/add.html?desgin_id=24&list_id=12";
    private static final String noonUrl = "http://yiban.sust.edu.cn/v4/public/index.php/Index/formflow/add.html?desgin_id=25&list_id=12";
    private static final String holidayUrl = "http://yiban.sust.edu.cn/v4/public/index.php/Index/formflow/add.html?desgin_id=13&list_id=9";
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

        holidayMap.put("13[0][0][name]","form[13][field_1587635120_1722][]");
        holidayMap.put("13[0][0][value]","36.6");

        holidayMap.put("13[0][1][name]","form[13][field_1587635142_8919][]");
        holidayMap.put("13[0][1][value]","正常");

        holidayMap.put("13[0][2][name]","form[13][field_1587635252_7450][]");
        holidayMap.put("13[0][2][value]","获取位置时出现问题");

        holidayMap.put("13[0][3][name]","form[13][field_1587635509_7740][]");
        holidayMap.put("13[0][3][value]","否");

        holidayMap.put("13[0][4][name]","form[13][field_1587998920_6988][]");
        holidayMap.put("13[0][4][value]","");

        holidayMap.put("13[0][5][name]","form[13][field_1587998777_8524][]");
        holidayMap.put("13[0][5][value]","否");

        holidayMap.put("13[0][6][name]","form[13][field_1587635441_3730][]");
        holidayMap.put("13[0][6][value]","");
    }


    /**
     * 获取accessToken
     * @param username 用户名
     * @param password 密码
     * @return accessToken
     * @throws BusinessException 登陆失败抛出异常
     */
    public String getAccessToken(String username, String password) throws BusinessException {
        log.info("开始获取accessToken");
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

    /**
     * 获取cookie
     * @param accessToken
     * @return
     * @throws BusinessException
     */
    public String getCookie(String accessToken) throws BusinessException{
        log.info("开始获取cookie");
        log.debug("PARAM: access_token " + accessToken);

        if (accessToken == null) throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);

        HttpResponse response = HttpRequest
                .get("http://f.yiban.cn/iapp/index?act=iapp610661&access_token="+accessToken)
                .setFollowRedirects(true)
                .execute();
        String res = response.header("Set-Cookie");
        log.debug("SUCCESS: Cookie " + res);
        return res;
    }

    /**
     * 提交表单
     * @param cookie cookie
     * @param url 向该地址提交表单
     * @param data 表单数据
     * @return
     */
    public CommonReturnType submit(String cookie,String url,Map<String,Object> data){
        log.info("开始提交表单");
        try {
            if (cookie == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"cookie不能为空");

            HttpResponse response = HttpRequest.post(url)
                    .contentType("application/x-www-form-urlencoded")
                    .form(data)
                    .cookie(cookie)
                    .execute();

            JsonElement element = parser.parse(response.body());
            JsonObject root = element.getAsJsonObject();
            String code = root.getAsJsonPrimitive("code").getAsString();
            String msg = root.getAsJsonPrimitive("msg").getAsString();

            if (code.equals("1") || msg.contains("请勿多次提交") || msg.contains("SU")){
                log.info("SUCCESS: "+msg);
                return CommonReturnType.create(msg,"success");
            } else {
                log.info("FAILED: " + msg);
                return CommonReturnType.create(msg,"failed");
            }

        } catch (Exception e){
            e.printStackTrace();
            return CommonReturnType.create("提交信息时发生异常","failed");
        }
    }

    /**
     * 假期签到
     * @param user
     * @return
     */
    public boolean checkin(User user){
        log.info(user.getAccount()+"开始签到");
        boolean flag = false;
        StringBuilder message = new StringBuilder();
        message.append("日期: ").append(new Date());
        try {
            String accessToken = getAccessToken(user.getAccount(),user.getPassword());
            if (accessToken == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"accessToken不能为null");
            String cookie = getCookie(accessToken);
            if (cookie == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"cookie不能为null");
            if (user.getAddress() == null || user.getAddress().equals("")) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"地址不能为null");

            Map<String,Object> map = holidayMap;
            map.replace("13[0][2][value]",user.getAddress());
            map.replace("13[0][0][value]",(new Random().nextInt(4)+4)/10.0+36);

            CommonReturnType info = submit(cookie,holidayUrl,map);

            message.append("\n易班返回消息: ").append(info.toString());

            flag = info.getStatus().equals("success");
            if (flag) log.info("签到成功");
            else log.info("签到失败");
        } catch (Exception e){
            message.append("\n失败信息: ").append(e.getMessage());
            log.error("签到失败:" + e.getMessage());
        } finally {
            if (user.getIsEnableEmailAlert() && user.getMail()!=null && !user.getMail().equals("")){
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

    public boolean checkin(User user, int checkinType) {

        boolean flag = false;
        StringBuilder message = new StringBuilder();
        message.append("日期: ").append(new Date());
        try {
            String accessToken = getAccessToken(user.getAccount(),user.getPassword());
            if (accessToken == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"accessToken不能为null");
            String cookie = getCookie(accessToken);
            if (cookie == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"cookie不能为null");

            Map<String,Object> map;
            String url;

            if (checkinType == 1){
                map = mornMap;
                url = mornUrl;
                map.replace("24[0][0][value]",(new Random().nextInt(4)+4)/10.0+36);
                if (!user.getIsUseDefaultAddress()){
                    map.replace("24[0][1][value]",user.getAddress());
                    map.replace("24[0][2][value]","否");
                }
            } else {
                map = noonMap;
                url = noonUrl;
                map.replace("25[0][0][value]",(new Random().nextInt(4)+4)/10.0+36);
                if (!user.getIsUseDefaultAddress()){
                    map.replace("25[0][1][value]",user.getAddress());
                    map.replace("25[0][2][value]","否");
                }
            }

            CommonReturnType info = submit(cookie,url,map);

            message.append("\n易班返回消息: ").append(info.toString());

            flag = info.getStatus().equals("success");
        } catch (Exception e){
            message.append("\n失败信息: ").append(e.getMessage());
            log.error("签到失败");
        } finally {
            if (user.getIsEnableEmailAlert() && user.getMail()!=null && !user.getMail().equals("")){
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
