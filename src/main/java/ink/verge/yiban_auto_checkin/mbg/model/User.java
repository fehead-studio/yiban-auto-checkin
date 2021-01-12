package ink.verge.yiban_auto_checkin.mbg.model;

import com.fehead.lang.validation.Create;
import com.fehead.lang.validation.Update;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

@Data
public class User implements Serializable {
    @Null(groups = Create.class)
    private Integer uid;

    @NotNull(groups = {Create.class, Update.class})
    private String account;

    @NotNull(groups = {Create.class, Update.class})
    private String password;

    @NotNull(groups = Create.class)
    private String address;

    @Email(groups = {Create.class,Update.class})
    @NotNull(groups = Create.class)
    private String mail;

    @Null(groups = {Create.class,Update.class})
    private Boolean morstatus;
    @Null(groups = {Create.class,Update.class})
    private Boolean noonstatus;
    @Null(groups = {Create.class,Update.class})
    private String openid;


}