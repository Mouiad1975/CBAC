//********************         Context-Based Access Control          ********************//
//********************                  Written By                   ********************//
//********************           Mouiad Abid Hani AL Wahah           ********************//
//********************  his Program is provided as is with No Warranty ******************//
//********************          To run This Program, You have to have the following items installed ***********//
//********************          Java jdk version 1.7 or later ****************//
//********************          You have to include Jena Library 2.4 or later in the project path ************//



import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.XSD;

import org.mindswap.pellet.jena.PelletReasonerFactory;

public class CBAC {

	public static InfModel DL_Inference(String FilePath){
		
		String FILESOURCE = FilePath;
		//** Creating Pellet Reasoner **//
		Reasoner reasoner = PelletReasonerFactory.theInstance().create();
		//** Creating an Empty Model **//
		Model emptyModel = ModelFactory.createDefaultModel( );
		//System.out.println(emptyModel.size());
		//** Creating an Inferencing Model Using Pellet Reasoner **//
		InfModel M = ModelFactory.createInfModel( reasoner, emptyModel );
		FileManager.get().readModel(M, FILESOURCE);
		//System.out.println(M.size());
		return M;
	}

	public static InfModel LP_Inference(InfModel M, String FilePath){
	Reasoner reasoner = new GenericRuleReasoner(Rule.rulesFromURL(FilePath));
	InfModel M2 = ModelFactory.createInfModel(reasoner,M);
	return M2;
	}

	
	
	public static void SaveModeltoFile (InfModel model, String FilePath, String Style)
	   {
		   String OutputFileName = FilePath;
		   PrintWriter writer = null;
		   //** Creating an Output Writer for Inference Model **//
		   try 
		    {
		      writer = new PrintWriter(OutputFileName);
		    } catch (FileNotFoundException e) {
		    System.err.println("'" + OutputFileName + "' is an invalid output file.");
		    return;
		   }
		   model.write(writer, Style);
	   }

	//** Get the Query from a File and Execute It **//
	public static void GetQueryFromFile(String FilePath, InfModel M){
		Query query = QueryFactory.read(FilePath);
		
		// Execute the Query and Get the Results
		QueryExecution ExecutionQ = QueryExecutionFactory.create(query, M);
		ResultSet Results =  ExecutionQ.execSelect();
		
		// Display Query Results    
		ResultSetFormatter.out(System.out, Results, query);
		ExecutionQ.close();
	}

	//** Get the Query from a String and Execute It **//
	public static ResultSet selectQuery(InfModel M, String QueryString){
		Query query = QueryFactory.create(QueryString);
		// Execute the Query and Get the Results
		QueryExecution ExecutionQ = QueryExecutionFactory.create(query, M);
		ResultSet Results =  ExecutionQ.execSelect();
		ResultSetFormatter.out(System.out, Results, query);
		ExecutionQ.close();
		return Results;
	}

	public static ResultSet query(String QueryString, Model M){
		
		com.hp.hpl.jena.query.Query query = QueryFactory.create(QueryString);
		QueryExecution qe = QueryExecutionFactory.create(query, M);
		return qe.execSelect();
	}

	public static Model ReadMFromFile2(String Filename) throws IOException{
		  Model model = ModelFactory.createDefaultModel();
		  model.read(Filename);
		  return model;
		  }
	 public OntModel AddDataFromFile(String Filename, String NameSpace) throws IOException{
		 	OntModel Model = null;
			String filename = Filename;
		 	String namespace = NameSpace;
		 	System.out.println("Loading From Ontoloy Instance File");
				InputStream inFoafInstance = com.hp.hpl.jena.util.FileManager.get().open(filename);
				Model.read(inFoafInstance,namespace);
				inFoafInstance.close();
				return Model;
			}
		 
		 public Model ReadMFromFile(String Filename) throws IOException{
		  Model model = ModelFactory.createDefaultModel();
		  model.read(Filename);
		  Reasoner reasoner = new GenericRuleReasoner( Rule.rulesFromURL( "C:/Users/Mouiad/Desktop/ToTo/jena-reasoning-with-rules-master/jena-reasoning-with-rules-master/rules.txt" ) );
		  InfModel infModel = ModelFactory.createInfModel(reasoner, model);	 	
		  return infModel;
		 }
		 
