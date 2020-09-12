package ink.verge.yiban_auto_checkin.common;

public class CommonResult<T> {
    private final long code;
    private final String message;
    private final T data;

    public CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static  <T> CommonResult<T> success(String message){
        return new CommonResult<>(200, message, null);
    }
    public static <T> CommonResult<T> success(T data,String message){
        return new CommonResult<>(200, message, data);
    }
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<>(500, message, null);
    }


    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "CommonResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
