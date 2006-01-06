//
// Marshalling code for Open Wire Format for DestinationInfo
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
    public class DestinationInfoMarshaller : AbstractCommandMarshaller
    {


        public override Command CreateCommand() {
            return new DestinationInfo();
        }

        public override void BuildCommand(Command command, BinaryReader dataIn) {
            base.BuildCommand(command, dataIn);

            DestinationInfo info = (DestinationInfo) command;
            info.ConnectionId = ReadConnectionId(dataIn);
            info.Destination = ReadDestination(dataIn);
            info.OperationType = dataIn.ReadByte();
            info.Timeout = dataIn.ReadInt64();
            info.BrokerPath = ReadBrokerIds(dataIn);

        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            base.WriteCommand(command, dataOut);

            DestinationInfo info = (DestinationInfo) command;
            WriteConnectionId(info.ConnectionId, dataOut);
            WriteDestination(info.Destination, dataOut);
            dataOut.Write(info.OperationType);
            dataOut.Write(info.Timeout);
            dataOut.WriteBrokerIds(info.BrokerPath);

        }
    }
}
