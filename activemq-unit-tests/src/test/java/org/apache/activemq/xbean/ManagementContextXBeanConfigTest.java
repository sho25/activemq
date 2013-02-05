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
name|xbean
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
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|ManagementContextXBeanConfigTest
extends|extends
name|TestCase
block|{
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ManagementContextXBeanConfigTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testManagmentContextConfiguredCorrectly
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|2011
argument_list|,
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getConnectorPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test.domain"
argument_list|,
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getJmxDomainName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Make sure the broker is registered in the right jmx domain.
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
literal|"Broker"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"brokerName"
argument_list|,
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
literal|"localhost"
argument_list|)
argument_list|)
expr_stmt|;
name|ObjectName
name|on
init|=
operator|new
name|ObjectName
argument_list|(
literal|"test.domain"
argument_list|,
name|map
argument_list|)
decl_stmt|;
name|Object
name|value
init|=
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|on
argument_list|,
literal|"TotalEnqueueCount"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSuccessAuthentication
parameter_list|()
throws|throws
name|Exception
block|{
name|JMXServiceURL
name|url
init|=
operator|new
name|JMXServiceURL
argument_list|(
literal|"service:jmx:rmi:///jndi/rmi://localhost:2011/jmxrmi"
argument_list|)
decl_stmt|;
name|Map
name|env
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|env
operator|.
name|put
argument_list|(
name|JMXConnector
operator|.
name|CREDENTIALS
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"admin"
block|,
literal|"activemq"
block|}
argument_list|)
expr_stmt|;
name|JMXConnector
name|connector
init|=
name|JMXConnectorFactory
operator|.
name|connect
argument_list|(
name|url
argument_list|,
name|env
argument_list|)
decl_stmt|;
name|assertAuthentication
argument_list|(
name|connector
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFailAuthentication
parameter_list|()
throws|throws
name|Exception
block|{
name|JMXServiceURL
name|url
init|=
operator|new
name|JMXServiceURL
argument_list|(
literal|"service:jmx:rmi:///jndi/rmi://localhost:2011/jmxrmi"
argument_list|)
decl_stmt|;
try|try
block|{
name|JMXConnector
name|connector
init|=
name|JMXConnectorFactory
operator|.
name|connect
argument_list|(
name|url
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertAuthentication
argument_list|(
name|connector
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
return|return;
block|}
name|fail
argument_list|(
literal|"Should have thrown an exception"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|assertAuthentication
parameter_list|(
name|JMXConnector
name|connector
parameter_list|)
throws|throws
name|Exception
block|{
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
name|ObjectName
name|name
init|=
operator|new
name|ObjectName
argument_list|(
literal|"test.domain:type=Broker,brokerName=localhost"
argument_list|)
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker "
operator|+
name|mbean
operator|.
name|getBrokerId
argument_list|()
operator|+
literal|" - "
operator|+
name|mbean
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokerService
operator|!=
literal|null
condition|)
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|uri
init|=
literal|"org/apache/activemq/xbean/management-context-test.xml"
decl_stmt|;
return|return
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"xbean:"
operator|+
name|uri
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit
