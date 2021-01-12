package ink.verge.utils.checkin.controller.model;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author weirwei 2020/12/23 14:54
 */
@Data
@NoArgsConstructor
public class ValidateCodeModel implements Serializable {
    private String telephone;

    private String code;

    private LocalDateTime expireTime;



    public ValidateCodeModel(String telephone, String code) {
        this.telephone = telephone;
        this.code = code;
        this.expireTime = LocalDateTime.now();
    }

    public void encode() {
        this.code = DigestUtil.bcrypt(this.code);
    }

    public boolean isExpired(Integer seconds) {
        return LocalDateTime.now().isAfter(this.getExpireTime().plusSeconds(seconds));
    }
}

