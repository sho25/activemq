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
name|transport
operator|.
name|failover
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|JMSException
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
name|Wait
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_comment
comment|/**  * Ensures connections aren't leaked when when we use backup=true and randomize=false  */
end_comment

begin_class
specifier|public
class|class
name|FailoverBackupLeakTest
block|{
specifier|private
specifier|static
name|BrokerService
name|s1
decl_stmt|,
name|s2
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|s1
operator|=
name|buildBroker
argument_list|(
literal|"broker1"
argument_list|)
expr_stmt|;
name|s2
operator|=
name|buildBroker
argument_list|(
literal|"broker2"
argument_list|)
expr_stmt|;
name|s1
operator|.
name|start
argument_list|()
expr_stmt|;
name|s1
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|s2
operator|.
name|start
argument_list|()
expr_stmt|;
name|s2
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|s2
operator|!=
literal|null
condition|)
block|{
name|s2
operator|.
name|stop
argument_list|()
expr_stmt|;
name|s2
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|s1
operator|!=
literal|null
condition|)
block|{
name|s1
operator|.
name|stop
argument_list|()
expr_stmt|;
name|s1
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|String
name|getConnectString
parameter_list|(
name|BrokerService
name|service
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|service
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
return|;
block|}
specifier|private
specifier|static
name|BrokerService
name|buildBroker
parameter_list|(
name|String
name|brokerName
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|service
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|service
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
name|service
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|setUseShutdownHook
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0?transport.closeAsync=false"
argument_list|)
expr_stmt|;
return|return
name|service
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|backupNoRandomize
parameter_list|()
throws|throws
name|Exception
block|{
name|check
argument_list|(
literal|"backup=true&randomize=false"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|priorityBackupNoRandomize
parameter_list|()
throws|throws
name|Exception
block|{
name|check
argument_list|(
literal|"priorityBackup=true&randomize=false"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|check
parameter_list|(
name|String
name|connectionProperties
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|s1URL
init|=
name|getConnectString
argument_list|(
name|s1
argument_list|)
decl_stmt|,
name|s2URL
init|=
name|getConnectString
argument_list|(
name|s2
argument_list|)
decl_stmt|;
name|String
name|uri
init|=
literal|"failover://("
operator|+
name|s1URL
operator|+
literal|","
operator|+
name|s2URL
operator|+
literal|")?"
operator|+
name|connectionProperties
decl_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|uri
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|buildConnection
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|connectionProperties
operator|+
literal|" broker1 connection count not zero: was["
operator|+
name|getConnectionCount
argument_list|(
name|s1
argument_list|)
operator|+
literal|"]"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getConnectionCount
argument_list|(
name|s1
argument_list|)
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|connectionProperties
operator|+
literal|" broker2 connection count not zero: was["
operator|+
name|getConnectionCount
argument_list|(
name|s2
argument_list|)
operator|+
literal|"]"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getConnectionCount
argument_list|(
name|s2
argument_list|)
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|getConnectionCount
parameter_list|(
name|BrokerService
name|service
parameter_list|)
block|{
return|return
name|service
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
specifier|private
name|void
name|buildConnection
parameter_list|(
name|ConnectionFactory
name|local
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|conn
init|=
literal|null
decl_stmt|;
name|Session
name|sess
init|=
literal|null
decl_stmt|;
try|try
block|{
name|conn
operator|=
name|local
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|sess
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|sess
operator|!=
literal|null
condition|)
name|sess
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ignore
parameter_list|)
block|{ }
try|try
block|{
if|if
condition|(
name|conn
operator|!=
literal|null
condition|)
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ignore
parameter_list|)
block|{ }
block|}
block|}
block|}
end_class

end_unit

