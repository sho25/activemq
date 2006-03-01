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
#include "marshal/WireFormatInfoMarshaller.hpp"

using namespace apache::activemq::client::marshal;

/*
 *  Marshalling code for Open Wire Format for WireFormatInfo
 *
 * NOTE!: This file is autogenerated - do not modify!
 *        if you need to make a change, please see the Groovy scripts in the
 *        activemq-core module
 */

WireFormatInfoMarshaller::WireFormatInfoMarshaller()
{
    // no-op
}

WireFormatInfoMarshaller::~WireFormatInfoMarshaller()
{
    // no-op
}



DataStructure* WireFormatInfoMarshaller::createObject() 
{
    return new WireFormatInfo();
}

byte WireFormatInfoMarshaller::getDataStructureType() 
{
    return WireFormatInfo.ID_WireFormatInfo;
}

    /* 
     * Un-marshal an object instance from the data input stream
     */ 
void WireFormatInfoMarshaller::unmarshal(OpenWireFormat& wireFormat, Object o, BinaryReader& dataIn, BooleanStream& bs) 
{
    base.unmarshal(wireFormat, o, dataIn, bs);

    WireFormatInfo& info = (WireFormatInfo&) o;
        info.setMagic(tightUnmarshalConstByteArray(dataIn, bs, 8));
        info.setVersion(dataIn.readInt());
        info.setCacheEnabled(bs.readBoolean());
        info.setStackTraceEnabled(bs.readBoolean());
        info.setTcpNoDelayEnabled(bs.readBoolean());
        info.setPrefixPacketSize(bs.readBoolean());
        info.setTightEncodingEnabled(bs.readBoolean());

}


/*
 * Write the booleans that this object uses to a BooleanStream
 */
int WireFormatInfoMarshaller::marshal1(OpenWireFormat& wireFormat, Object& o, BooleanStream& bs) {
    WireFormatInfo& info = (WireFormatInfo&) o;

    int rc = base.marshal1(wireFormat, info, bs);
            bs.writeBoolean(info.isCacheEnabled());
    bs.writeBoolean(info.isStackTraceEnabled());
    bs.writeBoolean(info.isTcpNoDelayEnabled());
    bs.writeBoolean(info.isPrefixPacketSize());
    bs.writeBoolean(info.isTightEncodingEnabled());

    return rc + 9;
}

/* 
 * Write a object instance to data output stream
 */
void WireFormatInfoMarshaller::marshal2(OpenWireFormat& wireFormat, Object& o, BinaryWriter& dataOut, BooleanStream& bs) {
    base.marshal2(wireFormat, o, dataOut, bs);

    WireFormatInfo& info = (WireFormatInfo&) o;
    dataOut.write(info.getMagic(), 0, 8);
    DataStreamMarshaller.writeInt(info.getVersion(), dataOut);
    bs.readBoolean();
    bs.readBoolean();
    bs.readBoolean();
    bs.readBoolean();
    bs.readBoolean();

}
