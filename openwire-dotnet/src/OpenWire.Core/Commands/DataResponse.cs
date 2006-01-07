//
// Marshalling code for Open Wire Format for DataResponse
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
    public class DataResponse : AbstractCommand
    {
    			public const int ID_DataResponse = 1;
    			
        Command data;



        // TODO generate Equals method
        // TODO generate GetHashCode method
        // TODO generate ToString method


        public override int GetCommandType() {
            return ID_DataResponse;
        }


        // Properties

        public Command Data
        {
            get
            {
                return data;
            }
            set
            {
                data = value;
            }            
        }

    }
}
