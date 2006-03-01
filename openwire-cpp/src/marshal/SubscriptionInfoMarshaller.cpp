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
#include "marshal/SubscriptionInfoMarshaller.hpp"

using namespace apache::activemq::client::marshal;

/*
 *  Marshalling code for Open Wire Format for SubscriptionInfo
 *
 * NOTE!: This file is autogenerated - do not modify!
 *        if you need to make a change, please see the Groovy scripts in the
 *        activemq-core module
 */

SubscriptionInfoMarshaller::SubscriptionInfoMarshaller()
{
    // no-op
}

SubscriptionInfoMarshaller::~SubscriptionInfoMarshaller()
{
    // no-op
}



DataStructure* SubscriptionInfoMarshaller::createObject() 
{
    return new SubscriptionInfo();
}

byte SubscriptionInfoMarshaller::getDataStructureType() 
{
    return SubscriptionInfo.ID_SubscriptionInfo;
}

    /* 
     * Un-marshal an object instance from the data input stream
     */ 
void SubscriptionInfoMarshaller::unmarshal(OpenWireFormat& wireFormat, Object o, BinaryReader& dataIn, BooleanStream& bs) 
{
    base.unmarshal(wireFormat, o, dataIn, bs);

    SubscriptionInfo& info = (SubscriptionInfo&) o;
        info.setClientId(tightUnmarshalString(dataIn, bs));
        info.setDestination((ActiveMQDestination) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setSelector(tightUnmarshalString(dataIn, bs));
        info.setSubcriptionName(tightUnmarshalString(dataIn, bs));

}


/*
 * Write the booleans that this object uses to a BooleanStream
 */
int SubscriptionInfoMarshaller::marshal1(OpenWireFormat& wireFormat, Object& o, BooleanStream& bs) {
    SubscriptionInfo& info = (SubscriptionInfo&) o;

    int rc = base.marshal1(wireFormat, info, bs);
    rc += writeString(info.getClientId(), bs);
    rc += marshal1CachedObject(wireFormat, info.getDestination(), bs);
    rc += writeString(info.getSelector(), bs);
    rc += writeString(info.getSubcriptionName(), bs);

    return rc + 0;
}

/* 
 * Write a object instance to data output stream
 */
void SubscriptionInfoMarshaller::marshal2(OpenWireFormat& wireFormat, Object& o, BinaryWriter& dataOut, BooleanStream& bs) {
    base.marshal2(wireFormat, o, dataOut, bs);

    SubscriptionInfo& info = (SubscriptionInfo&) o;
    writeString(info.getClientId(), dataOut, bs);
    marshal2CachedObject(wireFormat, info.getDestination(), dataOut, bs);
    writeString(info.getSelector(), dataOut, bs);
    writeString(info.getSubcriptionName(), dataOut, bs);

}
