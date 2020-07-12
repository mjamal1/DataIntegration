import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CountyHospitalsPhysicians {
    public static void execute_query(RepositoryConnection connection, JTable table, String physnum){

        DefaultTableModel tableModel = new DefaultTableModel();
        //Create array of Objects with size of column count from meta data
        Object[] row = new Object[1];
        tableModel.addColumn("counname");
        tableModel.addColumn("hoscount");
        tableModel.addColumn("physcount");
        tableModel.addColumn("spcount");



        String query = "PREFIX : <http://www.semanticweb.org/jinter/ontologies/2020/4/DIproject#>\n"+
        "SELECT ?counname \n"+"" +
                "(COUNT(DISTINCT ?hos) AS ?hoscount)\n"+
                "(COUNT(DISTINCT ?phys) AS ?physcount)\n"+
                "(COUNT(DISTINCT ?sp) AS ?spcount)\n"+
        "WHERE {\n"+
                    "?phys a :Physician .\n"+
                    "?phys :PhysicianSpecialty ?sp .\n"+
                    "?hos a :Hospital .\n"+
                    "?phys :WorksIn ?hos .\n"+
                    "?coun a :County .\n"+
                    "?hos :HospitalInCounty ?coun .\n"+
                    "?coun :CountyName ?counname .\n"+
        "}\n"+
        "GROUP BY ?counname\n"+
        "HAVING (COUNT(DISTINCT ?phys) <"+physnum+")\n"+
        "ORDER BY DESC(?hoscount) DESC(?physcount)";

        System.out.println("\n\n\nQuery: " + query + "\n\n");
        TupleQuery query_result = connection.prepareTupleQuery(query);
        //System.out.println(query_result);

        try (TupleQueryResult resultSet = query_result.evaluate()) {


            System.out.println("\n\n\nThis is the while\n");

            while (resultSet.hasNext()) {
                BindingSet bindingSet = resultSet.next();

                String rowString = bindingSet.toString();
                System.out.println("\n\n\nBinding Set\n");
                //System.out.println(bindingSet);

                String counname = StringUtils.substringBetween(rowString, "counname=\"", "\";hoscount=");
                String hoscount = StringUtils.substringBetween(rowString, "hoscount=\"", "\"^^<http://www.w3.org/2001/XMLSchema#integer>;physcount=\"");
                String physcount = StringUtils.substringBetween(rowString, ";physcount=\"", "\"^^<http://www.w3.org/2001/XMLSchema#integer>;spcount");
                String spcount = StringUtils.substringBetween(rowString, ";spcount=\"", "\"^^<http://www.w3.org/2001/XMLSchema#integer>");
                System.out.println(counname+"::"+physcount+"::"+hoscount+"::"+spcount);
                tableModel.addRow(new Object[]{counname,hoscount,physcount,spcount});
            }
            table.setModel(tableModel);
        }
    }
}
