
package commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.apache.hadoop.hbase.client.Table;
import java.io.IOException;
import java.util.List;

import model.Person;

/**
 * Created by salma on 02/11/2016.
 * The put command allows the user to insert a new person in the table or updates an existing one.
 * The mandatory fields are the firstname of the person, which is the row key, and the bff of the person, which is
 * stored in the column family friends
 * Other fields are optional
 * To guarantee the consistency of the database:
 * if a bff or an other friend of the person that is currently
 * added doesn't exist in the database, we create it and set its bff to the current person.
 * Since the row ids are unique, a person can't have the same name as the bff.
 */
@Parameters(commandDescription = "adds a new person to the table or updates an existing one")
public class CommandPut extends Command {

    //Attributes that are filled by the options of the command
    @Parameter(names = {"--firstName", "-fn"}, arity = 1, required = true, description = "name of the person, row key")
    private String name;

    @Parameter(names = {"--info:age", "-age"}, arity = 1,
            description = "age of the person, to be inserted in column family info")
    private Integer age;

    @Parameter(names = {"--info:email", "-email"}, arity = 1,
            description = "email of the person, to be inserted in column family info")
    private String email;

    @Parameter(names = {"--friends:bff", "-bff"}, arity = 1, required = true,
            description = "best friend of the person, to be inserted in column family friends")
    private String bff;

    @Parameter(names = {"--friends:others", "-others"}, variableArity = true,
            description = "other friend(s) of the person (separated by a space), to be inserted in column family friends")
    private List<String> otherFriends;


    /**
     * Constructor
     * @param table Hbase table where the operations are made
     */
    public CommandPut(Table table) {
        super(table);
    }


    /**
     * Checks if optional fields have been modified by the command and sets the attributes of the person
     * with the corresponding values
     * @param person person to be added
     * @throws IOException
     */
    private void fillModifiedFields(Person person) throws IOException {
        if(email != null && !email.trim().isEmpty())
            person.setEmail(email);
        if(age != null)
            person.setAge(String.valueOf(age));
        if(otherFriends != null && !otherFriends.isEmpty())
            person.setOthers(otherFriends);
    }


    @Override
    /**
     * Adds or updates a person in the table
     */
    public boolean execute() throws IOException {
        if(!name.trim().isEmpty() && !bff.trim().isEmpty()) {
            Person person = new Person(this.name, this.bff, table);
            fillModifiedFields(person);
            return person.putPerson();
        }
        else{
            System.out.println("Error: Invalid option argument.\n");
            return false;

        }


    }

}
