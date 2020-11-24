package ink.verge.yiban_auto_checkin;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * @Author Verge
 * @Date 2020/11/24 13:17
 * @Version 1.0
 */

public class TestHttpUtils {


    @Test
    public void test (){
        String res = HttpRequest.get("http://localost:8401/testB")
                .timeout(2000)
                .execute().body();
        System.out.println(res);
    }
}
