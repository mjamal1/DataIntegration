
import org.apache.solr.client.solrj.io.stream.TupleStream;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import java.io.IOException;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


public class app {
    app(final RepositoryConnection connection){

        final JFrame f=new JFrame("Healthcare Report for Decision Making");
        ///////////////////////////////////////////////////////////////////
        //Threshold number of physicians in county
        final JLabel countyphysLabel = new JLabel();
        countyphysLabel.setText("Critical PhysNum:");
        countyphysLabel.setBounds(10, 10, 150, 30);

        //Threshold number of infections in county
        final JLabel infcasesLabel = new JLabel();
        infcasesLabel.setText("Critical Inf-Cases:");
        infcasesLabel.setBounds(10, 45, 150, 30);

        //Threshold number of infections in county
        final JLabel outInpatientLabel = new JLabel();
        outInpatientLabel.setText("Critical NumServices:");
        outInpatientLabel.setBounds(400, 10, 150, 30);
        ///////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////
        //textfield critical number
        final JTextField countyphysTextField= new JTextField("");
        countyphysTextField.setBounds(160, 10, 100, 30);

        //textfield to enter phone number
        final JTextField infCasesTextField= new JTextField("");
        infCasesTextField.setBounds(160, 45, 100, 30);

        //textfield to enter critical number
        final JTextField outInpatientTextField= new JTextField("");
        outInpatientTextField.setBounds(560, 10, 100, 30);
        //////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////
        //query 1 submit button
        final JButton countyphysSearchButton =new JButton("Submit");
        countyphysSearchButton.setBounds(265,10,100, 30);
        //query 1 submit button
        final JButton infCasesSearchButton =new JButton("Submit");
        infCasesSearchButton.setBounds(265,45,100, 30);

        final JButton outInpatientSearchButton =new JButton("Submit");
        outInpatientSearchButton.setBounds(665,10,100, 30);


        String data[][]={ {" "}};
        String column[]={"Report Result"};
        //Table to display the info
        final JTable table = new JTable(data,column);
        table.setBounds(10,100,1200,300);
        JScrollPane sp = new JScrollPane();
        sp.setBounds(10,100,1200,440);
        sp.getViewport().add(table);
        sp.setVisible(true);
        //add to frame
        //f.add(label1);
        //f.add(phoneLabel);
        //f.add(rechargesSearchButton);
        f.add(infCasesTextField);
        f.add(infcasesLabel);
        f.add(infCasesSearchButton);
        f.add(outInpatientLabel);
        f.add(outInpatientSearchButton);
        f.add(outInpatientTextField);
        f.add(countyphysTextField);
        f.add(countyphysLabel);
        f.add(countyphysSearchButton);
        f.add(sp);
        f.setSize(1240,600);
        f.setLayout(null);
        f.setVisible(true);
        f.add(sp);
        f.revalidate();
        f.repaint();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        class theHandler implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(actionEvent.getSource()==infCasesSearchButton){
                    String numcases = infCasesTextField.getText();
                    CountyInfections.execute_query(connection, table,numcases);
                    f.revalidate();
                    f.repaint();
                }
                else if ((actionEvent.getSource()==countyphysSearchButton)){
                    String numcases = countyphysTextField.getText();
                    CountyHospitalsPhysicians.execute_query(connection, table,numcases);
                    f.revalidate();
                    f.repaint();
                }
                else if ((actionEvent.getSource()==outInpatientSearchButton)){
                    String numcases = outInpatientTextField.getText();
                    outInpatientmedicare.execute_query(connection, table,numcases);
                    f.revalidate();
                    f.repaint();
                }
            }
        }
        theHandler hand = new theHandler();
        countyphysSearchButton.addActionListener(hand);
        infCasesSearchButton.addActionListener(hand);
        outInpatientSearchButton.addActionListener(hand);

    }

    public static void main(String[] args)
            throws IOException
    {
        Repository repository = new SPARQLRepository("http://localhost:8080/sparql");
        repository.initialize();
        RepositoryConnection connection = repository.getConnection();

        new app(connection);
        connection.close();
}


}
