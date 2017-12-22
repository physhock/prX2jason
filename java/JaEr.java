class JaEr {

    private int code;
    private String msg;
    private String place;
    private String resource;
    private int request_id;
    
    JaEr(int code, String msg, String place, String resource, int request_id) {
        this.msg = msg;
        this.code = code;
        this.resource = resource;
        this.request_id = request_id;
        this.place = place;
    }
}
