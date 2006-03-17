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
name|ra
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
name|ActiveMQConnection
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
name|RedeliveryPolicy
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
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XAConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XASession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|NotSupportedException
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
name|ActivationSpec
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
name|BootstrapContext
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
name|ResourceAdapterInternalException
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
name|endpoint
operator|.
name|MessageEndpointFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAResource
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|net
operator|.
name|URISyntaxException
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

begin_comment
comment|/**  * Knows how to connect to one ActiveMQ server. It can then activate endpoints  * and deliver messages to those end points using the connection configure in the  * resource adapter.<p/>Must override equals and hashCode (JCA spec 16.4)  *  * @org.apache.xbean.XBean element="resourceAdapter" rootElement="true"  * description="The JCA Resource Adaptor for ActiveMQ"  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQResourceAdapter
implements|implements
name|ResourceAdapter
implements|,
name|Serializable
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|5417363537865649130L
decl_stmt|;
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
name|ActiveMQResourceAdapter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|HashMap
name|endpointWorkers
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQConnectionRequestInfo
name|info
init|=
operator|new
name|ActiveMQConnectionRequestInfo
argument_list|()
decl_stmt|;
specifier|private
name|BootstrapContext
name|bootstrapContext
decl_stmt|;
specifier|private
name|String
name|brokerXmlConfig
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|public
name|ActiveMQResourceAdapter
parameter_list|()
block|{     }
comment|/**      * @see javax.resource.spi.ResourceAdapter#start(javax.resource.spi.BootstrapContext)      */
specifier|public
name|void
name|start
parameter_list|(
name|BootstrapContext
name|bootstrapContext
parameter_list|)
throws|throws
name|ResourceAdapterInternalException
block|{
name|this
operator|.
name|bootstrapContext
operator|=
name|bootstrapContext
expr_stmt|;
if|if
condition|(
name|brokerXmlConfig
operator|!=
literal|null
operator|&&
name|brokerXmlConfig
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|broker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
name|brokerXmlConfig
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceAdapterInternalException
argument_list|(
literal|"Failed to startup an embedded broker: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|ActiveMQConnection
name|makeConnection
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|connectionFactory
operator|!=
literal|null
condition|)
block|{
return|return
name|makeConnection
argument_list|(
name|info
argument_list|,
name|connectionFactory
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|makeConnection
argument_list|(
name|info
argument_list|)
return|;
block|}
block|}
comment|/**      */
specifier|public
name|ActiveMQConnection
name|makeConnection
parameter_list|(
name|ActiveMQConnectionRequestInfo
name|info
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
name|createConnectionFactory
argument_list|(
name|info
argument_list|)
decl_stmt|;
return|return
name|makeConnection
argument_list|(
name|info
argument_list|,
name|connectionFactory
argument_list|)
return|;
block|}
specifier|public
name|ActiveMQConnection
name|makeConnection
parameter_list|(
name|ActiveMQConnectionRequestInfo
name|info
parameter_list|,
name|ActiveMQConnectionFactory
name|connectionFactory
parameter_list|)
throws|throws
name|JMSException
block|{
name|String
name|userName
init|=
name|info
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|String
name|password
init|=
name|info
operator|.
name|getPassword
argument_list|()
decl_stmt|;
name|ActiveMQConnection
name|physicalConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|String
name|clientId
init|=
name|info
operator|.
name|getClientid
argument_list|()
decl_stmt|;
if|if
condition|(
name|clientId
operator|!=
literal|null
operator|&&
name|clientId
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|physicalConnection
operator|.
name|setClientID
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
block|}
return|return
name|physicalConnection
return|;
block|}
comment|/**      * @param activationSpec      */
specifier|public
name|ActiveMQConnection
name|makeConnection
parameter_list|(
name|ActiveMQActivationSpec
name|activationSpec
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
name|createConnectionFactory
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|String
name|userName
init|=
name|defaultValue
argument_list|(
name|activationSpec
operator|.
name|getUserName
argument_list|()
argument_list|,
name|info
operator|.
name|getUserName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|password
init|=
name|defaultValue
argument_list|(
name|activationSpec
operator|.
name|getPassword
argument_list|()
argument_list|,
name|info
operator|.
name|getPassword
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|clientId
init|=
name|activationSpec
operator|.
name|getClientId
argument_list|()
decl_stmt|;
if|if
condition|(
name|clientId
operator|!=
literal|null
condition|)
block|{
name|connectionFactory
operator|.
name|setClientID
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|activationSpec
operator|.
name|isDurableSubscription
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No clientID specified for durable subscription: "
operator|+
name|activationSpec
argument_list|)
expr_stmt|;
block|}
block|}
name|ActiveMQConnection
name|physicalConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
decl_stmt|;
comment|// have we configured a redelivery policy
name|RedeliveryPolicy
name|redeliveryPolicy
init|=
name|activationSpec
operator|.
name|redeliveryPolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|redeliveryPolicy
operator|!=
literal|null
condition|)
block|{
name|physicalConnection
operator|.
name|setRedeliveryPolicy
argument_list|(
name|redeliveryPolicy
argument_list|)
expr_stmt|;
block|}
return|return
name|physicalConnection
return|;
block|}
comment|/**      * @param info      * @return      * @throws JMSException      * @throws URISyntaxException      */
specifier|synchronized
specifier|private
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|(
name|ActiveMQConnectionRequestInfo
name|info
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
name|connectionFactory
decl_stmt|;
if|if
condition|(
name|factory
operator|!=
literal|null
operator|&&
name|info
operator|.
name|isConnectionFactoryConfigured
argument_list|()
condition|)
block|{
name|factory
operator|=
name|factory
operator|.
name|copy
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|factory
operator|==
literal|null
condition|)
block|{
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
expr_stmt|;
block|}
name|info
operator|.
name|configure
argument_list|(
name|factory
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
specifier|private
name|String
name|defaultValue
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
return|return
name|value
return|;
return|return
name|defaultValue
return|;
block|}
comment|/**      * @see javax.resource.spi.ResourceAdapter#stop()      */
specifier|public
name|void
name|stop
parameter_list|()
block|{
while|while
condition|(
name|endpointWorkers
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ActiveMQEndpointActivationKey
name|key
init|=
operator|(
name|ActiveMQEndpointActivationKey
operator|)
name|endpointWorkers
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|endpointDeactivation
argument_list|(
name|key
operator|.
name|getMessageEndpointFactory
argument_list|()
argument_list|,
name|key
operator|.
name|getActivationSpec
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|broker
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|bootstrapContext
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * @return      */
specifier|public
name|BootstrapContext
name|getBootstrapContext
parameter_list|()
block|{
return|return
name|bootstrapContext
return|;
block|}
comment|/**      * @see javax.resource.spi.ResourceAdapter#endpointActivation(javax.resource.spi.endpoint.MessageEndpointFactory,      *      javax.resource.spi.ActivationSpec)      */
specifier|public
name|void
name|endpointActivation
parameter_list|(
name|MessageEndpointFactory
name|endpointFactory
parameter_list|,
name|ActivationSpec
name|activationSpec
parameter_list|)
throws|throws
name|ResourceException
block|{
comment|// spec section 5.3.3
if|if
condition|(
name|activationSpec
operator|.
name|getResourceAdapter
argument_list|()
operator|!=
name|this
condition|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"Activation spec not initialized with this ResourceAdapter instance"
argument_list|)
throw|;
block|}
if|if
condition|(
name|activationSpec
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|ActiveMQActivationSpec
operator|.
name|class
argument_list|)
condition|)
block|{
name|ActiveMQEndpointActivationKey
name|key
init|=
operator|new
name|ActiveMQEndpointActivationKey
argument_list|(
name|endpointFactory
argument_list|,
operator|(
name|ActiveMQActivationSpec
operator|)
name|activationSpec
argument_list|)
decl_stmt|;
comment|// This is weird.. the same endpoint activated twice.. must be a
comment|// container error.
if|if
condition|(
name|endpointWorkers
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Endpoint previously activated"
argument_list|)
throw|;
block|}
name|ActiveMQEndpointWorker
name|worker
init|=
operator|new
name|ActiveMQEndpointWorker
argument_list|(
name|this
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|endpointWorkers
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|worker
argument_list|)
expr_stmt|;
name|worker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|NotSupportedException
argument_list|(
literal|"That type of ActicationSpec not supported: "
operator|+
name|activationSpec
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see javax.resource.spi.ResourceAdapter#endpointDeactivation(javax.resource.spi.endpoint.MessageEndpointFactory,      *      javax.resource.spi.ActivationSpec)      */
specifier|public
name|void
name|endpointDeactivation
parameter_list|(
name|MessageEndpointFactory
name|endpointFactory
parameter_list|,
name|ActivationSpec
name|activationSpec
parameter_list|)
block|{
if|if
condition|(
name|activationSpec
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|ActiveMQActivationSpec
operator|.
name|class
argument_list|)
condition|)
block|{
name|ActiveMQEndpointActivationKey
name|key
init|=
operator|new
name|ActiveMQEndpointActivationKey
argument_list|(
name|endpointFactory
argument_list|,
operator|(
name|ActiveMQActivationSpec
operator|)
name|activationSpec
argument_list|)
decl_stmt|;
name|ActiveMQEndpointWorker
name|worker
init|=
operator|(
name|ActiveMQEndpointWorker
operator|)
name|endpointWorkers
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|worker
operator|==
literal|null
condition|)
block|{
comment|// This is weird.. that endpoint was not activated.. oh well..
comment|// this method
comment|// does not throw exceptions so just return.
return|return;
block|}
try|try
block|{
name|worker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// We interrupted.. we won't throw an exception but will stop
comment|// waiting for the worker
comment|// to stop.. we tried our best. Keep trying to interrupt the
comment|// thread.
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * We only connect to one resource manager per ResourceAdapter instance, so      * any ActivationSpec will return the same XAResource.      *      * @see javax.resource.spi.ResourceAdapter#getXAResources(javax.resource.spi.ActivationSpec[])      */
specifier|public
name|XAResource
index|[]
name|getXAResources
parameter_list|(
name|ActivationSpec
index|[]
name|activationSpecs
parameter_list|)
throws|throws
name|ResourceException
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|makeConnection
argument_list|()
expr_stmt|;
if|if
condition|(
name|connection
operator|instanceof
name|XAConnection
condition|)
block|{
name|XASession
name|session
init|=
operator|(
operator|(
name|XAConnection
operator|)
name|connection
operator|)
operator|.
name|createXASession
argument_list|()
decl_stmt|;
name|XAResource
name|xaResource
init|=
name|session
operator|.
name|getXAResource
argument_list|()
decl_stmt|;
return|return
operator|new
name|XAResource
index|[]
block|{
name|xaResource
block|}
return|;
block|}
else|else
block|{
return|return
operator|new
name|XAResource
index|[]
block|{}
return|;
block|}
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
finally|finally
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{             }
block|}
block|}
comment|// ///////////////////////////////////////////////////////////////////////
comment|//
comment|// Java Bean getters and setters for this ResourceAdapter class.
comment|//
comment|// ///////////////////////////////////////////////////////////////////////
comment|/**      * @return      */
specifier|public
name|String
name|getClientid
parameter_list|()
block|{
return|return
name|emptyToNull
argument_list|(
name|info
operator|.
name|getClientid
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @return      */
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|emptyToNull
argument_list|(
name|info
operator|.
name|getPassword
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @return      */
specifier|public
name|String
name|getServerUrl
parameter_list|()
block|{
return|return
name|info
operator|.
name|getServerUrl
argument_list|()
return|;
block|}
comment|/**      * @return      */
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|emptyToNull
argument_list|(
name|info
operator|.
name|getUserName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @param clientid      */
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
comment|/**      * @param password      */
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
comment|/**      * @param url      */
specifier|public
name|void
name|setServerUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|info
operator|.
name|setServerUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param userid      */
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
name|String
name|getBrokerXmlConfig
parameter_list|()
block|{
return|return
name|brokerXmlConfig
return|;
block|}
comment|/**      * Sets the<a href="http://activemq.org/Xml+Configuration">XML      * configuration file</a> used to configure the ActiveMQ broker via Spring      * if using embedded mode.      *      * @param brokerXmlConfig      *            is the filename which is assumed to be on the classpath unless      *            a URL is specified. So a value of<code>foo/bar.xml</code>      *            would be assumed to be on the classpath whereas      *<code>file:dir/file.xml</code> would use the file system.      *            Any valid URL string is supported.      * @see #setUseEmbeddedBroker(Boolean)      */
specifier|public
name|void
name|setBrokerXmlConfig
parameter_list|(
name|String
name|brokerXmlConfig
parameter_list|)
block|{
name|this
operator|.
name|brokerXmlConfig
operator|=
name|brokerXmlConfig
expr_stmt|;
block|}
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
comment|/**      * @return Returns the info.      */
specifier|public
name|ActiveMQConnectionRequestInfo
name|getInfo
parameter_list|()
block|{
return|return
name|info
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|ActiveMQResourceAdapter
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|ActiveMQResourceAdapter
name|activeMQResourceAdapter
init|=
operator|(
name|ActiveMQResourceAdapter
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|info
operator|.
name|equals
argument_list|(
name|activeMQResourceAdapter
operator|.
name|info
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|notEqual
argument_list|(
name|brokerXmlConfig
argument_list|,
name|activeMQResourceAdapter
operator|.
name|brokerXmlConfig
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|notEqual
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
return|return
operator|(
name|o1
operator|==
literal|null
operator|^
name|o2
operator|==
literal|null
operator|)
operator|||
operator|(
name|o1
operator|!=
literal|null
operator|&&
operator|!
name|o1
operator|.
name|equals
argument_list|(
name|o2
argument_list|)
operator|)
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
decl_stmt|;
name|result
operator|=
name|info
operator|.
name|hashCode
argument_list|()
expr_stmt|;
if|if
condition|(
name|brokerXmlConfig
operator|!=
literal|null
condition|)
block|{
name|result
operator|^=
name|brokerXmlConfig
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|String
name|emptyToNull
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|value
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|value
return|;
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
name|ActiveMQConnectionFactory
name|getConnectionFactory
parameter_list|()
block|{
return|return
name|connectionFactory
return|;
block|}
comment|/**      * This allows a connection factory to be configured and shared between a ResourceAdaptor and outbound messaging.      * Note that setting the connectionFactory will overload many of the properties on this POJO such as the redelivery      * and prefetch policies; the properties on the connectionFactory will be used instead.      */
specifier|public
name|void
name|setConnectionFactory
parameter_list|(
name|ActiveMQConnectionFactory
name|connectionFactory
parameter_list|)
block|{
name|this
operator|.
name|connectionFactory
operator|=
name|connectionFactory
expr_stmt|;
block|}
block|}
end_class

end_unit

