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
name|ConcurrentHashMap
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
name|BrokerMBeanSupport
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
name|command
operator|.
name|Message
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
specifier|private
specifier|final
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
specifier|final
name|ObjectName
name|connectorName
decl_stmt|;
specifier|private
specifier|final
name|NetworkBridgeConfiguration
name|networkBridgeConfiguration
decl_stmt|;
specifier|private
name|boolean
name|createdByDuplex
init|=
literal|false
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|NetworkBridge
argument_list|,
name|MBeanBridgeDestination
argument_list|>
name|destinationObjectNameMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|NetworkBridge
argument_list|,
name|MBeanBridgeDestination
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|MBeanNetworkListener
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|,
name|NetworkBridgeConfiguration
name|networkBridgeConfiguration
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
name|networkBridgeConfiguration
operator|=
name|networkBridgeConfiguration
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
name|NetworkBridgeView
name|view
init|=
operator|new
name|NetworkBridgeView
argument_list|(
name|bridge
argument_list|)
decl_stmt|;
name|view
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
name|MBeanBridgeDestination
name|mBeanBridgeDestination
init|=
operator|new
name|MBeanBridgeDestination
argument_list|(
name|brokerService
argument_list|,
name|networkBridgeConfiguration
argument_list|,
name|bridge
argument_list|,
name|view
argument_list|)
decl_stmt|;
name|destinationObjectNameMap
operator|.
name|put
argument_list|(
name|bridge
argument_list|,
name|mBeanBridgeDestination
argument_list|)
expr_stmt|;
name|mBeanBridgeDestination
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"registered: {} as: {}"
argument_list|,
name|bridge
argument_list|,
name|objectName
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
name|debug
argument_list|(
literal|"Network bridge could not be registered in JMX: {}"
argument_list|,
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
name|MBeanBridgeDestination
name|mBeanBridgeDestination
init|=
name|destinationObjectNameMap
operator|.
name|remove
argument_list|(
name|bridge
argument_list|)
decl_stmt|;
if|if
condition|(
name|mBeanBridgeDestination
operator|!=
literal|null
condition|)
block|{
name|mBeanBridgeDestination
operator|.
name|stop
argument_list|()
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
literal|"Network bridge could not be unregistered in JMX: {}"
argument_list|,
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
return|return
name|BrokerMBeanSupport
operator|.
name|createNetworkBridgeObjectName
argument_list|(
name|connectorName
argument_list|,
name|bridge
operator|.
name|getRemoteAddress
argument_list|()
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
annotation|@
name|Override
specifier|public
name|void
name|onOutboundMessage
parameter_list|(
name|NetworkBridge
name|bridge
parameter_list|,
name|Message
name|message
parameter_list|)
block|{
name|MBeanBridgeDestination
name|mBeanBridgeDestination
init|=
name|destinationObjectNameMap
operator|.
name|get
argument_list|(
name|bridge
argument_list|)
decl_stmt|;
if|if
condition|(
name|mBeanBridgeDestination
operator|!=
literal|null
condition|)
block|{
name|mBeanBridgeDestination
operator|.
name|onOutboundMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onInboundMessage
parameter_list|(
name|NetworkBridge
name|bridge
parameter_list|,
name|Message
name|message
parameter_list|)
block|{
name|MBeanBridgeDestination
name|mBeanBridgeDestination
init|=
name|destinationObjectNameMap
operator|.
name|get
argument_list|(
name|bridge
argument_list|)
decl_stmt|;
if|if
condition|(
name|mBeanBridgeDestination
operator|!=
literal|null
condition|)
block|{
name|mBeanBridgeDestination
operator|.
name|onInboundMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

