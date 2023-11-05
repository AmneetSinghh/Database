package IsolationLevels;

import java.sql.SQLOutput;

public class TimingOfOperations {
    public static void main(String args[]){
        System.out.println(System.currentTimeMillis());
    }
}


/*
CREATE TABLE test_updates(
  id SERIAL PRIMARY KEY NOT NULL,
  name CHARACTER VARYING(255)
);

table of 2 columns :
CREATE TABLE test_updates(
  id SERIAL PRIMARY KEY NOT NULL,
  name CHARACTER VARYING(255)
);

1. 10 million rows

Insert:

1. time : 49.69 seconds.




delete all :
1. delete from test_updates;
2. take 4 seconds.  why?
3. it just update x-max  thats it... make it a dead tuple.
3.



Updates all:

1.update all took 22 seconds.. why because update is slower than insert than why
2.



Do figure out why??

 */