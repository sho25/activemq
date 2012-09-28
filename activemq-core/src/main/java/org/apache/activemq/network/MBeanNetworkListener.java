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
package|;
end_package

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
name|Map
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
name|AnnotatedMBean
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
name|NetworkBridgeView
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
name|NetworkBridgeViewMBean
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

begin_class
specifier|public
class|class
name|MBeanNetworkListener
implements|implements
name|NetworkBridgeListener
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
name|MBeanNetworkListener
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|brokerService
decl_stmt|;
name|ObjectName
name|connectorName
decl_stmt|;
name|boolean
name|createdByDuplex
init|=
literal|false
decl_stmt|;
specifier|public
name|MBeanNetworkListener
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|,
name|ObjectName
name|connectorName
parameter_list|)
block|{
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
name|this
operator|.
name|connectorName
operator|=
name|connectorName
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|bridgeFailed
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|onStart
parameter_list|(
name|NetworkBridge
name|bridge
parameter_list|)
block|{
if|if
condition|(
operator|!
name|brokerService
operator|.
name|isUseJmx
argument_list|()
condition|)
block|{
return|return;
block|}
name|NetworkBridgeViewMBean
name|view
init|=
operator|new
name|NetworkBridgeView
argument_list|(
name|bridge
argument_list|)
decl_stmt|;
operator|(
operator|(
name|NetworkBridgeView
operator|)
name|view
operator|)
operator|.
name|setCreateByDuplex
argument_list|(
name|createdByDuplex
argument_list|)
expr_stmt|;
try|try
block|{
name|ObjectName
name|objectName
init|=
name|createNetworkBridgeObjectName
argument_list|(
name|bridge
argument_list|)
decl_stmt|;
name|AnnotatedMBean
operator|.
name|registerMBean
argument_list|(
name|brokerService
operator|.
name|getManagementContext
argument_list|()
argument_list|,
name|view
argument_list|,
name|objectName
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setMbeanObjectName
argument_list|(
name|objectName
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"registered: "
operator|+
name|bridge
operator|+
literal|" as: "
operator|+
name|objectName
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Network bridge could not be registered in JMX: "
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
annotation|@
name|Override
specifier|public
name|void
name|onStop
parameter_list|(
name|NetworkBridge
name|bridge
parameter_list|)
block|{
if|if
condition|(
operator|!
name|brokerService
operator|.
name|isUseJmx
argument_list|()
condition|)
block|{
return|return;
block|}
try|try
block|{
name|ObjectName
name|objectName
init|=
name|bridge
operator|.
name|getMbeanObjectName
argument_list|()
decl_stmt|;
if|if
condition|(
name|objectName
operator|!=
literal|null
condition|)
block|{
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|unregisterMBean
argument_list|(
name|objectName
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Network bridge could not be unregistered in JMX: "
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
specifier|protected
name|ObjectName
name|createNetworkBridgeObjectName
parameter_list|(
name|NetworkBridge
name|bridge
parameter_list|)
throws|throws
name|MalformedObjectNameException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|connectorName
operator|.
name|getKeyPropertyList
argument_list|()
argument_list|)
decl_stmt|;
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
literal|"Type=NetworkBridge,"
operator|+
literal|"NetworkConnectorName="
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
literal|"NetworkConnectorName"
argument_list|)
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
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
name|bridge
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|setCreatedByDuplex
parameter_list|(
name|boolean
name|createdByDuplex
parameter_list|)
block|{
name|this
operator|.
name|createdByDuplex
operator|=
name|createdByDuplex
expr_stmt|;
block|}
block|}
end_class

end_unit

