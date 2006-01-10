//
// Marshalling code for Open Wire Format for MessageAck
//
//
// NOTE!: This file is autogenerated - do not modify!
//        if you need to make a change, please see the Groovy scripts in the
//        activemq-openwire module
//

using System;
using System.Collections;

using OpenWire.Core;

namespace OpenWire.Core.Commands
{
    public class MessageAck : BaseCommand
    {
    			public const byte ID_MessageAck = 22;
    			
        ActiveMQDestination destination;
        TransactionId transactionId;
        ConsumerId consumerId;
        byte ackType;
        MessageId firstMessageId;
        MessageId lastMessageId;
        int messageCount;



        // TODO generate Equals method
        // TODO generate GetHashCode method
        // TODO generate ToString method


        public override byte GetCommandType() {
            return ID_MessageAck;
        }


        // Properties

        public ActiveMQDestination Destination
        {
            get { return destination; }
            set { this.destination = value; }            
        }

        public TransactionId TransactionId
        {
            get { return transactionId; }
            set { this.transactionId = value; }            
        }

        public ConsumerId ConsumerId
        {
            get { return consumerId; }
            set { this.consumerId = value; }            
        }

        public byte AckType
        {
            get { return ackType; }
            set { this.ackType = value; }            
        }

        public MessageId FirstMessageId
        {
            get { return firstMessageId; }
            set { this.firstMessageId = value; }            
        }

        public MessageId LastMessageId
        {
            get { return lastMessageId; }
            set { this.lastMessageId = value; }            
        }

        public int MessageCount
        {
            get { return messageCount; }
            set { this.messageCount = value; }            
        }

    }
}
