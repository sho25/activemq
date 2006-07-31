/*
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at
  
  http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
*/

#include "netinet/in.h"
#include "marshal/ProducerIdMarshaller.h"
#include "command/ProducerId.h"
#include "boost/shared_ptr.hpp"

using namespace ActiveMQ::Marshalling;
using namespace ActiveMQ::Command;
using namespace ActiveMQ::IO;
using std::auto_ptr;
using boost::shared_ptr;

/*
 *  Marshalling code for Open Wire Format for ProducerId
 *
 * NOTE!: This file is autogenerated - do not modify!
 *        if you need to make a change, please see the Groovy scripts in the
 *        activemq-core module
 */

ProducerIdMarshaller::ProducerIdMarshaller()
{
    // no-op
}

ProducerIdMarshaller::~ProducerIdMarshaller()
{
    // no-op
}

auto_ptr<IDataStructure> ProducerIdMarshaller::createCommand() 
{
    return auto_ptr<IDataStructure>(new ProducerId());
}

char ProducerIdMarshaller::getDataStructureType() 
{
    return ProducerId::TYPE;
}

/* 
 * Un-marshal an object instance from the data input stream
 */ 
void
ProducerIdMarshaller::unmarshal(ProtocolFormat& wireFormat, IDataStructure& o, BinaryReader& dataIn, BooleanStream& bs) 
{

    ProducerId& info = (ProducerId&) o;
    info.setConnectionId( unmarshalString(wireFormat, dataIn, bs) );
    info.setValue( unmarshalLong(wireFormat, dataIn, bs) );
    info.setSessionId( unmarshalLong(wireFormat, dataIn, bs) );

}

/*
 * Write the booleans that this object uses to a BooleanStream
 */
size_t
ProducerIdMarshaller::marshal1(ProtocolFormat& wireFormat, const IDataStructure& o, BooleanStream& bs) {
    ProducerId& info = (ProducerId&) o;

    int rc = 0;
    rc += writeString1(info.getConnectionId(), bs);
    rc += writeLong1(info.getValue(), bs);
    rc += writeLong1(info.getSessionId(), bs);

    return rc + 0;
}

/* 
 * Write a object instance to data output stream
 */
void
ProducerIdMarshaller::marshal2(ProtocolFormat& wireFormat, const IDataStructure& o, BinaryWriter& dataOut, BooleanStream& bs) {
    // }


    ProducerId& info = (ProducerId&) o;
    writeString2(info.getConnectionId(), dataOut, bs);
    writeLong2(info.getValue(), dataOut, bs);
    writeLong2(info.getSessionId(), dataOut, bs);
}
