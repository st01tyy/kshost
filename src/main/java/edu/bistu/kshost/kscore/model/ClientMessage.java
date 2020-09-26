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
        n = 0;
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

    public static ClientMessage matchResult(Integer gameID)
    {
        ClientMessage message = new ClientMessage();
        message.setType(3);
        message.setN(1);
        Integer[] arr = new Integer[1];
        arr[0] = gameID;
        message.setArr(arr);
        return message;
    }

    public static ClientMessage startQuestion(Integer questionPosition)
    {
        ClientMessage message = new ClientMessage();
        message.setType(4);
        message.setN(1);
        Integer[] arr = new Integer[1];
        arr[0] = questionPosition;
        message.setArr(arr);
        return message;
    }

    public static ClientMessage updateStatus(Integer[] arr)
    {
        ClientMessage message = new ClientMessage();
        message.setType(5);
        message.setN(arr.length);
        message.setArr(arr);
        return message;
    }

    public static ClientMessage endGame(Integer[] arr)
    {
        ClientMessage message = new ClientMessage();
        message.setType(6);
        message.setN(arr.length);
        message.setArr(arr);
        return message;
    }
}
