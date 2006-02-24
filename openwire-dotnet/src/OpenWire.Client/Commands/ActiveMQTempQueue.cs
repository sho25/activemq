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
using OpenWire.Client;
using OpenWire.Client.Commands;
using OpenWire.Client.Core;

namespace OpenWire.Client.Commands
{
    /// <summary>
    /// A Temporary Queue
    /// </summary>
    public class ActiveMQTempQueue : ActiveMQTempDestination, ITemporaryQueue
    {
        public const byte ID_ActiveMQTempQueue = 102;
        
        public ActiveMQTempQueue() : base()
        {
        }
        
        public ActiveMQTempQueue(String name) : base(name)
        {
        }
        
        public String GetQueueName()
        {
            return PhysicalName;
        }
        
        public override byte GetDataStructureType()
        {
            return ID_ActiveMQTempQueue;
        }
        
        public override int GetDestinationType()
        {
            return ACTIVEMQ_QUEUE;
        }
        
        public override ActiveMQDestination CreateDestination(String name)
        {
            return new ActiveMQTempQueue(name);
        }
    }
}
