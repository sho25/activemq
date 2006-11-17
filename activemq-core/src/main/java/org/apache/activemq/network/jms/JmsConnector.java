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
name|network
operator|.
name|jms
package|;
end_package

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
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
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
name|org
operator|.
name|springframework
operator|.
name|jndi
operator|.
name|JndiTemplate
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * This bridge joins the gap between foreign JMS providers and ActiveMQ As some  * JMS providers are still only 1.0.1 compliant, this bridge itself aimed to be  * JMS 1.0.2 compliant.  *   * @version $Revision: 1.1.1.1 $  */
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
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JmsConnector
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|JndiTemplate
name|jndiLocalTemplate
decl_stmt|;
specifier|protected
name|JndiTemplate
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
specifier|private
name|List
name|inboundBridges
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|()
decl_stmt|;
specifier|private
name|List
name|outboundBridges
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|()
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
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
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
specifier|private
name|String
name|name
decl_stmt|;
specifier|protected
name|LRUCache
name|replyToBridges
init|=
name|createLRUCache
argument_list|()
decl_stmt|;
specifier|static
specifier|private
name|LRUCache
name|createLRUCache
parameter_list|()
block|{
return|return
operator|new
name|LRUCache
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
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
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
name|lru
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
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
operator|(
name|DestinationBridge
operator|)
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
name|log
operator|.
name|info
argument_list|(
literal|"Expired bridge: "
operator|+
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
name|log
operator|.
name|warn
argument_list|(
literal|"stopping expired bridge"
operator|+
name|bridge
operator|+
literal|" caused an exception"
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
comment|/**      */
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
name|JndiTemplate
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
name|JndiTemplate
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
block|}
return|return
name|result
return|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|init
argument_list|()
expr_stmt|;
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|inboundBridges
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|DestinationBridge
name|bridge
init|=
operator|(
name|DestinationBridge
operator|)
name|inboundBridges
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|bridge
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|outboundBridges
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|DestinationBridge
name|bridge
init|=
operator|(
name|DestinationBridge
operator|)
name|outboundBridges
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|bridge
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"JMS Connector "
operator|+
name|getName
argument_list|()
operator|+
literal|" Started"
argument_list|)
expr_stmt|;
block|}
block|}
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|inboundBridges
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|DestinationBridge
name|bridge
init|=
operator|(
name|DestinationBridge
operator|)
name|inboundBridges
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|bridge
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|outboundBridges
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|DestinationBridge
name|bridge
init|=
operator|(
name|DestinationBridge
operator|)
name|outboundBridges
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|bridge
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"JMS Connector "
operator|+
name|getName
argument_list|()
operator|+
literal|" Stopped"
argument_list|)
expr_stmt|;
block|}
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
comment|/**      * One way to configure the local connection - this is called by The      * BrokerService when the Connector is embedded      *       * @param service      */
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
comment|/**      * @return Returns the jndiTemplate.      */
specifier|public
name|JndiTemplate
name|getJndiLocalTemplate
parameter_list|()
block|{
return|return
name|jndiLocalTemplate
return|;
block|}
comment|/**      * @param jndiTemplate      *            The jndiTemplate to set.      */
specifier|public
name|void
name|setJndiLocalTemplate
parameter_list|(
name|JndiTemplate
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
name|JndiTemplate
name|getJndiOutboundTemplate
parameter_list|()
block|{
return|return
name|jndiOutboundTemplate
return|;
block|}
comment|/**      * @param jndiOutboundTemplate      *            The jndiOutboundTemplate to set.      */
specifier|public
name|void
name|setJndiOutboundTemplate
parameter_list|(
name|JndiTemplate
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
comment|/**      * @param inboundMessageConvertor      *            The inboundMessageConvertor to set.      */
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
comment|/**      * @param outboundMessageConvertor      *            The outboundMessageConvertor to set.      */
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
comment|/**      * @param replyToDestinationCacheSize      *            The replyToDestinationCacheSize to set.      */
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
comment|/**      * @param localPassword      *            The localPassword to set.      */
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
comment|/**      * @param localUsername      *            The localUsername to set.      */
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
comment|/**      * @param outboundPassword      *            The outboundPassword to set.      */
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
comment|/**      * @param outboundUsername      *            The outboundUsername to set.      */
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
specifier|protected
name|void
name|addInboundBridge
parameter_list|(
name|DestinationBridge
name|bridge
parameter_list|)
block|{
name|inboundBridges
operator|.
name|add
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|addOutboundBridge
parameter_list|(
name|DestinationBridge
name|bridge
parameter_list|)
block|{
name|outboundBridges
operator|.
name|add
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
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
name|add
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
name|add
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
specifier|static
name|int
name|nextId
decl_stmt|;
specifier|static
specifier|private
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
specifier|public
specifier|abstract
name|void
name|restartProducerConnection
parameter_list|()
throws|throws
name|NamingException
throws|,
name|JMSException
function_decl|;
block|}
end_class

end_unit

