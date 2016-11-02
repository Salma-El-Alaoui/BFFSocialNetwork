
/**
 * Created by salma on 31/10/2016.
 */

import org.apache.hadoop.hbase.client.Table;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.IOException;
import java.util.List;

abstract class Command {

    protected static Table table;

    public Command(Table newTable) {
        table = newTable;
    }

    public abstract boolean execute() throws IOException;
}


@Parameters(commandDescription = "add a person to the table")
class CommandAdd extends Command {

    @Parameter(names = {"--firstName", "-fn"}, arity = 1, required = true, description = "name of the person")
    private String name;

    @Parameter(names = {"--info:age", "-age"}, arity = 1,
            description = "age of the person, to be inserted in column family info")
    private int age;

    @Parameter(names = {"--info:email", "-email"}, arity = 1,
            description = "email of the person, to be inserted in column family info")
    private String email;

    @Parameter(names = {"--friends:bff", "-bff"}, arity = 1, required = true,
            description = "best friend of the person, to be inserted in column family friends")
    private String bff;

    @Parameter(names = {"--friends:others", "-others"}, variableArity = true,
            description = "other friend of the person, to be inserted in column family friends")
    private List<String> otherFriends;

    public CommandAdd(Table table) {
        super(table);
    }


    protected void fillModifiedFields(Person person) throws IOException{
        if(email != null && !email.trim().isEmpty())
            person.setEmail(email);
        if(age != 0)
            person.setAge(age);
       if(otherFriends != null && !otherFriends.isEmpty())
           person.setOthers(otherFriends);
    }

    @Override
    public boolean execute() throws IOException {
        Person person = new Person(this.name, this.bff, table);
        fillModifiedFields(person);
        return person.addPerson();
    }

}
