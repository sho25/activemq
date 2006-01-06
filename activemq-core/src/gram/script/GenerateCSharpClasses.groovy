/**
 *
 * Copyright 2005-2006 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.apache.activemq.openwire.tool.OpenWireScript

/**
 * Generates the C# marshalling code for the Open Wire Format
 *
 * @version $Revision$
 */
class GenerateCSharpClasses extends OpenWireScript {

    Object run() {
        def destDir = new File("../openwire-dotnet/src/OpenWire.Core/Commands")
        destDir.mkdirs()

        def messageClasses = classes.findAll {
        		it.getAnnotation("openwire:marshaller")!=null
        }

        println "Generating Java marshalling code to directory ${destDir}"

        def buffer = new StringBuffer()

        int counter = 0
        Map map = [:]

        for (jclass in messageClasses) {

            println "Processing $jclass.simpleName"

            def properties = jclass.declaredProperties.findAll { isValidProperty(it) }
            def file = new File(destDir, jclass.simpleName + ".cs")

            String baseClass = "AbstractCommand"
            if (jclass.superclass?.simpleName == "ActiveMQMessage") {
                baseClass = "ActiveMQMessage"
            }

            buffer << """
${jclass.simpleName}.class
"""

            file.withWriter { out |
                out << """//
// Marshalling code for Open Wire Format for ${jclass.simpleName}
//
//
// NOTE!: This file is autogenerated - do not modify!
//        if you need to make a change, please see the Groovy scripts in the
//        activemq-openwire module
//

using System;
using System.Collections;

using OpenWire.Core;

namespace OpenWire.Core.Commands
{
    public class ${jclass.simpleName} : $baseClass
    {
"""
                for (property in properties) {

                    def type = toCSharpType(property.type)
                    def name = decapitalize(property.simpleName)
                    out << """        $type $name;
"""
                }

                out << """


        // TODO generate Equals method
        // TODO generate HashCode method
        // TODO generate ToString method


        public override int GetCommandType() {
            return ${getEnum(jclass)};
        }


        // Properties

"""
                for (property in properties) {
                    def type = toCSharpType(property.type)
                    def name = decapitalize(property.simpleName)
                    def propertyName = property.simpleName
                    if (propertyName == jclass.simpleName) {
                        // TODO think of a better naming convention :)
                    		propertyName += "Value"
                    }
                    def getter = capitalize(property.getter.simpleName)
                    def setter = capitalize(property.setter.simpleName)


                    out << """
        public $type $propertyName
        {
            get
            {
                return $name;
            }
            set
            {
                $name = value;
            }            
        }
"""
                }

                out << """
    }
}
"""
            }
        }
    }

    def getEnum(type) {
    			return 1
		}
		
    def toCSharpType(type) {
        def name = type.qualifiedName
        switch (type) {
            case "java.lang.String":
                return "string"
            case "boolean":
                return "bool"
            case "org.apache.activemq.command.DataStructure":
                return "Command"
            case "org.apache.activemq.message.ActiveMQDestination":
                return "ActiveMQDestination"
            case "org.apache.activemq.message.ActiveMQXid":
                return "ActiveMQXid"
            default:
                return type.simpleName
        }
    }
}