import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CountyInfections {
    public static void execute_query(RepositoryConnection connection, JTable table, String cases){

        DefaultTableModel tableModel = new DefaultTableModel();
        //Create array of Objects with size of column count from meta data
        Object[] row = new Object[1];
        tableModel.addColumn("counname");
        tableModel.addColumn("infection");
        tableModel.addColumn("year");
        tableModel.addColumn("numcases");



        String query = "PREFIX : <http://www.semanticweb.org/jinter/ontologies/2020/4/DIproject#>\n"+
                "SELECT ?counname ?disease ?year (SUM(?cases) AS ?totcases) \n"+
                "WHERE {\n"+
                "?coun a :County .\n"+
                "?infection a :Infectious_disease .\n"+
                " ?infection :InfectionInCounty ?coun .\n"+
                "?coun :CountyName ?counname .\n"+
                "?infection :InfectionCases ?cases .\n"+
                "?infection :InfectionGender ?gender .\n"+
                "?infection :InfectionDisease ?disease .\n"+
                "?infection :InfectionYear ?year .\n"+
                "}\n"+
                "GROUP BY ?counname ?disease ?year \n"+
                "HAVING (SUM(?cases) > "+cases+ ") \n"+
                "ORDER BY DESC(?totcases)";

        System.out.println("\n\n\nQuery: " + query + "\n\n");
        TupleQuery query_result = connection.prepareTupleQuery(query);
        //System.out.println(query_result);

        try (TupleQueryResult resultSet = query_result.evaluate()) {
            System.out.println("\n\n\nThis is the while\n");

            while (resultSet.hasNext()) {
                BindingSet bindingSet = resultSet.next();

                String rowString = bindingSet.toString();
                System.out.println("\n\n\nBinding Set\n");
                System.out.println(bindingSet);

                String counname = StringUtils.substringBetween(rowString, "counname=\"", "\";disease=");
                String infection = StringUtils.substringBetween(rowString, "disease=\"", "\";year=\"");
                String year = StringUtils.substringBetween(rowString, ";year=\"", "\"^^<http://www.w3.org/2001/XMLSchema#integer>;totcases");
                String numcases = StringUtils.substringBetween(rowString, ";totcases=\"", "\"^^<http://www.w3.org/2001/XMLSchema#double>");
                System.out.println(counname+"::"+infection+"::"+year+"::"+numcases);
                tableModel.addRow(new Object[]{counname,infection,year,numcases});
            }
            table.setModel(tableModel);
        }
    }
}

