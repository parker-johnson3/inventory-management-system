## MySQL Repo Docs
This is the docs for the MySQL Setup Repository


#### How to Initialize Tables

1. Navigate to the mysql-db-repo directory
2. Run `docker compose build`
3. Run `docker compose up`
4. Wait for SQL Server to start up. This message should pop up when it is ready `[Entrypoint] MySQL init process done. Ready for start up.`
5. Open another terminal and Run `docker exec -i some-mysql sh -c 'exec mysql -uroot -p"pass"' < Dump.sql`
6. If it worked, this should be the output: `mysql: [Warning] Using a password on the command line interface can be insecure.`

### How to Verify Table Creation
1. Run `docker exec -it <CONTAINER_ID> bash`
2. Run `mysql -u root -p` and enter 'pass'
3. Inside mysql, Run `show databases;`
4. Then Run `use appdb;`
5. Run `show tables;`
6. Finally Run `select * from Airplane;`. Nothing should output.

### Notes