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
name|usecases
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|Thread
operator|.
name|UncaughtExceptionHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
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
name|JmsMultipleBrokersTestSupport
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
name|broker
operator|.
name|BrokerService
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
name|broker
operator|.
name|jmx
operator|.
name|ManagementContext
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
name|broker
operator|.
name|jmx
operator|.
name|QueueViewMBean
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|PolicyEntry
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|PolicyMap
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
name|network
operator|.
name|NetworkConnector
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
name|Wait
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|VerifyNetworkConsumersDisconnectTest
extends|extends
name|JmsMultipleBrokersTestSupport
implements|implements
name|UncaughtExceptionHandler
block|{
specifier|public
specifier|static
specifier|final
name|int
name|BROKER_COUNT
init|=
literal|3
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|CONSUMER_COUNT
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|DUPLEX
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|CONDUIT
init|=
literal|true
decl_stmt|;
comment|// NETWORK_TTL=4 is problematic for consumer/demand propagation
comment|// needs setConsumerTTL=1 to override
specifier|public
specifier|static
specifier|final
name|int
name|NETWORK_TTL
init|=
literal|4
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|VerifyNetworkConsumersDisconnectTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|TIMEOUT
init|=
literal|30000
decl_stmt|;
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|MessageConsumer
argument_list|>
name|consumerMap
decl_stmt|;
name|Map
argument_list|<
name|Thread
argument_list|,
name|Throwable
argument_list|>
name|unhandeledExceptions
init|=
operator|new
name|HashMap
argument_list|<
name|Thread
argument_list|,
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|void
name|assertNoUnhandeledExceptions
parameter_list|()
block|{
for|for
control|(
name|Entry
argument_list|<
name|Thread
argument_list|,
name|Throwable
argument_list|>
name|e
range|:
name|unhandeledExceptions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Thread:"
operator|+
name|e
operator|.
name|getKey
argument_list|()
operator|+
literal|" Had unexpected: "
operator|+
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"There are no unhandelled exceptions, see: log for detail on: "
operator|+
name|unhandeledExceptions
argument_list|,
name|unhandeledExceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NetworkConnector
name|bridge
parameter_list|(
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
throws|throws
name|Exception
block|{
name|NetworkConnector
name|networkConnector
init|=
name|bridgeBrokers
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
literal|true
argument_list|,
name|NETWORK_TTL
argument_list|,
name|CONDUIT
argument_list|)
decl_stmt|;
name|networkConnector
operator|.
name|setSuppressDuplicateQueueSubscriptions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|networkConnector
operator|.
name|setDecreaseNetworkConsumerPriority
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|networkConnector
operator|.
name|setDuplex
argument_list|(
name|DUPLEX
argument_list|)
expr_stmt|;
comment|// infinite ttl for messages in a mesh
name|networkConnector
operator|.
name|setMessageTTL
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// one hop for consumers in a mesh
name|networkConnector
operator|.
name|setConsumerTTL
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|networkConnector
return|;
block|}
specifier|public
name|void
name|testQueueAllConnected
parameter_list|()
throws|throws
name|Exception
block|{
name|bridge
argument_list|(
literal|"Broker0"
argument_list|,
literal|"Broker1"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|DUPLEX
condition|)
name|bridge
argument_list|(
literal|"Broker1"
argument_list|,
literal|"Broker0"
argument_list|)
expr_stmt|;
name|bridge
argument_list|(
literal|"Broker1"
argument_list|,
literal|"Broker2"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|DUPLEX
condition|)
name|bridge
argument_list|(
literal|"Broker2"
argument_list|,
literal|"Broker1"
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
name|this
operator|.
name|waitForBridgeFormation
argument_list|()
expr_stmt|;
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Setup consumers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|consumerMap
operator|.
name|put
argument_list|(
literal|"Consumer:"
operator|+
name|i
operator|+
literal|":0"
argument_list|,
name|createConsumer
argument_list|(
literal|"Broker"
operator|+
name|i
argument_list|,
name|dest
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertExactConsumersConnect
argument_list|(
literal|"Broker0"
argument_list|,
name|dest
argument_list|,
literal|2
argument_list|,
name|TIMEOUT
argument_list|)
expr_stmt|;
name|assertExactConsumersConnect
argument_list|(
literal|"Broker2"
argument_list|,
name|dest
argument_list|,
literal|2
argument_list|,
name|TIMEOUT
argument_list|)
expr_stmt|;
comment|// piggy in the middle
name|assertExactConsumersConnect
argument_list|(
literal|"Broker1"
argument_list|,
name|dest
argument_list|,
literal|3
argument_list|,
name|TIMEOUT
argument_list|)
expr_stmt|;
name|assertNoUnhandeledExceptions
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Complate the mesh - 0->2"
argument_list|)
expr_stmt|;
comment|// shorter route
name|NetworkConnector
name|nc
init|=
name|bridge
argument_list|(
literal|"Broker0"
argument_list|,
literal|"Broker2"
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setBrokerName
argument_list|(
literal|"Broker0"
argument_list|)
expr_stmt|;
name|nc
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|DUPLEX
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"... complate the mesh - 2->0"
argument_list|)
expr_stmt|;
name|nc
operator|=
name|bridge
argument_list|(
literal|"Broker2"
argument_list|,
literal|"Broker0"
argument_list|)
expr_stmt|;
name|nc
operator|.
name|setBrokerName
argument_list|(
literal|"Broker2"
argument_list|)
expr_stmt|;
name|nc
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// wait for consumers to get propagated
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|assertExactConsumersConnect
argument_list|(
literal|"Broker"
operator|+
name|i
argument_list|,
name|dest
argument_list|,
literal|3
argument_list|,
name|TIMEOUT
argument_list|)
expr_stmt|;
block|}
comment|// reverse order close
name|consumerMap
operator|.
name|get
argument_list|(
literal|"Consumer:"
operator|+
literal|2
operator|+
literal|":0"
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|consumerMap
operator|.
name|get
argument_list|(
literal|"Consumer:"
operator|+
literal|1
operator|+
literal|":0"
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|consumerMap
operator|.
name|get
argument_list|(
literal|"Consumer:"
operator|+
literal|0
operator|+
literal|":0"
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Check for no consumers.."
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|assertExactConsumersConnect
argument_list|(
literal|"Broker"
operator|+
name|i
argument_list|,
name|dest
argument_list|,
literal|0
argument_list|,
name|TIMEOUT
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|assertExactConsumersConnect
parameter_list|(
specifier|final
name|String
name|brokerName
parameter_list|,
name|Destination
name|destination
parameter_list|,
specifier|final
name|int
name|count
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ManagementContext
name|context
init|=
name|brokers
operator|.
name|get
argument_list|(
name|brokerName
argument_list|)
operator|.
name|broker
operator|.
name|getManagementContext
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Excepected consumers count: "
operator|+
name|count
operator|+
literal|" on: "
operator|+
name|brokerName
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|QueueViewMBean
name|queueViewMBean
init|=
operator|(
name|QueueViewMBean
operator|)
name|context
operator|.
name|newProxyInstance
argument_list|(
name|brokers
operator|.
name|get
argument_list|(
name|brokerName
argument_list|)
operator|.
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueues
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|long
name|currentCount
init|=
name|queueViewMBean
operator|.
name|getConsumerCount
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"On "
operator|+
name|brokerName
operator|+
literal|" current consumer count for "
operator|+
name|queueViewMBean
operator|+
literal|", "
operator|+
name|currentCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|!=
name|currentCount
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sub IDs: "
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|queueViewMBean
operator|.
name|getSubscriptions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|currentCount
operator|==
name|count
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|": "
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
argument_list|,
name|timeout
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|unhandeledExceptions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// Setup n brokers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:6161"
operator|+
name|i
operator|+
literal|")/Broker"
operator|+
name|i
operator|+
literal|"?persistent=false&useJmx=true&brokerId=Broker"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|consumerMap
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|MessageConsumer
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
name|PolicyEntry
name|policyEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policyEntry
operator|.
name|setExpireMessagesPeriod
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|policyEntry
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|uncaughtException
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
synchronized|synchronized
init|(
name|unhandeledExceptions
init|)
block|{
name|unhandeledExceptions
operator|.
name|put
argument_list|(
name|t
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

