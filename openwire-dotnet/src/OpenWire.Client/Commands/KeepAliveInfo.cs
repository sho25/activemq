//
// Marshalling code for Open Wire Format for KeepAliveInfo
//
//
// NOTE!: This file is autogenerated - do not modify!
//        if you need to make a change, please see the Groovy scripts in the
//        activemq-openwire module
//

using System;
using System.Collections;

using OpenWire.Client;
using OpenWire.Client.Core;

namespace OpenWire.Client.Commands
{
    public class KeepAliveInfo : AbstractCommand
    {
    			public const byte ID_KeepAliveInfo = 10;
    			



        // TODO generate Equals method
        // TODO generate GetHashCode method
        // TODO generate ToString method


        public override byte GetCommandType() {
            return ID_KeepAliveInfo;
        }


        // Properties

    }
}
