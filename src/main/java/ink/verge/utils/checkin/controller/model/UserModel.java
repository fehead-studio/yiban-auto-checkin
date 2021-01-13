package ink.verge.utils.checkin.controller.model;

import com.fehead.lang.validation.Create;
import com.fehead.lang.validation.Update;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author Verge
 * @Date 2021/1/12 21:12
 * @Version 1.0
 */
@Data
public class UserModel {
    @NotNull(groups = {Create.class,Update.class})
    @ApiModelProperty(value = "易班账号")
    private String account;

    @NotNull(groups = {Create.class,Update.class})
    @ApiModelProperty(value = "易班密码")
    private String password;

    @ApiModelProperty(value = "签到时提交的地址")
    private String address;

    @ApiModelProperty(value = "签到状态提醒邮箱")
    private String mail;

    @NotNull(groups = Create.class)
    @ApiModelProperty(value = "是否使用默认地址(生活区)(1->使用默认地址,2->使用自定义地址)")
    private Boolean isUseDefaultAddress;

    @NotNull(groups = Create.class)
    @ApiModelProperty(value = "是否开启邮件提醒(1->开启,0->关闭)")
    private Boolean isEnableEmailAlert;

    @NotNull(groups = Create.class)
    @ApiModelProperty(value = "是否开启自动晨检(1->开启,0->关闭)")
    private Boolean isEnableMornCheck;

    @NotNull(groups = Create.class)
    @ApiModelProperty(value = "是否开启自动午检(1->开启,0->关闭)")
    private Boolean isEnableNoonCheck;
}
