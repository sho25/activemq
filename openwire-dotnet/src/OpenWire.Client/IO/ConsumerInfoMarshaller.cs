//
//
// Copyright 2005-2006 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

using System;
using System.Collections;
using System.IO;

using OpenWire.Client;
using OpenWire.Client.Commands;
using OpenWire.Client.Core;
using OpenWire.Client.IO;

namespace OpenWire.Client.IO
{
  //
  //  Marshalling code for Open Wire Format for ConsumerInfo
  //
  //
  //  NOTE!: This file is autogenerated - do not modify!
  //        if you need to make a change, please see the Groovy scripts in the
  //        activemq-core module
  //
  public class ConsumerInfoMarshaller : BaseCommandMarshaller
  {


    public override DataStructure CreateObject() 
    {
        return new ConsumerInfo();
    }

    public override byte GetDataStructureType() 
    {
        return ConsumerInfo.ID_ConsumerInfo;
    }

    // 
    // Un-marshal an object instance from the data input stream
    // 
    public override void TightUnmarshal(OpenWireFormat wireFormat, Object o, BinaryReader dataIn, BooleanStream bs) 
    {
        base.TightUnmarshal(wireFormat, o, dataIn, bs);

        ConsumerInfo info = (ConsumerInfo)o;
        info.ConsumerId = (ConsumerId) TightUnmarshalCachedObject(wireFormat, dataIn, bs);
        info.Browser = bs.ReadBoolean();
        info.Destination = (ActiveMQDestination) TightUnmarshalCachedObject(wireFormat, dataIn, bs);
        info.PrefetchSize = BaseDataStreamMarshaller.ReadInt(dataIn);
        info.DispatchAsync = bs.ReadBoolean();
        info.Selector = TightUnmarshalString(dataIn, bs);
        info.SubcriptionName = TightUnmarshalString(dataIn, bs);
        info.NoLocal = bs.ReadBoolean();
        info.Exclusive = bs.ReadBoolean();
        info.Retroactive = bs.ReadBoolean();
        info.Priority = BaseDataStreamMarshaller.ReadByte(dataIn);

        if (bs.ReadBoolean()) {
            short size = BaseDataStreamMarshaller.ReadShort(dataIn);
            BrokerId[] value = new BrokerId[size];
            for( int i=0; i < size; i++ ) {
                value[i] = (BrokerId) TightUnmarshalNestedObject(wireFormat,dataIn, bs);
            }
            info.BrokerPath = value;
        }
        else {
            info.BrokerPath = null;
        }
        info.NetworkSubscription = bs.ReadBoolean();

    }


    //
    // Write the booleans that this object uses to a BooleanStream
    //
    public override int TightMarshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) {
        ConsumerInfo info = (ConsumerInfo)o;

        int rc = base.TightMarshal1(wireFormat, info, bs);
    rc += TightMarshalCachedObject1(wireFormat, info.ConsumerId, bs);
    bs.WriteBoolean(info.Browser);
    rc += TightMarshalCachedObject1(wireFormat, info.Destination, bs);
        bs.WriteBoolean(info.DispatchAsync);
    rc += TightMarshalString1(info.Selector, bs);
    rc += TightMarshalString1(info.SubcriptionName, bs);
    bs.WriteBoolean(info.NoLocal);
    bs.WriteBoolean(info.Exclusive);
    bs.WriteBoolean(info.Retroactive);
        rc += TightMarshalObjectArray1(wireFormat, info.BrokerPath, bs);
    bs.WriteBoolean(info.NetworkSubscription);

        return rc + 5;
    }

    // 
    // Write a object instance to data output stream
    //
    public override void TightMarshal2(OpenWireFormat wireFormat, Object o, BinaryWriter dataOut, BooleanStream bs) {
        base.TightMarshal2(wireFormat, o, dataOut, bs);

        ConsumerInfo info = (ConsumerInfo)o;
    TightMarshalCachedObject2(wireFormat, info.ConsumerId, dataOut, bs);
    bs.ReadBoolean();
    TightMarshalCachedObject2(wireFormat, info.Destination, dataOut, bs);
    BaseDataStreamMarshaller.WriteInt(info.PrefetchSize, dataOut);
    bs.ReadBoolean();
    TightMarshalString2(info.Selector, dataOut, bs);
    TightMarshalString2(info.SubcriptionName, dataOut, bs);
    bs.ReadBoolean();
    bs.ReadBoolean();
    bs.ReadBoolean();
    BaseDataStreamMarshaller.WriteByte(info.Priority, dataOut);
    TightMarshalObjectArray2(wireFormat, info.BrokerPath, dataOut, bs);
    bs.ReadBoolean();

    }
  }
}
