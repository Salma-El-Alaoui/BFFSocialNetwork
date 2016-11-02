package model;

import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.Cell;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by salma on 01/11/2016.
 * The Person class represents the person that is stored in the database, and provides methods to manage it
 */
public class Person {

    //Hbase Table
    private static Table table;

    //row key
    private byte[] name;
    private String nameStr;

    //columns in column family info
    static final private byte[] INFO = Bytes.toBytes("info");
    static final private byte[] AGE_COL = Bytes.toBytes("age");
    static final private byte[] EMAIL_COL = Bytes.toBytes("email");
    private byte[] age;
    private byte[] email;

    //columns in column family friends;
    static final private byte[] FRIENDS = Bytes.toBytes("friends");
    static final private byte[] BFF_COL = Bytes.toBytes("bff");
    static final private byte[] OTHERS_COL = Bytes.toBytes("others");
    private byte[] bff;
    private String bffStr;
    private byte[] others;

    static final private String SEP = " ";


    /**
     * Constructor used for insertion
     * @param name     row key
     * @param bff      mandatory column in family name friends
     * @param newTable Hbase table
     */
    public Person(String name, String bff, Table newTable) {
        this.nameStr = name.toLowerCase().trim();
        this.name = Bytes.toBytes(this.nameStr);
        this.bffStr = bff.toLowerCase().trim();
        this.bff = Bytes.toBytes(this.bffStr);
        table = newTable;
    }


    /**
     * Constructor used for read-only operations
     * @param name     row key
     * @param newTable Hbase table
     */
    public Person(String name, Table newTable) {
        this.nameStr = name.toLowerCase().trim();
        this.name = Bytes.toBytes(this.nameStr);
        table = newTable;
    }


    /**
     * Setter for the age column
     * @param age
     */
    public void setAge(String age) {
        this.age = Bytes.toBytes(age);
    }


    /**
     * Setter for the email column
     * @param email
     */
    public void setEmail(String email) {
        this.email = toBytes(email);
    }


    /**
     * Setter for the bff column
     * @param bff
     */
    public void setBff(String bff) {
        this.bff = toBytes(bff);
    }


    /**
     * Setter for the other friends column. If one of the friends doesn't exist, we insert it in the database and set
     * its bff to the current person
     * @param othersList list of other friends
     * @throws IOException
     */
    public void setOthers(List<String> othersList) throws IOException {
        Set<String> otherFriends = new HashSet<String>(othersList);
        String friends = "";
        for (String friend : otherFriends) {
            if(!friend.trim().isEmpty()) {
                //if the the friend doesn't exist in the database, we create it and set its bff to the current person
                byte[] friendName = toBytes(friend);
                if (!this.exists(friendName)) {
                    Put putFriend = new Put(friendName);
                    putFriend.addColumn(FRIENDS, BFF_COL, this.name);
                    table.put(putFriend);
                    System.out.println("\tSuccess: The person " + friend + " has been correctly inserted in the database.");
                }
                friends = friends.concat(friend + SEP);
            }
        }
        this.others = toBytes(friends);
    }


    /**
     * @return True if a the a row with a the row key personName exists in the table
     * @throws IOException
     */
    public boolean exists(byte[] personName) throws IOException {
        return table.exists(new Get(personName));
    }


    /**
     * Inserts a person in the table
     * We check the modified attributes of the person object to set the values of the columns in the database
     * @return true if the insertion goes well, false if the person already exists in the database
     * @throws IOException
     */
    public boolean addPerson() throws IOException {
        if (this.exists(this.name)) {
            System.out.println("\tError: This person already exists in the database.");
            return false;
        } else {
            //if the the bff doesn't exist in the database, we create it and set its bff to the current person
            if (!this.exists(this.bff)) {
                Put putBff = new Put(this.bff);
                putBff.addColumn(FRIENDS, BFF_COL, this.name);
                table.put(putBff);
                System.out.println("\tSuccess: The person " + this.bffStr + " has been correctly inserted in the database.");
            }
            Put putPerson = new Put(this.name);
            putPerson.addColumn(FRIENDS, BFF_COL, this.bff);
            if (this.others != null && this.others.length != 0) {
                putPerson.addColumn(FRIENDS, OTHERS_COL, this.others);
            }
            if (this.age != null && this.age.length != 0) {
                putPerson.addColumn(INFO, AGE_COL, this.age);
            }
            if (this.email != null && this.email.length != 0) {
                putPerson.addColumn(INFO, EMAIL_COL, this.email);
            }
            table.put(putPerson);
            System.out.println("\tSuccess: The person " + this.nameStr + " has been correctly inserted in the database.");
            return true;

        }
    }


    /**
     * Gets a person from the table and displays its information
     * @return True if the person exists in the table and false otherwise
     * @throws IOException
     */
    public boolean getPerson() throws IOException {
        if (!this.exists(this.name)) {
            System.out.println("\tError: This person doesn't exists in the database.");
            return false;
        }
        else {
            Get get = new Get(this.name);
            Result result = table.get(get);
            for(Cell cell : result.rawCells()){
                byte[] family = CellUtil.cloneFamily(cell);
                byte[] column = CellUtil.cloneQualifier(cell);
                byte[] value = CellUtil.cloneValue(cell);
                System.out.println("\t" + Bytes.toString(family) + ":" + Bytes.toString(column) +
                        " = " + Bytes.toString(value));
            }
            return true;
        }

    }


    /**
     * BONUS question
     * Check whether all friends in column others have a row id
     * @return true if column others is consistent, false otherwise
     * @throws IOException
     */
    public boolean checkOthersConsistency() throws IOException {

        if (!this.exists(this.name)) {
            System.out.println("\tError: This person doesn't exists in the database.");
            return false;
        }
        else {

            Result row = table.get(new Get(this.name));
            if(row.containsColumn(FRIENDS, OTHERS_COL)) {
                String friends = Bytes.toString(row.getValue(FRIENDS, OTHERS_COL));
                String[] friendArray = friends.split(SEP);
                for (String friend : friendArray) {
                    if (exists(toBytes(friend))) {
                        System.out.println("\tFriend " + friend + " has a row id");
                    }
                    else {
                        //one of the friends doesn't exist in the table
                        System.out.println("\tFriend " + friend + " doesn't have a row id");
                        return false;
                    }
                }
                System.out.println("\tothers column is consistent for person "+ this.nameStr);
                return true;
            }
            else {
                //the person doesn't have other friends
                System.out.println("This person doesn't have an other friends column");
                return false;
            }

        }
    }


    /**
     * Converts string to a byte array, after lowercasing it removing trailing spaces
     * @param string
     * @return byte array
     */
    private byte[] toBytes(String string) {
        return Bytes.toBytes(string.toLowerCase().trim());
    }

}


