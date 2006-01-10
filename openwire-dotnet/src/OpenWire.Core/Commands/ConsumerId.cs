//
// Marshalling code for Open Wire Format for ConsumerId
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
    public class ConsumerId : AbstractCommand
    {
    			public const byte ID_ConsumerId = 122;
    			
        string connectionId;
        long sessionId;
        long value;



        // TODO generate Equals method
        // TODO generate GetHashCode method
        // TODO generate ToString method


        public override byte GetCommandType() {
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
