package de.hu_berlin.german.korpling.saltnpepper.pepperModules.toolboxModules;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer;

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
			if (qName == ((ToolboxImporterProperties) getProperties())
					.getPrimaryTextElement()) {
				if (((ToolboxImporterProperties) getProperties())
						.concatenateText()) {
					//TODO: florian an junit-test erinnern, checken wieso text überschrieben wird & wieso "null" reingeschrieben wird
					if (primaryText == null) {
						primaryText = SaltFactory.eINSTANCE.createSTextualDS();
						getSDocument().getSDocumentGraph().addNode(primaryText);
						String text = currentText.toString();
						primaryText.setSText(primaryText.getSText() + text);
						if (((ToolboxImporterProperties) getProperties())
								.tokenizeText()) {

							Tokenizer tokenizer = new Tokenizer();
							List<String> tokenList = tokenizer
									.tokenizeToString(currentText.toString(),
											null);

							int offset = primaryText.getSText().length();
							for (String tok : tokenList) {
								int currentPos = text.indexOf(tok);
								int start = offset + currentPos;
								int end = start + tok.length();
								offset += currentPos;
								text = text.substring(currentPos);
								getSDocument().getSDocumentGraph()
										.createSToken(primaryText, start, end);
							}
						} else {
							getSDocument().getSDocumentGraph().createSToken(
									primaryText,
									primaryText.getSText().length()
											- text.length(),
									primaryText.getSText().length());
						}
					}
				} else {
					primaryText = getSDocument().getSDocumentGraph()
							.createSTextualDS(currentText.toString());

				}
			}
			currentText = new StringBuilder();
		}
	}

}
