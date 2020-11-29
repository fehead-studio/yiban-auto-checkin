package ink.verge.yiban_auto_checkin.controller;

import com.fehead.lang.controller.BaseController;
import com.fehead.lang.error.BusinessException;
import com.fehead.lang.error.EmBusinessError;
import com.fehead.lang.response.WxUserMessageReturnType;
import com.thoughtworks.xstream.XStream;
import ink.verge.yiban_auto_checkin.controller.model.WxUserMessageModel;
import ink.verge.yiban_auto_checkin.mbg.model.User;
import ink.verge.yiban_auto_checkin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @Description: 接受微信发的消息
 * @Author: lmwis
 * @Date 2020-11-28 16:19
 * @Version 1.0
 */
@RestController
@RequestMapping("/wx")
@RequiredArgsConstructor
public class WxController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(WxController.class);
    @Value("${fehead.wx.serve.aeskey}")
    String aesKey;
    @Value("${fehead.wx.serve.token}")
    String token;

    final String yibanCheckUrl = "易班自动打卡：\nhttp://47.93.200.138:8099";

    final UserService userService;

    /**
     * 验证消息来自微信
     *
     * @param request
     * @param response
     * @return
     * @throws BusinessException
     */
    @GetMapping("/user/message")
    public String replyWxServe(ServletRequest request, ServletResponse response) throws BusinessException {
        // 获取数据
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        if (!validateNull(signature, timestamp, nonce, echostr)) {
            // 为空不是微信后台
            throw new BusinessException(EmBusinessError.SERVICE_AUTHENTICATION_INVALID);
        }
        List<String> list = new ArrayList<>();
        // 配置上所填写的token
        list.add(token);
        list.add(timestamp);
        list.add(nonce);
        // 字典序排序
        Collections.sort(list);
        // 拼接
        StringBuilder sb = new StringBuilder();
        list.forEach(sb::append);
        // sha1加密
        String hashCode = DigestUtils.sha1DigestAsHex(sb.toString());
        logger.info("hashCode:" + hashCode);
        logger.info("signature:" + signature);
        // 比对
        if (StringUtils.equals(hashCode, signature)) {
            return echostr;
        } else {
            return "";
        }
    }

    /**
     * 接受用户信息并作出自动回复
     * @param request
     * @param response
     * @return
     * @throws BusinessException
     * @throws IOException
     */
    @PostMapping("/user/message")
    public String replyUserMessage(ServletRequest request, ServletResponse response) throws BusinessException, IOException {

        WxUserMessageModel wxUserMessageModel = resolveXmlData(request.getInputStream());
        System.out.println(wxUserMessageModel);
        if (!validateNull(wxUserMessageModel)) {
            return "success";
        }
        WxUserMessageModel responseXmlData = new WxUserMessageModel();
        responseXmlData.setToUserName(wxUserMessageModel.getFromUserName());
        responseXmlData.setFromUserName(wxUserMessageModel.getToUserName());
        responseXmlData.setCreateTime(new Date().getTime());
        responseXmlData.setMsgType("text");
        if (StringUtils.equals(wxUserMessageModel.getContent(), "打卡")) {
            responseXmlData.setContent(yibanCheckUrl);
        } else if (StringUtils.equals(wxUserMessageModel.getContent(), "打卡状态")) {
            responseXmlData.setContent(checkCheckState(wxUserMessageModel.getFromUserName()));
        } else if (StringUtils.startsWith(wxUserMessageModel.getContent(), "手机号")) {
            responseXmlData.setContent(registerUser(wxUserMessageModel.getFromUserName(), wxUserMessageModel.getContent()));
        } else {
            responseXmlData.setContent(wxUserMessageModel.getContent());
        }
        XStream xstream = new XStream();
        xstream.processAnnotations(WxUserMessageModel.class);
        xstream.setClassLoader(WxUserMessageModel.class.getClassLoader());
        return xstream.toXML(responseXmlData);  //XStream的方法，直接将对象转换成 xml数据

    }

    private String registerUser(String fromUserName, String content) {
        String tel = StringUtils.substringAfter(content, "手机号");
        tel = tel.trim();
        User userByAccount = userService.getUserByAccount(tel);
        if (userByAccount == null) {
            return "绑定失败，请确认手机号是否正确或者格式问题，例：手机号15389159576";
        }
        userByAccount.setOpenid(fromUserName);
        userService.changeUserInfo(userByAccount);
        return "绑定成功";
    }

    private String checkCheckState(String fromUserName) {
        User userByOpenId = userService.getUserByOpenId(fromUserName);
        if (userByOpenId == null) {
            return "未绑定账号，请发送指定数据绑定账户，例: \n手机号15389159576";
        }
        Boolean morstatus = userByOpenId.getMorstatus();
        Boolean noonstatus = userByOpenId.getNoonstatus();
        String content = "今日打卡状态：\n";
        Calendar instance = Calendar.getInstance();
        int hour = instance.get(Calendar.HOUR_OF_DAY);
        if (hour > 6) {
            content += "晨检：未开始";
        } else {
            if (morstatus) {
                content += "晨检：成功";
            } else {
                content += "晨检：失败";
            }
        }
        if (hour > 12) {
            content += "午检：未开始";
        } else {
            if (noonstatus) {
                content += "晨检：成功";
            } else {
                content += "晨检：失败";
            }
        }
        return content;

    }

    /**
     * 解析xml数据
     *
     * @param in 输入流
     * @return 微信用户信息实体类
     * @throws IOException
     */
    private WxUserMessageModel resolveXmlData(InputStream in) throws IOException {
        String xmlData = IOUtils.toString(in);
        WxUserMessageModel wxXmlData = null;
        XStream xstream = new XStream();
        // 设置加载类的类加载器
        xstream.setClassLoader(WxUserMessageModel.class.getClassLoader());
        xstream.processAnnotations(WxUserMessageModel.class);
        xstream.alias("xml", WxUserMessageModel.class);
        wxXmlData = (WxUserMessageModel) xstream.fromXML(xmlData);
        return wxXmlData;
    }

    @GetMapping("/user/message/test")
    public WxUserMessageReturnType testNet() {
        return WxUserMessageReturnType.create("测试成功");
    }

}
