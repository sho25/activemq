/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
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
    ///  The ActiveMQ ConsumerControl Command
	/// </summary>
    public class ConsumerControl : BaseCommand
    {
        public const byte ID_ConsumerControl = 17;
    			
        bool close;
        ConsumerId consumerId;
        int prefetch;

		public override string ToString() {
            return GetType().Name + "["
                + " Close=" + Close
                + " ConsumerId=" + ConsumerId
                + " Prefetch=" + Prefetch
                + " ]";

		}
	


        public override byte GetDataStructureType() {
            return ID_ConsumerControl;
        }


        // Properties

        public bool Close
        {
            get { return close; }
            set { this.close = value; }            
        }

        public ConsumerId ConsumerId
        {
            get { return consumerId; }
            set { this.consumerId = value; }            
        }

        public int Prefetch
        {
            get { return prefetch; }
            set { this.prefetch = value; }            
        }

    }
}
