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
name|Set
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
name|ObjectInstance
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
name|BrokerService
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

begin_class
specifier|public
class|class
name|DuplexNetworkMBeanTest
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DuplexNetworkMBeanTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|int
name|numRestarts
init|=
literal|3
decl_stmt|;
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
literal|"broker"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:61617?transport.reuseAddress=true"
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|BrokerService
name|createNetworkedBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
literal|"networkedBroker"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:62617?transport.reuseAddress=true"
argument_list|)
expr_stmt|;
name|NetworkConnector
name|networkConnector
init|=
name|broker
operator|.
name|addNetworkConnector
argument_list|(
literal|"static:(tcp://localhost:61617?wireFormat.maxInactivityDuration=500)?useExponentialBackOff=false"
argument_list|)
decl_stmt|;
name|networkConnector
operator|.
name|setDuplex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|public
name|void
name|testMbeanPresenceOnNetworkBrokerRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|createBroker
argument_list|()
decl_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countMbeans
argument_list|(
name|broker
argument_list|,
literal|"Connector"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countMbeans
argument_list|(
name|broker
argument_list|,
literal|"Connection"
argument_list|)
argument_list|)
expr_stmt|;
name|BrokerService
name|networkedBroker
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
name|numRestarts
condition|;
name|i
operator|++
control|)
block|{
name|networkedBroker
operator|=
name|createNetworkedBroker
argument_list|()
expr_stmt|;
name|networkedBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countMbeans
argument_list|(
name|networkedBroker
argument_list|,
literal|"NetworkBridge"
argument_list|,
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countMbeans
argument_list|(
name|broker
argument_list|,
literal|"Connection"
argument_list|)
argument_list|)
expr_stmt|;
name|networkedBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|networkedBroker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countMbeans
argument_list|(
name|networkedBroker
argument_list|,
literal|"stopped"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countMbeans
argument_list|(
name|networkedBroker
argument_list|,
literal|"NetworkBridge"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countMbeans
argument_list|(
name|networkedBroker
argument_list|,
literal|"Connector"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countMbeans
argument_list|(
name|networkedBroker
argument_list|,
literal|"Connection"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countMbeans
argument_list|(
name|broker
argument_list|,
literal|"Connector"
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testMbeanPresenceOnBrokerRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|networkedBroker
init|=
name|createNetworkedBroker
argument_list|()
decl_stmt|;
name|networkedBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countMbeans
argument_list|(
name|networkedBroker
argument_list|,
literal|"Connector"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countMbeans
argument_list|(
name|networkedBroker
argument_list|,
literal|"Connection"
argument_list|)
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
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
name|numRestarts
condition|;
name|i
operator|++
control|)
block|{
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countMbeans
argument_list|(
name|networkedBroker
argument_list|,
literal|"NetworkBridge"
argument_list|,
literal|5000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"restart number: "
operator|+
name|i
argument_list|,
literal|1
argument_list|,
name|countMbeans
argument_list|(
name|broker
argument_list|,
literal|"Connection"
argument_list|,
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countMbeans
argument_list|(
name|broker
argument_list|,
literal|"stopped"
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|//assertEquals(0, countMbeans(networkedBroker, "NetworkBridge"));
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countMbeans
argument_list|(
name|networkedBroker
argument_list|,
literal|"Connector"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countMbeans
argument_list|(
name|networkedBroker
argument_list|,
literal|"Connection"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countMbeans
argument_list|(
name|broker
argument_list|,
literal|"Connection"
argument_list|)
argument_list|)
expr_stmt|;
name|networkedBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|private
name|int
name|countMbeans
parameter_list|(
name|BrokerService
name|broker
parameter_list|,
name|String
name|type
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|countMbeans
argument_list|(
name|broker
argument_list|,
name|type
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|private
name|int
name|countMbeans
parameter_list|(
name|BrokerService
name|broker
parameter_list|,
name|String
name|type
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|long
name|expiryTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|timeout
decl_stmt|;
name|JMXServiceURL
name|url
init|=
operator|new
name|JMXServiceURL
argument_list|(
literal|"service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi"
argument_list|)
decl_stmt|;
name|JMXConnector
name|jmxc
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
name|MBeanServerConnection
name|mbsc
init|=
name|jmxc
operator|.
name|getMBeanServerConnection
argument_list|()
decl_stmt|;
name|Set
name|all
init|=
name|mbsc
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"MBean total="
operator|+
name|all
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|all
control|)
block|{
name|ObjectInstance
name|bean
init|=
operator|(
name|ObjectInstance
operator|)
name|o
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|bean
operator|.
name|getObjectName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ObjectName
name|beanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:BrokerName="
operator|+
name|broker
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|",Type="
operator|+
name|type
operator|+
literal|",*"
argument_list|)
decl_stmt|;
name|Set
name|mbeans
init|=
literal|null
decl_stmt|;
do|do
block|{
if|if
condition|(
name|timeout
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|mbeans
operator|=
name|mbsc
operator|.
name|queryMBeans
argument_list|(
name|beanName
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|mbeans
operator|.
name|isEmpty
argument_list|()
operator|&&
name|expiryTime
operator|>
name|System
operator|.
name|currentTimeMillis
argument_list|()
condition|)
do|;
return|return
name|mbeans
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

