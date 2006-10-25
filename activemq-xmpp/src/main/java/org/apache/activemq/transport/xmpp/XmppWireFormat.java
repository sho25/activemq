begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|xmpp
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A wire format which uses XMPP format of messages  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|XmppWireFormat
implements|implements
name|WireFormat
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|XmppWireFormat
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|int
name|version
init|=
literal|1
decl_stmt|;
specifier|public
name|WireFormat
name|copy
parameter_list|()
block|{
return|return
operator|new
name|XmppWireFormat
argument_list|()
return|;
block|}
comment|/*     public Packet readPacket(DataInput in) throws IOException {         return null;     }      public Packet readPacket(int firstByte, DataInput in) throws IOException {         return null;     }      public Packet writePacket(Packet packet, DataOutput out) throws IOException, JMSException {         switch (packet.getPacketType()) {             case Packet.ACTIVEMQ_MESSAGE:                 writeMessage((ActiveMQMessage) packet, "", out);                 break;              case Packet.ACTIVEMQ_TEXT_MESSAGE:                 writeTextMessage((ActiveMQTextMessage) packet, out);                 break;              case Packet.ACTIVEMQ_BYTES_MESSAGE:                 writeBytesMessage((ActiveMQBytesMessage) packet, out);                 break;              case Packet.ACTIVEMQ_OBJECT_MESSAGE:                 writeObjectMessage((ActiveMQObjectMessage) packet, out);                 break;              case Packet.ACTIVEMQ_MAP_MESSAGE:             case Packet.ACTIVEMQ_STREAM_MESSAGE:               case Packet.ACTIVEMQ_BROKER_INFO:             case Packet.ACTIVEMQ_CONNECTION_INFO:             case Packet.ACTIVEMQ_MSG_ACK:             case Packet.CONSUMER_INFO:             case Packet.DURABLE_UNSUBSCRIBE:             case Packet.INT_RESPONSE_RECEIPT_INFO:             case Packet.PRODUCER_INFO:             case Packet.RECEIPT_INFO:             case Packet.RESPONSE_RECEIPT_INFO:             case Packet.SESSION_INFO:             case Packet.TRANSACTION_INFO:             case Packet.XA_TRANSACTION_INFO:             default:                 log.warn("Ignoring message type: " + packet.getPacketType() + " packet: " + packet);         }         return null;     } */
comment|//    /**
comment|//     * Can this wireformat process packets of this version
comment|//     * @param version the version number to test
comment|//     * @return true if can accept the version
comment|//     */
comment|//    public boolean canProcessWireFormatVersion(int version){
comment|//        return true;
comment|//    }
comment|//
comment|//    /**
comment|//     * @return the current version of this wire format
comment|//     */
comment|//    public int getCurrentWireFormatVersion(){
comment|//        return 1;
comment|//    }
comment|//
comment|//    // Implementation methods
comment|//    //-------------------------------------------------------------------------
comment|//    protected void writeObjectMessage(ActiveMQObjectMessage message, DataOutput out) throws JMSException, IOException {
comment|//        Serializable object = message.getObject();
comment|//        String text = (object != null) ? object.toString() : "";
comment|//        writeMessage(message, text, out);
comment|//    }
comment|//
comment|//    protected void writeTextMessage(ActiveMQTextMessage message, DataOutput out) throws JMSException, IOException {
comment|//        writeMessage(message, message.getText(), out);
comment|//    }
comment|//
comment|//    protected void writeBytesMessage(ActiveMQBytesMessage message, DataOutput out) throws IOException {
comment|//        ByteArray data = message.getBodyAsBytes();
comment|//        String text = encodeBinary(data.getBuf(),data.getOffset(),data.getLength());
comment|//        writeMessage(message, text, out);
comment|//    }
comment|//
comment|//    protected void writeMessage(ActiveMQMessage message, String body, DataOutput out) throws IOException {
comment|//        String type = getXmppType(message);
comment|//
comment|//        StringBuffer buffer = new StringBuffer("<");
comment|//        buffer.append(type);
comment|//        buffer.append(" to='");
comment|//        buffer.append(message.getJMSDestination().toString());
comment|//        buffer.append("' from='");
comment|//        buffer.append(message.getJMSReplyTo().toString());
comment|//        String messageID = message.getJMSMessageID();
comment|//        if (messageID != null) {
comment|//            buffer.append("' id='");
comment|//            buffer.append(messageID);
comment|//        }
comment|//
comment|//        HashMap properties = message.getProperties();
comment|//        if (properties != null) {
comment|//            for (Iterator iter = properties.entrySet().iterator(); iter.hasNext();) {
comment|//                Map.Entry entry = (Map.Entry) iter.next();
comment|//                Object key = entry.getKey();
comment|//                Object value = entry.getValue();
comment|//                if (value != null) {
comment|//                    buffer.append("' ");
comment|//                    buffer.append(key.toString());
comment|//                    buffer.append("='");
comment|//                    buffer.append(value.toString());
comment|//                }
comment|//            }
comment|//        }
comment|//
comment|//        buffer.append("'>");
comment|//
comment|//        String id = message.getJMSCorrelationID();
comment|//        if (id != null) {
comment|//            buffer.append("<thread>");
comment|//            buffer.append(id);
comment|//            buffer.append("</thread>");
comment|//        }
comment|//        buffer.append(body);
comment|//        buffer.append("</");
comment|//        buffer.append(type);
comment|//        buffer.append(">");
comment|//
comment|//        out.write(buffer.toString().getBytes());
comment|//    }
comment|//
comment|//    protected String encodeBinary(byte[] data,int offset,int length) {
comment|//        // TODO
comment|//        throw new RuntimeException("Not implemented yet!");
comment|//    }
comment|//
comment|//    protected String getXmppType(ActiveMQMessage message) {
comment|//        String type = message.getJMSType();
comment|//        if (type == null) {
comment|//            type = "message";
comment|//        }
comment|//        return type;
comment|//    }
specifier|public
name|ByteSequence
name|marshal
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|dos
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|marshal
argument_list|(
name|command
argument_list|,
name|dos
argument_list|)
expr_stmt|;
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|baos
operator|.
name|toByteSequence
argument_list|()
return|;
block|}
specifier|public
name|Object
name|unmarshal
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayInputStream
name|stream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|packet
argument_list|)
decl_stmt|;
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
name|stream
argument_list|)
decl_stmt|;
return|return
name|unmarshal
argument_list|(
name|dis
argument_list|)
return|;
block|}
specifier|public
name|void
name|marshal
parameter_list|(
name|Object
name|object
parameter_list|,
name|DataOutputStream
name|dataOutputStream
parameter_list|)
throws|throws
name|IOException
block|{
comment|/** TODO */
block|}
specifier|public
name|Object
name|unmarshal
parameter_list|(
name|DataInputStream
name|dataInputStream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
comment|/** TODO */
block|}
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
specifier|public
name|void
name|setVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
block|}
end_class

end_unit

