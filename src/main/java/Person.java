/**
 * Created by salma on 01/11/2016.
 * Person class which represents the person object, and provides methods to manage it in the database
 */

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Person {

    //Hbase Table
    protected Table table;

    //row key
    protected byte[] name;
    protected String nameStr;

    //columns in column family info
    final private byte[] INFO = Bytes.toBytes("info");
    final private byte[] AGE_COL = Bytes.toBytes("age");
    final private byte[] EMAIL_COL = Bytes.toBytes("email");
    private byte[] age;
    private byte[] email;

    //columns in column family friends;
    final private byte[] FRIENDS = Bytes.toBytes("friends");
    final private byte[] BFF_COL = Bytes.toBytes("bff");
    final private byte[] OTHERS_COL = Bytes.toBytes("others");
    private byte[] bff;
    private String bffStr;
    private byte[] others;
    private Set<String> otherFriends;

    final private String SEP = " ";


    /**
     * Constructor
     * @param name row key
     * @param bff mandatory column in family name friends
     * @param newTable Hbase table
     */
    public Person(String name, String bff, Table newTable) {
        this.nameStr = name.toLowerCase().trim();
        this.name = Bytes.toBytes(this.nameStr);
        this.bffStr = bff.toLowerCase().trim();
        this.bff = Bytes.toBytes(this.bffStr);
        this.table = newTable;
    }

    /**
     * Setter for the age column
     * @param age
     */
    public void setAge(int age) {
        this.age = Bytes.toBytes(age);
    }

    /**
     * Setter for the email column
     * @param email
     */
    public void setEmail(String email){
        this.email = toBytes(email);
    }

    /**
     * Setter for the bff column
     * @param bff
     */
    public void setBff(String bff){
        this.bff = toBytes(bff);
    }

    /**
     * Setter for the other friends column. If one of the friends doesn't exist, we insert it in the database and set
     * its bff to the current person
     * @param othersList list of other friends
     * @throws IOException
     */
    public void setOthers(List<String> othersList) throws IOException {
        otherFriends = new HashSet<String>(othersList);
        String friends = "";
        for (String friend : otherFriends) {
            //if the the friend doesn't exist in the database, we create it and set its bff to the current person
            byte[] friendName = toBytes(friend);
            if (!this.exists(friendName)) {
                Put putFriend = new Put(friendName);
                putFriend.addColumn(FRIENDS, BFF_COL, this.name);
                this.table.put(putFriend);
                System.out.println("The person " + friendName + " has been correctly inserted in the database.\n");
            }
            friends = friends.concat(friends + this.SEP);
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
     * inserts a person in the table
     * @return true if the insertion goes well, false if the person already exists in the database
     * @throws IOException
     */
    public boolean addPerson() throws IOException {
        if(this.exists(this.name)){
            System.out.println("This person already exists in the database. " +
                    "To update its fields, use the update command instead.\n");
            return false;
        }
        else {
                //if the the bff doesn't exist in the database, we create it and set its bff to the current person
                if (!this.exists(this.bff)){
                Put putBff = new Put(this.bff);
                putBff.addColumn(FRIENDS, BFF_COL, this.name);
                this.table.put(putBff);
                System.out.println("The person " + this.bffStr + " has been correctly inserted in the database.\n");
            }
            Put putPerson = new Put(this.name);
            putPerson.addColumn(FRIENDS, BFF_COL, this.bff);
            if (this.others != null) {
                putPerson.addColumn(FRIENDS, OTHERS_COL, this.others);
            }
            if (this.age != null) {
                putPerson.addColumn(INFO, AGE_COL, this.age);
            }
            if (this.email != null) {
                putPerson.addColumn(INFO, EMAIL_COL, this.email);
            }
            table.put(putPerson);
            System.out.println("The person " + this.nameStr + " has been correctly inserted in the database.\n");
            return true;

        }
    }

    private byte[] toBytes(String string) {
        return Bytes.toBytes(string.toLowerCase().trim());
    }

}


