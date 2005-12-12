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
import org.activemq.openwire.tool.OpenWireScript

/**
 * Generates the Java marshalling code for the Open Wire Format
 *
 * @version $Revision$
 */
class GenerateCSharpMarshalling extends OpenWireScript {

    Object run() {
        def destDir = new File("target/generated/dotnet/cs/org/activemq/openwire/io")
        destDir.mkdirs()

        def messageClasses = classes.findAll { isMessageType(it) }

        println "Generating Java marshalling code to directory ${destDir}"

        def buffer = new StringBuffer()

        int counter = 0
        Map map = [:]

        for (jclass in messageClasses) {

            println "Processing $jclass.simpleName"

            def properties = jclass.declaredProperties.findAll { isValidProperty(it) }
            def file = new File(destDir, jclass.simpleName + "Marshaller.cs")

            String baseClass = "AbstractPacketMarshaller"
            if (jclass.superclass?.simpleName == "ActiveMQMessage") {
                baseClass = "ActiveMQMessageMarshaller"
            }

            buffer << """
${jclass.simpleName}Marshaller.class
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

        public override int GetPacketType() {
            return ${getEnum(jclass)};
        }

        public override Packet CreatePacket() {
            return new ${jclass.simpleName}();
        }

        public override void BuildPacket(Packet packet, DataInput dataIn) throws IOException {
            super.buildPacket(packet, dataIn);
            ${jclass.simpleName} info = (${jclass.simpleName}) packet;
"""
                for (property in properties) {
                    out << "            info.${property.setter.simpleName}("

                    def type = property.type.qualifiedName
                    switch (type) {
                        case "java.lang.String":
                            out << "dataIn.readUTF()"
                            break;

                        case "org.activemq.message.ActiveMQDestination":
                            out << "readDestination(dataIn)"
                            break;

                        case "boolean":
                            out << "dataIn.readBoolean()"
                            break;

                        case "byte":
                            out << "dataIn.readByte()"
                            break;

                        case "char":
                            out << "dataIn.readChar()"
                            break;

                        case "short":
                            out << "dataIn.readShort()"
                            break;

                        case "int":
                            out << "dataIn.readInt()"
                            break;

                        case "long":
                            out << "dataIn.readLong()"
                            break;

                        case "float":
                            out << "dataIn.readFloat()"
                            break;

                        case "double":
                            out << "dataIn.readDouble()"
                            break;

                        default:
                            out << "(${type}) readObject(dataIn)"
                    }
                    out << """);
"""
                }

                out << """
        }

        public override void WritePacket(Packet packet, DataOutput dataOut) throws IOException {
            super.writePacket(packet, dataOut);
            ${jclass.simpleName} info = (${jclass.simpleName}) packet;
"""
                for (property in properties) {
                    def getter = "info." + property.getter.simpleName + "()"
                    out << "            "

                    def type = property.type.qualifiedName
                    switch (type) {
                        case "java.lang.String":
                            out << "writeUTF($getter, dataOut);"
                            break;

                        case "org.activemq.message.ActiveMQDestination":
                            out << "writeDestination($getter, dataOut);"
                            break;

                        case "boolean":
                            out << "dataOut.writeBoolean($getter);"
                            break;

                        case "byte":
                            out << "dataOut.writeByte($getter);"
                            break;

                        case "char":
                            out << "dataOut.writeChar($getter);"
                            break;

                        case "short":
                            out << "dataOut.writeShort($getter);"
                            break;

                        case "int":
                            out << "dataOut.writeInt($getter);"
                            break;

                        case "long":
                            out << "dataOut.writeLong($getter);"
                            break;

                        case "float":
                            out << "dataOut.writeFloat($getter);"
                            break;

                        case "double":
                            out << "dataOut.writeDouble($getter);"
                            break;

                        default:
                            out << "writeObject($getter, dataOut);"
                    }
                    out << """
"""
                }

                out << """
        }
    }
}
"""
            }
        }
    }
}