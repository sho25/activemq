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
#include "marshal/MessageIdMarshaller.hpp"

using namespace apache::activemq::client::marshal;

/*
 *  Marshalling code for Open Wire Format for MessageId
 *
 * NOTE!: This file is autogenerated - do not modify!
 *        if you need to make a change, please see the Groovy scripts in the
 *        activemq-core module
 */

MessageIdMarshaller::MessageIdMarshaller()
{
    // no-op
}

MessageIdMarshaller::~MessageIdMarshaller()
{
    // no-op
}



DataStructure* MessageIdMarshaller::createObject() 
{
    return new MessageId();
}

byte MessageIdMarshaller::getDataStructureType() 
{
    return MessageId.ID_MessageId;
}

    /* 
     * Un-marshal an object instance from the data input stream
     */ 
void MessageIdMarshaller::unmarshal(OpenWireFormat& wireFormat, Object o, BinaryReader& dataIn, BooleanStream& bs) 
{
    base.unmarshal(wireFormat, o, dataIn, bs);

    MessageId& info = (MessageId&) o;
        info.setProducerId((org.apache.activemq.command.ProducerId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setProducerSequenceId(tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setBrokerSequenceId(tightUnmarshalLong(wireFormat, dataIn, bs));

}


/*
 * Write the booleans that this object uses to a BooleanStream
 */
int MessageIdMarshaller::marshal1(OpenWireFormat& wireFormat, Object& o, BooleanStream& bs) {
    MessageId& info = (MessageId&) o;

    int rc = base.marshal1(wireFormat, info, bs);
    rc += marshal1CachedObject(wireFormat, info.getProducerId(), bs);
    rc += marshal1Long(wireFormat, info.getProducerSequenceId(), bs);
    rc += marshal1Long(wireFormat, info.getBrokerSequenceId(), bs);

    return rc + 0;
}

/* 
 * Write a object instance to data output stream
 */
void MessageIdMarshaller::marshal2(OpenWireFormat& wireFormat, Object& o, BinaryWriter& dataOut, BooleanStream& bs) {
    base.marshal2(wireFormat, o, dataOut, bs);

    MessageId& info = (MessageId&) o;
    marshal2CachedObject(wireFormat, info.getProducerId(), dataOut, bs);
    marshal2Long(wireFormat, info.getProducerSequenceId(), dataOut, bs);
    marshal2Long(wireFormat, info.getBrokerSequenceId(), dataOut, bs);

}
