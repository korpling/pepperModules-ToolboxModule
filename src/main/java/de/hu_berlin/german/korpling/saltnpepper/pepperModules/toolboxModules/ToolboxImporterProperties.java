package de.hu_berlin.german.korpling.saltnpepper.pepperModules.toolboxModules;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;

/**
 * Properties for the ToolboxImporter, those are: setting the element which
 * holds the primary text (key: 'textElement', default value: 'unicode'),
 * setting the element which holds the audio reference (key: 'audioRefElement',
 * default value: 'sound'), disable the concatenation of the primary text (key:
 * 'concatenateText', default is 'true'), disable the tokenization of the
 * primary text (key: 'tokenizeText', default value: 'true'), setting the
 * element, that seperates one text segment from another (key:
 * 'segmentingElement', default value: 'refGroup'), enable the creation of new
 * spans for each annotation (key: 'newSpan', default value: 'false'), setting the
 * root element (key: 'rootElement', default value: 'database') and finally, if
 * one segment (e.g. one refGroup) holds more than one entry of primary text and
 * there are annotations, that shall be associated not only to the last entry,
 * you can define annotation layer (as an enumeration string), that shall be
 * associated to the primary text of the whole segment (key:
 * 'associateWithAllToks', default value: 'null')
 */

public class ToolboxImporterProperties extends PepperModuleProperties {
	//TODO remove serial id and ignore warning in eclipse, Java can doo that automatrically, what is more error prune
	private static final long serialVersionUID = 3403186500470804226L;
	public static final String PROP_TEXT_ELEMENT = "textElement";
	public static final String PROP_SOUND_ELEMENT = "audioRefElement";
	public static final String PROP_CONCATENATE_TEXT = "concatenateText";
	public static final String PROP_TOKENIZE_TEXT = "tokenizeText";
	public static final String PROP_SEGMENTING_ELEMENT = "segmentingElement";
	public static final String PROP_ASSOCIATE_WITH_ALL_TOKEN = "associateWithAllToks";
	public static final String PROP_NEW_SPAN = "newSpan";
	public static final String PROP_ROOT_ELEMENT = "rootElement";

	public ToolboxImporterProperties() {
		addProperty(new PepperModuleProperty<>(
				PROP_TEXT_ELEMENT,
				String.class,
				"Defines the name of the xml tag that includes the textual data to be used as primary text (key: 'textElement', default value: 'unicode').",
				ToolboxXmlDictionary.TAG_UNICODE, false));
		addProperty(new PepperModuleProperty<>(
				PROP_SOUND_ELEMENT,
				String.class,
				"Defines the name of the sound recordings (key: 'audioRefElement', default value: 'sound').",
				ToolboxXmlDictionary.TAG_SOUND, false));
		addProperty(new PepperModuleProperty<>(
				PROP_SEGMENTING_ELEMENT,
				String.class,
				"Defines the element for segmenting into textualDS (key: 'segmentingElement', default value: 'refGroup').",
				ToolboxXmlDictionary.TAG_REF_GROUP, false));
		addProperty(new PepperModuleProperty<>(
				PROP_CONCATENATE_TEXT,
				Boolean.class,
				"Defines, if the textual data shall be concatenated or if a new string object shall be created (key: 'concatenateText', default is 'true').",
				true, false));
		addProperty(new PepperModuleProperty<>(
				PROP_TOKENIZE_TEXT,
				Boolean.class,
				"Defines, if the textual data shall be tokenized (key: 'tokenizeText', default value: 'true')",
				true, false));
		addProperty(new PepperModuleProperty<>(
				PROP_ASSOCIATE_WITH_ALL_TOKEN,
				String.class,
				"If one segment (refGroup) holds more than one entry of primary text and you wish to associate some annotations not only to the current primary text entry, but to the whole primary text of the current segment (refGroup), you can enumerate those annotation names here as a comma seperated string (key: 'associateWithAllToks', default value: 'null').",
				null, false));
		addProperty(new PepperModuleProperty<>(
				PROP_NEW_SPAN,
				Boolean.class,
				"Defines, if there shall be created a new span for each annotation (key: 'newSpan', default value: false).",
				false, false));
		addProperty(new PepperModuleProperty<>(
				PROP_ROOT_ELEMENT,
				String.class,
				"Defines the name of root xml element (key: 'rootElement', default value: 'database').",
				ToolboxXmlDictionary.TAG_DATABASE, false));
	}

	public String getPrimaryTextElement() {
		return (String) getProperty(PROP_TEXT_ELEMENT).getValue();
	}

	public String getAudioRecordElement() {
		return (String) getProperty(PROP_SOUND_ELEMENT).getValue();
	}

	public String getRootElement() {
		return (String) getProperty(PROP_ROOT_ELEMENT).getValue();
	}

	public String getSegmentingElement() {
		return (String) getProperty(PROP_SEGMENTING_ELEMENT).getValue();
	}

	/**
	 * enable/ disable concatenation of the primary text
	 * 
	 * @return {@link Boolean}
	 */
	public Boolean concatenateText() {
		return (Boolean) getProperty(PROP_CONCATENATE_TEXT).getValue();
	}

	/**
	 * enable/ disable tokenization of the primary text
	 * 
	 * @return {@link Boolean}
	 */
	public Boolean tokenizeText() {
		return (Boolean) getProperty(PROP_TOKENIZE_TEXT).getValue();
	}

	/**
	 * associate the enumerated, comma seperated annotations with the whole primary text of a
	 * segment, not only with the current one (only necessary for segments with
	 * more than one entry of primary text)
	 * 
	 * @return {@link String}
	 */
	public String associateWithAllToken() {
		return (String) getProperty(PROP_ASSOCIATE_WITH_ALL_TOKEN).getValue();
	}

	/**
	 * create a new span for each annotation of the given primary text
	 * 
	 * @return {@link Boolean}
	 */
	public Boolean createNewSpan() {
		return (Boolean) getProperty(PROP_NEW_SPAN).getValue();
	}
}
