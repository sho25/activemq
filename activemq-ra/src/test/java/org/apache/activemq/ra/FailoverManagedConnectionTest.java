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
name|ra
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ManagedConnection
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
name|broker
operator|.
name|BrokerService
import|;
end_import

begin_class
specifier|public
class|class
name|FailoverManagedConnectionTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_TRANSPORT
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_URL
init|=
literal|"failover://"
operator|+
name|BROKER_TRANSPORT
decl_stmt|;
specifier|private
name|ActiveMQManagedConnectionFactory
name|managedConnectionFactory
decl_stmt|;
specifier|private
name|ManagedConnection
name|managedConnection
decl_stmt|;
specifier|private
name|ManagedConnectionProxy
name|proxy
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|HashSet
argument_list|<
name|ManagedConnection
argument_list|>
name|connections
decl_stmt|;
specifier|private
name|ActiveMQConnectionRequestInfo
name|connectionInfo
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|createAndStartBroker
argument_list|()
expr_stmt|;
name|connectionInfo
operator|=
operator|new
name|ActiveMQConnectionRequestInfo
argument_list|()
expr_stmt|;
name|connectionInfo
operator|.
name|setServerUrl
argument_list|(
name|BROKER_URL
argument_list|)
expr_stmt|;
name|connectionInfo
operator|.
name|setUserName
argument_list|(
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_USER
argument_list|)
expr_stmt|;
name|connectionInfo
operator|.
name|setPassword
argument_list|(
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_PASSWORD
argument_list|)
expr_stmt|;
name|managedConnectionFactory
operator|=
operator|new
name|ActiveMQManagedConnectionFactory
argument_list|()
expr_stmt|;
name|managedConnection
operator|=
name|managedConnectionFactory
operator|.
name|createManagedConnection
argument_list|(
literal|null
argument_list|,
name|connectionInfo
argument_list|)
expr_stmt|;
name|connections
operator|=
operator|new
name|HashSet
argument_list|<
name|ManagedConnection
argument_list|>
argument_list|()
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|managedConnection
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createAndStartBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|BROKER_TRANSPORT
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testFailoverBeforeClose
parameter_list|()
throws|throws
name|Exception
block|{
name|createConnectionAndProxyAndSession
argument_list|()
expr_stmt|;
name|stopBroker
argument_list|()
expr_stmt|;
name|cleanupConnectionAndProxyAndSession
argument_list|()
expr_stmt|;
name|createAndStartBroker
argument_list|()
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|createConnectionAndProxyAndSession
argument_list|()
expr_stmt|;
name|cleanupConnectionAndProxyAndSession
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|cleanupConnectionAndProxyAndSession
parameter_list|()
throws|throws
name|Exception
block|{
name|proxy
operator|.
name|close
argument_list|()
expr_stmt|;
name|managedConnection
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createConnectionAndProxyAndSession
parameter_list|()
throws|throws
name|Exception
block|{
name|managedConnection
operator|=
name|managedConnectionFactory
operator|.
name|matchManagedConnections
argument_list|(
name|connections
argument_list|,
literal|null
argument_list|,
name|connectionInfo
argument_list|)
expr_stmt|;
name|proxy
operator|=
operator|(
name|ManagedConnectionProxy
operator|)
name|managedConnection
operator|.
name|getConnection
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|stopBroker
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
block|}
end_class

end_unit

