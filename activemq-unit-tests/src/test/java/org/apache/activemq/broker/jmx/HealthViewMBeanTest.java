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
name|BytesMessage
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
name|ConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|DeliveryMode
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
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
name|HealthViewMBeanTest
extends|extends
name|EmbeddedBrokerTestSupport
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
name|MBeanTest
operator|.
name|class
argument_list|)
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
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|64
argument_list|)
expr_stmt|;
name|answer
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getTempUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|64
argument_list|)
expr_stmt|;
name|answer
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|64
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
comment|// allow options to be visible via jmx
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
specifier|public
name|void
name|testHealthView
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|destination
operator|=
name|createDestination
argument_list|()
expr_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|60
condition|;
name|i
operator|++
control|)
block|{
name|BytesMessage
name|message
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|1024
operator|*
literal|1024
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|String
name|objectNameStr
init|=
name|broker
operator|.
name|getBrokerObjectName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|objectNameStr
operator|+=
literal|",service=Health"
expr_stmt|;
name|ObjectName
name|brokerName
init|=
name|assertRegisteredObjectName
argument_list|(
name|objectNameStr
argument_list|)
decl_stmt|;
name|HealthViewMBean
name|health
init|=
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|brokerName
argument_list|,
name|HealthViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|HealthStatus
argument_list|>
name|list
init|=
name|health
operator|.
name|healthList
argument_list|()
decl_stmt|;
for|for
control|(
name|HealthStatus
name|status
range|:
name|list
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Health status: {}"
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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

