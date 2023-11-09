const {Client} = require("pg")

const client = new Client(
        {
            "host" : "localhost",
            "port":"5432",
            "user": "postgresql",
            "password":"postgresql",
            "database":"postgresql"
        }
 )

connect();
async function connect(){
    await client.connect()
}

async function insert(){
    for(let i =1;i<=10000000;i++){
        let name = "Amneet" + i;
        await client.query("insert into employees (name) values($1)", [name])
        let result = await client.query("select count(*) from employees")
        console.log("Master::Rows count: " + JSON.stringify(result.rows[0]));
    }
    
}

insert()

