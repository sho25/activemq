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
 * Generates the Java marshalling code for the Open Wire Format
 *
 * @version $Revision$
 */
class GenerateCSharpMarshalling extends OpenWireScript {

    Object run() {
        def destDir = new File("../openwire-dotnet/src/OpenWire.Core/IO")
        destDir.mkdirs()

        def messageClasses = classes.findAll {
        		it.getAnnotation("openwire:marshaller")!=null
        }

        println "Generating Java marshalling code to directory ${destDir}"

        def buffer = new StringBuffer()
        def readMethodBuffer = new StringBuffer()
        def writeMethodBuffer = new StringBuffer()

        int counter = 0
        Map map = [:]
        def propertyList = null
        def type = null

        for (jclass in messageClasses) {

            println "Processing $jclass.simpleName"

            propertyList = jclass.declaredProperties.findAll { isValidProperty(it) }
            def file = new File(destDir, jclass.simpleName + "Marshaller.cs")

            String baseClass = "AbstractCommandMarshaller"
            if (jclass.superclass?.simpleName == "ActiveMQMessage") {
                baseClass = "ActiveMQMessageMarshaller"
            }
            
            def notAbstract = jclass.simpleName != "ActiveMQDestination"
            def abstractText = (notAbstract) ? "" : "abstract "
					
					 def marshallerType = jclass.simpleName + "Marshaller"
					 def marshallerField = decapitalize(marshallerType)

					 if (notAbstract) {
	            buffer << """
	            
	      private static $marshallerType $marshallerField = new $marshallerType();
	      
        public static $marshallerType $marshallerType
        {
            get
            {
                return $marshallerField;
            }
        }

"""
						readMethodBuffer << """
				case ${jclass.simpleName}.ID_${jclass.simpleName}:
						return ${marshallerField}.ReadCommand(dataIn);
						
"""						
						writeMethodBuffer << """
				case ${jclass.simpleName}.ID_${jclass.simpleName}:
						${marshallerField}.WriteCommand(command, dataOut);
						
"""						
					 }
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
using System.IO;

using OpenWire.Core;
using OpenWire.Core.Commands;
using OpenWire.Core.IO;

namespace OpenWire.Core.IO
{
    public ${abstractText}class $marshallerType : $baseClass
    {

"""
							 if (notAbstract) 
	                out << """
        public override Command CreateCommand() {
            return new ${jclass.simpleName}();
        }
"""
                out << """
        public override void BuildCommand(Command command, BinaryReader dataIn) {
            base.BuildCommand(command, dataIn);
"""
							 if (!propertyList.empty) {
                    out << """
            ${jclass.simpleName} info = (${jclass.simpleName}) command;
"""
							 }
                for (property in propertyList) {
                		 def propertyName = property.simpleName
                    if (propertyName == jclass.simpleName) {
                        // TODO think of a better naming convention :)
                    		propertyName += "Value"
                    }
                    out << "            info.${propertyName} = "

                    type = property.type.simpleName
                    switch (type) {
                        case "String":
                            out << "dataIn.ReadString()"
                            break;

                        case "boolean":
                            out << "dataIn.ReadBoolean()"
                            break;

                        case "byte":
                            out << "dataIn.ReadByte()"
                            break;

                        case "byte[]":
                            out << "ReadBytes(dataIn)"
                            break;

                        case "char":
                            out << "dataIn.ReadChar()"
                            break;

                        case "short":
                            out << "dataIn.ReadInt16()"
                            break;

                        case "int":
                            out << "dataIn.ReadInt32()"
                            break;

                        case "long":
                            out << "dataIn.ReadInt64()"
                            break;

                        case "float":
                            out << "dataIn.ReadDecimal()"
                            break;

                        case "double":
                            out << "dataIn.ReadDouble()"
                            break;
                            
                        case "ActiveMQDestination":
                            out << "ReadDestination(dataIn)"
                            break;

                        case "BrokerId[]":
                            out << "ReadBrokerIds(dataIn)"
                            break;

                        case "BrokerInfo[]":
                            out << "ReadBrokerInfos(dataIn)"
                            break;

                        case "DataStructure[]":
                            out << "ReadDataStructures(dataIn)"
                            break;

                        default:
                            out << "(${type}) CommandMarshallerRegistry.${type}Marshaller.ReadCommand(dataIn)"
                    }
                    out << """;
"""
                }

                out << """
        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            base.WriteCommand(command, dataOut);
"""
							 if (!propertyList.empty) {
                    out << """
            ${jclass.simpleName} info = (${jclass.simpleName}) command;
"""
							 }
							 
                for (property in propertyList) {
                		 def propertyName = property.simpleName
                    if (propertyName == jclass.simpleName) {
                        // TODO think of a better naming convention :)
                    		propertyName += "Value"
                    }
                    def getter = "info." + propertyName
                    out << "            "

                    type = property.type.simpleName
                    switch (type) {
                        case "String":
                            out << "dataOut.Write($getter);"
                            break;

                        case "boolean":
                            out << "dataOut.Write($getter);"
                            break;

                        case "byte":
                            out << "dataOut.Write($getter);"
                            break;

                        case "byte[]":
                            out << "WriteBytes($getter, dataOut);"
                            break;

                        case "char":
                            out << "dataOut.Write($getter);"
                            break;

                        case "short":
                            out << "dataOut.Write($getter);"
                            break;

                        case "int":
                            out << "dataOut.Write($getter);"
                            break;

                        case "long":
                            out << "dataOut.Write($getter);"
                            break;

                        case "float":
                            out << "dataOut.Write($getter);"
                            break;

                        case "double":
                            out << "dataOut.Write($getter);"
                            break;

                        case "ActiveMQDestination":
                            out << "WriteDestination($getter, dataOut);"
                            break;

                        case "BrokerId[]":
                            out << "WriteBrokerIds($getter, dataOut);"
                            break;

                        case "BrokerInfo[]":
                            out << "WriteBrokerInfos($getter, dataOut);"
                            break;

                        case "DataStructure[]":
                            out << "WriteDataStructures($getter, dataOut);"
                            break;

                        default:
                            out << "CommandMarshallerRegistry.${type}Marshaller.WriteCommand($getter, dataOut);"
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
        
        def file = new File(destDir, "CommandMarshallerRegistry.cs")
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
using System.IO;

using OpenWire.Core;
using OpenWire.Core.Commands;
using OpenWire.Core.IO;

namespace OpenWire.Core.IO
{
    public class CommandMarshallerRegistry
    {
				public static Command ReadCommand(BinaryReader dataIn) 
				{
						byte commandID = dataIn.ReadByte();
						switch (commandID) 
						{
$readMethodBuffer						
								default:
										throw new Exception("Unknown command type: " + commandID);
						}
				}


				public static void WriteCommand(Command command, BinaryWriter dataOut) 
				{
				    int commandID = command.CommandType;
						dataOut.Write(commandID);
						switch (commandID) 
						{
$readMethodBuffer						
								default:
										throw new Exception("Unknown command type: " + commandID);
						}
				}


				// Properties     
$buffer
		
    }
}
"""

				}		
    }
}