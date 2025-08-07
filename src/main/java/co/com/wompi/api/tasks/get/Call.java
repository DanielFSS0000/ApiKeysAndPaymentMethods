package co.com.wompi.api.tasks.get;

public class Call {

    //Llamar servicio ConsumeService Get
//Constructor vacio
    private Call(){}
    public static  ConsumeServiceGet service() {
        return new ConsumeServiceGet();
    }
}