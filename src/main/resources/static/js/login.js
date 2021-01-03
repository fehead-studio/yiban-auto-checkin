$(function () {
    $("#submit").click(function () {
        let tel = $("#telephone").val();
        let code = $("#code").val();
        let open_id = GetQueryString("open_id");
        $.ajax({
            url: "sys/sms/validate",
            type: "put",
            contentType: "application/x-www-form-urlencoded;charset=UTF-8",
            data: {
                "tel": tel,
                "code": code,
                "open_id": open_id,
            },
            success: function (data) {
                if (data.status === "success") {
                    $("#alert").html('<div class="alert alert-success" role="alert">' + data.data + '</div>');
                } else {
                    $("#alert").html('<div class="alert alert-danger" role="alert">' + data.data.errorMsg + '</div>');
                }
            },
            error: function (data) {
                $("#alert").html('<div class="alert alert-success" role="alert">' + "error" + '</div>');
            }
        })
    })
    $("#send").click(function () {
        // alert("点击后请等待一会")
        let tel = $("#telephone").val();
        $.ajax({
            url: "sys/sms/send",
            type: "post",
            contentType: "application/x-www-form-urlencoded;charset=UTF-8",
            data: {
                "tel": tel,
                "action": "register",
            },
            success: function (data) {
                if (data.status === "success") {
                    $("#alert").html('<div class="alert alert-success" role="alert">' + data.data + '</div>');
                } else {
                    $("#alert").html('<div class="alert alert-danger" role="alert">' + data.data.errorMsg + '</div>');
                }
            },
            error: function (data) {
                $("#alert").html('<div class="alert alert-success" role="alert">' + "error" + '</div>');
            }
        })
    })
    function GetQueryString(name) {
        let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        let r = window.location.search.substr(1).match(reg);
        if (r !== null) return unescape(r[2]);
        return null;
    }
})