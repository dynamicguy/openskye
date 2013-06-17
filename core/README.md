Notes on Tests
-

Currently the tests require a MySQL local database,  you will need a Skye user set-up

create database skye_test;
create user 'skye_test'@'localhost' identified by 'skye_test';
grant all on skye_test.* to 'skye_test'@'localhost';
