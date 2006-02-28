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
  //  Marshalling code for Open Wire Format for TransactionInfo
  //
  //
  //  NOTE!: This file is autogenerated - do not modify!
  //        if you need to make a change, please see the Groovy scripts in the
  //        activemq-core module
  //
  public class TransactionInfoMarshaller : BaseCommandMarshaller
  {


    public override DataStructure CreateObject() 
    {
        return new TransactionInfo();
    }

    public override byte GetDataStructureType() 
    {
        return TransactionInfo.ID_TransactionInfo;
    }

    // 
    // Un-marshal an object instance from the data input stream
    // 
    public override void Unmarshal(OpenWireFormat wireFormat, Object o, BinaryReader dataIn, BooleanStream bs) 
    {
        base.Unmarshal(wireFormat, o, dataIn, bs);

        TransactionInfo info = (TransactionInfo)o;
        info.ConnectionId = (ConnectionId) UnmarshalCachedObject(wireFormat, dataIn, bs);
        info.TransactionId = (TransactionId) UnmarshalCachedObject(wireFormat, dataIn, bs);
        info.Type = DataStreamMarshaller.ReadByte(dataIn);

    }


    //
    // Write the booleans that this object uses to a BooleanStream
    //
    public override int Marshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) {
        TransactionInfo info = (TransactionInfo)o;

        int rc = base.Marshal1(wireFormat, info, bs);
    rc += Marshal1CachedObject(wireFormat, info.ConnectionId, bs);
    rc += Marshal1CachedObject(wireFormat, info.TransactionId, bs);
    
        return rc + 1;
    }

    // 
    // Write a object instance to data output stream
    //
    public override void Marshal2(OpenWireFormat wireFormat, Object o, BinaryWriter dataOut, BooleanStream bs) {
        base.Marshal2(wireFormat, o, dataOut, bs);

        TransactionInfo info = (TransactionInfo)o;
    Marshal2CachedObject(wireFormat, info.ConnectionId, dataOut, bs);
    Marshal2CachedObject(wireFormat, info.TransactionId, dataOut, bs);
    DataStreamMarshaller.WriteByte(info.Type, dataOut);

    }
  }
}
