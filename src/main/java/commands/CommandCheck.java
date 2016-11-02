package commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import model.Person;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;

/**
 * Created by salma on 02/11/2016.
 * Command to check whether all the friends in the others column exist in the table (are row Ids).
 */
@Parameters(commandDescription = "check consistency between row key and others column for a person")
public class CommandCheck extends Command {

    @Parameter(names = {"--firstName", "-fn"}, arity = 1, required = true, description = "name of the person, row key")
    private String name;

    public CommandCheck(Table table) {
        super(table);
    }


    @Override
    public boolean execute() throws IOException {
        if(!name.trim().isEmpty()) {
            Person person = new Person(name, table);
            return person.checkOthersConsistency();
        }
        else {
            System.out.println("Error: Invalid option argument.\n");
            return false;
        }
    }
}