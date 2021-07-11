package ink.verge.utils.checkin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>
 *  记录每日打卡情况
 * </p>
 *
 * @author lmwis
 * @since 2021-01-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="DayCheckinState对象", description="")
public class DayCheckinState implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Date checkTime;

    private String checkState;

    private String address;

    private Double temperature;

    private String info;

    private Boolean status;


}