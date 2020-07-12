import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class outInpatientmedicare {
    public static void execute_query(RepositoryConnection connection, JTable table, String services){

        DefaultTableModel tableModel = new DefaultTableModel();
        //Create array of Objects with size of column count from meta data
        Object[] row = new Object[1];
        tableModel.addColumn("medicare provider");
        tableModel.addColumn("Provider Specialty");
        tableModel.addColumn("Inpatient services");
        tableModel.addColumn("Outpatient Services");



        String query = "PREFIX : <http://www.semanticweb.org/jinter/ontologies/2020/4/DIproject#>\n"+
        "SELECT ?name  ?specialty  (SUM(?inptotdis) AS ?totcases) (SUM(?services) AS ?totservices)\n"+
        "WHERE {\n"+
    	"?med a :Medicare .\n"+
         "           ?med :ProviderName ?name .\n"+
        "          ?med :ProviderSpecialty ?specialty .\n"+
        "           ?outp a :Outpatient .\n"+
        "           ?med :MedicareOutpatient ?outp .\n"+
        "           ?outp :OutpatientServices ?services .\n"+
        "           ?inp a :Inpatient .\n"+
        "           ?med :MedicareInpatient ?inp .\n"+
        "           ?inp :InpatientTotalDischarges ?inptotdis .\n"+
        "}\n"+
        "GROUP BY ?name ?specialty\n"+
        "HAVING (SUM(?inptotdis) > "+services+")\n"+
        "ORDER BY  DESC(?totcases) DESC(?totservices)\n";

        System.out.println("\n\n\nQuery: " + query + "\n\n");
        TupleQuery query_result = connection.prepareTupleQuery(query);
        System.out.println(query_result);

        try (TupleQueryResult resultSet = query_result.evaluate()) {

            while (resultSet.hasNext()) {
                BindingSet bindingSet = resultSet.next();

                String rowString = bindingSet.toString();
                System.out.println("\n\n\nBinding Set\n");
                System.out.println(bindingSet);

                String pname = StringUtils.substringBetween(rowString, "name=\"", "\";specialty=");
                String specialty = StringUtils.substringBetween(rowString, "specialty=\"", "\";totcases=\"");
                String totcases = StringUtils.substringBetween(rowString, ";totcases=\"", "\"^^<http://www.w3.org/2001/XMLSchema#double>;totservices");
                String totservices = StringUtils.substringBetween(rowString, ";totservices=\"", "\"^^<http://www.w3.org/2001/XMLSchema#integer>");
                System.out.println(pname+"::"+specialty+"::"+totcases+"::"+totservices);
                tableModel.addRow(new Object[]{pname,specialty,totcases,totservices});
            }
            table.setModel(tableModel);
        }
    }
}

