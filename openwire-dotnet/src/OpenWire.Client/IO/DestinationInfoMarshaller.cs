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
  //  Marshalling code for Open Wire Format for DestinationInfo
  //
  //
  //  NOTE!: This file is autogenerated - do not modify!
  //        if you need to make a change, please see the Groovy scripts in the
  //        activemq-core module
  //
  public class DestinationInfoMarshaller : BaseCommandMarshaller
  {


    public override DataStructure CreateObject() 
    {
        return new DestinationInfo();
    }

    public override byte GetDataStructureType() 
    {
        return DestinationInfo.ID_DestinationInfo;
    }

    // 
    // Un-marshal an object instance from the data input stream
    // 
    public override void TightUnmarshal(OpenWireFormat wireFormat, Object o, BinaryReader dataIn, BooleanStream bs) 
    {
        base.TightUnmarshal(wireFormat, o, dataIn, bs);

        DestinationInfo info = (DestinationInfo)o;
        info.ConnectionId = (ConnectionId) TightUnmarshalCachedObject(wireFormat, dataIn, bs);
        info.Destination = (ActiveMQDestination) TightUnmarshalCachedObject(wireFormat, dataIn, bs);
        info.OperationType = BaseDataStreamMarshaller.ReadByte(dataIn);
        info.Timeout = TightUnmarshalLong(wireFormat, dataIn, bs);

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

    }


    //
    // Write the booleans that this object uses to a BooleanStream
    //
    public override int TightMarshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) {
        DestinationInfo info = (DestinationInfo)o;

        int rc = base.TightMarshal1(wireFormat, info, bs);
    rc += TightMarshalCachedObject1(wireFormat, info.ConnectionId, bs);
    rc += TightMarshalCachedObject1(wireFormat, info.Destination, bs);
        rc += TightMarshalLong1(wireFormat, info.Timeout, bs);
    rc += TightMarshalObjectArray1(wireFormat, info.BrokerPath, bs);

        return rc + 1;
    }

    // 
    // Write a object instance to data output stream
    //
    public override void TightMarshal2(OpenWireFormat wireFormat, Object o, BinaryWriter dataOut, BooleanStream bs) {
        base.TightMarshal2(wireFormat, o, dataOut, bs);

        DestinationInfo info = (DestinationInfo)o;
    TightMarshalCachedObject2(wireFormat, info.ConnectionId, dataOut, bs);
    TightMarshalCachedObject2(wireFormat, info.Destination, dataOut, bs);
    BaseDataStreamMarshaller.WriteByte(info.OperationType, dataOut);
    TightMarshalLong2(wireFormat, info.Timeout, dataOut, bs);
    TightMarshalObjectArray2(wireFormat, info.BrokerPath, dataOut, bs);

    }
  }
}
