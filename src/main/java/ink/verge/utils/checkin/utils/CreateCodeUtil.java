package ink.verge.utils.checkin.utils;


import ink.verge.utils.checkin.controller.model.ValidateCodeModel;

import java.util.Random;

/**
 * @author Nightnessss 2019/7/22 18:29
 */
public class CreateCodeUtil {

    /**
     * 生成随机验证码
     *
     * @return
     */
    public static ValidateCodeModel createCode(String telephone, Integer number) {

        Random random = new Random();

        StringBuilder sRand = new StringBuilder();
        for (int i = 0; i < number; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand.append(rand);
        }

        return new ValidateCodeModel(telephone, sRand.toString());
    }


}
