# BFFSocialNetwork

Manage a social network in a HBase table.

Build
-----
```
mvn clean package
```
Use `target/BFFSocialNetwork-1.0-SNAPSHOT-jar-with-dependencies.jar` which contains
all the dependencies of the project. 

Usage
-----
Mandatory options are indicated with a star.
```
java -jar BFFSocialNetwork-1.0-SNAPSHOT-jar-with-dependencies.jar

[command] [command options]

Commands:
    get      get person from to the table and display its fields
      Usage: get [options]
        Options:
        * --firstName, -fn
             name of the person, row key

    check      check consistency between row key and others column for a person
      Usage: check [options]
        Options:
        * --firstName, -fn
             name of the person, row key

    put      adds a new person to the table or updates an existing one
      Usage: put [options]
        Options:
        * --firstName, -fn
             name of the person, row key
        * --friends:bff, -bff
             best friend of the person, to be inserted in column family friends
          --friends:others, -others
             other friend(s) of the person (separated by a space), to be
             inserted in column family friends
          --info:age, -age
             age of the person, to be inserted in column family info
          --info:email, -email
             email of the person, to be inserted in column family info

    help      see usage of a command
      Usage: help [options]
        Options:
          --command, -cmd
             command for which you want to see the usage

    exit      exit
      Usage: exit [options]
```

Description
-----------
- Hbase table **BFF_salma** previously exists and has the column families **friends** and **info**.
- Each row represents a person.
- The first name of the person is the **row id** and it's **unique**. 
- The **bff** column in the family *friends* is mandatory.
- Optional columns are:
    - in family info : **age** and **email** 
    - in family friends : **others** (other friends of the person)
- [Consistency] If the *bff* or *others* of the person currently being added to the table do not exist, we add them to the table and set their bff to the current person.


