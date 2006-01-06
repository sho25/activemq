//
// Marshalling code for Open Wire Format for ShutdownInfo
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
    public class ShutdownInfoMarshaller : AbstractCommandMarshaller
    {

        public override Command CreateCommand() {
            return new ShutdownInfo();
        }

        public override void BuildCommand(Command command, BinaryReader dataIn) {
            base.BuildCommand(command, dataIn);
            ShutdownInfo info = (ShutdownInfo) command;

        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            base.WriteCommand(command, dataOut);
            ShutdownInfo info = (ShutdownInfo) command;

        }
    }
}
