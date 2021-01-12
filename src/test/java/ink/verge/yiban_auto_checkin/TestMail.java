package ink.verge.yiban_auto_checkin;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author Verge
 * @Date 2020/11/12 17:46
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMail {
    /*@Autowired
    UserMapper userMapper;
    @Test
    public void mailSend() throws Exception{
        String sub = "关于易班打卡,一定要看";
        String content = "由于种种原因，不再提供自动打卡，请同学们明天起按时打卡，及时上报身体异常情况,由于邮件不能保证百分百送达，接到邮件的同学们提醒一下身边的人";
        //MailUtil.send("981340404@qq.com",sub,content,false);
        List<User> list =  userMapper.selectByExample(new UserExample());
        for (User user : list) {
            if (user.getMail() != null && !user.getMail().isEmpty()){
                System.out.println(user.getMail());
                try {
                    MailUtil.send(user.getMail(),sub,content,false);
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
                Thread.sleep(1000);
            }
        }
    }*/
}
