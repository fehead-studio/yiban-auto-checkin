package ink.verge.yiban_auto_checkin.mbg.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private Integer uid;

    private String account;

    private String password;

    private String address;

    private String mail;

    private Boolean morstatus;

    private Boolean noonstatus;

    private String openid;

    private static final long serialVersionUID = 1L;

}