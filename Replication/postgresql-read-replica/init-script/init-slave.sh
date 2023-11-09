#slave-config-1
cp /etc/postgresql/init-script/slave-config-1/* /var/lib/postgresql/data-slave-1

#slave-config-2
cp /etc/postgresql/init-script/slave-config-2/* /var/lib/postgresql/data-slave-2

#master config
cp /etc/postgresql/init-script/config/pg_hba.conf /var/lib/postgresql/data
cp /etc/postgresql/init-script/config/postgresql.conf /var/lib/postgresql/data
