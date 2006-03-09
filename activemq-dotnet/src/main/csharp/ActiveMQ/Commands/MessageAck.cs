/** Copyright 2006 The Apache Software Foundation or its licensors, as* applicable.** Licensed under the Apache License, Version 2.0 (the "License");* you may not use this file except in compliance with the License.* You may obtain a copy of the License at**     http://www.apache.org/licenses/LICENSE-2.0** Unless required by applicable law or agreed to in writing, software* distributed under the License is distributed on an "AS IS" BASIS,* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.* See the License for the specific language governing permissions and* limitations under the License.*/////  NOTE!: This file is autogenerated - do not modify!//         if you need to make a change, please see the Groovy scripts in the//         activemq-core module//using System;using System.Collections;using ActiveMQ.OpenWire;using ActiveMQ.Commands;namespace ActiveMQ.Commands{	/// <summary>    ///  The ActiveMQ MessageAck Command	/// </summary>    public class MessageAck : BaseCommand    {        public const byte ID_MessageAck = 22;    			        ActiveMQDestination destination;        TransactionId transactionId;        ConsumerId consumerId;        byte ackType;        MessageId firstMessageId;        MessageId lastMessageId;        int messageCount;		public override string ToString() {            return GetType().Name + "["                + " Destination=" + Destination                + " TransactionId=" + TransactionId                + " ConsumerId=" + ConsumerId                + " AckType=" + AckType                + " FirstMessageId=" + FirstMessageId                + " LastMessageId=" + LastMessageId                + " MessageCount=" + MessageCount                + " ]";		}	        public override byte GetDataStructureType() {            return ID_MessageAck;        }        // Properties        public ActiveMQDestination Destination        {            get { return destination; }            set { this.destination = value; }                    }        public TransactionId TransactionId        {            get { return transactionId; }            set { this.transactionId = value; }                    }        public ConsumerId ConsumerId        {            get { return consumerId; }            set { this.consumerId = value; }                    }        public byte AckType        {            get { return ackType; }            set { this.ackType = value; }                    }        public MessageId FirstMessageId        {            get { return firstMessageId; }            set { this.firstMessageId = value; }                    }        public MessageId LastMessageId        {            get { return lastMessageId; }            set { this.lastMessageId = value; }                    }        public int MessageCount        {            get { return messageCount; }            set { this.messageCount = value; }                    }    }}