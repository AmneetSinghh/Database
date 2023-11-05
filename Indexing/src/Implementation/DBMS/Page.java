package Implementation.DBMS;
import java.util.*;

/*
Page splitting can also happen
*/
public class Page {
    public int PAGE_ID;
    int PAGE_SIZE = 6;
    Record[] recordList;//
    Map<Integer,Integer> offset;
    public int currentSize = 0;
    String TAG = null;
    public Page(int id) {
        recordList = new Record[PAGE_SIZE];
        offset = new HashMap<>();
        this.PAGE_ID = id;
        TAG = "PAGE: "+ PAGE_ID;
    }

    public int insert(Record data){
        if(currentSize == PAGE_SIZE){
            System.out.println(TAG+" is full, Needs Splitting!");
            return -1;
        }
        recordList[currentSize] = data;
        offset.put(data.id,currentSize);
        System.out.println(TAG+" Record with id "+data.id+" is inserted on position : "+ currentSize);
        currentSize++;
        System.out.println("Updated page size : "+ currentSize);
        return 1;
    }

    public Record find(int id){
        return recordList[offset.get(id)]; // very fast.
    }


    // splitting page,
    public Page split(int ID){
        Page page = new Page(ID);
        for(int i=currentSize/2;i<PAGE_SIZE;i++){
            page.insert(recordList[i]);
        }
        currentSize -= (currentSize/2);
        return page;
    }

    public Set<Integer> getListOfRecords(){
        Set<Integer> rc = new HashSet<>();
        for(int i=0;i<currentSize;i++){
            rc.add(recordList[i].id);
        }
        return rc;
    }
}

/*
Index on id, age.
*/
