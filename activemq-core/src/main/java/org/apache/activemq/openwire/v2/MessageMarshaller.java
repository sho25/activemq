begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|openwire
operator|.
name|v2
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|DataStructure
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
name|command
operator|.
name|Message
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
name|openwire
operator|.
name|BooleanStream
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
name|openwire
operator|.
name|OpenWireFormat
import|;
end_import

begin_comment
comment|/**  * Marshalling code for Open Wire Format for MessageMarshaller NOTE!: This file  * is auto generated - do not modify! if you need to make a change, please see  * the modify the groovy scripts in the under src/gram/script and then use maven  * openwire:generate to regenerate this file.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MessageMarshaller
extends|extends
name|BaseCommandMarshaller
block|{
comment|/**      * Un-marshal an object instance from the data input stream      *       * @param o the object to un-marshal      * @param dataIn the data input stream to build the object from      * @throws IOException      */
specifier|public
name|void
name|tightUnmarshal
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|Object
name|o
parameter_list|,
name|DataInput
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
name|tightUnmarshal
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ProducerId
operator|)
name|tightUnmarsalCachedObject
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
operator|)
name|tightUnmarsalCachedObject
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|TransactionId
operator|)
name|tightUnmarsalCachedObject
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
operator|)
name|tightUnmarsalCachedObject
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|MessageId
operator|)
name|tightUnmarsalNestedObject
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|TransactionId
operator|)
name|tightUnmarsalCachedObject
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
name|tightUnmarshalString
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
name|tightUnmarshalString
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
name|tightUnmarshalLong
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
operator|)
name|tightUnmarsalNestedObject
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
name|tightUnmarshalLong
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
name|tightUnmarshalString
argument_list|(
name|dataIn
argument_list|,
name|bs
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setContent
argument_list|(
name|tightUnmarshalByteSequence
argument_list|(
name|dataIn
argument_list|,
name|bs
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setMarshalledProperties
argument_list|(
name|tightUnmarshalByteSequence
argument_list|(
name|dataIn
argument_list|,
name|bs
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDataStructure
argument_list|(
operator|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|DataStructure
operator|)
name|tightUnmarsalNestedObject
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ConsumerId
operator|)
name|tightUnmarsalCachedObject
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
name|apache
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
name|apache
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|BrokerId
operator|)
name|tightUnmarsalNestedObject
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
name|tightUnmarshalLong
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
name|tightUnmarshalString
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
name|setDroppable
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
name|tightMarshal1
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
name|tightMarshal1
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
name|tightMarshalCachedObject1
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalCachedObject1
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalCachedObject1
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalCachedObject1
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalNestedObject1
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalCachedObject1
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalString1
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
name|tightMarshalString1
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
name|tightMarshalLong1
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
name|tightMarshalNestedObject1
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalLong1
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
name|tightMarshalString1
argument_list|(
name|info
operator|.
name|getType
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|tightMarshalByteSequence1
argument_list|(
name|info
operator|.
name|getContent
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|tightMarshalByteSequence1
argument_list|(
name|info
operator|.
name|getMarshalledProperties
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|rc
operator|+=
name|tightMarshalNestedObject1
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalCachedObject1
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalObjectArray1
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
name|tightMarshalLong1
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
name|tightMarshalString1
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
name|bs
operator|.
name|writeBoolean
argument_list|(
name|info
operator|.
name|isDroppable
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rc
operator|+
literal|9
return|;
block|}
comment|/**      * Write a object instance to data output stream      *       * @param o the instance to be marshaled      * @param dataOut the output stream      * @throws IOException thrown if an error occurs      */
specifier|public
name|void
name|tightMarshal2
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|Object
name|o
parameter_list|,
name|DataOutput
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
name|tightMarshal2
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
name|tightMarshalCachedObject2
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalCachedObject2
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalCachedObject2
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalCachedObject2
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalNestedObject2
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalCachedObject2
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalString2
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
name|tightMarshalString2
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
name|tightMarshalLong2
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
name|tightMarshalNestedObject2
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalLong2
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
name|tightMarshalString2
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
name|tightMarshalByteSequence2
argument_list|(
name|info
operator|.
name|getContent
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|tightMarshalByteSequence2
argument_list|(
name|info
operator|.
name|getMarshalledProperties
argument_list|()
argument_list|,
name|dataOut
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|tightMarshalNestedObject2
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalCachedObject2
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
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
name|tightMarshalObjectArray2
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
name|tightMarshalLong2
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
name|tightMarshalString2
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
comment|/**      * Un-marshal an object instance from the data input stream      *       * @param o the object to un-marshal      * @param dataIn the data input stream to build the object from      * @throws IOException      */
specifier|public
name|void
name|looseUnmarshal
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|Object
name|o
parameter_list|,
name|DataInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|looseUnmarshal
argument_list|(
name|wireFormat
argument_list|,
name|o
argument_list|,
name|dataIn
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ProducerId
operator|)
name|looseUnmarsalCachedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
operator|)
name|looseUnmarsalCachedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|TransactionId
operator|)
name|looseUnmarsalCachedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
operator|)
name|looseUnmarsalCachedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|MessageId
operator|)
name|looseUnmarsalNestedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|TransactionId
operator|)
name|looseUnmarsalCachedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setGroupID
argument_list|(
name|looseUnmarshalString
argument_list|(
name|dataIn
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
name|looseUnmarshalString
argument_list|(
name|dataIn
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setPersistent
argument_list|(
name|dataIn
operator|.
name|readBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setExpiration
argument_list|(
name|looseUnmarshalLong
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
operator|)
name|looseUnmarsalNestedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setTimestamp
argument_list|(
name|looseUnmarshalLong
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setType
argument_list|(
name|looseUnmarshalString
argument_list|(
name|dataIn
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setContent
argument_list|(
name|looseUnmarshalByteSequence
argument_list|(
name|dataIn
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setMarshalledProperties
argument_list|(
name|looseUnmarshalByteSequence
argument_list|(
name|dataIn
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDataStructure
argument_list|(
operator|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|DataStructure
operator|)
name|looseUnmarsalNestedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ConsumerId
operator|)
name|looseUnmarsalCachedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setCompressed
argument_list|(
name|dataIn
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
name|dataIn
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
name|apache
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
name|apache
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|BrokerId
operator|)
name|looseUnmarsalNestedObject
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
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
name|looseUnmarshalLong
argument_list|(
name|wireFormat
argument_list|,
name|dataIn
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setUserID
argument_list|(
name|looseUnmarshalString
argument_list|(
name|dataIn
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setRecievedByDFBridge
argument_list|(
name|dataIn
operator|.
name|readBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDroppable
argument_list|(
name|dataIn
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
name|void
name|looseMarshal
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|Object
name|o
parameter_list|,
name|DataOutput
name|dataOut
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
name|super
operator|.
name|looseMarshal
argument_list|(
name|wireFormat
argument_list|,
name|o
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalCachedObject
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
name|info
operator|.
name|getProducerId
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalCachedObject
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
name|info
operator|.
name|getDestination
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalCachedObject
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
name|info
operator|.
name|getTransactionId
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalCachedObject
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
name|info
operator|.
name|getOriginalDestination
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalNestedObject
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
name|info
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalCachedObject
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
name|info
operator|.
name|getOriginalTransactionId
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalString
argument_list|(
name|info
operator|.
name|getGroupID
argument_list|()
argument_list|,
name|dataOut
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
name|looseMarshalString
argument_list|(
name|info
operator|.
name|getCorrelationId
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeBoolean
argument_list|(
name|info
operator|.
name|isPersistent
argument_list|()
argument_list|)
expr_stmt|;
name|looseMarshalLong
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getExpiration
argument_list|()
argument_list|,
name|dataOut
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
name|looseMarshalNestedObject
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
name|info
operator|.
name|getReplyTo
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalLong
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getTimestamp
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalString
argument_list|(
name|info
operator|.
name|getType
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalByteSequence
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getContent
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalByteSequence
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getMarshalledProperties
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalNestedObject
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
name|info
operator|.
name|getDataStructure
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalCachedObject
argument_list|(
name|wireFormat
argument_list|,
operator|(
name|DataStructure
operator|)
name|info
operator|.
name|getTargetConsumerId
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeBoolean
argument_list|(
name|info
operator|.
name|isCompressed
argument_list|()
argument_list|)
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
name|looseMarshalObjectArray
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getBrokerPath
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalLong
argument_list|(
name|wireFormat
argument_list|,
name|info
operator|.
name|getArrival
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|looseMarshalString
argument_list|(
name|info
operator|.
name|getUserID
argument_list|()
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeBoolean
argument_list|(
name|info
operator|.
name|isRecievedByDFBridge
argument_list|()
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeBoolean
argument_list|(
name|info
operator|.
name|isDroppable
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

