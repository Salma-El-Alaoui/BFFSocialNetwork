
package commands;

import org.apache.hadoop.hbase.client.Table;
import com.beust.jcommander.Parameter;
import java.io.IOException;
import java.util.List;

/**
 * Created by salma on 02/11/2016.
 * This abstract class models the commands which are provided by the command line tool
 */
public abstract class Command {

    // Hbase table where the operations are made
    protected static Table table;

    //Attributes that are filled by the options of the command
    @Parameter(names = {"--firstName", "-fn"}, arity = 1, required = true, description = "name of the person, row key")
    protected String name;

    @Parameter(names = {"--info:age", "-age"}, arity = 1,
            description = "age of the person (>= 1), to be inserted in column family info")
    protected int age;

    @Parameter(names = {"--info:email", "-email"}, arity = 1,
            description = "email of the person, to be inserted in column family info")
    protected String email;

    @Parameter(names = {"--friends:bff", "-bff"}, arity = 1, required = true,
            description = "best friend of the person, to be inserted in column family friends")
    protected String bff;

    @Parameter(names = {"--friends:others", "-others"}, variableArity = true,
            description = "other friend(s) of the person (separated by a space), to be inserted in column family friends")
    protected List<String> otherFriends;

    /**
     * Constructor
     */
    public Command(Table newTable) {
        table = newTable;
    }


    /**
     * Executes the operation provided by the command and is implemented in each subclass
     */
    public abstract boolean execute() throws IOException;
}
