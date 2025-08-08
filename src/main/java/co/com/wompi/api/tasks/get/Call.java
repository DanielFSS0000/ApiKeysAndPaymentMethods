package co.com.wompi.api.tasks.get;

public class Call {

    private Call(){}
    public static  ConsumeServiceGet service() {
        return new ConsumeServiceGet();
    }
}