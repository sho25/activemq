/**
* <a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>
*
* Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com
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
*
**/
import org.apache.activemq.openwire.tool.OpenWireScript

/**
 * Generates the C# marshalling code for the Open Wire Format
 *
 * @version $Revision$
 */
class GenerateCSharpClasses extends OpenWireScript {

    Object run() {
        def destDir = new File("target/generated/dotnet/cs/org/apache/activemq/openwire")
        destDir.mkdirs()

        def messageClasses = classes.findAll { isMessageType(it) }

        println "Generating Java marshalling code to directory ${destDir}"

        def buffer = new StringBuffer()

        int counter = 0
        Map map = [:]

        for (jclass in messageClasses) {

            println "Processing $jclass.simpleName"

            def properties = jclass.declaredProperties.findAll { isValidProperty(it) }
            def file = new File(destDir, jclass.simpleName + ".cs")

            String baseClass = "AbstractPacket"
            if (jclass.superclass?.simpleName == "ActiveMQMessage") {
                baseClass = "ActiveMQMessage"
            }

            buffer << """
${jclass.simpleName}.class
"""

            file.withWriter { out |
                out << """/**
 * Marshalling code for Open Wire Format for ${jclass.simpleName}
 *
 *
 * NOTE!: This file is autogenerated - do not modify!
 *        if you need to make a change, please see the Groovy scripts in the
 *        activemq-openwire module
 */

using System;
using System.Collections;

namespace ActiveMQ
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


        public overide int getPacketType() {
            return ${getEnum(jclass)};
        }


        // Properties

"""
                for (property in properties) {
                    def type = toCSharpType(property.type)
                    def name = decapitalize(property.simpleName)
                    def getter = property.getter.simpleName
                    def setter = property.setter.simpleName


                    out << """
        public $type $getter()
        {
            return this.$name;
        }

        public void $setter($type $name)
        {
              this.$name = $name;
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

    def toCSharpType(type) {
        def name = type.qualifiedName
        switch (type) {
            case "java.lang.String":
                return "string"
            case "org.apache.activemq.message.ActiveMQDestination":
                return "ActiveMQDestination"
            case "org.apache.activemq.message.ActiveMQXid":
                return "ActiveMQXid"
            default:
                return name
        }
    }
}