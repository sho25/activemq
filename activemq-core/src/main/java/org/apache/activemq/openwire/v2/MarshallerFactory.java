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
name|v2
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
name|openwire
operator|.
name|DataStreamMarshaller
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
comment|/**  * MarshallerFactory for Open Wire Format.  *  *  * NOTE!: This file is auto generated - do not modify!  *        if you need to make a change, please see the modify the groovy scripts in the  *        under src/gram/script and then use maven openwire:generate to regenerate   *        this file.  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MarshallerFactory
block|{
comment|/**      * Creates a Map of command type -> Marshallers      */
specifier|static
specifier|final
specifier|private
name|DataStreamMarshaller
name|marshaller
index|[]
init|=
operator|new
name|DataStreamMarshaller
index|[
literal|256
index|]
decl_stmt|;
static|static
block|{
name|add
argument_list|(
operator|new
name|ActiveMQBytesMessageMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ActiveMQMapMessageMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ActiveMQMessageMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ActiveMQObjectMessageMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ActiveMQQueueMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ActiveMQStreamMessageMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ActiveMQTempQueueMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ActiveMQTempTopicMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ActiveMQTextMessageMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ActiveMQTopicMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|BrokerIdMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|BrokerInfoMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ConnectionControlMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ConnectionErrorMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ConnectionIdMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ConnectionInfoMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ConsumerControlMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ConsumerIdMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ConsumerInfoMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ControlCommandMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|DataArrayResponseMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|DataResponseMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|DestinationInfoMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|DiscoveryEventMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ExceptionResponseMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|FlushCommandMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|IntegerResponseMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|JournalQueueAckMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|JournalTopicAckMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|JournalTraceMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|JournalTransactionMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|KeepAliveInfoMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|LastPartialCommandMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|LocalTransactionIdMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|MessageAckMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|MessageDispatchMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|MessageDispatchNotificationMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|MessageIdMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|MessagePullMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|NetworkBridgeFilterMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|PartialCommandMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ProducerIdMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ProducerInfoMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|RemoveInfoMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|RemoveSubscriptionInfoMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ReplayCommandMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ResponseMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|SessionIdMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|SessionInfoMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ShutdownInfoMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|SubscriptionInfoMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|TransactionInfoMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|WireFormatInfoMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|XATransactionIdMarshaller
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|static
specifier|private
name|void
name|add
parameter_list|(
name|DataStreamMarshaller
name|dsm
parameter_list|)
block|{
name|marshaller
index|[
name|dsm
operator|.
name|getDataStructureType
argument_list|()
index|]
operator|=
name|dsm
expr_stmt|;
block|}
specifier|static
specifier|public
name|DataStreamMarshaller
index|[]
name|createMarshallerMap
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|)
block|{
return|return
name|marshaller
return|;
block|}
block|}
end_class

end_unit

