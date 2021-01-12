package ink.verge.yiban_auto_checkin;

import ink.verge.utils.checkin.utils.YibanUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author Verge
 * @Date 2020/11/23 21:32
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestGetAccessToken {
    @Autowired
    YibanUtils yibanUtils;

    @Test
    public void testGetToken(){
        /*yibanUtils.getAccessToken("17765014581","jinxyang123");*/
    }
}
