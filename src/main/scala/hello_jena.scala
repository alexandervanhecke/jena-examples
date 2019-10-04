
import _root_.java.lang.System
import java.io.ByteArrayInputStream

import _root_.org.apache.jena.examples.Utils
import _root_.org.apache.jena.rdf.model.ModelFactory

object HelloJena {
    def main(args: Array[String]) {
        val input = Utils.getResourceAsStream("data/data.ttl")

      val json =
          """
            |{
            |  "@context": "http://schema.org/",
            |  "@type": "Person",
            |  "@id": "http://schema.org/Person/250600AD-E9E3-4A3D-8842-271205260BE5",
            |  "name": "Jane Doe",
            |  "jobTitle": "Professor",
            |  "telephone": "(425) 123-4567",
            |  "url": "http://www.janedoe.com"
            |}
            |""".stripMargin
        val input2 = new ByteArrayInputStream(json.getBytes)

        val model = ModelFactory.createDefaultModel()
//        model.read(input, null, "TURTLE")
        model.read(input2, null, "JSON-LD")

        System.out.println("\n---- OUTPUT ----")
        model.write(System.out, "JSON-LD")
    }
}
