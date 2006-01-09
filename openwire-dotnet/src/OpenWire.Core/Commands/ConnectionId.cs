//
// Marshalling code for Open Wire Format for ConnectionId
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
    public class ConnectionId : AbstractCommand
    {
    			public const byte ID_ConnectionId = 120;
    			
        string value;



        // TODO generate Equals method
        // TODO generate GetHashCode method
        // TODO generate ToString method


        public override byte GetCommandType() {
            return ID_ConnectionId;
        }


        // Properties

        public string Value
        {
            get
            {
                return value;
            }
            set
            {
                value = value;
            }            
        }

    }
}
