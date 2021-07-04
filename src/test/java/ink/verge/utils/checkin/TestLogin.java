package ink.verge.utils.checkin;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.fehead.lang.error.BusinessException;
import ink.verge.utils.checkin.entity.User;
import ink.verge.utils.checkin.utils.YibanUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Verge
 * @Date 2021/7/4 17:45
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestLogin {
    @Autowired
    private YibanUtils yibanUtils;
    @Test
    public void test() throws BusinessException {
        //System.out.println();
        //System.out.println(yibanUtils.getCookie(yibanUtils.getAccessToken("17765014581", "M/ZsS5epJOTwSdOh6b9o5g==")));

        yibanUtils.checkin(new User(){{
            setAccount("17765014581");
            setPassword("M/ZsS5epJOTwSdOh6b9o5g==");
            setIsEnableMornCheck(true);
            setIsEnableEmailAlert(true);
            setMail("981340404@qq.com");
            setIsUseDefaultAddress(true);
            setIsEnableEmailAlert(true);
        }},1);

    }
}