begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Marshalling code for Open Wire Format for Message  *  *  * NOTE!: This file is auto generated - do not modify!  *        if you need to make a change, please see the modify the groovy scripts in the  *        under src/gram/script and then use maven openwire:generate to regenerate   *        this file.  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MessageMarshaller
extends|extends
name|BaseCommandMarshaller
block|{
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
name|Message
name|info
init|=
operator|(
name|Message
operator|)
name|o
decl_stmt|;
name|info
operator|.
name|beforeUnmarshall
argument_list|(
name|wireFormat
argument_list|)
expr_stmt|;
name|info
operator|.
name|setProducerId
argument_list|(
operator|(
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|ProducerId
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
name|setOriginalDestination
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
name|setMessageId
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
name|setOriginalTransactionId
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
name|setGroupID
argument_list|(
name|readString
argument_list|(
name|dataIn
argument_list|,
name|bs
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setGroupSequence
argument_list|(
name|dataIn
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setCorrelationId
argument_list|(
name|readString
argument_list|(
name|dataIn
argument_list|,
name|bs
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setPersistent
argument_list|(
name|bs
operator|.
name|readBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setExpiration
argument_list|(
name|unmarshalLong
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
name|setPriority
argument_list|(
name|dataIn
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setReplyTo
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
name|setTimestamp
argument_list|(
name|unmarshalLong
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
name|setType
argument_list|(
name|readString
argument_list|(
name|dataIn
argument_list|,
name|bs
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|bs
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|int
name|size
init|=
name|dataIn
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|byte
name|data
index|[]
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|dataIn
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|info
operator|.
name|setContent
argument_list|(
operator|new
name|org
operator|.
name|activeio
operator|.
name|ByteSequence
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|info
operator|.
name|setContent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bs
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|int
name|size
init|=
name|dataIn
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|byte
name|data
index|[]
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|dataIn
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|info
operator|.
name|setMarshalledProperties
argument_list|(
operator|new
name|org
operator|.
name|activeio
operator|.
name|ByteSequence
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|info
operator|.
name|setMarshalledProperties
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setDataStructure
argument_list|(
operator|(
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|DataStructure
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
name|setTargetConsumerId
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
name|setCompressed
argument_list|(
name|bs
operator|.
name|readBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setRedeliveryCounter
argument_list|(
name|dataIn
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|bs
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|short
name|size
init|=
name|dataIn
operator|.
name|readShort
argument_list|()
decl_stmt|;
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|BrokerId
name|value
index|[]
init|=
operator|new
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|BrokerId
index|[
name|size
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|value
index|[
name|i
index|]
operator|=
operator|(
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|BrokerId
operator|)
name|unmarsalNestedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|,
name|bs
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setBrokerPath
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|info
operator|.
name|setBrokerPath
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setArrival
argument_list|(
name|unmarshalLong
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
name|setUserID
argument_list|(
name|readString
argument_list|(
name|dataIn
argument_list|,
name|bs
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setRecievedByDFBridge
argument_list|(
name|bs
operator|.
name|readBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|afterUnmarshall
argument_list|(
name|wireFormat
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
name|Message
name|info
init|=
operator|(
name|Message
operator|)
name|o
decl_stmt|;
name|info
operator|.
name|beforeMarshall
argument_list|(
name|wireFormat
argument_list|)
expr_stmt|;
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
name|getProducerId
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
name|getOriginalDestination
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
name|getMessageId
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
name|getOriginalTransactionId
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|writeString
argument_list|(
name|info
operator|.
name|getGroupID
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|writeString
argument_list|(
name|info
operator|.
name|getCorrelationId
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|bs
operator|.
name|writeBoolean
argument_list|(
name|info
operator|.
name|isPersistent
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|marshal1Long
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getExpiration
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
name|getReplyTo
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|marshal1Long
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getTimestamp
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|writeString
argument_list|(
name|info
operator|.
name|getType
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|bs
operator|.
name|writeBoolean
argument_list|(
name|info
operator|.
name|getContent
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|info
operator|.
name|getContent
argument_list|()
operator|==
literal|null
condition|?
literal|0
else|:
name|info
operator|.
name|getContent
argument_list|()
operator|.
name|getLength
argument_list|()
operator|+
literal|4
expr_stmt|;
name|bs
operator|.
name|writeBoolean
argument_list|(
name|info
operator|.
name|getMarshalledProperties
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|info
operator|.
name|getMarshalledProperties
argument_list|()
operator|==
literal|null
condition|?
literal|0
else|:
name|info
operator|.
name|getMarshalledProperties
argument_list|()
operator|.
name|getLength
argument_list|()
operator|+
literal|4
expr_stmt|;
name|rc
operator|+=
name|marshal1NestedObject
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getDataStructure
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
name|getTargetConsumerId
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|bs
operator|.
name|writeBoolean
argument_list|(
name|info
operator|.
name|isCompressed
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|marshalObjectArray
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getBrokerPath
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|marshal1Long
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getArrival
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|writeString
argument_list|(
name|info
operator|.
name|getUserID
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|bs
operator|.
name|writeBoolean
argument_list|(
name|info
operator|.
name|isRecievedByDFBridge
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rc
operator|+
literal|9
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
name|Message
name|info
init|=
operator|(
name|Message
operator|)
name|o
decl_stmt|;
name|marshal2CachedObject
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getProducerId
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
name|getOriginalDestination
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
name|getMessageId
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
name|getOriginalTransactionId
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|writeString
argument_list|(
name|info
operator|.
name|getGroupID
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
name|getGroupSequence
argument_list|()
argument_list|)
expr_stmt|;
name|writeString
argument_list|(
name|info
operator|.
name|getCorrelationId
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|bs
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|marshal2Long
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getExpiration
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
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
name|marshal2NestedObject
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getReplyTo
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|marshal2Long
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getTimestamp
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|writeString
argument_list|(
name|info
operator|.
name|getType
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
if|if
condition|(
name|bs
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|org
operator|.
name|activeio
operator|.
name|ByteSequence
name|data
init|=
name|info
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|data
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|write
argument_list|(
name|data
operator|.
name|getData
argument_list|()
argument_list|,
name|data
operator|.
name|getOffset
argument_list|()
argument_list|,
name|data
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bs
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|org
operator|.
name|activeio
operator|.
name|ByteSequence
name|data
init|=
name|info
operator|.
name|getMarshalledProperties
argument_list|()
decl_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|data
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|write
argument_list|(
name|data
operator|.
name|getData
argument_list|()
argument_list|,
name|data
operator|.
name|getOffset
argument_list|()
argument_list|,
name|data
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|marshal2NestedObject
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getDataStructure
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
name|getTargetConsumerId
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|bs
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|info
operator|.
name|getRedeliveryCounter
argument_list|()
argument_list|)
expr_stmt|;
name|marshalObjectArray
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getBrokerPath
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|marshal2Long
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getArrival
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|writeString
argument_list|(
name|info
operator|.
name|getUserID
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|bs
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|info
operator|.
name|afterMarshall
argument_list|(
name|wireFormat
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

