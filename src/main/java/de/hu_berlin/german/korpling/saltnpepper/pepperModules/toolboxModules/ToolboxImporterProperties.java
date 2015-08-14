package de.hu_berlin.german.korpling.saltnpepper.pepperModules.toolboxModules;

import java.util.List;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;

public class ToolboxImporterProperties extends PepperModuleProperties {

	public static final String PROP_TEXT_ELEMENT = "textElement";
	public static final String PROP_SOUND_ELEMENT = "soundElement";
	public static final String PROP_CONCATENATE_TEXT = "concatenateText";
	public static final String PROP_TOKENIZE_TEXT = "tokenizeText";
	public static final String PROP_SEGMENTING_ELEMENT = "segmentingElement";
	public static final String PROP_ASSOCIATE_WITH_ALL_TOKEN = "associateWithAllToks";
	public static final String PROP_NEW_SPAN = "newSpan";

	public ToolboxImporterProperties() {
		addProperty(new PepperModuleProperty<>(
				PROP_TEXT_ELEMENT,
				String.class,
				"Defines the name of the xml tag that includes the textual data to be used as primary text. This is set to 'unicode' by default.",
				ToolboxXmlDictionary.TAG_UNICODE, false));
		addProperty(new PepperModuleProperty<>(
				PROP_SOUND_ELEMENT,
				String.class,
				"Defines the name of the sound recordings. This is set to 'sound' by default.",
				ToolboxXmlDictionary.TAG_SOUND, false));
		addProperty(new PepperModuleProperty<>(
				PROP_SEGMENTING_ELEMENT,
				String.class,
				"Defines the element for segmenting into textualDS. This is set to 'refGroup' by default.",
				ToolboxXmlDictionary.TAG_REF_GROUP, false));
		addProperty(new PepperModuleProperty<>(
				PROP_CONCATENATE_TEXT,
				Boolean.class,
				"Defines, if the textual data shall be concatenated or if a new string object shall be created. This is set to 'false' by default.",
				true, false));
		addProperty(new PepperModuleProperty<>(
				PROP_TOKENIZE_TEXT,
				Boolean.class,
				"Defines, if the textual data shall be tokenized. This is set to 'true' by default.",
				true, false));
		addProperty(new PepperModuleProperty<>(
				PROP_ASSOCIATE_WITH_ALL_TOKEN,
				List.class,
				"If there is more than one tag of primary text in one segment (refGroup) and you wish to associate some annotations not only to the current primary text tag, but to all primary text tags of the current segment (refGroup), you can enumerate those annotation layer names here.",
				null, false));
		addProperty(new PepperModuleProperty<>(
				PROP_NEW_SPAN,
				Boolean.class,
				"Defines, if the textual data shall be tokenized. This is set to 'false' by default.",
				false, false));
	}

	public String getPrimaryTextElement() {
		return (String) getProperty(PROP_TEXT_ELEMENT).getValue();
	}
	public String getAudioRecordElement() {
		return (String) getProperty(PROP_SOUND_ELEMENT).getValue();
	}
	
	public String getSegmentingElement() {
		return (String) getProperty(PROP_SEGMENTING_ELEMENT).getValue();
	}

	// konkatenieren?
	public Boolean concatenateText() {
		return (Boolean) getProperty(PROP_CONCATENATE_TEXT).getValue();
	}

	// tokenisieren?
	public Boolean tokenizeText() {
		return (Boolean) getProperty(PROP_TOKENIZE_TEXT).getValue();
	}
	
	// associate annotations with all tokens of the current refGroup or only with the current one? 
	public List<String> associateWithAllToken() {
		return (List<String>) getProperty(PROP_ASSOCIATE_WITH_ALL_TOKEN).getValue();
	}
	
	// create new span for each annotation?
	public Boolean createNewSpan() {
		return (Boolean) getProperty(PROP_NEW_SPAN).getValue();
	}
}
