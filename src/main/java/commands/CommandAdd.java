
package commands;

import com.beust.jcommander.Parameters;
import org.apache.hadoop.hbase.client.Table;
import java.io.IOException;
import model.Person;

/**
 * Created by salma on 02/11/2016.
 * The add command allows the user to insert a new person in the table
 * The mandatory fields are the firstname of the person, which is the row key, and the bff of the person, which is
 * stored in the column family friends
 * Other fields are optional
 * To guarantee the consistency of the database:
 * if a bff or an other friend of the person that is currently
 * added doesn't exist in the database, we create it and set its bff to the current person.
 *
 */
@Parameters(commandDescription = "add a person to the table")
public class CommandAdd extends Command {

    /**
     * Constructor
     * @param table Hbase table where the operations are made
     */
    public CommandAdd(Table table) {
        super(table);
    }


    /**
     * Checks if optional fields have been modified by the command and sets the attributes of the person
     * with the corresponding values
     * @param person person to be added
     * @throws IOException
     */
    protected void fillModifiedFields(Person person) throws IOException {
        if(email != null && !email.trim().isEmpty())
            person.setEmail(email);
        if(age != 0)
            person.setAge(age);
        if(otherFriends != null && !otherFriends.isEmpty())
            person.setOthers(otherFriends);
    }


    @Override
    /**
     * Adds a new person to the table
     */
    public boolean execute() throws IOException {
        Person person = new Person(this.name, this.bff, table);
        fillModifiedFields(person);
        return person.addPerson();
    }

}
