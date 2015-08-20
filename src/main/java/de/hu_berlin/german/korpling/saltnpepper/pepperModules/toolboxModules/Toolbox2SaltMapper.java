package de.hu_berlin.german.korpling.saltnpepper.pepperModules.toolboxModules;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SAudioDSRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SAudioDataSource;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
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

	private StringBuilder currentText = new StringBuilder();

	public class DocumentStructureReader extends DefaultHandler2 {
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			currentText = new StringBuilder();
			// reset currentTokList
			if (qName == ((ToolboxImporterProperties) getProperties())
					.getPrimaryTextElement()) {
				currentTokList = new BasicEList<SToken>();
			}

			if (qName == ((ToolboxImporterProperties) getProperties())
					.getSegmentingElement()) {
				// reset annoListForRefGroup
				annoListForRefGroup = new HashMap<String, String>();
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			for (int i = start; i < start + length; i++) {
				currentText.append(ch[i]);
			}
		}

		STextualDS primaryText = null;

		EList<SToken> currentTokList = new BasicEList<SToken>();
		EList<SToken> refGroupTokList = new BasicEList<SToken>();
		SSpan tokSpan = SaltFactory.eINSTANCE.createSSpan();

		HashMap<String, String> annoList = new HashMap<String, String>();
		HashMap<String, String> annoListForRefGroup = new HashMap<String, String>();

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			SAudioDataSource audio = null;

			if (qName != ((ToolboxImporterProperties) getProperties())
					.getRootElement()) {
				// exclude the root element from operations

				if (qName == ((ToolboxImporterProperties) getProperties())
						.getPrimaryTextElement()) {
					// qName is primary text

					if (((ToolboxImporterProperties) getProperties())
							.concatenateText()) {
						// concatenate each primary text in the data to one
						// large
						// STextualDS

						if (primaryText == null) {
							// initialize primaryText

							primaryText = SaltFactory.eINSTANCE
									.createSTextualDS();
							primaryText.setSText("");
							getSDocument().getSDocumentGraph().addNode(
									primaryText);
						}
						String text = currentText.toString();

						if (((ToolboxImporterProperties) getProperties())
								.tokenizeText()) {
							// concatenated and tokenized

							Tokenizer tokenizer = new Tokenizer();
							List<String> tokenList = tokenizer
									.tokenizeToString(currentText.toString(),
											null);

							int offset = primaryText.getSText().length();
							primaryText.setSText(primaryText.getSText() + text);

							for (String tok : tokenList) {
								int currentPos = text.indexOf(tok);
								int start = offset + currentPos;
								int end = start + tok.length();
								offset += tok.length() + currentPos;
								text = text
										.substring(currentPos + tok.length());

								SToken currTok = getSDocument()
										.getSDocumentGraph().createSToken(
												primaryText, start, end);

								// remember all SToken
								currentTokList.add(currTok);
							}

						} else {
							// concatenated and not tokenized
							primaryText.setSText(primaryText.getSText() + text);
							SToken currTok = getSDocument().getSDocumentGraph()
									.createSToken(
											primaryText,
											primaryText.getSText().length()
													- text.length(),
											primaryText.getSText().length());
							currentTokList.add(currTok);
						}

					} else {
						// not concatenated
						primaryText = getSDocument().getSDocumentGraph()
								.createSTextualDS(currentText.toString());

						if (((ToolboxImporterProperties) getProperties())
								.tokenizeText()) {
							// not concatenated but tokenized
							Tokenizer tokenizer = new Tokenizer();
							currentTokList = tokenizer.tokenize(primaryText);
						} else {
							// not concatenated and not tokenized
							SToken currentTok = getSDocument()
									.getSDocumentGraph().createSToken(
											primaryText, 0,
											primaryText.getSText().length());
							currentTokList.add(currentTok);
						}

					}

					// save all token into an additional list to enable
					// seperation of annotations that are only associated to the
					// current primarx text from those annotations, that are
					// associated to the whole primary text of one segment (e.g.
					// one refGroup)
					for (SToken curTok : currentTokList) {
						refGroupTokList.add(curTok);
					}

					// create a span for the current primary text
					tokSpan = getSDocument().getSDocumentGraph().createSSpan(
							currentTokList);

					// create annotation of tags that were loaded before the
					// actual primary text
					if (!annoList.isEmpty()) {
						for (Entry<String, String> anno : annoList.entrySet()) {
							tokSpan.createSAnnotation(null, anno.getKey(),
									anno.getValue());
						}
						// reset annoList
						annoList = new HashMap<String, String>();
					}
				}

				// annotations are only associated with the current primary text
				if (((ToolboxImporterProperties) getProperties())
						.associateWithAllToken() == null
						|| !((ToolboxImporterProperties) getProperties())
								.associateWithAllToken().contains(qName)) {

					// qName is not an audio tag, a segmenting tag or the
					// tag
					// that holds the primary text
					if (qName != ((ToolboxImporterProperties) getProperties())
							.getPrimaryTextElement()
							&& qName != ((ToolboxImporterProperties) getProperties())
									.getSegmentingElement()) {
						if (qName != ((ToolboxImporterProperties) getProperties())
								.getAudioRecordElement()) {
							// save all annotations except audio and primary
							// text as new span
							if (!currentTokList.isEmpty()) {
								// create a new span for each annotation
								if (((ToolboxImporterProperties) getProperties())
										.createNewSpan()) {
									tokSpan = getSDocument()
											.getSDocumentGraph().createSSpan(
													currentTokList);
								}
								tokSpan.createSAnnotation(null, qName,
										currentText.toString());

							} else {
								// save annotations if primary text wasn't read
								// yet
								annoList.put(qName, currentText.toString());
							}
						}
					}
					if (qName == ((ToolboxImporterProperties) getProperties())
							.getAudioRecordElement()) {
						// create audioDataSource for audio element
						audio = createAudioData();
						// create audio relation for each token
						createAudioRelForEachTok(currentTokList, audio);

						
					}
				} else {
					// annotations are associated with all primary text tags of
					// the current segment

					if (qName != ((ToolboxImporterProperties) getProperties())
							.getPrimaryTextElement()
							&& qName != ((ToolboxImporterProperties) getProperties())
									.getSegmentingElement()) {
						// qName is neither a segmenting tag (e.g. refGroup) nor
						// the
						// tag that holds the primary text (e.g. unicode)

						// store all annotations, associated to the whole
						// refGroup-primary text into a list
						annoListForRefGroup.put(qName, currentText.toString());
					}

				}

				if (qName == ((ToolboxImporterProperties) getProperties())
						.getSegmentingElement()) {

					if (!annoListForRefGroup.isEmpty()) {
						// create a span for annotations, associated to the
						// primary text of the whole segment (e.g. refGroup)
						tokSpan = getSDocument().getSDocumentGraph()
								.createSSpan(refGroupTokList);

						for (Entry<String, String> anno : annoListForRefGroup
								.entrySet()) {
							if (anno.getKey() == ((ToolboxImporterProperties) getProperties())
									.getAudioRecordElement()) {
								// check if the audio element shall be
								// associated to the whole segment (e.g.
								// refGroup)
								audio = createAudioData();
								// create audio relation for each token
								createAudioRelForEachTok(refGroupTokList, audio);
							}
							if (anno.getKey() != ((ToolboxImporterProperties) getProperties())
									.getAudioRecordElement()) {
								// create a new span for each annotation
								if (((ToolboxImporterProperties) getProperties())
										.createNewSpan()) {
									tokSpan = getSDocument()
											.getSDocumentGraph().createSSpan(
													refGroupTokList);
								}
								tokSpan.createSAnnotation(null, anno.getKey(),
										anno.getValue());
							}
						}

					}
					// reset lists
					currentTokList = new BasicEList<SToken>();
					refGroupTokList = new BasicEList<SToken>();
					annoList = new HashMap<String, String>();
				}

				currentText = new StringBuilder();
			}
		}
	}

	/**
	 * 
	 * @return audio:SAudioDataSource to a given path 
	 */
	private SAudioDataSource createAudioData() {
		SAudioDataSource audio = SaltFactory.eINSTANCE.createSAudioDataSource();

		File audioFile = new File(currentText.toString());

		if (audioFile.exists()) {
			// absolute path is given
			audio.setSAudioReference(URI.createFileURI(currentText.toString()));
			getSDocument().getSDocumentGraph().addSNode(audio);
			return audio;
		} else {
			// check if relative path is given
			URI absPath = getResourceURI().appendFileExtension(
					currentText.toString());
			audioFile = new File(absPath.path());
			if (audioFile.exists()) {
				audio.setSAudioReference(URI.createFileURI(absPath.path()));
				getSDocument().getSDocumentGraph().addSNode(audio);
				return audio;
			} else {
				ToolboxImporter.logger.warn("No audio file for give path: "
						+ currentText.toString() + " found.");
			}
			return null;
		}
	}
	
	private void createAudioRelForEachTok(EList<SToken> tokList, SAudioDataSource audio) {
		if (!tokList.isEmpty() && audio != null) {
			// create an audio relation for each token
			for (SToken tok : tokList) {
				SAudioDSRelation audioRel = SaltFactory.eINSTANCE
						.createSAudioDSRelation();

				audioRel.setSToken(tok);
				audioRel.setSAudioDS(audio);
				getSDocument().getSDocumentGraph()
						.addSRelation(audioRel);
			}
		}
	}

}
