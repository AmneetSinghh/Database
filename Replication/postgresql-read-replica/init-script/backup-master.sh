#Backup master 1
pg_basebackup -D /var/lib/postgresql/data-slave-1 -S replication_slot_slave1 -X stream -P -U replicator -Fp -R

#Backup master 2
pg_basebackup -D /var/lib/postgresql/data-slave-2 -S replication_slot_slave1 -X stream -P -U replicator -Fp -R