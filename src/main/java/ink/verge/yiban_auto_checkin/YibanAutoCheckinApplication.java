package ink.verge.yiban_auto_checkin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableScheduling
@ComponentScan({"com.fehead.lang.**","ink.verge.yiban_auto_checkin"})
@MapperScan("ink.verge.yiban_auto_checkin.mbg.mapper")
@EnableSwagger2
public class YibanAutoCheckinApplication {

    public static void main(String[] args) {
        SpringApplication.run(YibanAutoCheckinApplication.class, args);
    }

}
