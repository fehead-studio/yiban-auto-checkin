package ink.verge.utils.checkin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Verge
 * @since 2021-01-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="User对象", description="")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "uid", type = IdType.AUTO)
    private Integer uid;

    @ApiModelProperty(value = "易班账号")
    private String account;

    @ApiModelProperty(value = "易班密码")
    private String password;

    @ApiModelProperty(value = "签到时提交的地址")
    private String address;

    @ApiModelProperty(value = "签到状态提醒邮箱")
    private String mail;

    @ApiModelProperty(value = "晨检状态(1->成功,0->失败)")
    private Boolean mornCheckStatus;

    @ApiModelProperty(value = "午检状态(1->成功,0->失败)")
    private Boolean noonCheckStatus;

    @ApiModelProperty(value = "微信openid")
    private String openid;

    @ApiModelProperty(value = "是否使用默认地址(生活区)(1->使用默认地址,2->使用自定义地址)")
    private Boolean isUseDefaultAddress;

    @ApiModelProperty(value = "是否开启邮件提醒(1->开启,0->关闭)")
    private Boolean isEnableEmailAlert;

    @ApiModelProperty(value = "是否开启自动晨检(1->开启,0->关闭)")
    private Boolean isEnableMornCheck;

    @ApiModelProperty(value = "是否开启自动午检(1->开启,0->关闭)")
    private Boolean isEnableNoonCheck;


}
