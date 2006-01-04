begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
name|advisory
operator|.
name|AdvisorySupport
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
name|CommandTypes
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
name|ConsumerId
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
name|RemoveInfo
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
name|command
operator|.
name|WireFormatInfo
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
name|filter
operator|.
name|BooleanExpression
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
name|filter
operator|.
name|MessageEvaluationContext
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
name|transport
operator|.
name|TransportListener
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
name|JMSExceptionSupport
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
name|LongSequenceGenerator
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

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * Forwards messages from the local broker to the remote broker based on   * demand.  *   * @org.xbean.XBean  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DemandForwardingBridge
implements|implements
name|Bridge
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
name|DemandForwardingBridge
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
name|LongSequenceGenerator
name|consumerIdGenerator
init|=
operator|new
name|LongSequenceGenerator
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
specifier|private
name|ConsumerInfo
name|demandConsumerInfo
decl_stmt|;
specifier|private
name|int
name|demandConsumerDispatched
decl_stmt|;
name|BrokerId
name|localBrokerId
decl_stmt|;
name|BrokerId
name|remoteBrokerId
decl_stmt|;
specifier|private
specifier|static
class|class
name|DemandSubscription
block|{
name|ConsumerInfo
name|remoteInfo
decl_stmt|;
name|ConsumerInfo
name|localInfo
decl_stmt|;
name|int
name|dispatched
decl_stmt|;
specifier|public
name|DemandSubscription
parameter_list|(
name|ConsumerInfo
name|info
parameter_list|)
block|{
name|remoteInfo
operator|=
name|info
expr_stmt|;
name|localInfo
operator|=
name|info
operator|.
name|copy
argument_list|()
expr_stmt|;
block|}
block|}
name|ConcurrentHashMap
name|subscriptionMapByLocalId
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
name|ConcurrentHashMap
name|subscriptionMapByRemoteId
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|BrokerId
name|localBrokerPath
index|[]
init|=
operator|new
name|BrokerId
index|[]
block|{
literal|null
block|}
decl_stmt|;
specifier|protected
specifier|final
name|BrokerId
name|remoteBrokerPath
index|[]
init|=
operator|new
name|BrokerId
index|[]
block|{
literal|null
block|}
decl_stmt|;
specifier|public
name|DemandForwardingBridge
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
name|TransportListener
argument_list|()
block|{
specifier|public
name|void
name|onCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
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
name|TransportListener
argument_list|()
block|{
specifier|public
name|void
name|onCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
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
specifier|protected
name|void
name|startBridge
parameter_list|()
throws|throws
name|IOException
block|{
name|BrokerInfo
name|brokerInfo
init|=
operator|new
name|BrokerInfo
argument_list|()
decl_stmt|;
name|remoteBroker
operator|.
name|oneway
argument_list|(
name|brokerInfo
argument_list|)
expr_stmt|;
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
comment|// Listen to consumer advisory messages on the remote broker to determine demand.
name|demandConsumerInfo
operator|=
operator|new
name|ConsumerInfo
argument_list|(
name|sessionInfo
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|demandConsumerInfo
operator|.
name|setDispatchAsync
argument_list|(
name|dispatchAsync
argument_list|)
expr_stmt|;
name|demandConsumerInfo
operator|.
name|setDestination
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
name|AdvisorySupport
operator|.
name|CONSUMER_ADVISORY_TOPIC_PREFIX
operator|+
name|destinationFilter
argument_list|)
argument_list|)
expr_stmt|;
name|demandConsumerInfo
operator|.
name|setPrefetchSize
argument_list|(
name|prefetchSize
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|oneway
argument_list|(
name|demandConsumerInfo
argument_list|)
expr_stmt|;
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
name|remoteBroker
operator|.
name|oneway
argument_list|(
operator|new
name|ShutdownInfo
argument_list|()
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
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Caught exception stopping"
argument_list|,
name|e
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
specifier|protected
name|void
name|serviceRemoteException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
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
literal|" shutdown: "
operator|+
name|error
operator|.
name|getMessage
argument_list|()
argument_list|,
name|error
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
name|isMessageDispatch
argument_list|()
condition|)
block|{
name|MessageDispatch
name|md
init|=
operator|(
name|MessageDispatch
operator|)
name|command
decl_stmt|;
name|serviceRemoteConsumerAdvisory
argument_list|(
name|md
operator|.
name|getMessage
argument_list|()
operator|.
name|getDataStructure
argument_list|()
argument_list|)
expr_stmt|;
name|demandConsumerDispatched
operator|++
expr_stmt|;
if|if
condition|(
name|demandConsumerDispatched
operator|>
operator|(
name|demandConsumerInfo
operator|.
name|getPrefetchSize
argument_list|()
operator|*
literal|.75
operator|)
condition|)
block|{
name|remoteBroker
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
name|demandConsumerDispatched
argument_list|)
argument_list|)
expr_stmt|;
name|demandConsumerDispatched
operator|=
literal|0
expr_stmt|;
block|}
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
name|remoteBrokerId
operator|=
operator|(
operator|(
name|BrokerInfo
operator|)
name|command
operator|)
operator|.
name|getBrokerId
argument_list|()
expr_stmt|;
name|remoteBrokerPath
index|[
literal|0
index|]
operator|=
name|remoteBrokerId
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
switch|switch
condition|(
name|command
operator|.
name|getDataStructureType
argument_list|()
condition|)
block|{
case|case
name|WireFormatInfo
operator|.
name|DATA_STRUCTURE_TYPE
case|:
break|break;
default|default:
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
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|serviceRemoteException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|serviceRemoteConsumerAdvisory
parameter_list|(
name|DataStructure
name|data
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|data
operator|.
name|getClass
argument_list|()
operator|==
name|ConsumerInfo
operator|.
name|class
condition|)
block|{
comment|// Create a new local subscription
name|ConsumerInfo
name|info
init|=
operator|(
name|ConsumerInfo
operator|)
name|data
decl_stmt|;
name|BrokerId
index|[]
name|path
init|=
name|info
operator|.
name|getBrokerPath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|path
operator|!=
literal|null
operator|&&
name|path
operator|.
name|length
operator|>
literal|0
operator|)
operator|||
name|info
operator|.
name|isNetworkSubscription
argument_list|()
condition|)
block|{
comment|// Ignore:  We only support directly connected brokers for now.
return|return;
block|}
if|if
condition|(
name|contains
argument_list|(
name|info
operator|.
name|getBrokerPath
argument_list|()
argument_list|,
name|localBrokerPath
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
comment|// Ignore this consumer as it's a consumer we locally sent to the broker.
return|return;
block|}
if|if
condition|(
name|log
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|log
operator|.
name|trace
argument_list|(
literal|"Forwarding sub on "
operator|+
name|localBroker
operator|+
literal|" from "
operator|+
name|remoteBroker
operator|+
literal|" on  "
operator|+
name|info
argument_list|)
expr_stmt|;
comment|// Update the packet to show where it came from.
name|info
operator|=
name|info
operator|.
name|copy
argument_list|()
expr_stmt|;
name|info
operator|.
name|setBrokerPath
argument_list|(
name|appendToBrokerPath
argument_list|(
name|info
operator|.
name|getBrokerPath
argument_list|()
argument_list|,
name|remoteBrokerPath
argument_list|)
argument_list|)
expr_stmt|;
name|DemandSubscription
name|sub
init|=
operator|new
name|DemandSubscription
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|sub
operator|.
name|localInfo
operator|.
name|setConsumerId
argument_list|(
operator|new
name|ConsumerId
argument_list|(
name|sessionInfo
operator|.
name|getSessionId
argument_list|()
argument_list|,
name|consumerIdGenerator
operator|.
name|getNextSequenceId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sub
operator|.
name|localInfo
operator|.
name|setDispatchAsync
argument_list|(
name|dispatchAsync
argument_list|)
expr_stmt|;
name|sub
operator|.
name|localInfo
operator|.
name|setPrefetchSize
argument_list|(
name|prefetchSize
argument_list|)
expr_stmt|;
name|byte
name|priority
init|=
name|ConsumerInfo
operator|.
name|NETWORK_CONSUMER_PRIORITY
decl_stmt|;
if|if
condition|(
name|priority
operator|>
name|Byte
operator|.
name|MIN_VALUE
operator|&&
name|info
operator|.
name|getBrokerPath
argument_list|()
operator|!=
literal|null
operator|&&
name|info
operator|.
name|getBrokerPath
argument_list|()
operator|.
name|length
operator|>
literal|1
condition|)
block|{
comment|// The longer the path to the consumer, the less it's consumer priority.
name|priority
operator|-=
name|info
operator|.
name|getBrokerPath
argument_list|()
operator|.
name|length
operator|+
literal|1
expr_stmt|;
block|}
name|sub
operator|.
name|localInfo
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|subscriptionMapByLocalId
operator|.
name|put
argument_list|(
name|sub
operator|.
name|localInfo
operator|.
name|getConsumerId
argument_list|()
argument_list|,
name|sub
argument_list|)
expr_stmt|;
name|subscriptionMapByRemoteId
operator|.
name|put
argument_list|(
name|sub
operator|.
name|remoteInfo
operator|.
name|getConsumerId
argument_list|()
argument_list|,
name|sub
argument_list|)
expr_stmt|;
name|sub
operator|.
name|localInfo
operator|.
name|setBrokerPath
argument_list|(
name|info
operator|.
name|getBrokerPath
argument_list|()
argument_list|)
expr_stmt|;
name|sub
operator|.
name|localInfo
operator|.
name|setNetworkSubscription
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// This works for now since we use a VM connection to the local broker.
comment|// may need to change if we ever subscribe to a remote broker.
name|sub
operator|.
name|localInfo
operator|.
name|setAdditionalPredicate
argument_list|(
operator|new
name|BooleanExpression
argument_list|()
block|{
specifier|public
name|boolean
name|matches
parameter_list|(
name|MessageEvaluationContext
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
try|try
block|{
return|return
name|matchesForwardingFilter
argument_list|(
name|message
operator|.
name|getMessage
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Object
name|evaluate
parameter_list|(
name|MessageEvaluationContext
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|matches
argument_list|(
name|message
argument_list|)
condition|?
name|Boolean
operator|.
name|TRUE
else|:
name|Boolean
operator|.
name|FALSE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
name|sub
operator|.
name|localInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|data
operator|.
name|getClass
argument_list|()
operator|==
name|RemoveInfo
operator|.
name|class
condition|)
block|{
name|ConsumerId
name|id
init|=
call|(
name|ConsumerId
call|)
argument_list|(
operator|(
name|RemoveInfo
operator|)
name|data
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|DemandSubscription
name|sub
init|=
operator|(
name|DemandSubscription
operator|)
name|subscriptionMapByRemoteId
operator|.
name|remove
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
name|subscriptionMapByLocalId
operator|.
name|remove
argument_list|(
name|sub
operator|.
name|localInfo
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
name|sub
operator|.
name|localInfo
operator|.
name|createRemoveCommand
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|serviceLocalException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
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
literal|" shutdown: "
operator|+
name|error
operator|.
name|getMessage
argument_list|()
argument_list|,
name|error
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
name|boolean
name|matchesForwardingFilter
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
if|if
condition|(
name|message
operator|.
name|isRecievedByDFBridge
argument_list|()
operator|||
name|contains
argument_list|(
name|message
operator|.
name|getBrokerPath
argument_list|()
argument_list|,
name|remoteBrokerPath
index|[
literal|0
index|]
argument_list|)
condition|)
return|return
literal|false
return|;
comment|// Don't propagate advisory messages about network subscriptions
if|if
condition|(
name|message
operator|.
name|isAdvisory
argument_list|()
operator|&&
name|message
operator|.
name|getDataStructure
argument_list|()
operator|!=
literal|null
operator|&&
name|message
operator|.
name|getDataStructure
argument_list|()
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|CommandTypes
operator|.
name|CONSUMER_INFO
condition|)
block|{
name|ConsumerInfo
name|info
init|=
operator|(
name|ConsumerInfo
operator|)
name|message
operator|.
name|getDataStructure
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|isNetworkSubscription
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|protected
name|void
name|serviceLocalCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
name|boolean
name|trace
init|=
name|log
operator|.
name|isTraceEnabled
argument_list|()
decl_stmt|;
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
name|DemandSubscription
name|sub
init|=
operator|(
name|DemandSubscription
operator|)
name|subscriptionMapByLocalId
operator|.
name|get
argument_list|(
name|md
operator|.
name|getConsumerId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
name|message
operator|=
name|message
operator|.
name|copy
argument_list|()
expr_stmt|;
comment|// Update the packet to show where it came from.
name|message
operator|.
name|setBrokerPath
argument_list|(
name|appendToBrokerPath
argument_list|(
name|message
operator|.
name|getBrokerPath
argument_list|()
argument_list|,
name|localBrokerPath
argument_list|)
argument_list|)
expr_stmt|;
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
name|message
operator|.
name|setDestination
argument_list|(
name|md
operator|.
name|getDestination
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
name|message
operator|.
name|setRecievedByDFBridge
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|evictMarshlledForm
argument_list|()
expr_stmt|;
if|if
condition|(
name|trace
condition|)
name|log
operator|.
name|trace
argument_list|(
literal|"bridging "
operator|+
name|localBroker
operator|+
literal|" -> "
operator|+
name|remoteBroker
operator|+
literal|": "
operator|+
name|message
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|oneway
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|sub
operator|.
name|dispatched
operator|++
expr_stmt|;
if|if
condition|(
name|sub
operator|.
name|dispatched
operator|>
operator|(
name|sub
operator|.
name|localInfo
operator|.
name|getPrefetchSize
argument_list|()
operator|*
literal|.75
operator|)
condition|)
block|{
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
name|demandConsumerDispatched
argument_list|)
argument_list|)
expr_stmt|;
name|sub
operator|.
name|dispatched
operator|=
literal|0
expr_stmt|;
block|}
block|}
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
name|localBrokerId
operator|=
operator|(
operator|(
name|BrokerInfo
operator|)
name|command
operator|)
operator|.
name|getBrokerId
argument_list|()
expr_stmt|;
name|localBrokerPath
index|[
literal|0
index|]
operator|=
name|localBrokerId
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
switch|switch
condition|(
name|command
operator|.
name|getDataStructureType
argument_list|()
condition|)
block|{
case|case
name|WireFormatInfo
operator|.
name|DATA_STRUCTURE_TYPE
case|:
break|break;
default|default:
name|log
operator|.
name|warn
argument_list|(
literal|"Unexpected local command: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
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
specifier|private
name|boolean
name|contains
parameter_list|(
name|BrokerId
index|[]
name|brokerPath
parameter_list|,
name|BrokerId
name|brokerId
parameter_list|)
block|{
if|if
condition|(
name|brokerPath
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|brokerPath
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|brokerId
operator|.
name|equals
argument_list|(
name|brokerPath
index|[
name|i
index|]
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|BrokerId
index|[]
name|appendToBrokerPath
parameter_list|(
name|BrokerId
index|[]
name|brokerPath
parameter_list|,
name|BrokerId
name|pathsToAppend
index|[]
parameter_list|)
block|{
if|if
condition|(
name|brokerPath
operator|==
literal|null
operator|||
name|brokerPath
operator|.
name|length
operator|==
literal|0
condition|)
return|return
name|pathsToAppend
return|;
name|BrokerId
name|rc
index|[]
init|=
operator|new
name|BrokerId
index|[
name|brokerPath
operator|.
name|length
operator|+
name|pathsToAppend
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|brokerPath
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|,
literal|0
argument_list|,
name|brokerPath
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|pathsToAppend
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|,
name|brokerPath
operator|.
name|length
argument_list|,
name|pathsToAppend
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
block|}
end_class

end_unit

