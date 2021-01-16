package ink.verge.utils.checkin.user;

import ink.verge.utils.checkin.entity.User;
import org.junit.After;
import org.junit.Test;

/**
 * @Author Verge
 * @Date 2021/1/12 21:46
 * @Version 1.0
 */
public class TestInsert {
    @Test
    public void testInsert(){
        User user = new User();
        user.setAccount("account");
        System.out.println("测个锤子，不写了");
    }
    @After
    public void deleteTestSample(){

    }
}
