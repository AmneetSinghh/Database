const app = require("express")();
const {Pool} = require("pg");


const pool = new Pool({
    "host": "localhost",
    "port": 5439,
    "user":"postgres",
    "password" : "hello",
    "database" : "postgres",
    "max": 10// max 10 connections in the pool
})



app.get("/", (req, res) => {
    res.sendFile("/Users/amneet.singh/Desktop/System design/Databse/Connection_pool_and_seat_booking/double_booking/index.html");
})

//get all seats 
app.get("/seats", async (req, res) => {
    console.log("entering seats")
    const result = await pool.query("select * from seats");
    console.log("Something", result.rows)
    res.send(result.rows)
})

//book a seat give the seatId and your name
 
app.put("/:id/:name", async (req, res) => {
    try{
        const id = req.params.id 
        const name = req.params.name;
 
        const conn = await pool.connect();
        //begin transaction
        await conn.query("BEGIN");
        //getting the row to make sure it is not booked
        const sql = "SELECT * FROM seats where id = $1 and isbooked = FALSE FOR UPDATE"  // we have used for-update means exclusive lock.
        const result = await conn.query(sql,[id])
        //if no rows found then the operation should fail can't book
        if (result.rowCount === 0) {
            res.send({"error": "Seat already booked"})
            return;
        } 
        //if we get the row, we are safe to update
        const sqlU= "update seats set isbooked = TRUE, name = $2 where id = $1"
        const updateResult = await conn.query(sqlU,[id, name]);
        
        //end transaction
        await conn.query("COMMIT");
        conn.release();
        res.send(updateResult)
    }
    catch(ex){
        console.log(ex)
        res.send(500);
    }
   
    
}) 

app.listen(9000, () => console.log("Listening on port 9000"))
