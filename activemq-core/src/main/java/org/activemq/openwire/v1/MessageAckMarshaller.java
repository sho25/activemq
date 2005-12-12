begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>   *   * Copyright 2005 Hiram Chirino  * Copyright 2005 Protique Ltd  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|openwire
operator|.
name|v1
package|;
end_package

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

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|openwire
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Marshalling code for Open Wire Format for MessageAck  *  *  * NOTE!: This file is auto generated - do not modify!  *        if you need to make a change, please see the modify the groovy scripts in the  *        under src/gram/script and then use maven openwire:generate to regenerate   *        this file.  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MessageAckMarshaller
extends|extends
name|BaseCommandMarshaller
block|{
comment|/**      * Return the type of Data Structure we marshal      * @return short representation of the type data structure      */
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|MessageAck
operator|.
name|DATA_STRUCTURE_TYPE
return|;
block|}
comment|/**      * @return a new object instance      */
specifier|public
name|DataStructure
name|createObject
parameter_list|()
block|{
return|return
operator|new
name|MessageAck
argument_list|()
return|;
block|}
comment|/**      * Un-marshal an object instance from the data input stream      *      * @param o the object to un-marshal      * @param dataIn the data input stream to build the object from      * @throws IOException      */
specifier|public
name|void
name|unmarshal
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|Object
name|o
parameter_list|,
name|DataInputStream
name|dataIn
parameter_list|,
name|BooleanStream
name|bs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|unmarshal
argument_list|(
name|wireFormat
argument_list|,
name|o
argument_list|,
name|dataIn
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|MessageAck
name|info
init|=
operator|(
name|MessageAck
operator|)
name|o
decl_stmt|;
name|info
operator|.
name|setDestination
argument_list|(
operator|(
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
operator|)
name|unmarsalCachedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|,
name|bs
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setTransactionId
argument_list|(
operator|(
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|TransactionId
operator|)
name|unmarsalCachedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|,
name|bs
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setConsumerId
argument_list|(
operator|(
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|ConsumerId
operator|)
name|unmarsalCachedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|,
name|bs
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setAckType
argument_list|(
name|dataIn
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setFirstMessageId
argument_list|(
operator|(
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|MessageId
operator|)
name|unmarsalNestedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|,
name|bs
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setLastMessageId
argument_list|(
operator|(
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|MessageId
operator|)
name|unmarsalNestedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|,
name|bs
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setMessageCount
argument_list|(
name|dataIn
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Write the booleans that this object uses to a BooleanStream      */
specifier|public
name|int
name|marshal1
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|Object
name|o
parameter_list|,
name|BooleanStream
name|bs
parameter_list|)
throws|throws
name|IOException
block|{
name|MessageAck
name|info
init|=
operator|(
name|MessageAck
operator|)
name|o
decl_stmt|;
name|int
name|rc
init|=
name|super
operator|.
name|marshal1
argument_list|(
name|wireFormat
argument_list|,
name|o
argument_list|,
name|bs
argument_list|)
decl_stmt|;
name|rc
operator|+=
name|marshal1CachedObject
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getDestination
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|marshal1CachedObject
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getTransactionId
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|marshal1CachedObject
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|marshal1NestedObject
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getFirstMessageId
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|marshal1NestedObject
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getLastMessageId
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
return|return
name|rc
operator|+
literal|5
return|;
block|}
comment|/**      * Write a object instance to data output stream      *      * @param o the instance to be marshaled      * @param dataOut the output stream      * @throws IOException thrown if an error occurs      */
specifier|public
name|void
name|marshal2
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|Object
name|o
parameter_list|,
name|DataOutputStream
name|dataOut
parameter_list|,
name|BooleanStream
name|bs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|marshal2
argument_list|(
name|wireFormat
argument_list|,
name|o
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|MessageAck
name|info
init|=
operator|(
name|MessageAck
operator|)
name|o
decl_stmt|;
name|marshal2CachedObject
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getDestination
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|marshal2CachedObject
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getTransactionId
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|marshal2CachedObject
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeByte
argument_list|(
name|info
operator|.
name|getAckType
argument_list|()
argument_list|)
expr_stmt|;
name|marshal2NestedObject
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getFirstMessageId
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|marshal2NestedObject
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getLastMessageId
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|info
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

