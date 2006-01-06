//
// Marshalling code for Open Wire Format for DataArrayResponse
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
    public class DataArrayResponse : AbstractCommand
    {
        Command[] data;



        // TODO generate Equals method
        // TODO generate HashCode method
        // TODO generate ToString method


        public override int GetCommandType() {
            return 1;
        }


        // Properties


        public Command[] Data
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
