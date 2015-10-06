![SaltNPepper project](./gh-site/img/SaltNPepper_logo2010.png)
# pepperModules-ToolboxModules
This project provides an importer to support the [Toolbox](http://www-01.sil.org/computing/toolbox/) format in linguistic converter framework Pepper (see https://u.hu-berlin.de/saltnpepper). A detailed description of the importer can be found in section [ToolboxImporter](#importer).

Pepper is a pluggable framework to convert a variety of linguistic formats (like [TigerXML](http://www.ims.uni-stuttgart.de/forschung/ressourcen/werkzeuge/TIGERSearch/doc/html/TigerXML.html), the [EXMARaLDA format](http://www.exmaralda.org/), [PAULA](http://www.sfb632.uni-potsdam.de/paula.html) etc.) into each other. Furthermore Pepper uses Salt (see https://github.com/korpling/salt), the graph-based meta model for linguistic data, which acts as an intermediate model to reduce the number of mappings to be implemented. That means converting data from a format _A_ to format _B_ consists of two steps. First the data is mapped from format _A_ to Salt and second from Salt to format _B_. This detour reduces the number of Pepper modules from _n<sup>2</sup>-n_ (in the case of a direct mapping) to _2n_ to handle a number of n formats.

![n:n mappings via SaltNPepper](./gh-site/img/puzzle.png)

In Pepper there are three different types of modules:
* importers (to map a format _A_ to a Salt model)
* manipulators (to map a Salt model to a Salt model, e.g. to add additional annotations, to rename things to merge data etc.)
* exporters (to map a Salt model to a format _B_).

For a simple Pepper workflow you need at least one importer and one exporter.

## Requirements
Since the here provided module is a plugin for Pepper, you need an instance of the Pepper framework. If you do not already have a running Pepper instance, click on the link below and download the latest stable version (not a SNAPSHOT):

> Note:
> Pepper is a Java based program, therefore you need to have at least Java 7 (JRE or JDK) on your system. You can download Java from https://www.oracle.com/java/index.html or http://openjdk.java.net/ .


## Install module
If this Pepper module is not yet contained in your Pepper distribution, you can easily install it. Just open a command line and enter one of the following program calls:

**Windows**
```
pepperStart.bat 
```

**Linux/Unix**
```
bash pepperStart.sh 
```

Then type in command *is* and the path from where to install the module:
```
pepper> update de.hu_berlin.german.korpling.saltnpepper::pepperModules-pepperModules-ToolboxModules::https://korpling.german.hu-berlin.de/maven2/
```

## Usage
To use this module in your Pepper workflow, put the following lines into the workflow description file. Note the fixed order of xml elements in the workflow description file: &lt;importer/>, &lt;manipulator/>, &lt;exporter/>. The ToolboxImporter is an importer module, which can be addressed by one of the following alternatives.
A detailed description of the Pepper workflow can be found on the [Pepper project site](https://u.hu-berlin.de/saltnpepper). 

### a) Identify the module by name

```xml
<importer name="ToolboxImporter" path="PATH_TO_CORPUS"/>
```

### b) Identify the module by formats
```xml
<importer formatName="Toolbox" formatVersion="1.0" path="PATH_TO_CORPUS"/>
```

### c) Use properties
```xml
<importer name="ToolboxImporter" path="PATH_TO_CORPUS">
  <property key="PROPERTY_NAME">PROPERTY_VALUE</property>
</importer>
```

## Contribute
Since this Pepper module is under a free license, please feel free to fork it from github and improve the module. If you even think that others can benefit from your improvements, don't hesitate to make a pull request, so that your changes can be merged.
If you have found any bugs, or have some feature request, please open an issue on github. If you need any help, please write an e-mail to saltnpepper@lists.hu-berlin.de.

## Funders
This project has been funded by the [department of corpus linguistics and morphology](https://www.linguistik.hu-berlin.de/institut/professuren/korpuslinguistik/) of the Humboldt-Universität zu Berlin, the Institut national de recherche en informatique et en automatique ([INRIA](www.inria.fr/en/)) and the [Sonderforschungsbereich 632](https://www.sfb632.uni-potsdam.de/en/). 

## License
  Copyright 2009 Humboldt-Universität zu Berlin, INRIA.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.


# <a name="importer">ToolboxImporter</a>
The ToolboxImporter imports data coming from [Toolbox's](http://www-01.sil.org/computing/toolbox/) xml format to a [Salt](https://github.com/korpling/salt) model. This importer provides a wide range of customization possibilities via the here described set of properties. Before we talk about the possibility of customizing the mapping, we describe the general and default mapping from  the Toolbox format to a Salt model.

### Mapping to Salt

During the mapping primary data objects are created for each 'unicode' element of a 'refGroup' element in the Toolbox format. Tokens in xml formats like Toolbox can be defined and interpreted in many
different ways and thus customization through properties deals with the
problems occuring because of this.
For references to audio files (usually taged by 'sound') each token of the current primary text gets a relation (sAudioRelation) to the reference, thus an audio file can be assigned to a text in [ANNIS](https://korpling.german.hu-berlin.de/annis3/).
For each other element spans (SSpan) are used. Those elements are interpreted as annotations (SAnnotations) of the primary text. If there is more than one 'unicode' element within a 'refGroup' annotations are only related to the current primary text. If annotations shall be related to all primary text elements of one 'refGroup', customization through properties deals with this problem as well. 

### Properties
The table  contains an overview of all usable properties to customize the behaviour of this pepper module. The following section contains a close description to each single property and describes the resulting differences in the mapping to the salt model.

| Name of property                              | Type of property | optional/mandatory | default value      |
|-----------------------------------------------|------------------|--------------------|--------------------|
| [textElement](#text)         | String          | optional           | unicode              |
| [audioRefElement](#audio)         | String           | optional           |       sound             |
| [segmentingElement](#seg)               | String          | optional           | refGroup              |
| [concatenateText](#conc)             | Boolean          | optional           | true              |
| [tokenizeText](#tok)           | Boolean           | optional           |     true               |
| [associateWithAllToks](#asso)        		| String          | optional           |               |
| [newSpan](#span)         | Boolean          | optional           | false              |
| [rootElement](#root)              | String          | optional     | database             |

<a name="text"></a>
### textElement

Defines the name of the xml tag that includes the textual data to be used as primary text. 
For example this
```xml
<unicode>This is a primary text.</unicode>
<firstAnno>And this is an annotation.</firstAnno>
```
would result in two nodes containing the text and the annotations, here marked with curly brackets by default:
```
[Node1]: This is a primary text.
[Node2]{firstAnno}: And this is an annotation.
```
Please note that if you set this property to a non existing tag, the primary text of your Toolbox file will be empty and thus, no annotations will be converted.

<a name="audio"></a>
### audioRefElement

Defines the name of the element that references to the sound recordings.
For example this
```xml
<unicode>This is a primary text.</unicode>
<sound>path/to/sound.mp3</sound>
```
would result in two nodes containing the text and the assigned audio data by default:
```
[Node1]: This is a primary text.
[Node2]{sound}: path/to/sound.mp3
```

<a name="seg"></a>
### segmentingElement

Defines the element for the segementation of the STextualDS.
For example this
```xml
<refGroup>
  <unicode>This is a primary text.</unicode>
</refGroup>
<refGroup>
  <unicode>This is a second primary text.</unicode>
</refGroup>
```
would result in two nodes (STextualDS) containing the respective primary text by default.
```
[Node1]: This is a primary text.
[Node2]: This is a second primary text.
```

<a name="conc"></a>
### concatenateText

Defines, if the textual data shall be concatenated or if a new string object shall be created. This has mainly reasons of efficiency.

<a name="tok"></a>
### tokenizeText

Defines, if the textual data shall be tokenized.
If you set this option to false, the primary text will not be split into smaller segments and the whole primary text will be interpreted as one token, eg:
```xml
<unicode>This is a primary text.</unicode>
```
will be interpreted as
```
[Node1]{token}: This is a primary text.
```
if this option is set to false.

<a name="asso"></a>
### associateWithAllToks

If one segment (like a 'refGroup' element) holds more than one entry of primary text (eg. 'unicode') and you wish to associate some annotations not only to the current primary text entry, but to the whole primary text of the current segment, you can enumerate those annotation names here as a comma seperated string.
For instance, if your list of annotation names contains 'annoThrd', this:
```xml
<refGroup>
  <unicode>This is a primary text.</unicode>
  <annoFirst>This is an annotation associated to the first unicode text.</anno>
  <unicode>This is a second primary text.</unicode>
  <annoSec>This is an annotation associated to the second unicode text.</anno>
  <annoThrd>This is an annotation associated to all unicode texts of the refGroup.</anno>
</refGroup>
```
would result in an annotion layer 'annoFirst', associated to the text span 'This is a primary text.', an annotation layer 'annoSec', associated to the text span 'This is a second primary text.' and a third annotation layer 'annoThrd', associated to all primary texts of the refGroup 'This is a primary text. This is a second primary text.'.

<a name="span"></a>
### newSpan

Defines, whether a new span for the primary text will be created  for each annotation. This has mainly reasons of efficiency.

<a name="root"></a>
### rootElement

Defines the name of the root element of the xml file.
