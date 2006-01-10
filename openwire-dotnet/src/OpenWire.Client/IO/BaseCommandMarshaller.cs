//
// Marshalling code for Open Wire Format for BaseCommand
//
//
// NOTE!: This file is autogenerated - do not modify!
//        if you need to make a change, please see the Groovy scripts in the
//        activemq-openwire module
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
    public abstract class BaseCommandMarshaller : AbstractCommandMarshaller
    {


        public override void BuildCommand(Command command, BinaryReader dataIn) {
            base.BuildCommand(command, dataIn);

            BaseCommand info = (BaseCommand) command;
            info.CommandId = dataIn.ReadInt16();
            info.ResponseRequired = dataIn.ReadBoolean();

        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            base.WriteCommand(command, dataOut);

            BaseCommand info = (BaseCommand) command;
            dataOut.Write(info.CommandId);
            dataOut.Write(info.ResponseRequired);

        }
    }
}
