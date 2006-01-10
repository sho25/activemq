//
// Marshalling code for Open Wire Format for Response
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
    public class Response : BaseCommand
    {
    			public const byte ID_Response = 30;
    			
        short correlationId;



        // TODO generate Equals method
        // TODO generate GetHashCode method
        // TODO generate ToString method


        public override byte GetCommandType() {
            return ID_Response;
        }


        // Properties

        public short CorrelationId
        {
            get { return correlationId; }
            set { this.correlationId = value; }            
        }

    }
}
