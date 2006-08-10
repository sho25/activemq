/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include "activemq/command/ConsumerId.hpp"

using namespace apache::activemq::command;

/*
 *
 *  Command and marshalling code for OpenWire format for ConsumerId
 *
 *
 *  NOTE!: This file is autogenerated - do not modify!
 *         if you need to make a change, please see the Groovy scripts in the
 *         activemq-core module
 *
 */
ConsumerId::ConsumerId()
{
    this->connectionId = NULL ;
    this->sessionId = 0 ;
    this->value = 0 ;
}

ConsumerId::~ConsumerId()
{
}

unsigned char ConsumerId::getDataStructureType()
{
    return ConsumerId::TYPE ; 
}

        
p<string> ConsumerId::getConnectionId()
{
    return connectionId ;
}

void ConsumerId::setConnectionId(p<string> connectionId)
{
    this->connectionId = connectionId ;
}

        
long long ConsumerId::getSessionId()
{
    return sessionId ;
}

void ConsumerId::setSessionId(long long sessionId)
{
    this->sessionId = sessionId ;
}

        
long long ConsumerId::getValue()
{
    return value ;
}

void ConsumerId::setValue(long long value)
{
    this->value = value ;
}

int ConsumerId::marshal(p<IMarshaller> marshaller, int mode, p<IOutputStream> ostream) throw (IOException)
{
    int size = 0 ;

    size += BaseDataStructure::marshal(marshaller, mode, ostream) ; 
    size += marshaller->marshalString(connectionId, mode, ostream) ; 
    size += marshaller->marshalLong(sessionId, mode, ostream) ; 
    size += marshaller->marshalLong(value, mode, ostream) ; 
    return size ;
}

void ConsumerId::unmarshal(p<IMarshaller> marshaller, int mode, p<IInputStream> istream) throw (IOException)
{
    BaseDataStructure::unmarshal(marshaller, mode, istream) ; 
    connectionId = p_cast<string>(marshaller->unmarshalString(mode, istream)) ; 
    sessionId = (marshaller->unmarshalLong(mode, istream)) ; 
    value = (marshaller->unmarshalLong(mode, istream)) ; 
}
