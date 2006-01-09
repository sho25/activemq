//
// Marshalling code for Open Wire Format for Message
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
    public abstract class MessageMarshaller : AbstractCommandMarshaller
    {


        public override void BuildCommand(Command command, BinaryReader dataIn) {
            base.BuildCommand(command, dataIn);

            Message info = (Message) command;
            info.ProducerId = (ProducerId) CommandMarshallerRegistry.ProducerIdMarshaller.ReadCommand(dataIn);
            info.Destination = ReadDestination(dataIn);
            info.TransactionId = (TransactionId) CommandMarshallerRegistry.ReadCommand(dataIn);
            info.OriginalDestination = ReadDestination(dataIn);
            info.MessageId = (MessageId) CommandMarshallerRegistry.MessageIdMarshaller.ReadCommand(dataIn);
            info.OriginalTransactionId = (TransactionId) CommandMarshallerRegistry.ReadCommand(dataIn);
            info.GroupID = dataIn.ReadString();
            info.GroupSequence = dataIn.ReadInt32();
            info.CorrelationId = dataIn.ReadString();
            info.Persistent = dataIn.ReadBoolean();
            info.Expiration = dataIn.ReadInt64();
            info.Priority = dataIn.ReadByte();
            info.ReplyTo = ReadDestination(dataIn);
            info.Timestamp = dataIn.ReadInt64();
            info.Type = dataIn.ReadString();
            info.Content = ReadBytes(dataIn);
            info.MarshalledProperties = ReadBytes(dataIn);
            info.DataStructure = CommandMarshallerRegistry.ReadCommand(dataIn);
            info.TargetConsumerId = (ConsumerId) CommandMarshallerRegistry.ConsumerIdMarshaller.ReadCommand(dataIn);
            info.Compressed = dataIn.ReadBoolean();
            info.RedeliveryCounter = dataIn.ReadInt32();
            info.BrokerPath = ReadBrokerIds(dataIn);
            info.Arrival = dataIn.ReadInt64();
            info.UserID = dataIn.ReadString();
            info.RecievedByDFBridge = dataIn.ReadBoolean();

        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            base.WriteCommand(command, dataOut);

            Message info = (Message) command;
            CommandMarshallerRegistry.ProducerIdMarshaller.WriteCommand(info.ProducerId, dataOut);
            WriteDestination(info.Destination, dataOut);
            CommandMarshallerRegistry.WriteCommand(info.TransactionId, dataOut);
            WriteDestination(info.OriginalDestination, dataOut);
            CommandMarshallerRegistry.MessageIdMarshaller.WriteCommand(info.MessageId, dataOut);
            CommandMarshallerRegistry.WriteCommand(info.OriginalTransactionId, dataOut);
            dataOut.Write(info.GroupID);
            dataOut.Write(info.GroupSequence);
            dataOut.Write(info.CorrelationId);
            dataOut.Write(info.Persistent);
            dataOut.Write(info.Expiration);
            dataOut.Write(info.Priority);
            WriteDestination(info.ReplyTo, dataOut);
            dataOut.Write(info.Timestamp);
            dataOut.Write(info.Type);
            WriteBytes(info.Content, dataOut);
            WriteBytes(info.MarshalledProperties, dataOut);
            CommandMarshallerRegistry.WriteCommand((Command) info.DataStructure, dataOut);
            CommandMarshallerRegistry.ConsumerIdMarshaller.WriteCommand(info.TargetConsumerId, dataOut);
            dataOut.Write(info.Compressed);
            dataOut.Write(info.RedeliveryCounter);
            WriteBrokerIds(info.BrokerPath, dataOut);
            dataOut.Write(info.Arrival);
            dataOut.Write(info.UserID);
            dataOut.Write(info.RecievedByDFBridge);

        }
    }
}
