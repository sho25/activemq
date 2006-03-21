/*
* Copyright 2006 The Apache Software Foundation or its licensors, as
* applicable.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

//
//  NOTE!: This file is autogenerated - do not modify!
//         if you need to make a change, please see the Groovy scripts in the
//         activemq-core module
//

using System;
using System.Collections;

using ActiveMQ.OpenWire;
using ActiveMQ.Commands;

namespace ActiveMQ.Commands
{
	/// <summary>
    ///  The ActiveMQ Message Command
	/// </summary>
    public class Message : BaseCommand, MarshallAware, MessageReference
    {
        public const byte ID_Message = 0;
    			
        ProducerId producerId;
        ActiveMQDestination destination;
        TransactionId transactionId;
        ActiveMQDestination originalDestination;
        MessageId messageId;
        TransactionId originalTransactionId;
        string groupID;
        int groupSequence;
        string correlationId;
        bool persistent;
        long expiration;
        byte priority;
        ActiveMQDestination replyTo;
        long timestamp;
        string type;
        byte[] content;
        byte[] marshalledProperties;
        DataStructure dataStructure;
        ConsumerId targetConsumerId;
        bool compressed;
        int redeliveryCounter;
        BrokerId[] brokerPath;
        long arrival;
        string userID;
        bool recievedByDFBridge;

		public override string ToString() {
            return GetType().Name + "["
                + " ProducerId=" + ProducerId
                + " Destination=" + Destination
                + " TransactionId=" + TransactionId
                + " OriginalDestination=" + OriginalDestination
                + " MessageId=" + MessageId
                + " OriginalTransactionId=" + OriginalTransactionId
                + " GroupID=" + GroupID
                + " GroupSequence=" + GroupSequence
                + " CorrelationId=" + CorrelationId
                + " Persistent=" + Persistent
                + " Expiration=" + Expiration
                + " Priority=" + Priority
                + " ReplyTo=" + ReplyTo
                + " Timestamp=" + Timestamp
                + " Type=" + Type
                + " Content=" + Content
                + " MarshalledProperties=" + MarshalledProperties
                + " DataStructure=" + DataStructure
                + " TargetConsumerId=" + TargetConsumerId
                + " Compressed=" + Compressed
                + " RedeliveryCounter=" + RedeliveryCounter
                + " BrokerPath=" + BrokerPath
                + " Arrival=" + Arrival
                + " UserID=" + UserID
                + " RecievedByDFBridge=" + RecievedByDFBridge
                + " ]";

		}
	


        public override byte GetDataStructureType() {
            return ID_Message;
        }


        // Properties

        public ProducerId ProducerId
        {
            get { return producerId; }
            set { this.producerId = value; }            
        }

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

        public ActiveMQDestination OriginalDestination
        {
            get { return originalDestination; }
            set { this.originalDestination = value; }            
        }

        public MessageId MessageId
        {
            get { return messageId; }
            set { this.messageId = value; }            
        }

        public TransactionId OriginalTransactionId
        {
            get { return originalTransactionId; }
            set { this.originalTransactionId = value; }            
        }

        public string GroupID
        {
            get { return groupID; }
            set { this.groupID = value; }            
        }

        public int GroupSequence
        {
            get { return groupSequence; }
            set { this.groupSequence = value; }            
        }

        public string CorrelationId
        {
            get { return correlationId; }
            set { this.correlationId = value; }            
        }

        public bool Persistent
        {
            get { return persistent; }
            set { this.persistent = value; }            
        }

        public long Expiration
        {
            get { return expiration; }
            set { this.expiration = value; }            
        }

        public byte Priority
        {
            get { return priority; }
            set { this.priority = value; }            
        }

        public ActiveMQDestination ReplyTo
        {
            get { return replyTo; }
            set { this.replyTo = value; }            
        }

        public long Timestamp
        {
            get { return timestamp; }
            set { this.timestamp = value; }            
        }

        public string Type
        {
            get { return type; }
            set { this.type = value; }            
        }

        public byte[] Content
        {
            get { return content; }
            set { this.content = value; }            
        }

        public byte[] MarshalledProperties
        {
            get { return marshalledProperties; }
            set { this.marshalledProperties = value; }            
        }

        public DataStructure DataStructure
        {
            get { return dataStructure; }
            set { this.dataStructure = value; }            
        }

        public ConsumerId TargetConsumerId
        {
            get { return targetConsumerId; }
            set { this.targetConsumerId = value; }            
        }

        public bool Compressed
        {
            get { return compressed; }
            set { this.compressed = value; }            
        }

        public int RedeliveryCounter
        {
            get { return redeliveryCounter; }
            set { this.redeliveryCounter = value; }            
        }

        public BrokerId[] BrokerPath
        {
            get { return brokerPath; }
            set { this.brokerPath = value; }            
        }

        public long Arrival
        {
            get { return arrival; }
            set { this.arrival = value; }            
        }

        public string UserID
        {
            get { return userID; }
            set { this.userID = value; }            
        }

        public bool RecievedByDFBridge
        {
            get { return recievedByDFBridge; }
            set { this.recievedByDFBridge = value; }            
        }

    }
}
