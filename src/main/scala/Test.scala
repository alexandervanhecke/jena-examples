
import java.io.StringReader
import java.time.LocalDate
import java.util.UUID
import java.util.function.Consumer

import org.apache.jena.datatypes.xsd.impl.XSDBaseNumericType
import org.apache.jena.datatypes.{BaseDatatype, RDFDatatype}
import org.apache.jena.datatypes.xsd.{XSDDatatype, XSDDateTime}
import org.apache.jena.query.{Dataset, DatasetFactory}
import org.apache.jena.rdf.model.{Model, ModelFactory, Resource, ResourceFactory, Statement}
import org.apache.jena.riot.adapters.RDFWriterRIOT
import org.apache.jena.riot.writer.RDFJSONWriter
import org.apache.jena.riot.{Lang, RDFFormat, RDFParser, RIOT}
import org.apache.jena.sparql.util.Context
import org.apache.jena.vocabulary.VCARD

object Test extends App {

  val personURI = "http://somewhere/JohnSmith"
  val givenName = "John"
  val familyName = "Smith"
  val fullName = givenName + " " + familyName


  val person2URI = "http://somewhere/JohnDoe"
  val fullName2 = "John Doe"

  val model: Model = ModelFactory.createDefaultModel

  val uuid = UUID.randomUUID()
  val dijkType = model.createResource("https://wegenenverkeer.data.vlaanderen.be/ns/abstracten#Dijk")
  val dijk = model.createResource(s"https://wegenenverkeer.data.vlaanderen.be/ns/abstracten#Dijk/$uuid", dijkType)

  val dijkPropertyHoogte = model.createProperty("https://wegenenverkeer.data.vlaanderen.be/ns/abstracten#Dijk.hoogte")

  val literal = ResourceFactory.createTypedLiteral("100", XSDDatatype.XSDint)

  val literalProperType = ResourceFactory.createTypedLiteral("100", new BaseDatatype("https://wegenenverkeer.data.vlaanderen.be/ns/implementatieelement#KwantWrdInMeterTAW"))

  dijk.addProperty(dijkPropertyHoogte, literalProperType)

  // indien je hier de constructor zonder URI gebruikt dan krijg je een blanko identifier voor het nested type
  // in feite is dat hier beter want een DijkTalud is geen ID, maar een type.
  val nestedResource = model.createResource("https://wegenenverkeer.data.vlaanderen.be/ns/abstracten#DijkTalud")
  nestedResource.addProperty(model.createProperty("https://wegenenverkeer.data.vlaanderen.be/ns/abstracten#DijkTalud.helling"), ResourceFactory.createTypedLiteral("500", XSDDatatype.XSDint))
  nestedResource.addProperty(model.createProperty("https://wegenenverkeer.data.vlaanderen.be/ns/abstracten#DijkTalud.notitie"), ResourceFactory.createPlainLiteral("dit is een notitie"))

  dijk.addProperty(model.createProperty("https://wegenenverkeer.data.vlaanderen.be/ns/abstracten#Dijk.dijkTalud"), nestedResource)

  val jenaCtx = new Context
  jenaCtx.set(RIOT.JSONLD_CONTEXT, "http://mygreat/json.ld")


    //  val johnSmith: Resource = model.createResource(personURI)
//  johnSmith.addProperty(VCARD.FN, fullName)
//  johnSmith.addLiteral(VCARD.Other, false)
//  johnSmith.addLiteral(VCARD.ADR, "mijnADRwaarde")
//  johnSmith.addProperty(VCARD.N, model.createResource().addProperty(VCARD.Given, givenName).addProperty(VCARD.Family, familyName))
//  val date = model.createTypedLiteral("2010-08-01", XSDDatatype.XSDdate)
//
//  val johnDoe: Resource = model.createResource(person2URI)
//
//  val dijkkant = model.createProperty("http://data.vlaanderen.be/Dijk#", "DijkKant")
//  model.setNsPrefix("dijkPrefix", "http://data.vlaanderen.be/Dijk#")
//  println(VCARD.Other.getNameSpace)
//  model.setNsPrefix("other", VCARD.Other.getNameSpace + VCARD.Other.getLocalName)
//  johnSmith.addProperty(dijkkant, "MijnDijkKant")
//  johnSmith.addProperty(VCARD.BDAY, date)


  //  val complexeProperty = model.createProperty("http://data.vlaanderen.be/ComplexeProperty#", "Complex")
//
//  val nestedResource = model.createResource("http://data.vlaanderen.be/DijkStuff")
//  nestedResource.addProperty(model.createProperty("http://data.vlaanderen.be/SimpleProperty#", "Simple"), "simpleValue")
//  complexeProperty.addProperty(model.createProperty("http://data.vlaanderen.be/ComplexeSimple#", "ComplexSimple"), nestedResource)


//  johnDoe.addProperty(VCARD.FN, fullName2)

  val writer = new RDFWriterRIOT("JSON-LD")
  writer.setProperty(RIOT.JSONLD_CONTEXT.toString, "https://my.great/json.ld")
//  writer.write(model, System.out, "")

  println(s"\n\n\n\nJSON-LD")
  model.write(System.out, "JSON-LD")
  println(s"\n\n\n\nRDF/JSON")
  model.write(System.out, "RDF/JSON")
//  model.write(System.out, "RDF/XML-ABBREV")

  // subject - predicate - object
  println(s"printen van het opgebouwde model")
  printStatements(model)

//  val foundResource: Resource = model.getResource(personURI)
//  println(foundResource.equals(johnSmith))
//  println(foundResource.getProperty(VCARD.N))

  val json =
    """
      |{
      |  "@id" : "https://wegenenverkeer.data.vlaanderen.be/ns/abstracten#Dijk/4bf75e2e-dc70-4c48-acf7-83c0c7b7a93b",
      |  "@type" : "https://wegenenverkeer.data.vlaanderen.be/ns/abstracten#Dijk",
      |  "Dijk.hoogte" : "100",
      |  "@context" : {
      |    "Dijk.hoogte" : {
      |      "@id" : "https://wegenenverkeer.data.vlaanderen.be/ns/abstracten#Dijk.hoogte",
      |      "@type" : "https://wegenenverkeer.data.vlaanderen.be/ns/implementatieelement#KwantWrdInMeterTAW"
      |    }
      |  }
      |}
      |
      |""".stripMargin

  println(s"printen van het uitgelezen model")
  val readModel = ModelFactory.createDefaultModel().read(new StringReader(json), null, "JSON-LD")
  printStatements(readModel)

  def printStatements(model: Model): Unit = model.listStatements().forEachRemaining(new Consumer[Statement] {
    override def accept(t: Statement): Unit = println(t)
  })

}
