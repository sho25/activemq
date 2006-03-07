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

using ActiveMQ.Commands;
using ActiveMQ.OpenWire;
using ActiveMQ.OpenWire.V1;

namespace ActiveMQ.OpenWire.V1
{
  //
  //  Marshalling code for Open Wire Format for ConnectionInfo
  //
  //
  //  NOTE!: This file is autogenerated - do not modify!
  //        if you need to make a change, please see the Groovy scripts in the
  //        activemq-core module
  //
  public class ConnectionInfoMarshaller : BaseCommandMarshaller
  {


    public override DataStructure CreateObject() 
    {
        return new ConnectionInfo();
    }

    public override byte GetDataStructureType() 
    {
        return ConnectionInfo.ID_ConnectionInfo;
    }

    // 
    // Un-marshal an object instance from the data input stream
    // 
    public override void TightUnmarshal(OpenWireFormat wireFormat, Object o, BinaryReader dataIn, BooleanStream bs) 
    {
        base.TightUnmarshal(wireFormat, o, dataIn, bs);

        ConnectionInfo info = (ConnectionInfo)o;
        info.ConnectionId = (ConnectionId) TightUnmarshalCachedObject(wireFormat, dataIn, bs);
        info.ClientId = TightUnmarshalString(dataIn, bs);
        info.Password = TightUnmarshalString(dataIn, bs);
        info.UserName = TightUnmarshalString(dataIn, bs);

        if (bs.ReadBoolean()) {
            short size = dataIn.ReadInt16();
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
        ConnectionInfo info = (ConnectionInfo)o;

        int rc = base.TightMarshal1(wireFormat, info, bs);
        rc += TightMarshalCachedObject1(wireFormat, (DataStructure)info.ConnectionId, bs);
        rc += TightMarshalString1(info.ClientId, bs);
        rc += TightMarshalString1(info.Password, bs);
        rc += TightMarshalString1(info.UserName, bs);
        rc += TightMarshalObjectArray1(wireFormat, info.BrokerPath, bs);

        return rc + 0;
    }

    // 
    // Write a object instance to data output stream
    //
    public override void TightMarshal2(OpenWireFormat wireFormat, Object o, BinaryWriter dataOut, BooleanStream bs) {
        base.TightMarshal2(wireFormat, o, dataOut, bs);

        ConnectionInfo info = (ConnectionInfo)o;
        TightMarshalCachedObject2(wireFormat, (DataStructure)info.ConnectionId, dataOut, bs);
        TightMarshalString2(info.ClientId, dataOut, bs);
        TightMarshalString2(info.Password, dataOut, bs);
        TightMarshalString2(info.UserName, dataOut, bs);
        TightMarshalObjectArray2(wireFormat, info.BrokerPath, dataOut, bs);

    }
  }
}
