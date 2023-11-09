const {Client} = require("pg")

const client = new Client(
        {
            "host" : "localhost",
            "port":"5433",
            "user": "postgresql",
            "password":"postgresql",
            "database":"postgresql"
        }
 )

connect();
async function connect(){
    await client.connect()
}

async function evntuallyConsistency(){
    console.log("Replica 1")
    while(true){
        const result = await client.query("select count(*) from employees")
        console.log("Replica_1::Rows count: " + JSON.stringify(result.rows[0]));
        await new Promise(resolve => setTimeout(resolve, 500));
    }
}

evntuallyConsistency()