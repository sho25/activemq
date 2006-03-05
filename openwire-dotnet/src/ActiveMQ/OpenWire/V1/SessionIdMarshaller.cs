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
  //  Marshalling code for Open Wire Format for SessionId
  //
  //
  //  NOTE!: This file is autogenerated - do not modify!
  //        if you need to make a change, please see the Groovy scripts in the
  //        activemq-core module
  //
  public class SessionIdMarshaller : BaseDataStreamMarshaller
  {


    public override DataStructure CreateObject() 
    {
        return new SessionId();
    }

    public override byte GetDataStructureType() 
    {
        return SessionId.ID_SessionId;
    }

    // 
    // Un-marshal an object instance from the data input stream
    // 
    public override void TightUnmarshal(OpenWireFormat wireFormat, Object o, BinaryReader dataIn, BooleanStream bs) 
    {
        base.TightUnmarshal(wireFormat, o, dataIn, bs);

        SessionId info = (SessionId)o;
        info.ConnectionId = TightUnmarshalString(dataIn, bs);
        info.Value = TightUnmarshalLong(wireFormat, dataIn, bs);

    }


    //
    // Write the booleans that this object uses to a BooleanStream
    //
    public override int TightMarshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) {
        SessionId info = (SessionId)o;

        int rc = base.TightMarshal1(wireFormat, info, bs);
    rc += TightMarshalString1(info.ConnectionId, bs);
    rc += TightMarshalLong1(wireFormat, info.Value, bs);

        return rc + 0;
    }

    // 
    // Write a object instance to data output stream
    //
    public override void TightMarshal2(OpenWireFormat wireFormat, Object o, BinaryWriter dataOut, BooleanStream bs) {
        base.TightMarshal2(wireFormat, o, dataOut, bs);

        SessionId info = (SessionId)o;
    TightMarshalString2(info.ConnectionId, dataOut, bs);
    TightMarshalLong2(wireFormat, info.Value, dataOut, bs);

    }
  }
}
