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
name|web
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
name|broker
operator|.
name|jmx
operator|.
name|BrokerViewMBean
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
name|management
operator|.
name|MBeanServerConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServerInvocationHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnector
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnectorFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXServiceURL
import|;
end_import

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
name|ArrayList
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
name|Set
import|;
end_import

begin_comment
comment|/**  * A {@link BrokerFacade} which uses a JMX-Connection to communicate with a  * broker  *  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|RemoteJMXBrokerFacade
extends|extends
name|BrokerFacadeSupport
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RemoteJMXBrokerFacade
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|jmxUrl
decl_stmt|;
specifier|private
name|String
name|brokerName
decl_stmt|;
specifier|private
name|JMXConnector
name|connector
decl_stmt|;
specifier|public
name|void
name|setBrokerName
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{
name|this
operator|.
name|brokerName
operator|=
name|brokerName
expr_stmt|;
block|}
specifier|public
name|void
name|setJmxUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|this
operator|.
name|jmxUrl
operator|=
name|url
expr_stmt|;
block|}
comment|/**      * Shutdown this facade aka close any open connection.      */
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|closeConnection
argument_list|()
expr_stmt|;
block|}
specifier|public
name|BrokerViewMBean
name|getBrokerAdmin
parameter_list|()
throws|throws
name|Exception
block|{
name|MBeanServerConnection
name|connection
init|=
name|getConnection
argument_list|()
decl_stmt|;
name|Set
name|brokers
init|=
name|findBrokers
argument_list|(
name|connection
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokers
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No broker could be found in the JMX."
argument_list|)
throw|;
block|}
name|ObjectName
name|name
init|=
operator|(
name|ObjectName
operator|)
name|brokers
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|BrokerViewMBean
name|mbean
init|=
operator|(
name|BrokerViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|connection
argument_list|,
name|name
argument_list|,
name|BrokerViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|mbean
return|;
block|}
specifier|protected
name|MBeanServerConnection
name|getConnection
parameter_list|()
throws|throws
name|IOException
block|{
name|JMXConnector
name|connector
init|=
name|this
operator|.
name|connector
decl_stmt|;
if|if
condition|(
name|isConnectionActive
argument_list|(
name|connector
argument_list|)
condition|)
block|{
return|return
name|connector
operator|.
name|getMBeanServerConnection
argument_list|()
return|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|closeConnection
argument_list|()
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Creating a new JMX-Connection to the broker"
argument_list|)
expr_stmt|;
name|this
operator|.
name|connector
operator|=
name|createConnection
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|connector
operator|.
name|getMBeanServerConnection
argument_list|()
return|;
block|}
block|}
specifier|protected
name|boolean
name|isConnectionActive
parameter_list|(
name|JMXConnector
name|connector
parameter_list|)
block|{
if|if
condition|(
name|connector
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
name|MBeanServerConnection
name|connection
init|=
name|connector
operator|.
name|getMBeanServerConnection
argument_list|()
decl_stmt|;
name|int
name|brokerCount
init|=
name|findBrokers
argument_list|(
name|connection
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
return|return
name|brokerCount
operator|>
literal|0
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|protected
name|JMXConnector
name|createConnection
parameter_list|()
block|{
name|String
index|[]
name|urls
init|=
name|this
operator|.
name|jmxUrl
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
if|if
condition|(
name|urls
operator|==
literal|null
operator|||
name|urls
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|urls
operator|=
operator|new
name|String
index|[]
block|{
name|this
operator|.
name|jmxUrl
block|}
expr_stmt|;
block|}
name|Exception
name|exception
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|urls
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|JMXConnector
name|connector
init|=
name|JMXConnectorFactory
operator|.
name|connect
argument_list|(
operator|new
name|JMXServiceURL
argument_list|(
name|urls
index|[
name|i
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|connector
operator|.
name|connect
argument_list|()
expr_stmt|;
name|MBeanServerConnection
name|connection
init|=
name|connector
operator|.
name|getMBeanServerConnection
argument_list|()
decl_stmt|;
name|Set
name|brokers
init|=
name|findBrokers
argument_list|(
name|connection
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokers
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Connected via JMX to the broker at "
operator|+
name|urls
index|[
name|i
index|]
argument_list|)
expr_stmt|;
return|return
name|connector
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Keep the exception for later
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|exception
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|exception
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|exception
argument_list|)
throw|;
block|}
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No broker is found at any of the urls "
operator|+
name|this
operator|.
name|jmxUrl
argument_list|)
throw|;
block|}
specifier|protected
specifier|synchronized
name|void
name|closeConnection
parameter_list|()
block|{
if|if
condition|(
name|connector
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Closing a connection to a broker ("
operator|+
name|connector
operator|.
name|getConnectionId
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|connector
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Ignore the exception, since it most likly won't matter
comment|// anymore
block|}
block|}
block|}
comment|/**      * Finds all ActiveMQ-Brokers registered on a certain JMX-Server or, if a      * JMX-BrokerName has been set, the broker with that name.      *      * @param connection not<code>null</code>      * @return Set with ObjectName-elements      * @throws IOException      * @throws MalformedObjectNameException      */
specifier|protected
name|Set
name|findBrokers
parameter_list|(
name|MBeanServerConnection
name|connection
parameter_list|)
throws|throws
name|IOException
throws|,
name|MalformedObjectNameException
block|{
name|ObjectName
name|name
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|brokerName
operator|==
literal|null
condition|)
block|{
name|name
operator|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:Type=Broker,*"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:BrokerName="
operator|+
name|this
operator|.
name|brokerName
operator|+
literal|",Type=Broker"
argument_list|)
expr_stmt|;
block|}
name|Set
name|brokers
init|=
name|connection
operator|.
name|queryNames
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|brokers
return|;
block|}
specifier|public
name|void
name|purgeQueue
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|QueueViewMBean
name|queue
init|=
name|getQueue
argument_list|(
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
decl_stmt|;
name|queue
operator|.
name|purge
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ManagementContext
name|getManagementContext
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"not supported"
argument_list|)
throw|;
block|}
specifier|protected
name|Collection
name|getManagedObjects
parameter_list|(
name|ObjectName
index|[]
name|names
parameter_list|,
name|Class
name|type
parameter_list|)
block|{
name|MBeanServerConnection
name|connection
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|getConnection
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|List
name|answer
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|connection
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
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ObjectName
name|name
init|=
name|names
index|[
name|i
index|]
decl_stmt|;
name|Object
name|value
init|=
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|connection
argument_list|,
name|name
argument_list|,
name|type
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|answer
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit

