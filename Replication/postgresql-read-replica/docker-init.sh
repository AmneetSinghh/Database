echo "Clearing data"
rm -rf /Users/amneet.singh/Desktop/System\ design/Databse/Replication/postgresql-read-replica/data/*
rm -rf /Users/amneet.singh/Desktop/System\ design/Databse/Replication/postgresql-read-replica/data-slave-1/*
rm -rf /Users/amneet.singh/Desktop/System\ design/Databse/Replication/postgresql-read-replica/data-slave-2/*

docker-compose down

docker-compose up -d  postgres_master

echo "Starting postgres_master node..."
sleep 20  # Waits for master note start complete

echo "Prepare replica config..."
docker exec -it postgres_master sh /etc/postgresql/init-script/init.sh
echo "Restart master node"
docker-compose restart postgres_master
sleep 5

echo "Starting slave_1 node..."
docker-compose up -d  postgres_slave_1
sleep 20  # Waits for note start complete

echo "Done"
sleep 5

echo "Starting slave_2 node..."
docker-compose up -d  postgres_slave_2
sleep 20  # Waits for note start complete

echo "Done"
sleep 5

docker exec -it postgres_master sh /etc/postgresql/init-script/validate.sh
