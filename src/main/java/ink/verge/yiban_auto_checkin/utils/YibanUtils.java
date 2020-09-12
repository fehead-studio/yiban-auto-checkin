package ink.verge.yiban_auto_checkin.utils;

import cn.hutool.extra.mail.MailUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import ink.verge.yiban_auto_checkin.common.CheckinResult;
import ink.verge.yiban_auto_checkin.common.CommonResult;
import ink.verge.yiban_auto_checkin.mbg.model.User;
import ink.verge.yiban_auto_checkin.service.CheckinService;
import ink.verge.yiban_auto_checkin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class YibanUtils {

    private final CheckinService checkinService;
    private final UserService userService;

    @Autowired
    public YibanUtils(CheckinService checkinService, UserService userService) {
        this.checkinService = checkinService;
        this.userService = userService;
    }

    //获取ACCESS_TOKEN
    public static CommonResult<String> getAccessToken(String username, String password){
        //设置请求参数
        HashMap<String,Object> map = new HashMap<>();
        map.put("account",username);
        map.put("passwd",password);
        map.put("ct",1);
        map.put("identify",0);
        map.put("v","4.7.4");

        //获取返回的字符串
        String jsonStr = HttpUtil.get("https://mobile.yiban.cn/api/v2/passport/login",map);

        //把获取到的字符串转换为JSONObject对象
        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
        try {
            if (jsonObject.get("response").equals("100")){
                String res = ((JSONObject) jsonObject.get("data")).getStr("access_token");
                return CommonResult.success(res,"成功获取access_token");
            } else {
                return CommonResult.failed("易班返回参数不为100，登录失败");
            }
        } catch (Exception e){
            return CommonResult.failed("易班返回的数据可能有变化,未获取到易班返回的状态码");
        }
    }

    //获取Cookie
    public static CommonResult<String> getCookie(String accesstoken){
        //请求数据
        HashMap<String,Object> map = new HashMap<>();
        map.put("act","iapp610661");
        map.put("access_token",accesstoken);

        //发送请求，从cookie中获取PHPSESSIONID
        HttpResponse response = HttpRequest
                .get("http://f.yiban.cn/iapp/index")
                .form(map)
                .setFollowRedirects(true)
                .execute();
        try {
            String res = response.header("Set-Cookie");
            return CommonResult.success(res,"成功获取SESSID");
        } catch (NullPointerException e) {
            System.out.println(response.toString());
            return CommonResult.failed("没获取到SESSID");
        }
    }

    //提交签到请求
    public  CommonResult<String> submit(String cookie,int type){
        double temp = (new Random().nextInt(4)+4)/10.0+36;

        Map<String,Object> map = new LinkedHashMap<>();
        String url;
        if(type == 1){
            url = "http://yiban.sust.edu.cn/v4/public/index.php/Index/formflow/add.html?desgin_id=24&list_id=12";

            map.put("24[0][0][name]","form[24][field_1588749561_2922][]");
            map.put("24[0][0][value]",temp);

            map.put("24[0][1][name]","form[24][field_1588749738_1026][]");
            map.put("24[0][1][value]","陕西省+西安市+未央区+111县道+111县+靠近陕西科技大学学生生活区+");

            map.put("24[0][2][name]","form[24][field_1588749759_6865][]");
            map.put("24[0][2][value]","是");

            map.put("24[0][3][name]","form[24][field_1588749842_2715][]");
            map.put("24[0][3][value]","否");

            map.put("24[0][4][name]","form[24][field_1588749886_2103][]");
            map.put("24[0][4][value]","");

        } else {
            url = "http://yiban.sust.edu.cn/v4/public/index.php/Index/formflow/add.html?desgin_id=25&list_id=12";

            map.put("25[0][0][name]","form[25][field_1588750276_2934][]");
            map.put("25[0][0][value]",temp);

            map.put("25[0][1][name]","form[25][field_1588750304_5363][]");
            map.put("25[0][1][value]","陕西省+西安市+未央区+111县道+111县+靠近陕西科技大学学生生活区+");

            map.put("25[0][2][name]","form[25][field_1588750323_2500][]");
            map.put("25[0][2][value]","是");

            map.put("25[0][3][name]","form[25][field_1588750343_3510][]");
            map.put("25[0][3][value]","否");

            map.put("25[0][4][name]","form[25][field_1588750363_5268][]");
            map.put("25[0][4][value]","");
        }

        HttpResponse response = HttpRequest.post(url)
                .contentType("application/x-www-form-urlencoded")
                .form(map)
                .cookie(cookie)
                .execute();

        /*System.out.println("--------请求链接---------");
        System.out.println(url);
        System.out.println("--------map内容---------");
        System.out.println(map);
        System.out.println("----------温度----------");
        System.out.println(map.get("25[0][0][value]"));
        System.out.println("--------返回消息---------");
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        System.out.println(jsonObject);
        System.out.println("------------------------");*/


        JSONObject returnInfo = JSONUtil.parseObj(response.body());
        String msg = (String) returnInfo.get("msg");
        if (returnInfo.get("code").equals("1") || msg.contains("请勿多次提交") || msg.contains("SU")){
            return CommonResult.success(msg);
        } else {
            return CommonResult.failed(msg);
        }
    }

    //融合获取Access_token、cookie、提交请求为一体
    public  CommonResult<List<User>> checkin(List<User> list, int type){
        List<User> doneUserList = new LinkedList<>();
        for (User user : list) {
            String message,yibanMessage = null;
            long code;
            CommonResult<String> at = getAccessToken(user.getAccount(),user.getPassword());
            if(at.getCode() == 200){
                CommonResult<String> ck = getCookie( at.getData());
                if(ck.getCode() == 200){
                    CommonResult<String> sb = submit(ck.getData(),type);
                    if(sb.getCode() == 200){
                        code = 200;
                        message = "提交成功";
                        doneUserList.add(user);
                    } else {
                        code = 500;
                        message = "提交表单时出错,签到失败";
                    }
                    yibanMessage = sb.getMessage();
                } else {
                    code = 500;
                    message = "获取Cookie时出错";
                }
            } else {
                code = 500;
                message = "获取access_token时失败，可能是账号或密码错误";
            }

            CheckinResult checkinResult = new CheckinResult(code,message,yibanMessage);
            String mail = user.getMail();
            if (mail.length() > 0){
                if (code == 200){
                    MailUtil.send(mail,"今日打卡成功",checkinResult.toString(),false);
                } else {
                    MailUtil.send(mail,"！！！今日打卡失败！！！",checkinResult.toString(),false);
                }
                System.out.println(checkinResult.toString());
            }
        }
        return CommonResult.success(doneUserList,"返回完成签到的列表");
    }

    //通过User对象获取Cookie
    public static CommonResult<String> getCookieOneStep(User user){
        CommonResult<String> at = getAccessToken(user.getAccount(), user.getPassword());
        if (at.getCode() == 200){
            CommonResult<String> ck = getCookie(at.getData());
            if (ck.getCode() == 200){
                return CommonResult.success(ck.getData(),"成功获取Cookie");
            } else {
                return CommonResult.failed(ck.getMessage());
            }
        } else {
            return CommonResult.failed(at.getMessage());
        }
    }

    //通过数据库里存的cookie签到
    public CommonResult<String> checkinWithCookie(User user,int type){
       CommonResult<String> info = submit(user.getCookie(),type);
       if (info.getCode() == 200){
           CommonResult<String> resultInfo = checkinService.setCheckinStatus(user,type);
           if (resultInfo.getCode() == 200 ){
               return CommonResult.success("成功");
           } else {
               return CommonResult.failed("签到成功，但设置签到状态时出现错误");
           }
       } else {
           System.out.println("签到时失败，尝试更新cookie");
           CommonResult<String> resultInfo = userService.setCookie(user);
           if (resultInfo.getCode() == 200){
               System.out.println("更新cookie成功将会在，下一轮签到中再次尝试");
           }
           return CommonResult.failed("失败，尝试更新cookie");
       }
    }

}
