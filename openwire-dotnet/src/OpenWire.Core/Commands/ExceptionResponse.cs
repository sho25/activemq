//
// Marshalling code for Open Wire Format for ExceptionResponse
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
    public class ExceptionResponse : Response
    {
    			public const byte ID_ExceptionResponse = 31;
    			
        byte[] exception;



        // TODO generate Equals method
        // TODO generate GetHashCode method
        // TODO generate ToString method


        public override byte GetCommandType() {
            return ID_ExceptionResponse;
        }


        // Properties

        public byte[] Exception
        {
            get
            {
                return exception;
            }
            set
            {
                exception = value;
            }            
        }

    }
}
