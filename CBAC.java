//********************         Context-Based Access Control          ********************//
//********************                  Written By                   ********************//
//********************           Mouiad Abid Hani AL Wahah           ********************//


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.StmtIteratorImpl;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.RDF;

public class CBAC {

public static final String INDENT = " ";

	
public static InfModel readOnt(String filepath){
	//String SOURCE = filepath;
	FileInputStream inputStream = null;
	OntModel model = ModelFactory.createOntologyModel( OntModelSpec.OWL_DL_MEM );
	try 
      {
         inputStream = new FileInputStream(filepath);
      } catch (FileNotFoundException e) {
         System.err.println("'" + filepath 
            + "' is an invalid input file.");
         
      }
	model.read(inputStream, filepath);
	printTripleData(model);
	InfModel infModel = ModelFactory.createInfModel( PelletReasonerFactory.theInstance().create(), model);
	    return infModel;
}

public void printindividualtofile (InfModel model, String outputfile)
{
   String outputFileName = outputfile;
   PrintWriter writer = null;
   //create an output print writer for the results
   try 
    {
      writer = new PrintWriter(outputFileName);
    } catch (FileNotFoundException e) {
    System.err.println("'" + outputFileName + "' is an invalid output file.");
    return;
   }
   //Iterate over the individuals, print statements about them
   ExtendedIterator iIndividuals = ((OntModel) model).listIndividuals();
   while(iIndividuals.hasNext())
    {
     Individual i = (Individual)iIndividuals.next();
     printIndividual(i, writer);
    }
   iIndividuals.close();
	   writer.close();
}

public static void printIterator(Iterator<?> i, String header) {
  System.out.println(header);
  for(int c = 0; c < header.length(); c++)
      System.out.print("=");
  System.out.println();
  
  if(i.hasNext()) {
        while (i.hasNext()) 
            System.out.println( i.next() );
  }       
  else
      System.out.println("<EMPTY>");
  
  System.out.println();
}

/**
* Print information about the individual
* @param i The individual to output
* @param writer The writer to which to output
*/
public static void printIndividual(
Individual i, PrintWriter writer)
{
//print the local name of the individual (to keep it terse)
writer.println("Individual: " + i.getLocalName());

//print the statements about this individual
StmtIterator iProperties = i.listProperties();
while(iProperties.hasNext())
{
   Statement s = (Statement)iProperties.next();
   writer.println("  " + s.getPredicate().getLocalName() 
      + " : " + s.getObject().toString());
}
iProperties.close();
writer.println();
}	

public static void printTripleData(Model model){
  System.out.println("No. of triples: " +
  countStatements(null, null, null, model));
  System.out.println("No. of subject: " +
  countSubjects(null, model));
  //System.out.println("No. of predicates: " +
  //countPredicates(null, model));
  System.out.println("No. of objects: " +
  countObjects(null, model));
  }
public static int countStatements(Resource s, Property p, RDFNode o, Model m){
 if(s == null && p == null && m == null){
 return getIteratorSize(m.listStatements());
 } else{
 return getIteratorSize(m.listStatements(s, p, o));
 }
 }
 public static int countSubjects(Resource s, Model m){
 if(s == null){
 return getIteratorSize(m.listSubjects());
 } else{
 return getIteratorSize(m.listStatements(s, null, (RDFNode)null));
 }
 }
 public static int getIteratorSize(Iterator i){
	  int count = 0;
	  while(i.hasNext()){
	  count++;
	  i.next();
	  }
	  return count;
	  }

public static int countObjects(RDFNode o, Model m){
if(o == null){
	  return getIteratorSize(m.listObjects());
	  } else{
	  return getIteratorSize(m.listStatements(null, null, o));
	  }
}
public static void printType(Model model){
	  NodeIterator ni = model.listObjectsOfProperty(RDF.type);
	  while(ni.hasNext()){
	  RDFNode o = (RDFNode) ni.next();
	  System.out.println(o.toString());
	  ResIterator ri = model.listResourcesWithProperty(RDF.type, o);
	  while(ri.hasNext()){
	  System.out.println(INDENT + ri.next());
	  }
	  }
	  }
public static void ListModelTriples (Model M){
	 StmtIterator it = M.listStatements();
	 while ( it.hasNext() )
	  {
		Statement stmt = it.nextStatement();
		Resource subject = stmt.getSubject();
		Property predicate = stmt.getPredicate();
		RDFNode object = stmt.getObject();
		System.out.println( subject.toString() + " " + predicate.toString() + " " + object.toString() );
	  }

	}

public static ResultSet query(String qstring, Model M){

	com.hp.hpl.jena.query.Query query = QueryFactory.create(qstring);
	QueryExecution qe = QueryExecutionFactory.create(query, M);
	return qe.execSelect();
}
public static void main (String args[]) {
	
	String filepath = "C:/Users/Mouiad/Desktop/OWL ONTOLOGIES/CBAC-1/CBAC2.owl";/*//*"C:/Users/Mouiad/Desktop/OWL ONTOLOGIES/CBAC-1/CBAC.owl";*"C:/Users/Mouiad/Desktop/OWL ONTOLOGIES/C3-2018/CBAC-Ontology.owl";/*"C:/Users/Mouiad/Desktop/OWL ONTOLOGIES/C3-2018/acpr.owl";/*"C:/Users/Mouiad/Desktop/OWL ONTOLOGIES/C3-2018/ssn-noDUL.owl"*//*"C:/Users/Mouiad/Desktop/OWL ONTOLOGIES/C3-2018/iot.owl";*/
	String outputFileName = "C:/Users/Mouiad/Desktop/filed4.owl";
	   PrintWriter writer = null;
	   
	   //create an output print writer for the results
	   try 
	    {
	      writer = new PrintWriter(outputFileName);
	    } catch (FileNotFoundException e) {
	    System.err.println("'" + outputFileName + "' is an invalid output file.");
	    return;
	   }
	InfModel M= null;
	//final long startTime = System.currentTimeMillis();
	M = readOnt(filepath);
	//long y = M.size();
	//M.write(writer,"N3");
	//ListModelTriples (M);
	//final long endTime = System.currentTimeMillis();
	//System.out.println("Total Execution Time for DL Reasoning: " + (endTime - startTime) );
	//System.out.println(y);
	//Reasoner reasoner = new GenericRuleReasoner(Rule.rulesFromURL("C:/Users/Mouiad/Desktop/Need For Context/GenericRules11.txt"));
	//InfModel M2 = ModelFactory.createInfModel(reasoner, M );
	
	String qstring = "prefix cc:      <http://creativecommons.org/ns#>" +
			"prefix iot-lite:  <http://purl.oclc.org/NET/UNIS/fiware/iot-lite#>" +
			"prefix qu:      <http://purl.org/NET/ssnx/qu/qu#>" +
			"prefix owl:     <http://www.w3.org/2002/07/owl#>" +
			"prefix ns:      <http://creativecommons.org/ns#>" +
			"prefix xsd:     <http://www.w3.org/2001/XMLSchema#>" +
			"prefix skos:    <http://www.w3.org/2004/02/skos/core#>" +
			"prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#>" +
			"prefix m3-lite:  <http://purl.org/iot/vocab/m3-lite#>" +
			"prefix ssn:     <http://purl.oclc.org/NET/ssnx/ssn#>" +
			"prefix geo:     <http://www.w3.org/2003/01/geo/wgs84_pos#>" +
			"prefix dct:     <http://purl.org/dc/terms/>" +
			"prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
			"prefix terms:   <http://purl.org/dc/terms/>" +
			"prefix qu-rec20:  <http://purl.org/NET/ssnx/qu/qu-rec20#>" +
			"prefix wgs84_pos:  <http://www.w3.org/2003/01/geo/wgs84_pos#>" +
			"prefix time:    <http://www.w3.org/2006/time#>" +
			"prefix dc:      <http://purl.org/dc/elements/1.1/>" +
			"prefix CBAC1:   <http://www.semanticweb.org/mouiad/Me/CBAC1#>" +
			"SELECT DISTINCT ?r ?o ?w  ?t ?d ?i" + 				
				"WHERE {" +
							"?r rdf:type CBAC1:Request."+
							"?r CBAC1:hasObject ?o."+
							"?o CBAC1:hasOwner ?w."+
							"?r CBAC1:hasType ?t."+
							"?r CBAC1:hasDecision CBAC1:D2.}";
							//"?d CBAC1:hasImpact ?i." +
							//"?i rdf:type ?k}";				
	ResultSet results = query(qstring, M);
	for ( ; results.hasNext() ; ) {
	    QuerySolution soln = results.nextSolution();
	    System.out.println();
	    System.out.print("Request"+"    "+"Object"+"          "+"Owner" +"           "+"Type"+"       "+"Decision"+"    "+"Impatc");
	    System.out.println();
	    System.out.print("-------"+"    "+"-------"+"        "+"------"+"           "+"-------"+"     "+"------"+"    "+"------");
	    System.out.println();
	    System.out.print("  "+soln.getResource("r").getLocalName()/*"/*+soln.getResource("s").getLocalName()*/);
	    System.out.print("    "+soln.getResource("o").getLocalName());
	    System.out.print("      "+soln.getResource("w").getLocalName());
	    System.out.print("          "+soln.getResource("t").getLocalName());
	    System.out.print("        "+soln.getResource("d").getLocalName());
	    //System.out.print("       "+soln.getResource("i").getLocalName());
	    //System.out.print("       "+soln.getResource("k").getLocalName());

	}

	//M2.write(writer, "RDF/XML");
	//int g = (int) M2.size();
	//System.out.println(g);
	
}

}

