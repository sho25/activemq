/*
* Copyright 2006 The Apache Software Foundation or its licensors, as
* applicable.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
#include "activemq/command/ControlCommand.hpp"

using namespace apache::activemq::command;

/*
 *
 *  Command and marshalling code for OpenWire format for ControlCommand
 *
 *
 *  NOTE!: This file is autogenerated - do not modify!
 *         if you need to make a change, please see the Groovy scripts in the
 *         activemq-core module
 *
 */
ControlCommand::ControlCommand()
{
    this->command = NULL ;
}

ControlCommand::~ControlCommand()
{
}

unsigned char ControlCommand::getDataStructureType()
{
    return ControlCommand::TYPE ; 
}

        
p<string> ControlCommand::getCommand()
{
    return command ;
}

void ControlCommand::setCommand(p<string> command)
{
    this->command = command ;
}

int ControlCommand::marshal(p<IMarshaller> marshaller, int mode, p<IOutputStream> ostream) throw (IOException)
{
    int size = 0 ;

    size += BaseCommand::marshal(marshaller, mode, ostream) ; 
    size += marshaller->marshalString(command, mode, ostream) ; 
    return size ;
}

void ControlCommand::unmarshal(p<IMarshaller> marshaller, int mode, p<IInputStream> istream) throw (IOException)
{
    BaseCommand::unmarshal(marshaller, mode, istream) ; 
    command = p_cast<string>(marshaller->unmarshalString(mode, istream)) ; 
}
