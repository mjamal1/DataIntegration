import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.resultio.text.csv.SPARQLResultsCSVWriter;
import org.eclipse.rdf4j.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.IOException;
import java.io.InputStream;

/**
 * RDF Tutorial example 15: executing a simple SPARQL query on the database
 *
 * @author Jeen Broekstra
 */
public class Example15SimpleSPARQLQuery {

    public static void main(String[] args)
            throws IOException
    {
        // Create a new Repository.
        Repository db = new SailRepository(new MemoryStore());
        db.init();

        // Open a connection to the database
        try (RepositoryConnection conn = db.getConnection()) {


            // We do a simple SPARQL SELECT-query that retrieves all resources of type `ex:Artist`,
            // and their first names.
            String queryString = "PREFIX : <http://www.semanticweb.org/jinter/ontologies/2020/4/DIproject#>\n"+
                    "SELECT ?counname "+
                    "{\n"+
                    "?coun a :County .\n"+
                    "?coun :CountyName ?counname .\n"+
                    "}\n";
            System.out.println(queryString);
            TupleQuery query = conn.prepareTupleQuery(queryString);

            // A QueryResult is also an AutoCloseable resource, so make sure it gets closed when done.
            try (TupleQueryResult result = query.evaluate()) {
                System.out.println(result);
                // we just iterate over all solutions in the result...
                while (result.hasNext()) {

                    BindingSet solution = result.next();
                    System.out.println("solution:\n\n\n"+ solution);
                    // ... and print out the value of the variable binding for ?s and ?n
                    System.out.println("?counname = " + solution.getValue("counname"));
                    System.out.println("?physcount = " + solution.getValue("physcount"));
                }
            }
        }
        finally {
            // Before our program exits, make sure the database is properly shut down.
            db.shutDown();
        }
    }
}
