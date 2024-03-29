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
operator|.
name|jms
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|network
operator|.
name|jms
operator|.
name|ReconnectionPolicy
operator|.
name|INFINITE
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
name|concurrent
operator|.
name|CopyOnWriteArrayList
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
name|LinkedBlockingQueue
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
name|ThreadFactory
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
name|ThreadPoolExecutor
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|AtomicReference
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
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
name|util
operator|.
name|LRUCache
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
name|ThreadPoolUtils
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

begin_comment
comment|/**  * This bridge joins the gap between foreign JMS providers and ActiveMQ As some  * JMS providers are still only in compliance with JMS v1.0.1 , this bridge itself  * aimed to be in compliance with the JMS 1.0.2 specification.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|JmsConnector
implements|implements
name|Service
block|{
specifier|private
specifier|static
name|int
name|nextId
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
name|JmsConnector
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|boolean
name|preferJndiDestinationLookup
init|=
literal|false
decl_stmt|;
specifier|protected
name|JndiLookupFactory
name|jndiLocalTemplate
decl_stmt|;
specifier|protected
name|JndiLookupFactory
name|jndiOutboundTemplate
decl_stmt|;
specifier|protected
name|JmsMesageConvertor
name|inboundMessageConvertor
decl_stmt|;
specifier|protected
name|JmsMesageConvertor
name|outboundMessageConvertor
decl_stmt|;
specifier|protected
name|AtomicBoolean
name|initialized
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|protected
name|AtomicBoolean
name|localSideInitialized
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|protected
name|AtomicBoolean
name|foreignSideInitialized
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|protected
name|AtomicBoolean
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|protected
name|AtomicBoolean
name|failed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|protected
name|AtomicReference
argument_list|<
name|Connection
argument_list|>
name|foreignConnection
init|=
operator|new
name|AtomicReference
argument_list|<
name|Connection
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|AtomicReference
argument_list|<
name|Connection
argument_list|>
name|localConnection
init|=
operator|new
name|AtomicReference
argument_list|<
name|Connection
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|ActiveMQConnectionFactory
name|embeddedConnectionFactory
decl_stmt|;
specifier|protected
name|int
name|replyToDestinationCacheSize
init|=
literal|10000
decl_stmt|;
specifier|protected
name|String
name|outboundUsername
decl_stmt|;
specifier|protected
name|String
name|outboundPassword
decl_stmt|;
specifier|protected
name|String
name|localUsername
decl_stmt|;
specifier|protected
name|String
name|localPassword
decl_stmt|;
specifier|protected
name|String
name|outboundClientId
decl_stmt|;
specifier|protected
name|String
name|localClientId
decl_stmt|;
specifier|protected
name|LRUCache
argument_list|<
name|Destination
argument_list|,
name|DestinationBridge
argument_list|>
name|replyToBridges
init|=
name|createLRUCache
argument_list|()
decl_stmt|;
specifier|private
name|ReconnectionPolicy
name|policy
init|=
operator|new
name|ReconnectionPolicy
argument_list|()
decl_stmt|;
specifier|protected
name|ThreadPoolExecutor
name|connectionService
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|DestinationBridge
argument_list|>
name|inboundBridges
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|DestinationBridge
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|DestinationBridge
argument_list|>
name|outboundBridges
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|DestinationBridge
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
specifier|static
name|LRUCache
argument_list|<
name|Destination
argument_list|,
name|DestinationBridge
argument_list|>
name|createLRUCache
parameter_list|()
block|{
return|return
operator|new
name|LRUCache
argument_list|<
name|Destination
argument_list|,
name|DestinationBridge
argument_list|>
argument_list|()
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|7446792754185879286L
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|Destination
argument_list|,
name|DestinationBridge
argument_list|>
name|enty
parameter_list|)
block|{
if|if
condition|(
name|size
argument_list|()
operator|>
name|maxCacheSize
condition|)
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Destination
argument_list|,
name|DestinationBridge
argument_list|>
argument_list|>
name|iter
init|=
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|Destination
argument_list|,
name|DestinationBridge
argument_list|>
name|lru
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|remove
argument_list|(
name|lru
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|DestinationBridge
name|bridge
init|=
name|lru
operator|.
name|getValue
argument_list|()
decl_stmt|;
try|try
block|{
name|bridge
operator|.
name|stop
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Expired bridge: {}"
argument_list|,
name|bridge
argument_list|)
expr_stmt|;
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
literal|"Stopping expired bridge {} caused an exception"
argument_list|,
name|bridge
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
specifier|public
name|boolean
name|init
parameter_list|()
block|{
name|boolean
name|result
init|=
name|initialized
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
condition|)
block|{
if|if
condition|(
name|jndiLocalTemplate
operator|==
literal|null
condition|)
block|{
name|jndiLocalTemplate
operator|=
operator|new
name|JndiLookupFactory
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|jndiOutboundTemplate
operator|==
literal|null
condition|)
block|{
name|jndiOutboundTemplate
operator|=
operator|new
name|JndiLookupFactory
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|inboundMessageConvertor
operator|==
literal|null
condition|)
block|{
name|inboundMessageConvertor
operator|=
operator|new
name|SimpleJmsMessageConvertor
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|outboundMessageConvertor
operator|==
literal|null
condition|)
block|{
name|outboundMessageConvertor
operator|=
operator|new
name|SimpleJmsMessageConvertor
argument_list|()
expr_stmt|;
block|}
name|replyToBridges
operator|.
name|setMaxCacheSize
argument_list|(
name|getReplyToDestinationCacheSize
argument_list|()
argument_list|)
expr_stmt|;
name|connectionService
operator|=
name|createExecutor
argument_list|()
expr_stmt|;
comment|// Subclasses can override this to customize their own it.
name|result
operator|=
name|doConnectorInit
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|protected
name|boolean
name|doConnectorInit
parameter_list|()
block|{
comment|// We try to make a connection via a sync call first so that the
comment|// JmsConnector is fully initialized before the start call returns
comment|// in order to avoid missing any messages that are dispatched
comment|// immediately after startup.  If either side fails we queue an
comment|// asynchronous task to manage the reconnect attempts.
try|try
block|{
name|initializeLocalConnection
argument_list|()
expr_stmt|;
name|localSideInitialized
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Queue up the task to attempt the local connection.
name|scheduleAsyncLocalConnectionReconnect
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|initializeForeignConnection
argument_list|()
expr_stmt|;
name|foreignSideInitialized
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Queue up the task for the foreign connection now.
name|scheduleAsyncForeignConnectionReconnect
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|init
argument_list|()
expr_stmt|;
for|for
control|(
name|DestinationBridge
name|bridge
range|:
name|inboundBridges
control|)
block|{
name|bridge
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|DestinationBridge
name|bridge
range|:
name|outboundBridges
control|)
block|{
name|bridge
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"JMS Connector {} started"
argument_list|,
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|ThreadPoolUtils
operator|.
name|shutdown
argument_list|(
name|connectionService
argument_list|)
expr_stmt|;
name|connectionService
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|foreignConnection
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|foreignConnection
operator|.
name|get
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                 }
block|}
if|if
condition|(
name|localConnection
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|localConnection
operator|.
name|get
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                 }
block|}
for|for
control|(
name|DestinationBridge
name|bridge
range|:
name|inboundBridges
control|)
block|{
name|bridge
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|DestinationBridge
name|bridge
range|:
name|outboundBridges
control|)
block|{
name|bridge
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"JMS Connector {} stopped"
argument_list|,
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|clearBridges
parameter_list|()
block|{
name|inboundBridges
operator|.
name|clear
argument_list|()
expr_stmt|;
name|outboundBridges
operator|.
name|clear
argument_list|()
expr_stmt|;
name|replyToBridges
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|Destination
name|createReplyToBridge
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|Connection
name|consumerConnection
parameter_list|,
name|Connection
name|producerConnection
parameter_list|)
function_decl|;
comment|/**      * One way to configure the local connection - this is called by The      * BrokerService when the Connector is embedded      *      * @param service      */
specifier|public
name|void
name|setBrokerService
parameter_list|(
name|BrokerService
name|service
parameter_list|)
block|{
name|embeddedConnectionFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|service
operator|.
name|getVmConnectorURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Connection
name|getLocalConnection
parameter_list|()
block|{
return|return
name|this
operator|.
name|localConnection
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|Connection
name|getForeignConnection
parameter_list|()
block|{
return|return
name|this
operator|.
name|foreignConnection
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * @return Returns the jndiTemplate.      */
specifier|public
name|JndiLookupFactory
name|getJndiLocalTemplate
parameter_list|()
block|{
return|return
name|jndiLocalTemplate
return|;
block|}
comment|/**      * @param jndiTemplate The jndiTemplate to set.      */
specifier|public
name|void
name|setJndiLocalTemplate
parameter_list|(
name|JndiLookupFactory
name|jndiTemplate
parameter_list|)
block|{
name|this
operator|.
name|jndiLocalTemplate
operator|=
name|jndiTemplate
expr_stmt|;
block|}
comment|/**      * @return Returns the jndiOutboundTemplate.      */
specifier|public
name|JndiLookupFactory
name|getJndiOutboundTemplate
parameter_list|()
block|{
return|return
name|jndiOutboundTemplate
return|;
block|}
comment|/**      * @param jndiOutboundTemplate The jndiOutboundTemplate to set.      */
specifier|public
name|void
name|setJndiOutboundTemplate
parameter_list|(
name|JndiLookupFactory
name|jndiOutboundTemplate
parameter_list|)
block|{
name|this
operator|.
name|jndiOutboundTemplate
operator|=
name|jndiOutboundTemplate
expr_stmt|;
block|}
comment|/**      * @return Returns the inboundMessageConvertor.      */
specifier|public
name|JmsMesageConvertor
name|getInboundMessageConvertor
parameter_list|()
block|{
return|return
name|inboundMessageConvertor
return|;
block|}
comment|/**      * @param jmsMessageConvertor The jmsMessageConvertor to set.      */
specifier|public
name|void
name|setInboundMessageConvertor
parameter_list|(
name|JmsMesageConvertor
name|jmsMessageConvertor
parameter_list|)
block|{
name|this
operator|.
name|inboundMessageConvertor
operator|=
name|jmsMessageConvertor
expr_stmt|;
block|}
comment|/**      * @return Returns the outboundMessageConvertor.      */
specifier|public
name|JmsMesageConvertor
name|getOutboundMessageConvertor
parameter_list|()
block|{
return|return
name|outboundMessageConvertor
return|;
block|}
comment|/**      * @param outboundMessageConvertor The outboundMessageConvertor to set.      */
specifier|public
name|void
name|setOutboundMessageConvertor
parameter_list|(
name|JmsMesageConvertor
name|outboundMessageConvertor
parameter_list|)
block|{
name|this
operator|.
name|outboundMessageConvertor
operator|=
name|outboundMessageConvertor
expr_stmt|;
block|}
comment|/**      * @return Returns the replyToDestinationCacheSize.      */
specifier|public
name|int
name|getReplyToDestinationCacheSize
parameter_list|()
block|{
return|return
name|replyToDestinationCacheSize
return|;
block|}
comment|/**      * @param replyToDestinationCacheSize The replyToDestinationCacheSize to set.      */
specifier|public
name|void
name|setReplyToDestinationCacheSize
parameter_list|(
name|int
name|replyToDestinationCacheSize
parameter_list|)
block|{
name|this
operator|.
name|replyToDestinationCacheSize
operator|=
name|replyToDestinationCacheSize
expr_stmt|;
block|}
comment|/**      * @return Returns the localPassword.      */
specifier|public
name|String
name|getLocalPassword
parameter_list|()
block|{
return|return
name|localPassword
return|;
block|}
comment|/**      * @param localPassword The localPassword to set.      */
specifier|public
name|void
name|setLocalPassword
parameter_list|(
name|String
name|localPassword
parameter_list|)
block|{
name|this
operator|.
name|localPassword
operator|=
name|localPassword
expr_stmt|;
block|}
comment|/**      * @return Returns the localUsername.      */
specifier|public
name|String
name|getLocalUsername
parameter_list|()
block|{
return|return
name|localUsername
return|;
block|}
comment|/**      * @param localUsername The localUsername to set.      */
specifier|public
name|void
name|setLocalUsername
parameter_list|(
name|String
name|localUsername
parameter_list|)
block|{
name|this
operator|.
name|localUsername
operator|=
name|localUsername
expr_stmt|;
block|}
comment|/**      * @return Returns the outboundPassword.      */
specifier|public
name|String
name|getOutboundPassword
parameter_list|()
block|{
return|return
name|outboundPassword
return|;
block|}
comment|/**      * @param outboundPassword The outboundPassword to set.      */
specifier|public
name|void
name|setOutboundPassword
parameter_list|(
name|String
name|outboundPassword
parameter_list|)
block|{
name|this
operator|.
name|outboundPassword
operator|=
name|outboundPassword
expr_stmt|;
block|}
comment|/**      * @return Returns the outboundUsername.      */
specifier|public
name|String
name|getOutboundUsername
parameter_list|()
block|{
return|return
name|outboundUsername
return|;
block|}
comment|/**      * @param outboundUsername The outboundUsername to set.      */
specifier|public
name|void
name|setOutboundUsername
parameter_list|(
name|String
name|outboundUsername
parameter_list|)
block|{
name|this
operator|.
name|outboundUsername
operator|=
name|outboundUsername
expr_stmt|;
block|}
comment|/**      * @return the outboundClientId      */
specifier|public
name|String
name|getOutboundClientId
parameter_list|()
block|{
return|return
name|outboundClientId
return|;
block|}
comment|/**      * @param outboundClientId the outboundClientId to set      */
specifier|public
name|void
name|setOutboundClientId
parameter_list|(
name|String
name|outboundClientId
parameter_list|)
block|{
name|this
operator|.
name|outboundClientId
operator|=
name|outboundClientId
expr_stmt|;
block|}
comment|/**      * @return the localClientId      */
specifier|public
name|String
name|getLocalClientId
parameter_list|()
block|{
return|return
name|localClientId
return|;
block|}
comment|/**      * @param localClientId the localClientId to set      */
specifier|public
name|void
name|setLocalClientId
parameter_list|(
name|String
name|localClientId
parameter_list|)
block|{
name|this
operator|.
name|localClientId
operator|=
name|localClientId
expr_stmt|;
block|}
comment|/**      * @return the currently configured reconnection policy.      */
specifier|public
name|ReconnectionPolicy
name|getReconnectionPolicy
parameter_list|()
block|{
return|return
name|this
operator|.
name|policy
return|;
block|}
comment|/**      * @param policy The new reconnection policy this {@link JmsConnector} should use.      */
specifier|public
name|void
name|setReconnectionPolicy
parameter_list|(
name|ReconnectionPolicy
name|policy
parameter_list|)
block|{
name|this
operator|.
name|policy
operator|=
name|policy
expr_stmt|;
block|}
comment|/**      * @return the preferJndiDestinationLookup      */
specifier|public
name|boolean
name|isPreferJndiDestinationLookup
parameter_list|()
block|{
return|return
name|preferJndiDestinationLookup
return|;
block|}
comment|/**      * Sets whether the connector should prefer to first try to find a destination in JNDI before      * using JMS semantics to create a Destination.  By default the connector will first use JMS      * semantics and then fall-back to JNDI lookup, setting this value to true will reverse that      * ordering.      *      * @param preferJndiDestinationLookup the preferJndiDestinationLookup to set      */
specifier|public
name|void
name|setPreferJndiDestinationLookup
parameter_list|(
name|boolean
name|preferJndiDestinationLookup
parameter_list|)
block|{
name|this
operator|.
name|preferJndiDestinationLookup
operator|=
name|preferJndiDestinationLookup
expr_stmt|;
block|}
comment|/**      * @return returns true if the {@link JmsConnector} is connected to both brokers.      */
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
name|localConnection
operator|.
name|get
argument_list|()
operator|!=
literal|null
operator|&&
name|foreignConnection
operator|.
name|get
argument_list|()
operator|!=
literal|null
return|;
block|}
specifier|protected
name|void
name|addInboundBridge
parameter_list|(
name|DestinationBridge
name|bridge
parameter_list|)
block|{
if|if
condition|(
operator|!
name|inboundBridges
operator|.
name|contains
argument_list|(
name|bridge
argument_list|)
condition|)
block|{
name|inboundBridges
operator|.
name|add
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|addOutboundBridge
parameter_list|(
name|DestinationBridge
name|bridge
parameter_list|)
block|{
if|if
condition|(
operator|!
name|outboundBridges
operator|.
name|contains
argument_list|(
name|bridge
argument_list|)
condition|)
block|{
name|outboundBridges
operator|.
name|add
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|removeInboundBridge
parameter_list|(
name|DestinationBridge
name|bridge
parameter_list|)
block|{
name|inboundBridges
operator|.
name|remove
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|removeOutboundBridge
parameter_list|(
name|DestinationBridge
name|bridge
parameter_list|)
block|{
name|outboundBridges
operator|.
name|remove
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|name
operator|=
literal|"Connector:"
operator|+
name|getNextId
argument_list|()
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|synchronized
name|int
name|getNextId
parameter_list|()
block|{
return|return
name|nextId
operator|++
return|;
block|}
specifier|public
name|boolean
name|isFailed
parameter_list|()
block|{
return|return
name|this
operator|.
name|failed
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * Performs the work of connection to the local side of the Connection.      *<p>      * This creates the initial connection to the local end of the {@link JmsConnector}      * and then sets up all the destination bridges with the information needed to bridge      * on the local side of the connection.      *      * @throws Exception if the connection cannot be established for any reason.      */
specifier|protected
specifier|abstract
name|void
name|initializeLocalConnection
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Performs the work of connection to the foreign side of the Connection.      *<p>      * This creates the initial connection to the foreign end of the {@link JmsConnector}      * and then sets up all the destination bridges with the information needed to bridge      * on the foreign side of the connection.      *      * @throws Exception if the connection cannot be established for any reason.      */
specifier|protected
specifier|abstract
name|void
name|initializeForeignConnection
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Callback method that the Destination bridges can use to report an exception to occurs      * during normal bridging operations.      *      * @param connection      * 		The connection that was in use when the failure occured.      */
name|void
name|handleConnectionFailure
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
comment|// Can happen if async exception listener kicks in at the same time.
if|if
condition|(
name|connection
operator|==
literal|null
operator|||
operator|!
name|this
operator|.
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"JmsConnector handling loss of connection [{}]"
argument_list|,
name|connection
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO - How do we handle the re-wiring of replyToBridges in this case.
name|replyToBridges
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|foreignConnection
operator|.
name|compareAndSet
argument_list|(
name|connection
argument_list|,
literal|null
argument_list|)
condition|)
block|{
comment|// Stop the inbound bridges when the foreign connection is dropped since
comment|// the bridge has no consumer and needs to be restarted once a new connection
comment|// to the foreign side is made.
for|for
control|(
name|DestinationBridge
name|bridge
range|:
name|inboundBridges
control|)
block|{
try|try
block|{
name|bridge
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                 }
block|}
comment|// We got here first and cleared the connection, now we queue a reconnect.
name|this
operator|.
name|connectionService
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|doInitializeConnection
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to initialize foreign connection for the JMSConnector"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|localConnection
operator|.
name|compareAndSet
argument_list|(
name|connection
argument_list|,
literal|null
argument_list|)
condition|)
block|{
comment|// Stop the outbound bridges when the local connection is dropped since
comment|// the bridge has no consumer and needs to be restarted once a new connection
comment|// to the local side is made.
for|for
control|(
name|DestinationBridge
name|bridge
range|:
name|outboundBridges
control|)
block|{
try|try
block|{
name|bridge
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                 }
block|}
comment|// We got here first and cleared the connection, now we queue a reconnect.
name|this
operator|.
name|connectionService
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|doInitializeConnection
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to initialize local connection for the JMSConnector"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|scheduleAsyncLocalConnectionReconnect
parameter_list|()
block|{
name|this
operator|.
name|connectionService
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|doInitializeConnection
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to initialize local connection for the JMSConnector"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|scheduleAsyncForeignConnectionReconnect
parameter_list|()
block|{
name|this
operator|.
name|connectionService
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|doInitializeConnection
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to initialize foreign connection for the JMSConnector"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doInitializeConnection
parameter_list|(
name|boolean
name|local
parameter_list|)
throws|throws
name|Exception
block|{
name|ThreadPoolExecutor
name|connectionService
init|=
name|this
operator|.
name|connectionService
decl_stmt|;
name|int
name|attempt
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|maxRetries
decl_stmt|;
if|if
condition|(
name|local
condition|)
block|{
name|maxRetries
operator|=
operator|!
name|localSideInitialized
operator|.
name|get
argument_list|()
condition|?
name|policy
operator|.
name|getMaxInitialConnectAttempts
argument_list|()
else|:
name|policy
operator|.
name|getMaxReconnectAttempts
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|maxRetries
operator|=
operator|!
name|foreignSideInitialized
operator|.
name|get
argument_list|()
condition|?
name|policy
operator|.
name|getMaxInitialConnectAttempts
argument_list|()
else|:
name|policy
operator|.
name|getMaxReconnectAttempts
argument_list|()
expr_stmt|;
block|}
do|do
block|{
if|if
condition|(
name|attempt
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|long
name|nextDelay
init|=
name|policy
operator|.
name|getNextDelay
argument_list|(
name|attempt
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Bridge reconnect attempt {} waiting {}ms before next attempt."
argument_list|,
name|attempt
argument_list|,
name|nextDelay
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|nextDelay
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{                 }
block|}
if|if
condition|(
name|connectionService
operator|.
name|isTerminating
argument_list|()
condition|)
block|{
return|return;
block|}
try|try
block|{
if|if
condition|(
name|local
condition|)
block|{
name|initializeLocalConnection
argument_list|()
expr_stmt|;
name|localSideInitialized
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|initializeForeignConnection
argument_list|()
expr_stmt|;
name|foreignSideInitialized
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// Once we are connected we ensure all the bridges are started.
if|if
condition|(
name|localConnection
operator|.
name|get
argument_list|()
operator|!=
literal|null
operator|&&
name|foreignConnection
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|DestinationBridge
name|bridge
range|:
name|inboundBridges
control|)
block|{
name|bridge
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|DestinationBridge
name|bridge
range|:
name|outboundBridges
control|)
block|{
name|bridge
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
return|return;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to establish initial {} connection for JmsConnector [{}]"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|(
name|local
condition|?
literal|"local"
else|:
literal|"foreign"
operator|)
block|,
name|attempt
block|}
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|attempt
operator|++
expr_stmt|;
block|}
block|}
do|while
condition|(
operator|(
name|maxRetries
operator|==
name|INFINITE
operator|||
name|maxRetries
operator|>
name|attempt
operator|)
operator|&&
operator|!
name|connectionService
operator|.
name|isShutdown
argument_list|()
condition|)
do|;
name|this
operator|.
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
name|ThreadFactory
name|factory
init|=
operator|new
name|ThreadFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|runnable
parameter_list|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|runnable
argument_list|,
literal|"JmsConnector Async Connection Task: "
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
block|}
decl_stmt|;
specifier|private
name|ThreadPoolExecutor
name|createExecutor
parameter_list|()
block|{
name|ThreadPoolExecutor
name|exec
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|,
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
name|factory
argument_list|)
decl_stmt|;
name|exec
operator|.
name|allowCoreThreadTimeOut
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|exec
return|;
block|}
block|}
end_class

end_unit

