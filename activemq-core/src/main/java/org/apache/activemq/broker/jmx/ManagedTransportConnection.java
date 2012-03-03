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
name|broker
operator|.
name|jmx
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
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|Broker
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
name|TransportConnection
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
name|Response
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
name|thread
operator|.
name|TaskRunnerFactory
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
name|util
operator|.
name|IOExceptionSupport
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
name|JMXSupport
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
comment|/**  * A managed transport connection  */
end_comment

begin_class
specifier|public
class|class
name|ManagedTransportConnection
extends|extends
name|TransportConnection
block|{
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
name|ManagedTransportConnection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ManagementContext
name|managementContext
decl_stmt|;
specifier|private
specifier|final
name|ObjectName
name|connectorName
decl_stmt|;
specifier|private
name|ConnectionViewMBean
name|mbean
decl_stmt|;
specifier|private
name|ObjectName
name|byClientIdName
decl_stmt|;
specifier|private
name|ObjectName
name|byAddressName
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|populateUserName
decl_stmt|;
specifier|public
name|ManagedTransportConnection
parameter_list|(
name|TransportConnector
name|connector
parameter_list|,
name|Transport
name|transport
parameter_list|,
name|Broker
name|broker
parameter_list|,
name|TaskRunnerFactory
name|factory
parameter_list|,
name|ManagementContext
name|context
parameter_list|,
name|ObjectName
name|connectorName
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|connector
argument_list|,
name|transport
argument_list|,
name|broker
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|this
operator|.
name|managementContext
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|connectorName
operator|=
name|connectorName
expr_stmt|;
name|this
operator|.
name|mbean
operator|=
operator|new
name|ConnectionView
argument_list|(
name|this
argument_list|,
name|managementContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|populateUserName
operator|=
name|broker
operator|.
name|getBrokerService
argument_list|()
operator|.
name|isPopulateUserNameInMBeans
argument_list|()
expr_stmt|;
if|if
condition|(
name|managementContext
operator|.
name|isAllowRemoteAddressInMBeanNames
argument_list|()
condition|)
block|{
name|byAddressName
operator|=
name|createByAddressObjectName
argument_list|(
literal|"address"
argument_list|,
name|transport
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
expr_stmt|;
name|registerMBean
argument_list|(
name|byAddressName
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|doStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|isStarting
argument_list|()
condition|)
block|{
name|setPendingStop
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|unregisterMBean
argument_list|(
name|byClientIdName
argument_list|)
expr_stmt|;
name|unregisterMBean
argument_list|(
name|byAddressName
argument_list|)
expr_stmt|;
name|byClientIdName
operator|=
literal|null
expr_stmt|;
name|byAddressName
operator|=
literal|null
expr_stmt|;
block|}
name|super
operator|.
name|doStop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Response
name|processAddConnection
parameter_list|(
name|ConnectionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|Response
name|answer
init|=
name|super
operator|.
name|processAddConnection
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|String
name|clientId
init|=
name|info
operator|.
name|getClientId
argument_list|()
decl_stmt|;
if|if
condition|(
name|populateUserName
condition|)
block|{
operator|(
operator|(
name|ConnectionView
operator|)
name|mbean
operator|)
operator|.
name|setUserName
argument_list|(
name|info
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|clientId
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|byClientIdName
operator|==
literal|null
condition|)
block|{
name|byClientIdName
operator|=
name|createByClientIdObjectName
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|registerMBean
argument_list|(
name|byClientIdName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|void
name|registerMBean
parameter_list|(
name|ObjectName
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|AnnotatedMBean
operator|.
name|registerMBean
argument_list|(
name|managementContext
argument_list|,
name|mbean
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to register MBean: "
operator|+
name|name
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failure reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|unregisterMBean
parameter_list|(
name|ObjectName
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|managementContext
operator|.
name|unregisterMBean
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to unregister mbean: "
operator|+
name|name
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failure reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|ObjectName
name|createByAddressObjectName
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|connectorName
operator|.
name|getKeyPropertyList
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|ObjectName
argument_list|(
name|connectorName
operator|.
name|getDomain
argument_list|()
operator|+
literal|":"
operator|+
literal|"BrokerName="
operator|+
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"BrokerName"
argument_list|)
argument_list|)
operator|+
literal|","
operator|+
literal|"Type=Connection,"
operator|+
literal|"ConnectorName="
operator|+
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"ConnectorName"
argument_list|)
argument_list|)
operator|+
literal|","
operator|+
literal|"ViewType="
operator|+
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
name|type
argument_list|)
operator|+
literal|","
operator|+
literal|"Name="
operator|+
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|ObjectName
name|createByClientIdObjectName
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|connectorName
operator|.
name|getKeyPropertyList
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|ObjectName
argument_list|(
name|connectorName
operator|.
name|getDomain
argument_list|()
operator|+
literal|":"
operator|+
literal|"BrokerName="
operator|+
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"BrokerName"
argument_list|)
argument_list|)
operator|+
literal|","
operator|+
literal|"Type=Connection,"
operator|+
literal|"ConnectorName="
operator|+
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"ConnectorName"
argument_list|)
argument_list|)
operator|+
literal|","
operator|+
literal|"Connection="
operator|+
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

