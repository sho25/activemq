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
#include "marshal/JournalTopicAckMarshaller.h"
#include "command/JournalTopicAck.h"
#include "boost/shared_ptr.hpp"

using namespace ActiveMQ::Marshalling;
using namespace ActiveMQ::Command;
using namespace ActiveMQ::IO;
using std::auto_ptr;
using boost::shared_ptr;

/*
 *  Marshalling code for Open Wire Format for JournalTopicAck
 *
 * NOTE!: This file is autogenerated - do not modify!
 *        if you need to make a change, please see the Groovy scripts in the
 *        activemq-core module
 */

JournalTopicAckMarshaller::JournalTopicAckMarshaller()
{
    // no-op
}

JournalTopicAckMarshaller::~JournalTopicAckMarshaller()
{
    // no-op
}

auto_ptr<IDataStructure> JournalTopicAckMarshaller::createCommand() 
{
    return auto_ptr<IDataStructure>(new JournalTopicAck());
}

char JournalTopicAckMarshaller::getDataStructureType() 
{
    return JournalTopicAck::TYPE;
}

/* 
 * Un-marshal an object instance from the data input stream
 */ 
void
JournalTopicAckMarshaller::unmarshal(ProtocolFormat& wireFormat, IDataStructure& o, BinaryReader& dataIn, BooleanStream& bs) 
{

    JournalTopicAck& info = (JournalTopicAck&) o;
    info.setDestination( shared_ptr<ActiveMQDestination>(static_cast<ActiveMQDestination*>(unmarshalNestedObject(wireFormat, dataIn, bs).release())) );
    info.setMessageId( shared_ptr<MessageId>(static_cast<MessageId*>(unmarshalNestedObject(wireFormat, dataIn, bs).release())) );
    info.setMessageSequenceId( unmarshalLong(wireFormat, dataIn, bs) );
    info.setSubscritionName( unmarshalString(wireFormat, dataIn, bs) );
    info.setClientId( unmarshalString(wireFormat, dataIn, bs) );
    info.setTransactionId( shared_ptr<TransactionId>(static_cast<TransactionId*>(unmarshalNestedObject(wireFormat, dataIn, bs).release())) );

}

/*
 * Write the booleans that this object uses to a BooleanStream
 */
size_t
JournalTopicAckMarshaller::marshal1(ProtocolFormat& wireFormat, const IDataStructure& o, BooleanStream& bs) {
    JournalTopicAck& info = (JournalTopicAck&) o;

    int rc = 0;
    rc += marshal1NestedObject(wireFormat, info.getDestination(), bs);
    rc += marshal1NestedObject(wireFormat, info.getMessageId(), bs);
    rc += writeLong1(info.getMessageSequenceId(), bs);
    rc += writeString1(info.getSubscritionName(), bs);
    rc += writeString1(info.getClientId(), bs);
    rc += marshal1NestedObject(wireFormat, info.getTransactionId(), bs);

    return rc + 0;
}

/* 
 * Write a object instance to data output stream
 */
void
JournalTopicAckMarshaller::marshal2(ProtocolFormat& wireFormat, const IDataStructure& o, BinaryWriter& dataOut, BooleanStream& bs) {
    // }


    JournalTopicAck& info = (JournalTopicAck&) o;
    marshal2NestedObject(wireFormat, info.getDestination(), dataOut, bs);
    marshal2NestedObject(wireFormat, info.getMessageId(), dataOut, bs);
    writeLong2(info.getMessageSequenceId(), dataOut, bs);
    writeString2(info.getSubscritionName(), dataOut, bs);
    writeString2(info.getClientId(), dataOut, bs);
    marshal2NestedObject(wireFormat, info.getTransactionId(), dataOut, bs);
}
