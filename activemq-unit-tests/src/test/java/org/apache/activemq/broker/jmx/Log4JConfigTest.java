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
name|util
operator|.
name|List
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
name|javax
operator|.
name|management
operator|.
name|MBeanServer
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
name|EmbeddedBrokerTestSupport
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
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|Log4JConfigTest
extends|extends
name|EmbeddedBrokerTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|org
operator|.
name|slf4j
operator|.
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Log4JConfigTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_LOGGER
init|=
literal|"org.apache.activemq.broker.BrokerService"
decl_stmt|;
specifier|protected
name|MBeanServer
name|mbeanServer
decl_stmt|;
specifier|protected
name|String
name|domain
init|=
literal|"org.apache.activemq"
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|bindAddress
operator|=
literal|"tcp://localhost:0"
expr_stmt|;
name|useTopic
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|mbeanServer
operator|=
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getMBeanServer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setSchedulerSupport
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLog4JConfigViewExists
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|brokerObjectName
init|=
name|broker
operator|.
name|getBrokerObjectName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|log4jConfigViewName
init|=
name|BrokerMBeanSupport
operator|.
name|createLog4JConfigViewName
argument_list|(
name|brokerObjectName
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertRegisteredObjectName
argument_list|(
name|log4jConfigViewName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLog4JConfigViewGetLoggers
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|brokerObjectName
init|=
name|broker
operator|.
name|getBrokerObjectName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ObjectName
name|log4jConfigViewName
init|=
name|BrokerMBeanSupport
operator|.
name|createLog4JConfigViewName
argument_list|(
name|brokerObjectName
argument_list|)
decl_stmt|;
name|Log4JConfigViewMBean
name|log4jConfigView
init|=
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|log4jConfigViewName
argument_list|,
name|Log4JConfigViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|loggers
init|=
name|log4jConfigView
operator|.
name|getLoggers
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|loggers
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|loggers
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLog4JConfigViewGetLevel
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|brokerObjectName
init|=
name|broker
operator|.
name|getBrokerObjectName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ObjectName
name|log4jConfigViewName
init|=
name|BrokerMBeanSupport
operator|.
name|createLog4JConfigViewName
argument_list|(
name|brokerObjectName
argument_list|)
decl_stmt|;
name|Log4JConfigViewMBean
name|log4jConfigView
init|=
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|log4jConfigViewName
argument_list|,
name|Log4JConfigViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|level
init|=
name|log4jConfigView
operator|.
name|getLogLevel
argument_list|(
name|BROKER_LOGGER
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|level
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLog4JConfigViewGetLevelUnknownLoggerName
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|brokerObjectName
init|=
name|broker
operator|.
name|getBrokerObjectName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ObjectName
name|log4jConfigViewName
init|=
name|BrokerMBeanSupport
operator|.
name|createLog4JConfigViewName
argument_list|(
name|brokerObjectName
argument_list|)
decl_stmt|;
name|Log4JConfigViewMBean
name|log4jConfigView
init|=
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|log4jConfigViewName
argument_list|,
name|Log4JConfigViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// Non-existent loggers will return a name equal to the root level.
name|String
name|level
init|=
name|log4jConfigView
operator|.
name|getLogLevel
argument_list|(
literal|"not.a.logger"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|level
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|getLevel
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLog4JConfigViewSetLevel
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|brokerObjectName
init|=
name|broker
operator|.
name|getBrokerObjectName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ObjectName
name|log4jConfigViewName
init|=
name|BrokerMBeanSupport
operator|.
name|createLog4JConfigViewName
argument_list|(
name|brokerObjectName
argument_list|)
decl_stmt|;
name|Log4JConfigViewMBean
name|log4jConfigView
init|=
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|log4jConfigViewName
argument_list|,
name|Log4JConfigViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|level
init|=
name|log4jConfigView
operator|.
name|getLogLevel
argument_list|(
name|BROKER_LOGGER
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|level
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|log4jConfigView
operator|.
name|setLogLevel
argument_list|(
name|BROKER_LOGGER
argument_list|,
literal|"WARN"
argument_list|)
expr_stmt|;
name|level
operator|=
name|log4jConfigView
operator|.
name|getLogLevel
argument_list|(
name|BROKER_LOGGER
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"WARN"
argument_list|,
name|level
argument_list|)
expr_stmt|;
name|log4jConfigView
operator|.
name|setLogLevel
argument_list|(
name|BROKER_LOGGER
argument_list|,
literal|"INFO"
argument_list|)
expr_stmt|;
name|level
operator|=
name|log4jConfigView
operator|.
name|getLogLevel
argument_list|(
name|BROKER_LOGGER
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"INFO"
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLog4JConfigViewSetLevelNoChangeIfLevelIsBad
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|brokerObjectName
init|=
name|broker
operator|.
name|getBrokerObjectName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ObjectName
name|log4jConfigViewName
init|=
name|BrokerMBeanSupport
operator|.
name|createLog4JConfigViewName
argument_list|(
name|brokerObjectName
argument_list|)
decl_stmt|;
name|Log4JConfigViewMBean
name|log4jConfigView
init|=
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|log4jConfigViewName
argument_list|,
name|Log4JConfigViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|log4jConfigView
operator|.
name|setLogLevel
argument_list|(
name|BROKER_LOGGER
argument_list|,
literal|"INFO"
argument_list|)
expr_stmt|;
name|String
name|level
init|=
name|log4jConfigView
operator|.
name|getLogLevel
argument_list|(
name|BROKER_LOGGER
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"INFO"
argument_list|,
name|level
argument_list|)
expr_stmt|;
name|log4jConfigView
operator|.
name|setLogLevel
argument_list|(
name|BROKER_LOGGER
argument_list|,
literal|"BAD"
argument_list|)
expr_stmt|;
name|level
operator|=
name|log4jConfigView
operator|.
name|getLogLevel
argument_list|(
name|BROKER_LOGGER
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"INFO"
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLog4JConfigViewGetRootLogLevel
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|brokerObjectName
init|=
name|broker
operator|.
name|getBrokerObjectName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ObjectName
name|log4jConfigViewName
init|=
name|BrokerMBeanSupport
operator|.
name|createLog4JConfigViewName
argument_list|(
name|brokerObjectName
argument_list|)
decl_stmt|;
name|Log4JConfigViewMBean
name|log4jConfigView
init|=
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|log4jConfigViewName
argument_list|,
name|Log4JConfigViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|level
init|=
name|log4jConfigView
operator|.
name|getRootLogLevel
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|level
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|currentRootLevel
init|=
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|getLevel
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|currentRootLevel
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLog4JConfigViewSetRootLevel
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|brokerObjectName
init|=
name|broker
operator|.
name|getBrokerObjectName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ObjectName
name|log4jConfigViewName
init|=
name|BrokerMBeanSupport
operator|.
name|createLog4JConfigViewName
argument_list|(
name|brokerObjectName
argument_list|)
decl_stmt|;
name|Log4JConfigViewMBean
name|log4jConfigView
init|=
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|log4jConfigViewName
argument_list|,
name|Log4JConfigViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|currentRootLevel
init|=
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|getLevel
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|log4jConfigView
operator|.
name|setRootLogLevel
argument_list|(
literal|"WARN"
argument_list|)
expr_stmt|;
name|currentRootLevel
operator|=
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|getLevel
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"WARN"
argument_list|,
name|currentRootLevel
argument_list|)
expr_stmt|;
name|log4jConfigView
operator|.
name|setRootLogLevel
argument_list|(
literal|"INFO"
argument_list|)
expr_stmt|;
name|currentRootLevel
operator|=
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|getLevel
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"INFO"
argument_list|,
name|currentRootLevel
argument_list|)
expr_stmt|;
name|Level
name|level
decl_stmt|;
block|}
specifier|protected
name|ObjectName
name|assertRegisteredObjectName
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|MalformedObjectNameException
throws|,
name|NullPointerException
block|{
name|ObjectName
name|objectName
init|=
operator|new
name|ObjectName
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|mbeanServer
operator|.
name|isRegistered
argument_list|(
name|objectName
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Bean Registered: "
operator|+
name|objectName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Could not find MBean!: "
operator|+
name|objectName
argument_list|)
expr_stmt|;
block|}
return|return
name|objectName
return|;
block|}
block|}
end_class

end_unit

