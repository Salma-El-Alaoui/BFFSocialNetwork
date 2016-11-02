package commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import model.Person;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;

/**
 * Created by salma on 02/11/2016.
 * Command allowing to get an already existing person from the table
 */
@Parameters(commandDescription = "get person from to the table and display its fields")
public class CommandGet extends Command {

    //Attributes that are filled by the options of the command
    @Parameter(names = {"--firstName", "-fn"}, arity = 1, required = true, description = "name of the person, row key")
    private String name;


    public CommandGet(Table table){
        super(table);
    }

    @Override
    public boolean execute() throws IOException {

        if(!name.trim().isEmpty()) {
            Person person = new Person(name, table);
            return person.getPerson();
        }
        else {
            System.out.println("Error: Invalid option argument.\n");
            return false;
        }
    }

}
