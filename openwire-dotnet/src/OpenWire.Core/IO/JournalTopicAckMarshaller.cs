//
// Marshalling code for Open Wire Format for JournalTopicAck
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
    public class JournalTopicAckMarshaller : AbstractCommandMarshaller
    {


        public override Command CreateCommand() {
            return new JournalTopicAck();
        }

        public override void BuildCommand(Command command, BinaryReader dataIn) {
            base.BuildCommand(command, dataIn);

            JournalTopicAck info = (JournalTopicAck) command;
            info.Destination = ReadDestination(dataIn);
            info.MessageId = (MessageId) CommandMarshallerRegistry.MessageIdMarshaller.ReadCommand(dataIn);
            info.MessageSequenceId = dataIn.ReadInt64();
            info.SubscritionName = dataIn.ReadString();
            info.ClientId = dataIn.ReadString();
            info.TransactionId = (TransactionId) CommandMarshallerRegistry.ReadCommand(dataIn);

        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            base.WriteCommand(command, dataOut);

            JournalTopicAck info = (JournalTopicAck) command;
            WriteDestination(info.Destination, dataOut);
            CommandMarshallerRegistry.MessageIdMarshaller.WriteCommand(info.MessageId, dataOut);
            dataOut.Write(info.MessageSequenceId);
            dataOut.Write(info.SubscritionName);
            dataOut.Write(info.ClientId);
            CommandMarshallerRegistry.WriteCommand(info.TransactionId, dataOut);

        }
    }
}
