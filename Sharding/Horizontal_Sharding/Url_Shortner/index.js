/*
            ------ Horizontal Sharding --- Sharding by rows for x table --- 
*/



const app = require("express")();
const {Client} = require("pg")
const crypto = require("crypto")
const HashRing = require("hashring")
/*
TODO:
Implement how to split the url-country side.... asia, us on different shards.

Sol : Pass countrySide code into hash, so for asia it will always goes to same server, same for america.

/*
* Default serverNode pushed into consistent hash
*/
const hr = new HashRing();
hr.add("5433")
hr.add("5434")
hr.add("5435")

/*
* Default 3 shards
*/

const clients = {
    "5433": new Client(
        {
            "host" : "localhost",
            "port":"5433",
            "user": "postgres",
            "password":"hello",
            "database":"postgres"
        }
    ),
    "5434": new Client(
        {
            "host" : "localhost",
            "port":"5434",
            "user": "postgres",
            "password":"hello",
            "database":"postgres"
        }
    ),
    "5435": new Client(
        {
            "host" : "localhost",
            "port":"5435",
            "user": "postgres",
            "password":"hello",
            "database":"postgres"
        }
    ),
}

connect();
async function connect(){
    await clients["5433"].connect()
    await clients["5434"].connect()
    await clients["5435"].connect()
}

app.get("/:urlId",async (req,res)=>{
    //http://localhost:8081/[fjsy]  <- this is url id.
    const urlId = req.params.urlId;
    const server = hr.get(urlId)
    const result = await clients[server].query("select * from url_table where _url_id = $1",[urlId])
    if(result.rowCount > 0){
        res.send({
            "urlId":urlId,
            "url": result.rows[0],
            "server" : server
           })
    }
    else{
        res.sendStatus(404)
    }
    
})

app.post("/",async (req,res)=>{
    const url = req.query.url;
    // consistently hash this to get a port.

   const hash = crypto.createHash("sha256").update(url).digest("base64")
   const urlId = hash.substr(1,5)

   const server = hr.get(urlId)
   await clients[server].query("insert into url_table (_url,_url_id) values($1,$2)", [url,urlId])

   res.send({
    "urlId":urlId,
    "hash": hash,
    "server" : server
   })
})


app.listen(8081,()=>{
    console.log("Listening to 8081")
})