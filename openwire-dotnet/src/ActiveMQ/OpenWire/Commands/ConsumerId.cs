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
    //  Marshalling code for Open Wire Format for ConsumerId
    //
    //
    //  NOTE!: This file is autogenerated - do not modify!
    //         if you need to make a change, please see the Groovy scripts in the
    //         activemq-core module
    //
    public class ConsumerId : AbstractCommand
    {
        public const byte ID_ConsumerId = 122;
    			
        string connectionId;
        long sessionId;
        long value;

		public override int GetHashCode() {
            int answer = 0;
            answer = (answer * 37) + HashCode(ConnectionId);
            answer = (answer * 37) + HashCode(SessionId);
            answer = (answer * 37) + HashCode(Value);
            return answer;

		}
	

		public override bool Equals(object that) {
	    	if (that is ConsumerId) {
	    	    return Equals((ConsumerId) that);
			}
			return false;
    	}
    
		public virtual bool Equals(ConsumerId that) {
            if (! Equals(this.ConnectionId, that.ConnectionId)) return false;
            if (! Equals(this.SessionId, that.SessionId)) return false;
            if (! Equals(this.Value, that.Value)) return false;
            return true;

		}
	

		public override string ToString() {
            return GetType().Name + "["
                + " ConnectionId=" + ConnectionId
                + " SessionId=" + SessionId
                + " Value=" + Value
                + " ]";

		}
	


        public override byte GetDataStructureType() {
            return ID_ConsumerId;
        }


        // Properties

        public string ConnectionId
        {
            get { return connectionId; }
            set { this.connectionId = value; }            
        }

        public long SessionId
        {
            get { return sessionId; }
            set { this.sessionId = value; }            
        }

        public long Value
        {
            get { return value; }
            set { this.value = value; }            
        }

    }
}
