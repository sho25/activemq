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
name|openwire
operator|.
name|v6
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
name|apache
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test case for the OpenWire marshalling for Message  *  *  * NOTE!: This file is auto generated - do not modify!  *        if you need to make a change, please see the modify the groovy scripts in the  *        under src/gram/script and then use maven openwire:generate to regenerate   *        this file.  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MessageTestSupport
extends|extends
name|BaseCommandTestSupport
block|{
specifier|protected
name|void
name|populateObject
parameter_list|(
name|Object
name|object
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|populateObject
argument_list|(
name|object
argument_list|)
expr_stmt|;
name|Message
name|info
init|=
operator|(
name|Message
operator|)
name|object
decl_stmt|;
name|info
operator|.
name|setProducerId
argument_list|(
name|createProducerId
argument_list|(
literal|"ProducerId:1"
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDestination
argument_list|(
name|createActiveMQDestination
argument_list|(
literal|"Destination:2"
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setTransactionId
argument_list|(
name|createTransactionId
argument_list|(
literal|"TransactionId:3"
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setOriginalDestination
argument_list|(
name|createActiveMQDestination
argument_list|(
literal|"OriginalDestination:4"
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setMessageId
argument_list|(
name|createMessageId
argument_list|(
literal|"MessageId:5"
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setOriginalTransactionId
argument_list|(
name|createTransactionId
argument_list|(
literal|"OriginalTransactionId:6"
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setGroupID
argument_list|(
literal|"GroupID:7"
argument_list|)
expr_stmt|;
name|info
operator|.
name|setGroupSequence
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|info
operator|.
name|setCorrelationId
argument_list|(
literal|"CorrelationId:8"
argument_list|)
expr_stmt|;
name|info
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|info
operator|.
name|setExpiration
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|info
operator|.
name|setPriority
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|info
operator|.
name|setReplyTo
argument_list|(
name|createActiveMQDestination
argument_list|(
literal|"ReplyTo:9"
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setTimestamp
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|info
operator|.
name|setType
argument_list|(
literal|"Type:10"
argument_list|)
expr_stmt|;
block|{
name|byte
name|data
index|[]
init|=
literal|"Content:11"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|info
operator|.
name|setContent
argument_list|(
operator|new
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|byte
name|data
index|[]
init|=
literal|"MarshalledProperties:12"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|info
operator|.
name|setMarshalledProperties
argument_list|(
operator|new
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setDataStructure
argument_list|(
name|createDataStructure
argument_list|(
literal|"DataStructure:13"
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setTargetConsumerId
argument_list|(
name|createConsumerId
argument_list|(
literal|"TargetConsumerId:14"
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setCompressed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|info
operator|.
name|setRedeliveryCounter
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|{
name|BrokerId
name|value
index|[]
init|=
operator|new
name|BrokerId
index|[
literal|2
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
literal|2
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
name|createBrokerId
argument_list|(
literal|"BrokerPath:15"
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
name|info
operator|.
name|setArrival
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|info
operator|.
name|setUserID
argument_list|(
literal|"UserID:16"
argument_list|)
expr_stmt|;
name|info
operator|.
name|setRecievedByDFBridge
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDroppable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|{
name|BrokerId
name|value
index|[]
init|=
operator|new
name|BrokerId
index|[
literal|2
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
literal|2
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
name|createBrokerId
argument_list|(
literal|"Cluster:17"
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setCluster
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setBrokerInTime
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|info
operator|.
name|setBrokerOutTime
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

