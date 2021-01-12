package ink.verge.utils.checkin.controller.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Verge
 * @Date 2021/1/12 21:12
 * @Version 1.0
 */
@Data
public class UserInsertModel {
    @ApiModelProperty(value = "易班账号")
    private String account;

    @ApiModelProperty(value = "易班密码")
    private String password;

    @ApiModelProperty(value = "签到时提交的地址")
    private String address;

    @ApiModelProperty(value = "签到状态提醒邮箱")
    private String mail;

    @ApiModelProperty(value = "是否使用默认地址(生活区)(1->使用默认地址,2->使用自定义地址)")
    private Boolean isUseDefaultAddress;

    @ApiModelProperty(value = "是否开启邮件提醒(1->开启,0->关闭)")
    private Boolean isEnableEmailAlert;

    @ApiModelProperty(value = "是否开启自动晨检(1->开启,0->关闭)")
    private Boolean isEnableMornCheck;

    @ApiModelProperty(value = "是否开启自动午检(1->开启,0->关闭)")
    private Boolean isEnableNoonCheck;
}
