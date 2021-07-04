package ink.verge.utils.checkin;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;

import java.util.Arrays;

/**
 * @Author Verge
 * @Date 2021/6/28 12:32
 * @Version 1.0
 */
public class AESTest {
    public static void main(String[] args) {
        System.out.println(SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()));
        //System.out.println(Arrays.toString(key));
    }
}