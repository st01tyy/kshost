package edu.bistu.kshost.kscore.model;

public class ClientMessage
{
    /**
     * Message from server to client
     */

    private Long time;
    private Integer type;
    private Integer n;
    private Integer[] arr;

    private ClientMessage()
    {
        time = System.currentTimeMillis();
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Integer[] getArr() {
        return arr;
    }

    public void setArr(Integer[] arr) {
        this.arr = arr;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public static ClientMessage registerSuccess()
    {
        ClientMessage message = new ClientMessage();
        message.setType(1);
        return message;
    }

    public static ClientMessage registerFail()
    {
        ClientMessage message = new ClientMessage();
        message.setType(2);
        return message;
    }
}
