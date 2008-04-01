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
name|camel
operator|.
name|component
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
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
name|spring
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
name|camel
operator|.
name|component
operator|.
name|jms
operator|.
name|JmsConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|jms
operator|.
name|connection
operator|.
name|SingleConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|jms
operator|.
name|core
operator|.
name|JmsTemplate
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQConfiguration
extends|extends
name|JmsConfiguration
block|{
specifier|private
name|String
name|brokerURL
init|=
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_BROKER_URL
decl_stmt|;
specifier|private
name|boolean
name|useSingleConnection
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|usePooledConnection
init|=
literal|true
decl_stmt|;
specifier|public
name|ActiveMQConfiguration
parameter_list|()
block|{     }
specifier|public
name|String
name|getBrokerURL
parameter_list|()
block|{
return|return
name|brokerURL
return|;
block|}
comment|/**      * Sets the broker URL to use to connect to ActiveMQ using the      *<a href="http://activemq.apache.org/configuring-transports.html">ActiveMQ URI format</a>      *      * @param brokerURL the URL of the broker.      */
specifier|public
name|void
name|setBrokerURL
parameter_list|(
name|String
name|brokerURL
parameter_list|)
block|{
name|this
operator|.
name|brokerURL
operator|=
name|brokerURL
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseSingleConnection
parameter_list|()
block|{
return|return
name|useSingleConnection
return|;
block|}
comment|/**      * Enables or disables whether a Spring {@link SingleConnectionFactory} will be used so that when      * messages are sent to ActiveMQ from outside of a message consuming thread, pooling will be used rather      * than the default with the Spring {@link JmsTemplate} which will create a new connection, session, producer      * for each message then close them all down again.      *<p/>      * The default value is true so that a single connection is used by default.      *      * @param useSingleConnection      */
specifier|public
name|void
name|setUseSingleConnection
parameter_list|(
name|boolean
name|useSingleConnection
parameter_list|)
block|{
name|this
operator|.
name|useSingleConnection
operator|=
name|useSingleConnection
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUsePooledConnection
parameter_list|()
block|{
return|return
name|usePooledConnection
return|;
block|}
comment|/**      * Enables or disables whether a PooledConnectionFactory will be used so that when      * messages are sent to ActiveMQ from outside of a message consuming thread, pooling will be used rather      * than the default with the Spring {@link JmsTemplate} which will create a new connection, session, producer      * for each message then close them all down again.      *<p/>      * The default value is false by default as it requires an extra dependency on commons-pool.      */
specifier|public
name|void
name|setUsePooledConnection
parameter_list|(
name|boolean
name|usePooledConnection
parameter_list|)
block|{
name|this
operator|.
name|usePooledConnection
operator|=
name|usePooledConnection
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
block|{
name|ActiveMQConnectionFactory
name|answer
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
decl_stmt|;
if|if
condition|(
name|answer
operator|.
name|getBeanName
argument_list|()
operator|==
literal|null
condition|)
block|{
name|answer
operator|.
name|setBeanName
argument_list|(
literal|"Camel"
argument_list|)
expr_stmt|;
block|}
name|answer
operator|.
name|setBrokerURL
argument_list|(
name|getBrokerURL
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isUseSingleConnection
argument_list|()
condition|)
block|{
return|return
operator|new
name|SingleConnectionFactory
argument_list|(
name|answer
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|isUsePooledConnection
argument_list|()
condition|)
block|{
return|return
name|createPooledConnectionFactory
argument_list|(
name|answer
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|answer
return|;
block|}
block|}
specifier|protected
name|ConnectionFactory
name|createPooledConnectionFactory
parameter_list|(
name|ActiveMQConnectionFactory
name|connectionFactory
parameter_list|)
block|{
comment|// lets not use classes directly to avoid a runtime dependency on commons-pool
comment|// for folks not using this option
try|try
block|{
name|Class
name|type
init|=
name|loadClass
argument_list|(
literal|"org.apache.activemq.pool.PooledConnectionFactory"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|Constructor
name|constructor
init|=
name|type
operator|.
name|getConstructor
argument_list|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
operator|(
name|ConnectionFactory
operator|)
name|constructor
operator|.
name|newInstance
argument_list|(
name|connectionFactory
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to instantiate PooledConnectionFactory: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|Class
argument_list|<
name|?
argument_list|>
name|loadClass
parameter_list|(
name|String
name|name
parameter_list|,
name|ClassLoader
name|loader
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
name|ClassLoader
name|contextClassLoader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|contextClassLoader
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|contextClassLoader
operator|.
name|loadClass
argument_list|(
name|name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
try|try
block|{
return|return
name|loader
operator|.
name|loadClass
argument_list|(
name|name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e1
parameter_list|)
block|{
throw|throw
name|e1
throw|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

