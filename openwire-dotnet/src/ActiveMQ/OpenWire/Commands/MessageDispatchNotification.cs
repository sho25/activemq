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

using System;
using System.Collections;

using ActiveMQ.OpenWire;
using ActiveMQ.OpenWire.Commands;

namespace ActiveMQ.OpenWire.Commands
{
    //
    //  Marshalling code for Open Wire Format for MessageDispatchNotification
    //
    //
    //  NOTE!: This file is autogenerated - do not modify!
    //         if you need to make a change, please see the Groovy scripts in the
    //         activemq-core module
    //
    public class MessageDispatchNotification : BaseCommand
    {
        public const byte ID_MessageDispatchNotification = 90;
    			
        ConsumerId consumerId;
        ActiveMQDestination destination;
        long deliverySequenceId;
        MessageId messageId;

		public override string ToString() {
            return GetType().Name + "["
                + " ConsumerId=" + ConsumerId
                + " Destination=" + Destination
                + " DeliverySequenceId=" + DeliverySequenceId
                + " MessageId=" + MessageId
                + " ]";

		}
	


        public override byte GetDataStructureType() {
            return ID_MessageDispatchNotification;
        }


        // Properties

        public ConsumerId ConsumerId
        {
            get { return consumerId; }
            set { this.consumerId = value; }            
        }

        public ActiveMQDestination Destination
        {
            get { return destination; }
            set { this.destination = value; }            
        }

        public long DeliverySequenceId
        {
            get { return deliverySequenceId; }
            set { this.deliverySequenceId = value; }            
        }

        public MessageId MessageId
        {
            get { return messageId; }
            set { this.messageId = value; }            
        }

    }
}
