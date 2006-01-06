//
// Marshalling code for Open Wire Format for TransactionInfo
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
    public class TransactionInfo : AbstractCommand
    {
        ConnectionId connectionId;
        TransactionId transactionId;
        byte type;



        // TODO generate Equals method
        // TODO generate GetHashCode method
        // TODO generate ToString method


        public override int GetCommandType() {
            return 1;
        }


        // Properties

        public ConnectionId ConnectionId
        {
            get
            {
                return connectionId;
            }
            set
            {
                connectionId = value;
            }            
        }

        public TransactionId TransactionId
        {
            get
            {
                return transactionId;
            }
            set
            {
                transactionId = value;
            }            
        }

        public byte Type
        {
            get
            {
                return type;
            }
            set
            {
                type = value;
            }            
        }

    }
}
