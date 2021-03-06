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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|atomic
operator|.
name|AtomicReference
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
name|xbean
operator|.
name|BrokerFactoryBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|ClassPathResource
import|;
end_import

begin_comment
comment|/**  * Tests for AMQ-3719  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionHangOnStartupTest
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
name|ConnectionHangOnStartupTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// short maxInactivityDurationInitalDelay to trigger the bug, short
comment|// maxReconnectDelay so that the test runs faster (because it will retry
comment|// connection sooner)
specifier|protected
name|String
name|uriString
init|=
literal|"failover://(tcp://localhost:62001?wireFormat.maxInactivityDurationInitalDelay=1,tcp://localhost:62002?wireFormat.maxInactivityDurationInitalDelay=1)?randomize=false&maxReconnectDelay=200"
decl_stmt|;
specifier|protected
name|BrokerService
name|master
init|=
literal|null
decl_stmt|;
specifier|protected
name|AtomicReference
argument_list|<
name|BrokerService
argument_list|>
name|slave
init|=
operator|new
name|AtomicReference
argument_list|<
name|BrokerService
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|brokerService
init|=
name|slave
operator|.
name|get
argument_list|()
decl_stmt|;
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
if|if
condition|(
name|master
operator|!=
literal|null
condition|)
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|uriString
argument_list|)
return|;
block|}
specifier|protected
name|void
name|createMaster
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerFactoryBean
name|brokerFactory
init|=
operator|new
name|BrokerFactoryBean
argument_list|(
operator|new
name|ClassPathResource
argument_list|(
name|getMasterXml
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|brokerFactory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|master
operator|=
name|brokerFactory
operator|.
name|getBroker
argument_list|()
expr_stmt|;
name|master
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|createSlave
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerFactoryBean
name|brokerFactory
init|=
operator|new
name|BrokerFactoryBean
argument_list|(
operator|new
name|ClassPathResource
argument_list|(
name|getSlaveXml
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|brokerFactory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|BrokerService
name|broker
init|=
name|brokerFactory
operator|.
name|getBroker
argument_list|()
decl_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|slave
operator|.
name|set
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|getSlaveXml
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/broker/ft/sharedFileSlave.xml"
return|;
block|}
specifier|protected
name|String
name|getMasterXml
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/broker/ft/sharedFileMaster.xml"
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testInitialWireFormatNegotiationTimeout
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicReference
argument_list|<
name|Connection
argument_list|>
name|conn
init|=
operator|new
name|AtomicReference
argument_list|<
name|Connection
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|connStarted
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|conn
operator|.
name|set
argument_list|(
name|createConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|()
argument_list|)
expr_stmt|;
name|conn
operator|.
name|get
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"could not create or start connection"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
name|connStarted
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|createMaster
argument_list|()
expr_stmt|;
comment|// slave will never start unless the master dies!
comment|//createSlave();
name|conn
operator|.
name|get
argument_list|()
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

