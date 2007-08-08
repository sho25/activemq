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
name|network
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|Service
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
name|ActiveMQQueue
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
name|ActiveMQTopic
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
name|BrokerId
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
name|BrokerInfo
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
name|Command
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
name|ConnectionId
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
name|ConnectionInfo
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
name|ConsumerInfo
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
name|ExceptionResponse
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
name|command
operator|.
name|MessageAck
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
name|MessageDispatch
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
name|ProducerInfo
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
name|Response
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
name|SessionInfo
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
name|ShutdownInfo
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
name|transport
operator|.
name|DefaultTransportListener
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
name|transport
operator|.
name|FutureResponse
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
name|transport
operator|.
name|ResponseCallback
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
name|transport
operator|.
name|Transport
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
name|IdGenerator
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
name|ServiceStopper
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
name|ServiceSupport
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

begin_comment
comment|/**  * Forwards all messages from the local broker to the remote broker.  *   * @org.apache.xbean.XBean  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ForwardingBridge
implements|implements
name|Service
block|{
specifier|static
specifier|final
specifier|private
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ForwardingBridge
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Transport
name|localBroker
decl_stmt|;
specifier|private
specifier|final
name|Transport
name|remoteBroker
decl_stmt|;
name|IdGenerator
name|idGenerator
init|=
operator|new
name|IdGenerator
argument_list|()
decl_stmt|;
name|ConnectionInfo
name|connectionInfo
decl_stmt|;
name|SessionInfo
name|sessionInfo
decl_stmt|;
name|ProducerInfo
name|producerInfo
decl_stmt|;
name|ConsumerInfo
name|queueConsumerInfo
decl_stmt|;
name|ConsumerInfo
name|topicConsumerInfo
decl_stmt|;
specifier|private
name|String
name|clientId
decl_stmt|;
specifier|private
name|int
name|prefetchSize
init|=
literal|1000
decl_stmt|;
specifier|private
name|boolean
name|dispatchAsync
decl_stmt|;
specifier|private
name|String
name|destinationFilter
init|=
literal|">"
decl_stmt|;
name|BrokerId
name|localBrokerId
decl_stmt|;
name|BrokerId
name|remoteBrokerId
decl_stmt|;
specifier|private
name|NetworkBridgeListener
name|bridgeFailedListener
decl_stmt|;
name|BrokerInfo
name|localBrokerInfo
decl_stmt|;
name|BrokerInfo
name|remoteBrokerInfo
decl_stmt|;
specifier|final
name|AtomicLong
name|enqueueCounter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|dequeueCounter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|public
name|ForwardingBridge
parameter_list|(
name|Transport
name|localBroker
parameter_list|,
name|Transport
name|remoteBroker
parameter_list|)
block|{
name|this
operator|.
name|localBroker
operator|=
name|localBroker
expr_stmt|;
name|this
operator|.
name|remoteBroker
operator|=
name|remoteBroker
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Starting a network connection between "
operator|+
name|localBroker
operator|+
literal|" and "
operator|+
name|remoteBroker
operator|+
literal|" has been established."
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|setTransportListener
argument_list|(
operator|new
name|DefaultTransportListener
argument_list|()
block|{
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|o
decl_stmt|;
name|serviceLocalCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|serviceLocalException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|setTransportListener
argument_list|(
operator|new
name|DefaultTransportListener
argument_list|()
block|{
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|o
decl_stmt|;
name|serviceRemoteCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|serviceRemoteException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|triggerStartBridge
parameter_list|()
throws|throws
name|IOException
block|{
name|Thread
name|thead
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|startBridge
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to start network bridge: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|thead
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * @throws IOException      */
specifier|final
name|void
name|startBridge
parameter_list|()
throws|throws
name|IOException
block|{
name|connectionInfo
operator|=
operator|new
name|ConnectionInfo
argument_list|()
expr_stmt|;
name|connectionInfo
operator|.
name|setConnectionId
argument_list|(
operator|new
name|ConnectionId
argument_list|(
name|idGenerator
operator|.
name|generateId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|connectionInfo
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
name|connectionInfo
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|oneway
argument_list|(
name|connectionInfo
argument_list|)
expr_stmt|;
name|sessionInfo
operator|=
operator|new
name|SessionInfo
argument_list|(
name|connectionInfo
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
name|sessionInfo
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|oneway
argument_list|(
name|sessionInfo
argument_list|)
expr_stmt|;
name|queueConsumerInfo
operator|=
operator|new
name|ConsumerInfo
argument_list|(
name|sessionInfo
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queueConsumerInfo
operator|.
name|setDispatchAsync
argument_list|(
name|dispatchAsync
argument_list|)
expr_stmt|;
name|queueConsumerInfo
operator|.
name|setDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|destinationFilter
argument_list|)
argument_list|)
expr_stmt|;
name|queueConsumerInfo
operator|.
name|setPrefetchSize
argument_list|(
name|prefetchSize
argument_list|)
expr_stmt|;
name|queueConsumerInfo
operator|.
name|setPriority
argument_list|(
name|ConsumerInfo
operator|.
name|NETWORK_CONSUMER_PRIORITY
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
name|queueConsumerInfo
argument_list|)
expr_stmt|;
name|producerInfo
operator|=
operator|new
name|ProducerInfo
argument_list|(
name|sessionInfo
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|producerInfo
operator|.
name|setResponseRequired
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|oneway
argument_list|(
name|producerInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|connectionInfo
operator|.
name|getClientId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|topicConsumerInfo
operator|=
operator|new
name|ConsumerInfo
argument_list|(
name|sessionInfo
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|topicConsumerInfo
operator|.
name|setDispatchAsync
argument_list|(
name|dispatchAsync
argument_list|)
expr_stmt|;
name|topicConsumerInfo
operator|.
name|setSubscriptionName
argument_list|(
literal|"topic-bridge"
argument_list|)
expr_stmt|;
name|topicConsumerInfo
operator|.
name|setRetroactive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|topicConsumerInfo
operator|.
name|setDestination
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
name|destinationFilter
argument_list|)
argument_list|)
expr_stmt|;
name|topicConsumerInfo
operator|.
name|setPrefetchSize
argument_list|(
name|prefetchSize
argument_list|)
expr_stmt|;
name|topicConsumerInfo
operator|.
name|setPriority
argument_list|(
name|ConsumerInfo
operator|.
name|NETWORK_CONSUMER_PRIORITY
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
name|topicConsumerInfo
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Network connection between "
operator|+
name|localBroker
operator|+
literal|" and "
operator|+
name|remoteBroker
operator|+
literal|" has been established."
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|connectionInfo
operator|!=
literal|null
condition|)
block|{
name|localBroker
operator|.
name|request
argument_list|(
name|connectionInfo
operator|.
name|createRemoveCommand
argument_list|()
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|request
argument_list|(
name|connectionInfo
operator|.
name|createRemoveCommand
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|localBroker
operator|.
name|setTransportListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|setTransportListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
operator|new
name|ShutdownInfo
argument_list|()
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|oneway
argument_list|(
operator|new
name|ShutdownInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ServiceStopper
name|ss
init|=
operator|new
name|ServiceStopper
argument_list|()
decl_stmt|;
name|ss
operator|.
name|stop
argument_list|(
name|localBroker
argument_list|)
expr_stmt|;
name|ss
operator|.
name|stop
argument_list|(
name|remoteBroker
argument_list|)
expr_stmt|;
name|ss
operator|.
name|throwFirstException
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|serviceRemoteException
parameter_list|(
name|Throwable
name|error
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Unexpected remote exception: "
operator|+
name|error
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Exception trace: "
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|serviceRemoteCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|command
operator|.
name|isBrokerInfo
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|remoteBrokerInfo
operator|=
operator|(
operator|(
name|BrokerInfo
operator|)
name|command
operator|)
expr_stmt|;
name|remoteBrokerId
operator|=
name|remoteBrokerInfo
operator|.
name|getBrokerId
argument_list|()
expr_stmt|;
if|if
condition|(
name|localBrokerId
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|localBrokerId
operator|.
name|equals
argument_list|(
name|remoteBrokerId
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Disconnecting loop back connection."
argument_list|)
expr_stmt|;
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|triggerStartBridge
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unexpected remote command: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|serviceLocalException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|serviceLocalException
parameter_list|(
name|Throwable
name|error
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Unexpected local exception: "
operator|+
name|error
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Exception trace: "
argument_list|,
name|error
argument_list|)
expr_stmt|;
name|fireBridgeFailed
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|serviceLocalCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|command
operator|.
name|isMessageDispatch
argument_list|()
condition|)
block|{
name|enqueueCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
specifier|final
name|MessageDispatch
name|md
init|=
operator|(
name|MessageDispatch
operator|)
name|command
decl_stmt|;
name|Message
name|message
init|=
name|md
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setProducerId
argument_list|(
name|producerInfo
operator|.
name|getProducerId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|.
name|getOriginalTransactionId
argument_list|()
operator|==
literal|null
condition|)
name|message
operator|.
name|setOriginalTransactionId
argument_list|(
name|message
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|setTransactionId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|message
operator|.
name|isResponseRequired
argument_list|()
condition|)
block|{
comment|// If the message was originally sent using async send, we
comment|// will preserve that QOS
comment|// by bridging it using an async send (small chance of
comment|// message loss).
name|remoteBroker
operator|.
name|oneway
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|dequeueCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
operator|new
name|MessageAck
argument_list|(
name|md
argument_list|,
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// The message was not sent using async send, so we should
comment|// only ack the local
comment|// broker when we get confirmation that the remote broker
comment|// has received the message.
name|ResponseCallback
name|callback
init|=
operator|new
name|ResponseCallback
argument_list|()
block|{
specifier|public
name|void
name|onCompletion
parameter_list|(
name|FutureResponse
name|future
parameter_list|)
block|{
try|try
block|{
name|Response
name|response
init|=
name|future
operator|.
name|getResult
argument_list|()
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|isException
argument_list|()
condition|)
block|{
name|ExceptionResponse
name|er
init|=
operator|(
name|ExceptionResponse
operator|)
name|response
decl_stmt|;
name|serviceLocalException
argument_list|(
name|er
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dequeueCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
operator|new
name|MessageAck
argument_list|(
name|md
argument_list|,
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|serviceLocalException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|remoteBroker
operator|.
name|asyncRequest
argument_list|(
name|message
argument_list|,
name|callback
argument_list|)
expr_stmt|;
block|}
comment|// Ack on every message since we don't know if the broker is
comment|// blocked due to memory
comment|// usage and is waiting for an Ack to un-block him.
comment|// Acking a range is more efficient, but also more prone to
comment|// locking up a server
comment|// Perhaps doing something like the following should be policy
comment|// based.
comment|// if(
comment|// md.getConsumerId().equals(queueConsumerInfo.getConsumerId())
comment|// ) {
comment|// queueDispatched++;
comment|// if( queueDispatched> (queueConsumerInfo.getPrefetchSize()/2)
comment|// ) {
comment|// localBroker.oneway(new MessageAck(md,
comment|// MessageAck.STANDARD_ACK_TYPE, queueDispatched));
comment|// queueDispatched=0;
comment|// }
comment|// } else {
comment|// topicDispatched++;
comment|// if( topicDispatched> (topicConsumerInfo.getPrefetchSize()/2)
comment|// ) {
comment|// localBroker.oneway(new MessageAck(md,
comment|// MessageAck.STANDARD_ACK_TYPE, topicDispatched));
comment|// topicDispatched=0;
comment|// }
comment|// }
block|}
elseif|else
if|if
condition|(
name|command
operator|.
name|isBrokerInfo
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|localBrokerInfo
operator|=
operator|(
operator|(
name|BrokerInfo
operator|)
name|command
operator|)
expr_stmt|;
name|localBrokerId
operator|=
name|localBrokerInfo
operator|.
name|getBrokerId
argument_list|()
expr_stmt|;
if|if
condition|(
name|remoteBrokerId
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|remoteBrokerId
operator|.
name|equals
argument_list|(
name|localBrokerId
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Disconnecting loop back connection."
argument_list|)
expr_stmt|;
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|triggerStartBridge
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Unexpected local command: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|serviceLocalException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getClientId
parameter_list|()
block|{
return|return
name|clientId
return|;
block|}
specifier|public
name|void
name|setClientId
parameter_list|(
name|String
name|clientId
parameter_list|)
block|{
name|this
operator|.
name|clientId
operator|=
name|clientId
expr_stmt|;
block|}
specifier|public
name|int
name|getPrefetchSize
parameter_list|()
block|{
return|return
name|prefetchSize
return|;
block|}
specifier|public
name|void
name|setPrefetchSize
parameter_list|(
name|int
name|prefetchSize
parameter_list|)
block|{
name|this
operator|.
name|prefetchSize
operator|=
name|prefetchSize
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDispatchAsync
parameter_list|()
block|{
return|return
name|dispatchAsync
return|;
block|}
specifier|public
name|void
name|setDispatchAsync
parameter_list|(
name|boolean
name|dispatchAsync
parameter_list|)
block|{
name|this
operator|.
name|dispatchAsync
operator|=
name|dispatchAsync
expr_stmt|;
block|}
specifier|public
name|String
name|getDestinationFilter
parameter_list|()
block|{
return|return
name|destinationFilter
return|;
block|}
specifier|public
name|void
name|setDestinationFilter
parameter_list|(
name|String
name|destinationFilter
parameter_list|)
block|{
name|this
operator|.
name|destinationFilter
operator|=
name|destinationFilter
expr_stmt|;
block|}
specifier|public
name|void
name|setNetworkBridgeFailedListener
parameter_list|(
name|NetworkBridgeListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|bridgeFailedListener
operator|=
name|listener
expr_stmt|;
block|}
specifier|private
name|void
name|fireBridgeFailed
parameter_list|()
block|{
name|NetworkBridgeListener
name|l
init|=
name|this
operator|.
name|bridgeFailedListener
decl_stmt|;
if|if
condition|(
name|l
operator|!=
literal|null
condition|)
block|{
name|l
operator|.
name|bridgeFailed
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getRemoteAddress
parameter_list|()
block|{
return|return
name|remoteBroker
operator|.
name|getRemoteAddress
argument_list|()
return|;
block|}
specifier|public
name|String
name|getLocalAddress
parameter_list|()
block|{
return|return
name|localBroker
operator|.
name|getRemoteAddress
argument_list|()
return|;
block|}
specifier|public
name|String
name|getLocalBrokerName
parameter_list|()
block|{
return|return
name|localBrokerInfo
operator|==
literal|null
condition|?
literal|null
else|:
name|localBrokerInfo
operator|.
name|getBrokerName
argument_list|()
return|;
block|}
specifier|public
name|String
name|getRemoteBrokerName
parameter_list|()
block|{
return|return
name|remoteBrokerInfo
operator|==
literal|null
condition|?
literal|null
else|:
name|remoteBrokerInfo
operator|.
name|getBrokerName
argument_list|()
return|;
block|}
specifier|public
name|long
name|getDequeueCounter
parameter_list|()
block|{
return|return
name|dequeueCounter
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|long
name|getEnqueueCounter
parameter_list|()
block|{
return|return
name|enqueueCounter
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

