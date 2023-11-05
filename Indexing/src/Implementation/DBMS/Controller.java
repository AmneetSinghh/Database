package Implementation.DBMS;

import Implementation.DataFile.ClusterIndex;
import Implementation.IndexFile.Age_Index_File;

/*

Also maintained page splitting cost.
 */
public class Controller {
    public static void main(String args[]){
        int THRESHOLD = 3;
        new ClusterIndex(THRESHOLD);// cluster index created.
        new Age_Index_File(THRESHOLD);
        int[] keysToInsert = {1, 3, 7, 10, 11, 13, 14, 15, 18, 16, 19, 24, 25, 26, 21, 4, 5, 20, 22, 2, 17, 12, 6};
        /*
        Do write how many pages used, how many IO calls and all things.......
         */
        /*
         * I have maintained index at id that is cluster index and age that is secondary index.
         */
        for (int key : keysToInsert) {
            Record record = new Record(key, key,"name","metadata" );
            ClusterIndex.insert(record);
            Age_Index_File.insert(record);
        }


        /// log N
        System.out.println(" -------------------------- start search using age-> ---------------------- ");// get the id.
        int id = Age_Index_File.search(15);// from 14 from here.
        System.out.println("Id-> "+id);


        // Log N
        System.out.println(" -------------------------- start search using id-> ---------------------- ");// get the id.
        Record record = ClusterIndex.search(id);// from 14 from here.
        System.out.println("Id-> "+record);




        System.out.println("Final traverse");
        Age_Index_File.traverse();
    }
}
