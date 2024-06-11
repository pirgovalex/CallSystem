public class Call {
    private String from;
    private String to;
    private String code;

    public Call(String from, String to, String code) {
        this.from = from;
        this.to = to;
        this.code = code;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getCode() {
        return code;
    }
}
