const app = require("express")();
const {Client} = require("pg");
let con = 1
app.get("/", async (req, res) => {
    res.send("Health Check successfull")
})
app.get("/all", async (req, res) => {
    const fromDate = new Date();

    const client = new Client({
        host: 'localhost', // Use the IP address of your Docker container
        port: 5439,          // Port mapped to the host
        user: 'postgres',
        password: 'hello',
        database: 'postgres'
      });

    /*
     * Connecting to database tcp server
     */
    console.log("Client : ")
    try {
        var conStartTime = new Date();
        await client.connect();
        var conEndTime= new Date();
        console.log('Connected to PostgreSQL', con);
        con++;
      } catch (error) {
        console.error('Error connecting to PostgreSQL:', error);
        res.status(500).send('Error querying PostgreSQL: ' + error.message);

      }
    //return all rows
    try{

      const results = await client.query("select * from employees limit 10000")// time taking query.

      // console.log("Rows:Result::",results.rows.length)
     
       /*
       * stopping the connection of database tcp server
       */
      
  
      var _conEndStartTime = new Date();
      await client.end();
      var _conEndEndTime= new Date();
          
      const toDate = new Date();
      const elapsed_in_ms = toDate.getTime() - fromDate.getTime();
  
      //send it to the wire
      console.log(results.rows.length, " ", con )
      res.send({"rows": results.rows.length, "totalTime": elapsed_in_ms,
      "connection_Start_time": conEndTime.getTime() - conStartTime.getTime(),
      "connection_Stopping_time": _conEndEndTime.getTime() - _conEndStartTime.getTime(),
       "method": "old"})
    } catch(error){
      console.error('Error Second part ', error);
      res.status(500).send('Error querying PostgreSQL: ' + error.message);

    }
    
})

app.listen(9000, () => console.log("Listening on port 9000"))



/*
Findings ; 


db input : Million rows : 1000000, select * from name, million names 
output : 

{
"rows": 1000002,
"totalTime": 801,
"connection_Start_time": 51,     // start time also 51 miliseconds why?   Do figure out internal details.
"connection_Stopping_time": 4,
"method": "old"
}


default open connecitons for dbms are 100, after that it shows : 
2023-10-15 19:06:12 2023-10-15 13:36:12.037 UTC [2628] FATAL:  sorry, too many clients already

*/