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
name|broker
operator|.
name|jmx
operator|.
name|NetworkDestinationView
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
name|apache
operator|.
name|activemq
operator|.
name|thread
operator|.
name|Scheduler
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
name|MBeanBridgeDestination
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
name|MBeanBridgeDestination
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
name|NetworkBridge
name|bridge
decl_stmt|;
specifier|private
specifier|final
name|NetworkBridgeView
name|networkBridgeView
decl_stmt|;
specifier|private
specifier|final
name|NetworkBridgeConfiguration
name|networkBridgeConfiguration
decl_stmt|;
specifier|private
specifier|final
name|Scheduler
name|scheduler
decl_stmt|;
specifier|private
specifier|final
name|Runnable
name|purgeInactiveDestinationViewTask
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|ActiveMQDestination
argument_list|,
name|ObjectName
argument_list|>
name|destinationObjectNameMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ActiveMQDestination
argument_list|,
name|ObjectName
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|ActiveMQDestination
argument_list|,
name|NetworkDestinationView
argument_list|>
name|outboundDestinationViewMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ActiveMQDestination
argument_list|,
name|NetworkDestinationView
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|ActiveMQDestination
argument_list|,
name|NetworkDestinationView
argument_list|>
name|inboundDestinationViewMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ActiveMQDestination
argument_list|,
name|NetworkDestinationView
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|MBeanBridgeDestination
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|,
name|NetworkBridgeConfiguration
name|networkBridgeConfiguration
parameter_list|,
name|NetworkBridge
name|bridge
parameter_list|,
name|NetworkBridgeView
name|networkBridgeView
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
name|bridge
operator|=
name|bridge
expr_stmt|;
name|this
operator|.
name|networkBridgeView
operator|=
name|networkBridgeView
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|brokerService
operator|.
name|getScheduler
argument_list|()
expr_stmt|;
name|purgeInactiveDestinationViewTask
operator|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|purgeInactiveDestinationViews
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
specifier|public
name|void
name|onOutboundMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|ActiveMQDestination
name|destination
init|=
name|message
operator|.
name|getDestination
argument_list|()
decl_stmt|;
name|NetworkDestinationView
name|networkDestinationView
init|=
name|outboundDestinationViewMap
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|networkDestinationView
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|destinationObjectNameMap
init|)
block|{
if|if
condition|(
operator|(
name|networkDestinationView
operator|=
name|outboundDestinationViewMap
operator|.
name|get
argument_list|(
name|destination
argument_list|)
operator|)
operator|==
literal|null
condition|)
block|{
name|ObjectName
name|bridgeObjectName
init|=
name|bridge
operator|.
name|getMbeanObjectName
argument_list|()
decl_stmt|;
try|try
block|{
name|ObjectName
name|objectName
init|=
name|BrokerMBeanSupport
operator|.
name|createNetworkOutBoundDestinationObjectName
argument_list|(
name|bridgeObjectName
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|networkDestinationView
operator|=
operator|new
name|NetworkDestinationView
argument_list|(
name|networkBridgeView
argument_list|,
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
name|AnnotatedMBean
operator|.
name|registerMBean
argument_list|(
name|brokerService
operator|.
name|getManagementContext
argument_list|()
argument_list|,
name|networkDestinationView
argument_list|,
name|objectName
argument_list|)
expr_stmt|;
name|destinationObjectNameMap
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|objectName
argument_list|)
expr_stmt|;
name|outboundDestinationViewMap
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|networkDestinationView
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
literal|"Failed to register "
operator|+
name|destination
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|networkDestinationView
operator|.
name|messageSent
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|onInboundMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|ActiveMQDestination
name|destination
init|=
name|message
operator|.
name|getDestination
argument_list|()
decl_stmt|;
name|NetworkDestinationView
name|networkDestinationView
init|=
name|inboundDestinationViewMap
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|networkDestinationView
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|destinationObjectNameMap
init|)
block|{
if|if
condition|(
operator|(
name|networkDestinationView
operator|=
name|inboundDestinationViewMap
operator|.
name|get
argument_list|(
name|destination
argument_list|)
operator|)
operator|==
literal|null
condition|)
block|{
name|ObjectName
name|bridgeObjectName
init|=
name|bridge
operator|.
name|getMbeanObjectName
argument_list|()
decl_stmt|;
try|try
block|{
name|ObjectName
name|objectName
init|=
name|BrokerMBeanSupport
operator|.
name|createNetworkInBoundDestinationObjectName
argument_list|(
name|bridgeObjectName
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|networkDestinationView
operator|=
operator|new
name|NetworkDestinationView
argument_list|(
name|networkBridgeView
argument_list|,
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
name|networkBridgeView
operator|.
name|addNetworkDestinationView
argument_list|(
name|networkDestinationView
argument_list|)
expr_stmt|;
name|AnnotatedMBean
operator|.
name|registerMBean
argument_list|(
name|brokerService
operator|.
name|getManagementContext
argument_list|()
argument_list|,
name|networkDestinationView
argument_list|,
name|objectName
argument_list|)
expr_stmt|;
name|destinationObjectNameMap
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|objectName
argument_list|)
expr_stmt|;
name|inboundDestinationViewMap
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|networkDestinationView
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
literal|"Failed to register "
operator|+
name|destination
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|networkDestinationView
operator|.
name|messageSent
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|networkBridgeConfiguration
operator|.
name|isGcDestinationViews
argument_list|()
condition|)
block|{
name|long
name|period
init|=
name|networkBridgeConfiguration
operator|.
name|getGcSweepTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|period
operator|>
literal|0
condition|)
block|{
name|scheduler
operator|.
name|executePeriodically
argument_list|(
name|purgeInactiveDestinationViewTask
argument_list|,
name|period
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
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
name|scheduler
operator|.
name|cancel
argument_list|(
name|purgeInactiveDestinationViewTask
argument_list|)
expr_stmt|;
for|for
control|(
name|ObjectName
name|objectName
range|:
name|destinationObjectNameMap
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
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
name|destinationObjectNameMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|outboundDestinationViewMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|inboundDestinationViewMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|purgeInactiveDestinationViews
parameter_list|()
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
name|purgeInactiveDestinationView
argument_list|(
name|inboundDestinationViewMap
argument_list|)
expr_stmt|;
name|purgeInactiveDestinationView
argument_list|(
name|outboundDestinationViewMap
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|purgeInactiveDestinationView
parameter_list|(
name|Map
argument_list|<
name|ActiveMQDestination
argument_list|,
name|NetworkDestinationView
argument_list|>
name|map
parameter_list|)
block|{
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|networkBridgeConfiguration
operator|.
name|getGcSweepTime
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ActiveMQDestination
argument_list|,
name|NetworkDestinationView
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getLastAccessTime
argument_list|()
operator|<=
name|time
condition|)
block|{
synchronized|synchronized
init|(
name|destinationObjectNameMap
init|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|ObjectName
name|objectName
init|=
name|destinationObjectNameMap
operator|.
name|remove
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|objectName
operator|!=
literal|null
condition|)
block|{
try|try
block|{
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
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

