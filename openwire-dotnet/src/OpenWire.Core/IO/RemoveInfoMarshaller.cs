//
// Marshalling code for Open Wire Format for RemoveInfo
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
    public class RemoveInfoMarshaller : AbstractCommandMarshaller
    {


        public override Command CreateCommand() {
            return new RemoveInfo();
        }

        public override void BuildCommand(Command command, BinaryReader dataIn) {
            base.BuildCommand(command, dataIn);

            RemoveInfo info = (RemoveInfo) command;
            info.ObjectId = (DataStructure) CommandMarshallerRegistry.DataStructureMarshaller.ReadCommand(dataIn);

        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            base.WriteCommand(command, dataOut);

            RemoveInfo info = (RemoveInfo) command;
            CommandMarshallerRegistry.DataStructureMarshaller.WriteCommand(info.ObjectId, dataOut);

        }
    }
}
