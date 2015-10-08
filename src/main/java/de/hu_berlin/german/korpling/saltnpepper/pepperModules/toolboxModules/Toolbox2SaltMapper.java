/**
 * Copyright 2009 Humboldt-Universität zu Berlin, INRIA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.toolboxModules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SMedialDS;
import org.corpus_tools.salt.common.SMedialRelation;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.common.tokenizer.Tokenizer;
import org.corpus_tools.salt.core.SAnnotation;
import org.eclipse.emf.common.util.URI;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import com.google.common.io.Files;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;


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
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

			currentText = new StringBuilder();

			if (((ToolboxImporterProperties) getProperties()).getPrimaryTextElement().equals(qName)) {
				// reset currentTokList for each new primary text
				currentTokList = new ArrayList<>();
			}

			if (((ToolboxImporterProperties) getProperties()).getSegmentingElement().equals(qName)) {
				// reset lists
				currentTokList = new ArrayList<>();
				segmentTokList = new ArrayList<>();
				annoList = new HashSet<>();
				audioList = new HashMap<>();
				annoListForSegmentElem = new HashMap<>();
				tokSpan = SaltFactory.createSSpan();
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			for (int i = start; i < start + length; i++) {
				currentText.append(ch[i]);
			}
		}

		STextualDS primaryText = null;

		// save all tokens of the current primary text
		List<SToken> currentTokList = new ArrayList<>();
		// save all tokens of the whole segment
		List<SToken> segmentTokList = new ArrayList<>();
		SSpan tokSpan = SaltFactory.createSSpan();

		Set<SAnnotation> annoList = new HashSet<SAnnotation>();
		Map<String, String> audioList = new HashMap<>();
		Map<String, String> annoListForSegmentElem = new HashMap<>();

		String annosToAssociateWithWholeSegment = ((ToolboxImporterProperties) getProperties()).associateWithAllToken();
		List<String> annosToAssociateWithWholeSegmentList = new ArrayList<>();

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			SMedialDS audio = null;

			if (annosToAssociateWithWholeSegment != null) {
				// convert string of annotations, that shall be associated with
				// the primary texts of the whole segment, to a list, to ensure
				// proper function (e.g.: a string "refGroup" contains "ref" but
				// a list
				// with only one element "refGroup" does not)
				annosToAssociateWithWholeSegmentList = Arrays.asList(annosToAssociateWithWholeSegment.split("\\s*,\\s*"));
			}

			if (!((ToolboxImporterProperties) getProperties()).getRootElement().equals(qName)) {
				// exclude the root element from operations

				if (((ToolboxImporterProperties) getProperties()).getPrimaryTextElement().equals(qName)) {
					// qName is primary text
					currentTokList = new ArrayList<>();

					if (((ToolboxImporterProperties) getProperties()).concatenateText()) {
						// concatenate each primary text in the data to one
						// large STextualDS

						if (primaryText == null) {
							// initialize primaryText

							primaryText = SaltFactory.createSTextualDS();
							primaryText.setText("");
							getDocument().getDocumentGraph().addNode(primaryText);
						}
						String text = currentText.toString();

						if (((ToolboxImporterProperties) getProperties()).tokenizeText()) {
							// concatenated and tokenized

							Tokenizer tokenizer = new Tokenizer();
							List<String> tokenList = tokenizer.tokenizeToString(currentText.toString(), null);

							int offset = primaryText.getText().length();
							primaryText.setText(primaryText.getText() + text);

							for (String tok : tokenList) {
								int currentPos = text.indexOf(tok);
								int start = offset + currentPos;
								int end = start + tok.length();
								offset += tok.length() + currentPos;
								text = text.substring(currentPos + tok.length());

								SToken currTok = getDocument().getDocumentGraph().createToken(primaryText, start, end);

								// remember all SToken
								currentTokList.add(currTok);
							}

						} else {
							// concatenated and not tokenized
							primaryText.setText(primaryText.getText() + text);
							SToken currTok = getDocument().getDocumentGraph().createToken(primaryText, primaryText.getText().length() - text.length(), primaryText.getText().length());
							currentTokList.add(currTok);
						}

					} else {
						// not concatenated
						primaryText = getDocument().getDocumentGraph().createTextualDS(currentText.toString());

						if (((ToolboxImporterProperties) getProperties()).tokenizeText()) {
							// not concatenated but tokenized
							Tokenizer tokenizer = new Tokenizer();
							currentTokList = tokenizer.tokenize(primaryText);
						} else {
							// not concatenated and not tokenized
							SToken currentTok = getDocument().getDocumentGraph().createToken(primaryText, 0, primaryText.getText().length());
							currentTokList.add(currentTok);
						}

					}

					// save all token into an additional list to enable
					// seperation of annotations that are only associated to the
					// current primarx text from those annotations, that are
					// associated to the whole primary text of one segment (e.g.
					// one refGroup)
					for (SToken curTok : currentTokList) {
						segmentTokList.add(curTok);
					}

					// create a span for the current primary text
					tokSpan = getDocument().getDocumentGraph().createSpan(currentTokList);

					// create annotation of tags that were loaded before the
					// actual primary text
					if (!annoList.isEmpty()) {
						for (SAnnotation anno : annoList) {
							tokSpan.addAnnotation(anno);
						}
						// reset annoList for next primary text
						annoList = new HashSet<>();
					}

					if (!audioList.isEmpty()) {
						for (Entry<String, String> audioEntry : audioList.entrySet()) {
							audio = createAudioData(audioEntry.getValue());
							createAudioRelForEachTok(currentTokList, audio);
						}
						audioList = new HashMap<>();
					}
				}

				// annotations are only associated with the current primary text
				if (annosToAssociateWithWholeSegmentList == null || !annosToAssociateWithWholeSegmentList.contains(qName)) {

					// qName is not an audio tag, a segmenting tag or the
					// tag
					// that holds the primary text
					if (!((ToolboxImporterProperties) getProperties()).getPrimaryTextElement().equals(qName) && !((ToolboxImporterProperties) getProperties()).getSegmentingElement().equals(qName)) {
						if (!((ToolboxImporterProperties) getProperties()).getAudioRecordElement().equals(qName)) {
							// save all annotations except audio and primary
							// text as new span
							if (!currentTokList.isEmpty()) {
								// create a new span for each annotation
								if (((ToolboxImporterProperties) getProperties()).createNewSpan()) {
									tokSpan = getDocument().getDocumentGraph().createSpan(currentTokList);
								}

								checkForAndRenameDoubledAnno(tokSpan, qName, currentText.toString());

							} else {
								// save annotations if primary text wasn't read
								// yet
								annoList.add(getDocument().getDocumentGraph().createAnnotation(null, qName, currentText.toString()));
							}
						} else {
							if (!currentTokList.isEmpty()) {
								// create audioDataSource for audio element
								audio = createAudioData(currentText.toString());

								// create audio relation for each token
								createAudioRelForEachTok(currentTokList, audio);
							} else {
								audioList.put(qName, currentText.toString());
							}
						}
					}
				} else if (annosToAssociateWithWholeSegmentList.contains(qName)) {
					// annotations are associated with all primary text tags of
					// the current segment

					if (!((ToolboxImporterProperties) getProperties()).getPrimaryTextElement().equals(qName) && !((ToolboxImporterProperties) getProperties()).getSegmentingElement().equals(qName)) {
						// qName is neither a segmenting tag (e.g. refGroup) nor
						// the
						// tag that holds the primary text (e.g. unicode)

						// store all annotations, associated to the whole
						// primary text of one segment into a list
						annoListForSegmentElem.put(qName, currentText.toString());
					}
				}

				if (((ToolboxImporterProperties) getProperties()).getSegmentingElement().equals(qName)) {

					if (!annoListForSegmentElem.isEmpty()) {
						// create a span for annotations, associated to the
						// primary text of the whole segment (e.g. refGroup)
						tokSpan = getDocument().getDocumentGraph().createSpan(segmentTokList);

						for (Entry<String, String> anno : annoListForSegmentElem.entrySet()) {
							if (((ToolboxImporterProperties) getProperties()).getAudioRecordElement().equals(anno.getKey())) {
								// check if the audio element shall be
								// associated to the whole segment (e.g.
								// to the whole refGroup)
								audio = createAudioData(anno.getValue());
								// create audio relation for each token
								createAudioRelForEachTok(segmentTokList, audio);

							} else {
								// check if there are any annotations that shall
								// be associated to the whole segment
								if (((ToolboxImporterProperties) getProperties()).createNewSpan()) {
									// create a new span for each annotation
									tokSpan = getDocument().getDocumentGraph().createSpan(segmentTokList);
								}
								checkForAndRenameDoubledAnno(tokSpan, anno.getKey(), anno.getValue());
							}
							annoListForSegmentElem.remove(anno);
						}

					}
					// reset lists
					currentTokList = new ArrayList<>();
					segmentTokList = new ArrayList<>();
					annoList = new HashSet<>();
					annoListForSegmentElem = new HashMap<>();
				}

				currentText = new StringBuilder();
			}
		}
	}

	/**
	 * Method to create a {@link SMedialDS} and to set a
	 * {@link SMedialDS#setSAudioReference(URI)} to a (relative or
	 * absolute) path, given in the xml file
	 * 
	 * @return {@link SMedialDS}
	 */
	private SMedialDS createAudioData(String uriString) {

		SMedialDS audio = SaltFactory.createSMedialDS();

		File audioFile = new File(uriString);

		if (audioFile.exists()) {
			// absolute path is given
			audio.setMediaReference(URI.createFileURI(uriString));
			getDocument().getDocumentGraph().addNode(audio);
			return audio;
		} else {
			// check if relative path is given
			String absPath = getResourceURI().toFileString().replace(this.getResourceURI().lastSegment(), uriString);

			audioFile = new File(absPath);
			if (audioFile.exists()) {
				audio.setMediaReference(URI.createFileURI(absPath));
				getDocument().getDocumentGraph().addNode(audio);
				return audio;
			} else {
				// neither a relative nor an absolute path is given
				ToolboxImporter.logger.warn("No audio file for path '" + audioFile.getAbsolutePath() + "' found.");
			}
			return null;
		}
	}

	/**
	 * Method to create a {@link SMedialRelation} for each {@link SToken} from
	 * a given {@link EList} (tokList) to a given {@link SMedialDS} and
	 * to add it to the {@link SDocumentGraph}
	 * 
	 * @param tokList
	 * @param audio
	 */

	private void createAudioRelForEachTok(List<SToken> tokList, SMedialDS audio) {
		if (!tokList.isEmpty() && audio != null) {
			// create an audio relation for each token
			for (SToken tok : tokList) {
				SMedialRelation audioRel = SaltFactory.createSMedialRelation();
				File file = new File(audio.getMediaReference().toFileString());
				double duration = computeDuration(file);
				audioRel.setSource(tok);
				audioRel.setTarget(audio);
				audioRel.setStart(0.0);
				audioRel.setEnd(duration);
				getDocument().getDocumentGraph().addRelation(audioRel);
			}
		}
	}

	/**
	 * Method to check whether an annotation name was already and if so, to
	 * rename this annotation name. Further this method creates those
	 * annotations.
	 * 
	 * @param tokSpan
	 * @param name
	 * @param value
	 */
	private void checkForAndRenameDoubledAnno(SSpan tokSpan, String name, String value) {
		if (!tokSpan.containsLabel(name)) {
			tokSpan.createAnnotation(null, name, value);
		} else {
			int i = 1;
			String annoName = name;
			while (tokSpan.containsLabel(annoName) && i <= tokSpan.getAnnotations().size()) {
				if (!tokSpan.containsLabel(annoName + "_" + i)) {
					annoName = annoName + "_" + i;
				}
				i++;
			}
			ToolboxImporter.logger.warn("The annotation layer '" + name + "' allready exists and was replaced by '" + annoName + "'.");

			tokSpan.createAnnotation(null, annoName, value);
		}
	}

	/**
	 * Method to compute the duration of an audio file
	 * 
	 * @param file
	 * @return {@link Double}
	 */
	private double computeDuration(File file) {
		double duration = 0.0;
		if (Files.getFileExtension(file.getAbsolutePath()).equalsIgnoreCase("mp3")) {
			try {
				AudioFile audioFile = AudioFileIO.read(new File(file.getAbsolutePath()));
				duration = ((MP3AudioHeader) audioFile.getAudioHeader()).getPreciseTrackLength();
				duration = duration / 100;
			} catch (Exception e) {
				ToolboxImporter.logger.warn("The end of the audioFile '" + file + "' could not be computed and will not be set.");
			}
		} else {
			AudioInputStream audioInputStream;
			try {
				audioInputStream = AudioSystem.getAudioInputStream(file);
				AudioFormat format = audioInputStream.getFormat();
				long frames = audioInputStream.getFrameLength();
				duration = (frames + 0.0) / format.getFrameRate();
			} catch (UnsupportedAudioFileException e) {
				ToolboxImporter.logger.warn("The end of the audioFile '" + file + "' could not be computed and will not be set.");
			} catch (IOException e) {
				ToolboxImporter.logger.warn("The end of the audioFile '" + file + "' could not be computed and will not be set.");
			}
		}
		return duration;
	}
}
