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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
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
name|util
operator|.
name|MessageIdList
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
name|command
operator|.
name|ActiveMQDestination
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
name|xbean
operator|.
name|BrokerFactoryBean
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
name|BrokerFactory
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
name|TransportConnector
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
name|CombinationTestSupport
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
name|ActiveMQConnectionFactory
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
name|ConnectionClosedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Collection
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

begin_comment
comment|/**  * Test case support that allows the easy management and connection of several brokers.  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|JmsMultipleBrokersTestSupport
extends|extends
name|CombinationTestSupport
block|{
specifier|public
specifier|static
specifier|final
name|String
name|AUTO_ASSIGN_TRANSPORT
init|=
literal|"tcp://localhost:0"
decl_stmt|;
specifier|public
specifier|static
name|int
name|MAX_SETUP_TIME
init|=
literal|5000
decl_stmt|;
specifier|protected
name|Map
name|brokers
decl_stmt|;
specifier|protected
name|Map
name|destinations
decl_stmt|;
specifier|protected
name|int
name|messageSize
init|=
literal|1
decl_stmt|;
specifier|protected
name|boolean
name|persistentDelivery
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|verbose
init|=
literal|false
decl_stmt|;
specifier|protected
name|void
name|bridgeBrokers
parameter_list|(
name|String
name|localBrokerName
parameter_list|,
name|String
name|remoteBrokerName
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|localBroker
init|=
operator|(
operator|(
name|BrokerItem
operator|)
name|brokers
operator|.
name|get
argument_list|(
name|localBrokerName
argument_list|)
operator|)
operator|.
name|broker
decl_stmt|;
name|BrokerService
name|remoteBroker
init|=
operator|(
operator|(
name|BrokerItem
operator|)
name|brokers
operator|.
name|get
argument_list|(
name|remoteBrokerName
argument_list|)
operator|)
operator|.
name|broker
decl_stmt|;
name|bridgeBrokers
argument_list|(
name|localBroker
argument_list|,
name|remoteBroker
argument_list|)
expr_stmt|;
block|}
comment|// Overwrite this method to specify how you want to bridge the two brokers
comment|// By default, bridge them using add network connector of the local broker and the first connector of the remote broker
specifier|protected
name|void
name|bridgeBrokers
parameter_list|(
name|BrokerService
name|localBroker
parameter_list|,
name|BrokerService
name|remoteBroker
parameter_list|)
throws|throws
name|Exception
block|{
name|List
name|transportConnectors
init|=
name|remoteBroker
operator|.
name|getTransportConnectors
argument_list|()
decl_stmt|;
name|URI
name|remoteURI
decl_stmt|;
if|if
condition|(
operator|!
name|transportConnectors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|remoteURI
operator|=
operator|(
operator|(
name|TransportConnector
operator|)
name|transportConnectors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getConnectUri
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|addNetworkConnector
argument_list|(
literal|"static:"
operator|+
name|remoteURI
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Remote broker has no registered connectors."
argument_list|)
throw|;
block|}
name|MAX_SETUP_TIME
operator|=
literal|2000
expr_stmt|;
block|}
comment|// This will interconnect all brokes using multicast
specifier|protected
name|void
name|bridgeAllBrokers
parameter_list|()
throws|throws
name|Exception
block|{
name|bridgeAllBrokers
argument_list|(
literal|"default"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|bridgeAllBrokers
parameter_list|(
name|String
name|groupName
parameter_list|)
throws|throws
name|Exception
block|{
name|Collection
name|brokerList
init|=
name|brokers
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|brokerList
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|BrokerService
name|broker
init|=
operator|(
operator|(
name|BrokerItem
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|broker
decl_stmt|;
name|List
name|transportConnectors
init|=
name|broker
operator|.
name|getTransportConnectors
argument_list|()
decl_stmt|;
if|if
condition|(
name|transportConnectors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|broker
operator|.
name|addConnector
argument_list|(
operator|new
name|URI
argument_list|(
name|AUTO_ASSIGN_TRANSPORT
argument_list|)
argument_list|)
expr_stmt|;
name|transportConnectors
operator|=
name|broker
operator|.
name|getTransportConnectors
argument_list|()
expr_stmt|;
block|}
name|TransportConnector
name|transport
init|=
operator|(
name|TransportConnector
operator|)
name|transportConnectors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|transport
operator|.
name|setDiscoveryUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"multicast://"
operator|+
name|groupName
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addNetworkConnector
argument_list|(
literal|"multicast://"
operator|+
name|groupName
argument_list|)
expr_stmt|;
block|}
comment|// Multicasting may take longer to setup
name|MAX_SETUP_TIME
operator|=
literal|8000
expr_stmt|;
block|}
specifier|protected
name|void
name|startAllBrokers
parameter_list|()
throws|throws
name|Exception
block|{
name|Collection
name|brokerList
init|=
name|brokers
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|brokerList
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|BrokerService
name|broker
init|=
operator|(
operator|(
name|BrokerItem
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|broker
decl_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|MAX_SETUP_TIME
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|String
name|brokerName
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
name|brokers
operator|.
name|put
argument_list|(
name|brokerName
argument_list|,
operator|new
name|BrokerItem
argument_list|(
name|broker
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|URI
name|brokerUri
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
name|brokerUri
argument_list|)
decl_stmt|;
name|brokers
operator|.
name|put
argument_list|(
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|,
operator|new
name|BrokerItem
argument_list|(
name|broker
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|Resource
name|configFile
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerFactoryBean
name|brokerFactory
init|=
operator|new
name|BrokerFactoryBean
argument_list|(
name|configFile
argument_list|)
decl_stmt|;
name|brokerFactory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|BrokerService
name|broker
init|=
name|brokerFactory
operator|.
name|getBroker
argument_list|()
decl_stmt|;
name|brokers
operator|.
name|put
argument_list|(
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|,
operator|new
name|BrokerItem
argument_list|(
name|broker
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|ConnectionFactory
name|getConnectionFactory
parameter_list|(
name|String
name|brokerName
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerItem
name|brokerItem
init|=
operator|(
name|BrokerItem
operator|)
name|brokers
operator|.
name|get
argument_list|(
name|brokerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokerItem
operator|!=
literal|null
condition|)
block|{
return|return
name|brokerItem
operator|.
name|factory
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|(
name|String
name|brokerName
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerItem
name|brokerItem
init|=
operator|(
name|BrokerItem
operator|)
name|brokers
operator|.
name|get
argument_list|(
name|brokerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokerItem
operator|!=
literal|null
condition|)
block|{
return|return
name|brokerItem
operator|.
name|createConnection
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|MessageConsumer
name|createConsumer
parameter_list|(
name|String
name|brokerName
parameter_list|,
name|Destination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerItem
name|brokerItem
init|=
operator|(
name|BrokerItem
operator|)
name|brokers
operator|.
name|get
argument_list|(
name|brokerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokerItem
operator|!=
literal|null
condition|)
block|{
return|return
name|brokerItem
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|MessageConsumer
name|createDurableSubscriber
parameter_list|(
name|String
name|brokerName
parameter_list|,
name|Topic
name|dest
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerItem
name|brokerItem
init|=
operator|(
name|BrokerItem
operator|)
name|brokers
operator|.
name|get
argument_list|(
name|brokerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokerItem
operator|!=
literal|null
condition|)
block|{
return|return
name|brokerItem
operator|.
name|createDurableSubscriber
argument_list|(
name|dest
argument_list|,
name|name
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|MessageIdList
name|getBrokerMessages
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{
name|BrokerItem
name|brokerItem
init|=
operator|(
name|BrokerItem
operator|)
name|brokers
operator|.
name|get
argument_list|(
name|brokerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokerItem
operator|!=
literal|null
condition|)
block|{
return|return
name|brokerItem
operator|.
name|getAllMessages
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|MessageIdList
name|getConsumerMessages
parameter_list|(
name|String
name|brokerName
parameter_list|,
name|MessageConsumer
name|consumer
parameter_list|)
block|{
name|BrokerItem
name|brokerItem
init|=
operator|(
name|BrokerItem
operator|)
name|brokers
operator|.
name|get
argument_list|(
name|brokerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokerItem
operator|!=
literal|null
condition|)
block|{
return|return
name|brokerItem
operator|.
name|getConsumerMessages
argument_list|(
name|consumer
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|void
name|sendMessages
parameter_list|(
name|String
name|brokerName
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerItem
name|brokerItem
init|=
operator|(
name|BrokerItem
operator|)
name|brokers
operator|.
name|get
argument_list|(
name|brokerName
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|brokerItem
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|sess
init|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|brokerItem
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|,
name|sess
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|persistentDelivery
condition|?
name|DeliveryMode
operator|.
name|PERSISTENT
else|:
name|DeliveryMode
operator|.
name|NON_PERSISTENT
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|msg
init|=
name|createTextMessage
argument_list|(
name|sess
argument_list|,
name|conn
operator|.
name|getClientID
argument_list|()
operator|+
literal|": Message-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|sess
operator|.
name|close
argument_list|()
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
name|brokerItem
operator|.
name|connections
operator|.
name|remove
argument_list|(
name|conn
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|TextMessage
name|createTextMessage
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|initText
parameter_list|)
throws|throws
name|Exception
block|{
name|TextMessage
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
comment|// Pad message text
if|if
condition|(
name|initText
operator|.
name|length
argument_list|()
operator|<
name|messageSize
condition|)
block|{
name|char
index|[]
name|data
init|=
operator|new
name|char
index|[
name|messageSize
operator|-
name|initText
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|data
argument_list|,
literal|'*'
argument_list|)
expr_stmt|;
name|String
name|str
init|=
operator|new
name|String
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|msg
operator|.
name|setText
argument_list|(
name|initText
operator|+
name|str
argument_list|)
expr_stmt|;
comment|// Do not pad message text
block|}
else|else
block|{
name|msg
operator|.
name|setText
argument_list|(
name|initText
argument_list|)
expr_stmt|;
block|}
return|return
name|msg
return|;
block|}
specifier|protected
name|ActiveMQDestination
name|createDestination
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|topic
parameter_list|)
throws|throws
name|JMSException
block|{
name|Destination
name|dest
decl_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|dest
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|destinations
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|dest
argument_list|)
expr_stmt|;
return|return
operator|(
name|ActiveMQDestination
operator|)
name|dest
return|;
block|}
else|else
block|{
name|dest
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|destinations
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|dest
argument_list|)
expr_stmt|;
return|return
operator|(
name|ActiveMQDestination
operator|)
name|dest
return|;
block|}
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|brokers
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|destinations
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|destroyAllBrokers
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|destroyBroker
parameter_list|(
name|String
name|brokerName
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerItem
name|brokerItem
init|=
operator|(
name|BrokerItem
operator|)
name|brokers
operator|.
name|remove
argument_list|(
name|brokerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokerItem
operator|!=
literal|null
condition|)
block|{
name|brokerItem
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|destroyAllBrokers
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|brokers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|BrokerItem
name|brokerItem
init|=
operator|(
name|BrokerItem
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|brokerItem
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
name|brokers
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// Class to group broker components together
specifier|protected
class|class
name|BrokerItem
block|{
specifier|public
name|BrokerService
name|broker
decl_stmt|;
specifier|public
name|ActiveMQConnectionFactory
name|factory
decl_stmt|;
specifier|public
name|List
name|connections
decl_stmt|;
specifier|public
name|Map
name|consumers
decl_stmt|;
specifier|public
name|MessageIdList
name|allMessages
init|=
operator|new
name|MessageIdList
argument_list|()
decl_stmt|;
specifier|private
name|IdGenerator
name|id
decl_stmt|;
specifier|public
name|boolean
name|persistent
init|=
literal|false
decl_stmt|;
specifier|public
name|BrokerItem
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|broker
operator|.
name|getVmConnectorURI
argument_list|()
argument_list|)
expr_stmt|;
name|consumers
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|()
argument_list|)
expr_stmt|;
name|connections
operator|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|()
argument_list|)
expr_stmt|;
name|allMessages
operator|.
name|setVerbose
argument_list|(
name|verbose
argument_list|)
expr_stmt|;
name|id
operator|=
operator|new
name|IdGenerator
argument_list|(
name|broker
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|":"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|conn
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setClientID
argument_list|(
name|id
operator|.
name|generateId
argument_list|()
argument_list|)
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|conn
argument_list|)
expr_stmt|;
return|return
name|conn
return|;
block|}
specifier|public
name|MessageConsumer
name|createConsumer
parameter_list|(
name|Destination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|c
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|c
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|s
init|=
name|c
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
return|return
name|createConsumer
argument_list|(
name|dest
argument_list|,
name|s
argument_list|)
return|;
block|}
specifier|public
name|MessageConsumer
name|createConsumer
parameter_list|(
name|Destination
name|dest
parameter_list|,
name|Session
name|sess
parameter_list|)
throws|throws
name|Exception
block|{
name|MessageConsumer
name|client
init|=
name|sess
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|MessageIdList
name|messageIdList
init|=
operator|new
name|MessageIdList
argument_list|()
decl_stmt|;
name|messageIdList
operator|.
name|setParent
argument_list|(
name|allMessages
argument_list|)
expr_stmt|;
name|client
operator|.
name|setMessageListener
argument_list|(
name|messageIdList
argument_list|)
expr_stmt|;
name|consumers
operator|.
name|put
argument_list|(
name|client
argument_list|,
name|messageIdList
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
specifier|public
name|MessageConsumer
name|createDurableSubscriber
parameter_list|(
name|Topic
name|dest
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|c
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|c
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|s
init|=
name|c
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
return|return
name|createDurableSubscriber
argument_list|(
name|dest
argument_list|,
name|s
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|public
name|MessageConsumer
name|createDurableSubscriber
parameter_list|(
name|Topic
name|dest
parameter_list|,
name|Session
name|sess
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|MessageConsumer
name|client
init|=
name|sess
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|dest
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|MessageIdList
name|messageIdList
init|=
operator|new
name|MessageIdList
argument_list|()
decl_stmt|;
name|messageIdList
operator|.
name|setParent
argument_list|(
name|allMessages
argument_list|)
expr_stmt|;
name|client
operator|.
name|setMessageListener
argument_list|(
name|messageIdList
argument_list|)
expr_stmt|;
name|consumers
operator|.
name|put
argument_list|(
name|client
argument_list|,
name|messageIdList
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
specifier|public
name|MessageIdList
name|getAllMessages
parameter_list|()
block|{
return|return
name|allMessages
return|;
block|}
specifier|public
name|MessageIdList
name|getConsumerMessages
parameter_list|(
name|MessageConsumer
name|consumer
parameter_list|)
block|{
return|return
operator|(
name|MessageIdList
operator|)
name|consumers
operator|.
name|get
argument_list|(
name|consumer
argument_list|)
return|;
block|}
specifier|public
name|MessageProducer
name|createProducer
parameter_list|(
name|Destination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|c
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|c
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|s
init|=
name|c
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
return|return
name|createProducer
argument_list|(
name|dest
argument_list|,
name|s
argument_list|)
return|;
block|}
specifier|public
name|MessageProducer
name|createProducer
parameter_list|(
name|Destination
name|dest
parameter_list|,
name|Session
name|sess
parameter_list|)
throws|throws
name|Exception
block|{
name|MessageProducer
name|client
init|=
name|sess
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|client
operator|.
name|setDeliveryMode
argument_list|(
name|persistent
condition|?
name|DeliveryMode
operator|.
name|PERSISTENT
else|:
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|Exception
block|{
while|while
condition|(
operator|!
name|connections
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Connection
name|c
init|=
operator|(
name|Connection
operator|)
name|connections
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConnectionClosedException
name|e
parameter_list|)
block|{                 }
block|}
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|consumers
operator|.
name|clear
argument_list|()
expr_stmt|;
name|broker
operator|=
literal|null
expr_stmt|;
name|connections
operator|=
literal|null
expr_stmt|;
name|consumers
operator|=
literal|null
expr_stmt|;
name|factory
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

