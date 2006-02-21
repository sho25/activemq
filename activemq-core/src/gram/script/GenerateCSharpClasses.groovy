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
import org.apache.activemq.openwire.tool.OpenWireCSharpClassesScript

/**
 * Generates the C# marshalling code for the Open Wire Format
 *
 * @version $Revision$
 */
class GenerateCSharpClasses extends OpenWireCSharpClassesScript {

	void generateFile(PrintWriter out) {
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

using OpenWire.Client;
using OpenWire.Client.Core;

namespace OpenWire.Client.Commands
{
    public class ${jclass.simpleName} : $baseClass
    {
    			public const byte ID_${jclass.simpleName} = ${getOpenWireOpCode(jclass)};
    			
"""
                for (property in properties) {

                    def type = toCSharpType(property.type)
                    def name = decapitalize(property.simpleName)
                    out << """        $type $name;
"""
                }

                out << """


        // TODO generate Equals method
        // TODO generate GetHashCode method
        // TODO generate ToString method


        public override byte GetCommandType() {
            return ID_${jclass.simpleName};
        }


        // Properties
"""
                for (property in properties) {
                    def type = toCSharpType(property.type)
                    def name = decapitalize(property.simpleName)
                    def propertyName = property.simpleName
                    def getter = capitalize(property.getter.simpleName)
                    def setter = capitalize(property.setter.simpleName)


                    out << """
        public $type $propertyName
        {
            get { return $name; }
            set { this.$name = value; }            
        }
"""
                }

                out << """
    }
}
"""
    }
}