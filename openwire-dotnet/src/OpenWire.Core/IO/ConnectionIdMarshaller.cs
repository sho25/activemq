//
// Marshalling code for Open Wire Format for ConnectionId
//
//
// NOTE!: This file is autogenerated - do not modify!
//        if you need to make a change, please see the Groovy scripts in the
//        activemq-openwire module
//

using System;
using System.Collections;
using System.IO;

using OpenWire.Core;
using OpenWire.Core.Commands;
using OpenWire.Core.IO;

namespace OpenWire.Core.IO
{
    public class ConnectionIdMarshaller : AbstractCommandMarshaller
    {

        public override Command CreateCommand() {
            return new ConnectionId();
        }

        public override void BuildCommand(Command command, BinaryReader dataIn) {
            super.buildCommand(command, dataIn);
            ConnectionId info = (ConnectionId) command;
            info.setConnectionId(dataIn.readUTF());

        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            super.writeCommand(command, dataOut);
            ConnectionId info = (ConnectionId) command;
            writeUTF(info.getConnectionId(), dataOut);

        }
    }
}
