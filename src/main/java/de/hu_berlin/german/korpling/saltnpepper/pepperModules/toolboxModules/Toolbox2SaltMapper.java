package de.hu_berlin.german.korpling.saltnpepper.pepperModules.toolboxModules;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SAudioDataSource;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer;

//TODO remember created tokens and create a span for all tokens. 
//TODO For each xml element (except audio element) create an annotation (name= name of xml element, value= name of xml element)
//TODO create a property for defining the xml element containing the audio file (default is 'sound'), for xml element sound create a SAudioDataSource, set the audio reference to a uri containing the absolute pfad to the audio file (audio.setSAudioReference(URI.createFileURI('absolute path'));) 
//TODO link SAudioDataSource with each token via a SAudioRelation
public class Toolbox2SaltMapper extends PepperMapperImpl {

	@Override
	public DOCUMENT_STATUS mapSDocument() {
		DocumentStructureReader contentHandler = new DocumentStructureReader();
		this.readXMLResource(contentHandler, getResourceURI());
		return DOCUMENT_STATUS.COMPLETED;
	}

	public class DocumentStructureReader extends DefaultHandler2 {
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
		}

		private StringBuilder currentText = new StringBuilder();

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			for (int i = start; i < start + length; i++) {
				currentText.append(ch[i]);
			}
		}

		STextualDS primaryText = null;

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			SAudioDataSource audio= null;
			
			if (qName == ((ToolboxImporterProperties) getProperties())
					.getPrimaryTextElement()) {
				// concatenate each primary text in the data to one large
				// STextualDS
				if (((ToolboxImporterProperties) getProperties())
						.concatenateText()) {
					if (primaryText == null) {
						primaryText = SaltFactory.eINSTANCE.createSTextualDS();
						primaryText.setSText("");
						getSDocument().getSDocumentGraph().addNode(primaryText);
					}
					String text = currentText.toString();

					if (((ToolboxImporterProperties) getProperties())
							.tokenizeText()) {

						Tokenizer tokenizer = new Tokenizer();
						List<String> tokenList = tokenizer.tokenizeToString(
								currentText.toString(), null);

						int offset = primaryText.getSText().length();
						primaryText.setSText(primaryText.getSText() + text);
						for (String tok : tokenList) {
							int currentPos = text.indexOf(tok);
							int start = offset + currentPos;
							int end = start + tok.length();
							offset += tok.length() + currentPos;
							text = text.substring(currentPos + tok.length());

							getSDocument().getSDocumentGraph().createSToken(
									primaryText, start, end);
						}
					} else {
						primaryText.setSText(primaryText.getSText() + text);
						getSDocument().getSDocumentGraph()
								.createSToken(
										primaryText,
										primaryText.getSText().length()
												- text.length(),
										primaryText.getSText().length());
					}

				} else {
					primaryText = getSDocument().getSDocumentGraph()
							.createSTextualDS(currentText.toString());
					
					if (((ToolboxImporterProperties) getProperties())
							.tokenizeText()) {
						getSDocument().getSDocumentGraph().tokenize();
					}
					

				}
			}
			currentText = new StringBuilder();
		}
	}

}
