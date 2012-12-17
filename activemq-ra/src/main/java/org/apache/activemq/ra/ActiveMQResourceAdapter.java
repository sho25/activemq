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
name|ra
package|;
end_package

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
name|HashMap
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

begin_comment
comment|/**  * Knows how to connect to one ActiveMQ server. It can then activate endpoints  * and deliver messages to those end points using the connection configure in  * the resource adapter.<p/>Must override equals and hashCode (JCA spec 16.4)  *   * @org.apache.xbean.XBean element="resourceAdapter" rootElement="true"  *                         description="The JCA Resource Adaptor for ActiveMQ"  *   */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQResourceAdapter
extends|extends
name|ActiveMQConnectionSupport
implements|implements
name|MessageResourceAdapter
block|{
specifier|private
specifier|final
name|HashMap
argument_list|<
name|ActiveMQEndpointActivationKey
argument_list|,
name|ActiveMQEndpointWorker
argument_list|>
name|endpointWorkers
init|=
operator|new
name|HashMap
argument_list|<
name|ActiveMQEndpointActivationKey
argument_list|,
name|ActiveMQEndpointWorker
argument_list|>
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
name|Thread
name|brokerStartThread
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|connectionFactory
decl_stmt|;
comment|/**      *       */
specifier|public
name|ActiveMQResourceAdapter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
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
name|brokerStartThread
operator|=
operator|new
name|Thread
argument_list|(
literal|"Starting ActiveMQ Broker"
argument_list|)
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
comment|// ensure RAR resources are available to xbean (needed for weblogic)
name|log
operator|.
name|debug
argument_list|(
literal|"original thread context classLoader: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"current (from getClass()) thread context classLoader: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|ActiveMQResourceAdapter
operator|.
name|this
init|)
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
block|}
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
name|log
operator|.
name|warn
argument_list|(
literal|"Could not start up embeded ActiveMQ Broker '"
operator|+
name|brokerXmlConfig
operator|+
literal|"': "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Reason for: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|brokerStartThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerStartThread
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Wait up to 5 seconds for the broker to start up in the async thread.. otherwise keep going without it..
try|try
block|{
name|brokerStartThread
operator|.
name|join
argument_list|(
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
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
comment|/**      * @see org.apache.activemq.ra.MessageResourceAdapter#makeConnection()      */
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
operator|==
literal|null
condition|)
block|{
return|return
name|makeConnection
argument_list|(
name|getInfo
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|makeConnection
argument_list|(
name|getInfo
argument_list|()
argument_list|,
name|connectionFactory
argument_list|)
return|;
block|}
block|}
comment|/**      * @param activationSpec      */
specifier|public
name|ActiveMQConnection
name|makeConnection
parameter_list|(
name|MessageActivationSpec
name|activationSpec
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
name|getConnectionFactory
argument_list|()
decl_stmt|;
if|if
condition|(
name|cf
operator|==
literal|null
condition|)
block|{
name|cf
operator|=
name|createConnectionFactory
argument_list|(
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|getInfo
argument_list|()
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
name|getInfo
argument_list|()
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
name|cf
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
name|cf
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
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|brokerStartThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|brokerStartThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
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
block|}
name|this
operator|.
name|bootstrapContext
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * @see org.apache.activemq.ra.MessageResourceAdapter#getBootstrapContext()      */
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
operator|!
name|equals
argument_list|(
name|activationSpec
operator|.
name|getResourceAdapter
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"Activation spec not initialized with this ResourceAdapter instance ("
operator|+
name|activationSpec
operator|.
name|getResourceAdapter
argument_list|()
operator|+
literal|" != "
operator|+
name|this
operator|+
literal|")"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|activationSpec
operator|instanceof
name|MessageActivationSpec
operator|)
condition|)
block|{
throw|throw
operator|new
name|NotSupportedException
argument_list|(
literal|"That type of ActivationSpec not supported: "
operator|+
name|activationSpec
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
name|ActiveMQEndpointActivationKey
name|key
init|=
operator|new
name|ActiveMQEndpointActivationKey
argument_list|(
name|endpointFactory
argument_list|,
operator|(
name|MessageActivationSpec
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
operator|instanceof
name|MessageActivationSpec
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
name|MessageActivationSpec
operator|)
name|activationSpec
argument_list|)
decl_stmt|;
name|ActiveMQEndpointWorker
name|worker
init|=
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
comment|/**      * We only connect to one resource manager per ResourceAdapter instance, so      * any ActivationSpec will return the same XAResource.      *       * @see javax.resource.spi.ResourceAdapter#getXAResources(javax.resource.spi.ActivationSpec[])      */
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
return|return
operator|new
name|XAResource
index|[]
block|{}
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
block|{
comment|//
block|}
block|}
block|}
comment|// ///////////////////////////////////////////////////////////////////////
comment|//
comment|// Java Bean getters and setters for this ResourceAdapter class.
comment|//
comment|// ///////////////////////////////////////////////////////////////////////
comment|/**      * @see org.apache.activemq.ra.MessageResourceAdapter#getBrokerXmlConfig()      */
specifier|public
name|String
name|getBrokerXmlConfig
parameter_list|()
block|{
return|return
name|brokerXmlConfig
return|;
block|}
comment|/**      * Sets the<a href="http://activemq.org/Xml+Configuration">XML      * configuration file</a> used to configure the ActiveMQ broker via Spring      * if using embedded mode.      *       * @param brokerXmlConfig is the filename which is assumed to be on the      *                classpath unless a URL is specified. So a value of      *<code>foo/bar.xml</code> would be assumed to be on the      *                classpath whereas<code>file:dir/file.xml</code> would      *                use the file system. Any valid URL string is supported.      */
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
comment|/**      * @see java.lang.Object#equals(java.lang.Object)      */
annotation|@
name|Override
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
name|MessageResourceAdapter
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|MessageResourceAdapter
name|activeMQResourceAdapter
init|=
operator|(
name|MessageResourceAdapter
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|getInfo
argument_list|()
operator|.
name|equals
argument_list|(
name|activeMQResourceAdapter
operator|.
name|getInfo
argument_list|()
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
name|getBrokerXmlConfig
argument_list|()
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
comment|/**      * @see java.lang.Object#hashCode()      */
annotation|@
name|Override
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
name|getInfo
argument_list|()
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
specifier|public
name|ActiveMQConnectionFactory
name|getConnectionFactory
parameter_list|()
block|{
return|return
name|connectionFactory
return|;
block|}
specifier|public
name|void
name|setConnectionFactory
parameter_list|(
name|ActiveMQConnectionFactory
name|aConnectionFactory
parameter_list|)
block|{
name|this
operator|.
name|connectionFactory
operator|=
name|aConnectionFactory
expr_stmt|;
block|}
block|}
end_class

end_unit

