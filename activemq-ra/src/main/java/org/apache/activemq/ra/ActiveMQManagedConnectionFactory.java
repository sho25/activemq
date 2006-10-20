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
name|ra
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|Set
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
name|resource
operator|.
name|ResourceException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ConnectionManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ConnectionRequestInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ManagedConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ManagedConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ResourceAdapter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ResourceAdapterAssociation
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_comment
comment|/**  * @version $Revisio n$  *   * TODO: Must override equals and hashCode (JCA spec 16.4)  *   * @org.apache.xbean.XBean element="managedConnectionFactory"    */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQManagedConnectionFactory
implements|implements
name|ManagedConnectionFactory
implements|,
name|ResourceAdapterAssociation
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|6196921962230582875L
decl_stmt|;
specifier|private
name|ActiveMQResourceAdapter
name|adapter
decl_stmt|;
specifier|private
name|PrintWriter
name|logWriter
decl_stmt|;
specifier|private
name|ActiveMQConnectionRequestInfo
name|info
init|=
operator|new
name|ActiveMQConnectionRequestInfo
argument_list|()
decl_stmt|;
specifier|public
name|void
name|setResourceAdapter
parameter_list|(
name|ResourceAdapter
name|adapter
parameter_list|)
throws|throws
name|ResourceException
block|{
name|this
operator|.
name|adapter
operator|=
operator|(
name|ActiveMQResourceAdapter
operator|)
name|adapter
expr_stmt|;
name|ActiveMQConnectionRequestInfo
name|baseInfo
init|=
name|this
operator|.
name|adapter
operator|.
name|getInfo
argument_list|()
operator|.
name|copy
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|getClientid
argument_list|()
operator|==
literal|null
condition|)
name|info
operator|.
name|setClientid
argument_list|(
name|baseInfo
operator|.
name|getClientid
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|getPassword
argument_list|()
operator|==
literal|null
condition|)
name|info
operator|.
name|setPassword
argument_list|(
name|baseInfo
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|getServerUrl
argument_list|()
operator|==
literal|null
condition|)
name|info
operator|.
name|setServerUrl
argument_list|(
name|baseInfo
operator|.
name|getServerUrl
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|getUseInboundSession
argument_list|()
operator|==
literal|null
condition|)
name|info
operator|.
name|setUseInboundSession
argument_list|(
name|baseInfo
operator|.
name|getUseInboundSession
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|getUserName
argument_list|()
operator|==
literal|null
condition|)
name|info
operator|.
name|setUserName
argument_list|(
name|baseInfo
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|object
operator|==
literal|null
operator|||
name|object
operator|.
name|getClass
argument_list|()
operator|!=
name|ActiveMQManagedConnectionFactory
operator|.
name|class
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|(
operator|(
name|ActiveMQManagedConnectionFactory
operator|)
name|object
operator|)
operator|.
name|info
operator|.
name|equals
argument_list|(
name|info
argument_list|)
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|info
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|ResourceAdapter
name|getResourceAdapter
parameter_list|()
block|{
return|return
name|adapter
return|;
block|}
comment|/**      * @see javax.resource.spi.ManagedConnectionFactory#createConnectionFactory(javax.resource.spi.ConnectionManager)      */
specifier|public
name|Object
name|createConnectionFactory
parameter_list|(
name|ConnectionManager
name|manager
parameter_list|)
throws|throws
name|ResourceException
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|this
argument_list|,
name|manager
argument_list|,
name|info
argument_list|)
return|;
block|}
comment|/**      * This is used when not running in an app server. For now we are creating a      * ConnectionFactory that has our SimpleConnectionManager implementation but      * it may be a better idea to not support this. The JMS api will have many      * quirks the user may not expect when running through the resource adapter.      *       * @see javax.resource.spi.ManagedConnectionFactory#createConnectionFactory()      */
specifier|public
name|Object
name|createConnectionFactory
parameter_list|()
throws|throws
name|ResourceException
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|this
argument_list|,
operator|new
name|SimpleConnectionManager
argument_list|()
argument_list|,
name|info
argument_list|)
return|;
block|}
comment|/**      * @see javax.resource.spi.ManagedConnectionFactory#createManagedConnection(javax.security.auth.Subject,      *      javax.resource.spi.ConnectionRequestInfo)      */
specifier|public
name|ManagedConnection
name|createManagedConnection
parameter_list|(
name|Subject
name|subject
parameter_list|,
name|ConnectionRequestInfo
name|info
parameter_list|)
throws|throws
name|ResourceException
block|{
try|try
block|{
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|info
operator|=
name|this
operator|.
name|info
expr_stmt|;
block|}
name|ActiveMQConnectionRequestInfo
name|amqInfo
init|=
operator|(
name|ActiveMQConnectionRequestInfo
operator|)
name|info
decl_stmt|;
return|return
operator|new
name|ActiveMQManagedConnection
argument_list|(
name|subject
argument_list|,
name|adapter
operator|.
name|makeConnection
argument_list|(
name|amqInfo
argument_list|)
argument_list|,
name|amqInfo
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"Could not create connection."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see javax.resource.spi.ManagedConnectionFactory#matchManagedConnections(java.util.Set,      *      javax.security.auth.Subject,      *      javax.resource.spi.ConnectionRequestInfo)      */
specifier|public
name|ManagedConnection
name|matchManagedConnections
parameter_list|(
name|Set
name|connections
parameter_list|,
name|Subject
name|subject
parameter_list|,
name|ConnectionRequestInfo
name|info
parameter_list|)
throws|throws
name|ResourceException
block|{
name|Iterator
name|iterator
init|=
name|connections
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ActiveMQManagedConnection
name|c
init|=
operator|(
name|ActiveMQManagedConnection
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|matches
argument_list|(
name|subject
argument_list|,
name|info
argument_list|)
condition|)
block|{
try|try
block|{
name|c
operator|.
name|associate
argument_list|(
name|subject
argument_list|,
operator|(
name|ActiveMQConnectionRequestInfo
operator|)
name|info
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * @see javax.resource.spi.ManagedConnectionFactory#setLogWriter(java.io.PrintWriter)      */
specifier|public
name|void
name|setLogWriter
parameter_list|(
name|PrintWriter
name|logWriter
parameter_list|)
throws|throws
name|ResourceException
block|{
name|this
operator|.
name|logWriter
operator|=
name|logWriter
expr_stmt|;
block|}
comment|/**      * @see javax.resource.spi.ManagedConnectionFactory#getLogWriter()      */
specifier|public
name|PrintWriter
name|getLogWriter
parameter_list|()
throws|throws
name|ResourceException
block|{
return|return
name|logWriter
return|;
block|}
comment|// /////////////////////////////////////////////////////////////////////////
comment|//
comment|// Bean setters and getters.
comment|//
comment|// /////////////////////////////////////////////////////////////////////////
specifier|public
name|String
name|getClientid
parameter_list|()
block|{
return|return
name|info
operator|.
name|getClientid
argument_list|()
return|;
block|}
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|info
operator|.
name|getPassword
argument_list|()
return|;
block|}
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|info
operator|.
name|getUserName
argument_list|()
return|;
block|}
specifier|public
name|void
name|setClientid
parameter_list|(
name|String
name|clientid
parameter_list|)
block|{
name|info
operator|.
name|setClientid
argument_list|(
name|clientid
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
name|info
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setUserName
parameter_list|(
name|String
name|userid
parameter_list|)
block|{
name|info
operator|.
name|setUserName
argument_list|(
name|userid
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Boolean
name|getUseInboundSession
parameter_list|()
block|{
return|return
name|info
operator|.
name|getUseInboundSession
argument_list|()
return|;
block|}
specifier|public
name|void
name|setUseInboundSession
parameter_list|(
name|Boolean
name|useInboundSession
parameter_list|)
block|{
name|info
operator|.
name|setUseInboundSession
argument_list|(
name|useInboundSession
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseInboundSessionEnabled
parameter_list|()
block|{
return|return
name|info
operator|.
name|isUseInboundSessionEnabled
argument_list|()
return|;
block|}
comment|// Redelivery policy configuration
specifier|public
name|Long
name|getInitialRedeliveryDelay
parameter_list|()
block|{
return|return
name|info
operator|.
name|getInitialRedeliveryDelay
argument_list|()
return|;
block|}
specifier|public
name|Integer
name|getMaximumRedeliveries
parameter_list|()
block|{
return|return
name|info
operator|.
name|getMaximumRedeliveries
argument_list|()
return|;
block|}
specifier|public
name|Short
name|getRedeliveryBackOffMultiplier
parameter_list|()
block|{
return|return
name|info
operator|.
name|getRedeliveryBackOffMultiplier
argument_list|()
return|;
block|}
specifier|public
name|Boolean
name|getRedeliveryUseExponentialBackOff
parameter_list|()
block|{
return|return
name|info
operator|.
name|getRedeliveryUseExponentialBackOff
argument_list|()
return|;
block|}
specifier|public
name|void
name|setInitialRedeliveryDelay
parameter_list|(
name|Long
name|value
parameter_list|)
block|{
name|info
operator|.
name|setInitialRedeliveryDelay
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setMaximumRedeliveries
parameter_list|(
name|Integer
name|value
parameter_list|)
block|{
name|info
operator|.
name|setMaximumRedeliveries
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setRedeliveryBackOffMultiplier
parameter_list|(
name|Short
name|value
parameter_list|)
block|{
name|info
operator|.
name|setRedeliveryBackOffMultiplier
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setRedeliveryUseExponentialBackOff
parameter_list|(
name|Boolean
name|value
parameter_list|)
block|{
name|info
operator|.
name|setRedeliveryUseExponentialBackOff
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|// Prefetch policy configuration
specifier|public
name|Integer
name|getDurableTopicPrefetch
parameter_list|()
block|{
return|return
name|info
operator|.
name|getDurableTopicPrefetch
argument_list|()
return|;
block|}
specifier|public
name|Integer
name|getInputStreamPrefetch
parameter_list|()
block|{
return|return
name|info
operator|.
name|getInputStreamPrefetch
argument_list|()
return|;
block|}
specifier|public
name|Integer
name|getQueueBrowserPrefetch
parameter_list|()
block|{
return|return
name|info
operator|.
name|getQueueBrowserPrefetch
argument_list|()
return|;
block|}
specifier|public
name|Integer
name|getQueuePrefetch
parameter_list|()
block|{
return|return
name|info
operator|.
name|getQueuePrefetch
argument_list|()
return|;
block|}
specifier|public
name|Integer
name|getTopicPrefetch
parameter_list|()
block|{
return|return
name|info
operator|.
name|getTopicPrefetch
argument_list|()
return|;
block|}
specifier|public
name|void
name|setAllPrefetchValues
parameter_list|(
name|Integer
name|i
parameter_list|)
block|{
name|info
operator|.
name|setAllPrefetchValues
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDurableTopicPrefetch
parameter_list|(
name|Integer
name|durableTopicPrefetch
parameter_list|)
block|{
name|info
operator|.
name|setDurableTopicPrefetch
argument_list|(
name|durableTopicPrefetch
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setInputStreamPrefetch
parameter_list|(
name|Integer
name|inputStreamPrefetch
parameter_list|)
block|{
name|info
operator|.
name|setInputStreamPrefetch
argument_list|(
name|inputStreamPrefetch
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setQueueBrowserPrefetch
parameter_list|(
name|Integer
name|queueBrowserPrefetch
parameter_list|)
block|{
name|info
operator|.
name|setQueueBrowserPrefetch
argument_list|(
name|queueBrowserPrefetch
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setQueuePrefetch
parameter_list|(
name|Integer
name|queuePrefetch
parameter_list|)
block|{
name|info
operator|.
name|setQueuePrefetch
argument_list|(
name|queuePrefetch
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTopicPrefetch
parameter_list|(
name|Integer
name|topicPrefetch
parameter_list|)
block|{
name|info
operator|.
name|setTopicPrefetch
argument_list|(
name|topicPrefetch
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