		public void ListModelTriples (Model M){
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
		 
		 
		 
		 
		 public OntModel AddClass (OntModel Model, String C, String NameSpace){ 
		 @SuppressWarnings("unused")
		 OntClass CLS = Model.createClass(NameSpace + C);
		 CLS = null;
		 return Model;
		}
			
		public OntModel AddObjectProperty (OntModel Model, String P, String NameSpace, String D, String R){ 
		 ObjectProperty Prop = Model.createObjectProperty(NameSpace + P);
		 OntClass D1 = Model.getOntClass(NameSpace + D);
		 OntClass R1 = Model.getOntClass(NameSpace + R);
		 Prop.setDomain(D1);
		 Prop.setRange(R1);
		 return Model;
		}

		 public OntModel AddDataProperty (OntModel Model, String P, String NameSpace, String D, String kind){ 
		 OntClass D1;
		 DatatypeProperty Pr1;
		 OntModel M = Model; 
		 switch (kind){
		 case "integer":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.integer);
		  break;
		 case "string":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.xstring);
		  break;
		 case "dateTime":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.dateTime);
		  break;
		 case "date":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.date);
		  break;
		 case "time":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.time);
		  break;
		 case "anyURI":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.anyURI);
		  break;
		 case "xboolean":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.xboolean);
		  break;
		 case "xfloat":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.xfloat);
		  break;
		 case "xdouble":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.xdouble);
		  break;
		 case "xlong":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.xlong);
		  break;
		  default: 
		 }
		 Model = M;
		 return Model;
		}
				
			
			
		public OntModel CreateOntology(String prefix, String NameSpace){
				
		 OntModel Model = ModelFactory.createOntologyModel();
		 Model.setNsPrefix(prefix, NameSpace);
		 return Model;
		}
			
		public OntModel AddSubClass(OntModel Model, String SC, String C, String NameSpace){
				
		 OntClass CLS = Model.getOntClass(NameSpace + C);
		 OntClass SCLS = Model.createClass(NameSpace + SC);
		 SCLS.addSubClass(CLS);
		 return Model;
		}

		public OntModel AddIndividual(OntModel Model, String I, String C, String NameSpace){
		 OntClass CLS = Model.getOntClass(NameSpace + C);
		 @SuppressWarnings("unused")
		 Individual Ind = Model.createIndividual(NameSpace + I, CLS);
		 Ind = null;
		 return Model;
		}
		public OntModel AddStatement (OntModel Model, String P, String NameSpace, String D, String kind){ 
		 	
		 OntClass D1;
		 DatatypeProperty Pr1;
		 OntModel M = Model; 
		 switch (kind){
		 case "integer":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.integer);
		  break;
		 case "string":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.xstring);
		  break;
		 case "dateTime":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.dateTime);
		  break;
		 case "date":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.date);
		  break;
		 case "time":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.time);
		  break;
		 case "anyURI":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.anyURI);
		  break;
		 case "xboolean":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.xboolean);
		  break;
		 case "xfloat":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.xfloat);
		  break;
		 case "xdouble":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.xdouble);
		  break;
		 case "xlong":
		  Pr1 = M.createDatatypeProperty(NameSpace + P);
		  D1 = M.getOntClass(NameSpace + D);
		  Pr1.addDomain(D1);
		  Pr1.addRange(XSD.xlong);
		  break;
		  default: 
		 }
		 Model = M;
		 return Model;
		 }
				
		public List<OntClass> UnionClasses(List<OntClass> result, OntResource R){ 

		 if (R.isClass()){ //Check if it's an OntClass 
		  OntClass CLS = R.asClass();      
		  if (CLS.isUnionClass()){
		   //Check if it's a Union class; if so, add the separate classes to result 
		   UnionClass U = CLS.asUnionClass(); 
		   ExtendedIterator E = U.listOperands(); 
		   while (E.hasNext()){ 
			OntClass CL = (OntClass) E.next();       
			result.add(CL); 
		   } 
		  } 
		 else 
		  result.add(CLS); 
		 } 
		return result;
		} 

			
		public boolean isClass(OntModel Model, String label) { 
		 OntClass c = Model.getOntClass(label); 
		 if (c != null) 
		  return true; 
		 return false; 
		} 
			 
		public boolean isDataProperty(OntModel Model, String label) { 
		 DatatypeProperty P = Model.getDatatypeProperty(label); 
		 if (P != null) 
		  return true; 
		 return false; 
		} 
				   
		public boolean isObjectProperty(OntModel Model, String label) { 
		 ObjectProperty P = Model.getObjectProperty(label); 
		 if (P != null) 
		  return true; 
		 return false; 
		}
			
		public List<String> ListSubClasses(OntModel Model, String C, String NameSpace){
		 List<String> L = new ArrayList<String>();
		 OntClass CLS = Model.getOntClass(NameSpace + C); 
		 System.out.println("The SubClasses Are:");
		 for(ExtendedIterator i=CLS.listSubClasses(); i.hasNext();) { 
		  OntClass Class=(OntClass)i.next(); 
		  L.add(Class.getURI());
		  /*System.out.println("The SubClass:" +*/
		 }
		return L;
		}
			 
			
		/** Retrieves all properties in the ontology  * @return Iterator with properties 
		 **/ 
		public Iterator<OntProperty> getProperties(OntModel Model){ 
		 return Model.listOntProperties(); 
		} 
			  
		/** Retrieves all properties in the ontology  * @return Iterator with properties 
		 **/ 
		public ExtendedIterator GetDeclaredProperties(OntModel Model) 
		 { 
		  return Model.listOntProperties();
		 } 
			  
		/** Retrieves all classes in the ontology  * @return Iterator with classes 
		 **/ 
		public Iterator<OntClass> GetModelClasses(OntModel Model){ 
		 return Model.listNamedClasses();
		}

		/** Retrieves all individuals in the ontology  * @return Iterator with individuals 
		 **/ 

		public static void ListIndividuals(OntModel Model, String C, String NameSpace){
		 OntClass CLS = Model.getOntClass(NameSpace + C);
		 ExtendedIterator iterator = Model.listIndividuals(CLS); 
		 while (iterator.hasNext()) { 
		  System.out.println(iterator.next()); 
		 }
		}

		/** 
		* List all (indirect) parents of an ontology class That is, list its 
		* parent, its parent's parent, etc. 
		**/ 
		public static Collection<OntClass> listSubClasses1(OntClass C) { 
		 Collection<OntClass> ancestors = new ArrayList<OntClass>(); 
		 @SuppressWarnings("unchecked")
		 Iterator<OntClass> superClassIterator = C.listSuperClasses(); 
		 while (superClassIterator.hasNext()) { 
		  OntClass superClass = superClassIterator.next(); 
		  ancestors.add(superClass); 
		  ancestors.addAll(listSubClasses1(superClass)); 
		 } 
		 return ancestors; 
		} 

		/** 
		* List all (indirect) parents of an ontology class That is, list its 
		* parent, its parent's parent, etc. 
		**/ 
		public static Collection<OntClass> listSubClasses(OntModel Model, String C, String NameSpace){ //OntClass ontClass) { 
		 OntClass CLS = Model.getOntClass(NameSpace + C);
		 Collection<OntClass> ancestors = new ArrayList<OntClass>(); 
		 ancestors = listSubClasses1(CLS);
		 return ancestors; 
		} 

		/** 
		 * List all (indirect) descendants of an ontology class That is, list its 
		 * children, its children's children, etc. 
		 **/ 
		public static Collection<OntClass> listSuperClasses1(OntClass ontClass) { 
		 Collection<OntClass> descendants = new ArrayList<OntClass>(); 
		 @SuppressWarnings("unchecked")
		 Iterator<OntClass> subClassIterator = ontClass.listSubClasses(); 
		 while (subClassIterator.hasNext()) { 
		  OntClass subClass = subClassIterator.next(); 
		  descendants.add(subClass); 
		  descendants.addAll(listSuperClasses1(subClass)); 
		 } 
		 return descendants; 
		} 
		public static Collection<OntClass> listSuperClasses(OntModel Model, String C, String NameSpace){ 
		 OntClass CLS = Model.getOntClass(NameSpace + C);
		 Collection<OntClass> descendants = new ArrayList<OntClass>(); 
		 descendants = listSuperClasses1(CLS);
		 return descendants;
		}
			  	
		public static void printCollection(Collection<OntClass> C) {
		 Iterator<OntClass> i = C.iterator();
		 while (i.hasNext()) {
		  Object O = i.next();
		  System.out.println(O);
		 }
		} 

		 	
		// Given an Individual, Find The Classes It Belongs To
		@SuppressWarnings("rawtypes")
		public ArrayList<String> GetClassTypesOfIndividual(OntModel Model, String I, String NameSpace){
		 Individual Ind = Model.getIndividual(NameSpace+I);   
		 ArrayList<String> results = new ArrayList<>();
		 ExtendedIterator classes = Ind.listOntClasses(true);
		  while (classes.hasNext()) {
		   OntClass thisClass = (OntClass) classes.next();
		   results.add(thisClass.getURI());
		  }
		return results;
		}
			

		public ArrayList<Property> GetOutGoingPropertiesOfIndividual(OntModel Model, String I, String NameSpace){
			        
		 ArrayList<Property> results = new ArrayList<>();
		 Individual Ind = Model.getIndividual(NameSpace+I);
		 StmtIterator iter = Ind.listProperties();
		 while (iter.hasNext()) {
		  Statement st = iter.next();
		  Property prop = st.getPredicate();
		  results.add(prop);
		 }
		 return results;
		}
		    
		public static void PrintModelClasses(Iterator<OntClass> LC){
		 
		 while (LC.hasNext()) {
		  Object O = LC.next();
		  System.out.println(O);
		 }
		}
			  	
		public static void PrintModelProperties(Iterator<OntProperty> LP){
			  	    
		 while (LP.hasNext()){
		  Object O = LP.next();
		  System.out.println(O);
		 }
		}	  	

		/**
		 * Print information about the individual
		 * param(1) i The individual to output
		 * param(2) writer The writer to which to output
		 **/

		public static void printIndividual(Individual i, PrintWriter writer)
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


		@SuppressWarnings("resource")
		public void InferenceFromFile(String inputFileName, String inputFileFormat, String outputFileName, String reasoningLevel)
		 {
		  /** Create an Input Stream for The Input File  **/

		@SuppressWarnings("unused")
		FileInputStream inputStream = null;
		@SuppressWarnings("unused")
		PrintWriter writer = null;
		  try
		   {
		    inputStream = new FileInputStream(inputFileName);
		   } catch (FileNotFoundException e){
			  System.err.println("'" + inputFileName + "' is an invalide file.");
			  return;
		     }
		  /** Create an Output Stream for The Result File  **/
		  try
		   {
			writer = new PrintWriter(outputFileName);  
		   } catch (FileNotFoundException e){
			  System.err.println("'" + outputFileName + "' is an invalide file.");
			  return;
		     }
		   /** Create The Appropriate Jena Model **/
		  OntModel M = null;
		  if("none".equals(reasoningLevel.toLowerCase()))
		   {
			M = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM); 
		   }
		   else if("rdfs".equals(reasoningLevel.toLowerCase()))
		    {
			   M = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);   
		    }
		   else if("owl".equals(reasoningLevel.toLowerCase()))
		   {
			   Reasoner reasoner = PelletReasonerFactory.theInstance().create(); 
			   Model infModel = ModelFactory.createInfModel(reasoner, ModelFactory.createDefaultModel());
			      
		   }
		 }	
	
		
	
	public static void main (String args[]) throws IOException {
		
		Model M1;
		String FilePath1 = /*"C:/Users/Mouiad/Desktop/OWL ONTOLOGIES/Learning/GGG.owl"*/"C:/Users/Mouiad/Desktop/ZZ/app2/app2.owl";/*"C:/Users/Mouiad/Desktop/Inference Folder/app1/app1.owl";*/
		String FilePath2 = "C:/Users/Mouiad/Desktop/Need For Context/Generic-Jena-Rules/Generic-JenaRules7.txt";
		String QString;
		long startTime1 = System.currentTimeMillis();
		InfModel M = DL_Inference(FilePath1);
		long stopTime1 = System.currentTimeMillis();
		long startTime2 = System.currentTimeMillis();
		InfModel M2 = LP_Inference(M,FilePath2);
		long stopTime2 = System.currentTimeMillis();
		QString = "C:/Users/Mouiad/Desktop/Need For Context/Queries/Q6_1.rq";
		long startTime3 = System.currentTimeMillis();
		GetQueryFromFile(QString,M);
		long stopTime3 = System.currentTimeMillis();
		System.out.print("DL-INFERENCE TIME IS:");
		//M.write(System.out, "N3");
		System.out.println(stopTime1-startTime1);
		System.out.print("PL-INFERENCE TIME IS:");
		System.out.println(stopTime2-startTime2);
		System.out.print("SPAEQL QUERY TIME IS:");
		System.out.println(stopTime3-startTime3);
		//M1 = ReadMFromFile("C:/Users/Mouiad/Desktop/zz2.owl");
		//M2.write(System.out, "N3");
		//System.out.println(M.size());
		//SaveModeltoFile(M,"C:/Users/Mouiad/Desktop/deductions2.owl","Turtle");
		
		

	}
}
