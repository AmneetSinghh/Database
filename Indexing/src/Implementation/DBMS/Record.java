package Implementation.DBMS;

public class Record{
    public int id;// Primary key, we will make cluster index from it.
    public int age;
    public String name;
    public String metaData;

    public  Record(int id, int age, String name, String metaData){
        this.id = id;
        this.age = age;
        this.name = name;
        this.metaData = metaData;
    }

}