const app = require("express")();
const {Pool} = require("pg");

let poolCount = 0;
let poolSum = 0;

const pool = new Pool({
    "host": "localhost",
    "port": 5439,
    "user":"postgres",
    "password" : "hello",
    "database" : "postgres",
    "max": 10// max 10 connections in the pool
})



app.get("/pool", async (req, res) => {
    const fromDate = new Date();
    poolCount ++;
    //return all rows
    console.log("Get the request",poolCount)
    console.log('Number of clients in use:', pool.totalCount - pool.idleCount);
    console.log('Number of idle clients:', pool.idleCount);
    const results = await pool.query("select * from employees LIMIT 500000")
    const estimatedSize = roughSizeOfObject(results);
    console.log('Estimated memory usage of results object:', estimatedSize, 'bytes');
    console.table(results.rows.length, )


    

    const toDate = new Date();
    const elapsed = toDate.getTime() - fromDate.getTime();
    poolSum += elapsed;
    //send it to the wire
    res.send({"rows": results.rows, "elapsed": elapsed, "avg": Math.round(poolSum/poolCount), "method": "pool"})
})

app.listen(9000, () => console.log("Listening on port 9000"))

/*

for (let i = 0; i < 1000; i++) fetch(`http://localhost:9000/old`).then(a=>a.json()).then(console.log).catch(console.error);
for (let i = 0; i < 1000; i++) fetch(`http://localhost:9000/pool`).then(a=>a.json()).then(console.log).catch(console.error);

*/


/* 
Findings : 

js showing heap out of memory : because results object we are taking 10 millin rows, so heap out of memory.
1. connections concurrent 1000
2. poll has max 10 connections active
3. 10 million rows inside table for select query.

*/



app.get("/pool1", async (req, res) => {
    console.log("Poool reached")
    const results = await pool.query("select * from employees LIMIT 500000")
    console.table(results.rows.length, )

})

function roughSizeOfObject(object) {
    const objectList = [];
    const stack = [object];
    let bytes = 0;

    while (stack.length) {
        const value = stack.pop();

        if (typeof value === 'boolean') {
            bytes += 4;
        } else if (typeof value === 'string') {
            bytes += value.length * 2;
        } else if (typeof value === 'number') {
            bytes += 8;
        } else if (typeof value === 'object' && objectList.indexOf(value) === -1) {
            objectList.push(value);

            for (const key in value) {
                stack.push(value[key]);
            }
        }
    }

    return bytes;
}