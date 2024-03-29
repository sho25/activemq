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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Timer
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
name|TimeUnit
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
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageListener
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
name|Queue
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
name|resource
operator|.
name|ResourceException
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
name|BootstrapContext
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
name|UnavailableException
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
name|XATerminator
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
name|endpoint
operator|.
name|MessageEndpoint
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
name|endpoint
operator|.
name|MessageEndpointFactory
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
name|work
operator|.
name|ExecutionContext
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
name|work
operator|.
name|Work
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
name|work
operator|.
name|WorkException
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
name|work
operator|.
name|WorkListener
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
name|work
operator|.
name|WorkManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAResource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|Xid
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
name|command
operator|.
name|ActiveMQQueue
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
name|Before
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

begin_class
specifier|public
class|class
name|FailoverManagedClusterTest
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
name|FailoverManagedClusterTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|long
name|txGenerator
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MASTER_BIND_ADDRESS
init|=
literal|"tcp://localhost:0"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|SLAVE_BIND_ADDRESS
init|=
literal|"tcp://localhost:0"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|KAHADB_DIRECTORY
init|=
literal|"target/activemq-data/"
decl_stmt|;
specifier|private
name|String
name|masterConnectionUri
decl_stmt|;
specifier|private
name|String
name|slaveConnectionUri
decl_stmt|;
specifier|private
name|String
name|brokerUri
decl_stmt|;
specifier|private
name|BrokerService
name|master
decl_stmt|;
specifier|private
name|BrokerService
name|slave
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|slaveThreadStarted
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|createAndStartMaster
argument_list|()
expr_stmt|;
name|createAndStartSlave
argument_list|()
expr_stmt|;
name|brokerUri
operator|=
literal|"failover://("
operator|+
name|masterConnectionUri
operator|+
literal|","
operator|+
name|slaveConnectionUri
operator|+
literal|")?randomize=false"
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|slave
operator|!=
literal|null
condition|)
block|{
name|slave
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
block|{
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|createAndStartMaster
parameter_list|()
throws|throws
name|Exception
block|{
name|master
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|master
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|master
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|master
operator|.
name|setDataDirectory
argument_list|(
name|KAHADB_DIRECTORY
argument_list|)
expr_stmt|;
name|master
operator|.
name|setBrokerName
argument_list|(
literal|"BROKER"
argument_list|)
expr_stmt|;
name|masterConnectionUri
operator|=
name|master
operator|.
name|addConnector
argument_list|(
name|MASTER_BIND_ADDRESS
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
name|master
operator|.
name|start
argument_list|()
expr_stmt|;
name|master
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createAndStartSlave
parameter_list|()
throws|throws
name|Exception
block|{
name|slave
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|slave
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|slave
operator|.
name|setDataDirectory
argument_list|(
name|KAHADB_DIRECTORY
argument_list|)
expr_stmt|;
name|slave
operator|.
name|setBrokerName
argument_list|(
literal|"BROKER"
argument_list|)
expr_stmt|;
name|slaveConnectionUri
operator|=
name|slave
operator|.
name|addConnector
argument_list|(
name|SLAVE_BIND_ADDRESS
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
comment|// Start the slave asynchronously, since this will block
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
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
name|slaveThreadStarted
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|slave
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"slave has started"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|testFailover
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUri
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
name|ActiveMQResourceAdapter
name|adapter
init|=
operator|new
name|ActiveMQResourceAdapter
argument_list|()
decl_stmt|;
name|adapter
operator|.
name|setServerUrl
argument_list|(
name|brokerUri
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|start
argument_list|(
operator|new
name|StubBootstrapContext
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|CountDownLatch
name|messageDelivered
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|StubMessageEndpoint
name|endpoint
init|=
operator|new
name|StubMessageEndpoint
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message "
operator|+
name|message
argument_list|)
expr_stmt|;
name|super
operator|.
name|onMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|messageDelivered
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
empty_stmt|;
block|}
decl_stmt|;
name|ActiveMQActivationSpec
name|activationSpec
init|=
operator|new
name|ActiveMQActivationSpec
argument_list|()
decl_stmt|;
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
name|Queue
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setDestination
argument_list|(
literal|"TEST"
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setResourceAdapter
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|validate
argument_list|()
expr_stmt|;
name|MessageEndpointFactory
name|messageEndpointFactory
init|=
operator|new
name|MessageEndpointFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MessageEndpoint
name|createEndpoint
parameter_list|(
name|XAResource
name|resource
parameter_list|)
throws|throws
name|UnavailableException
block|{
name|endpoint
operator|.
name|xaresource
operator|=
name|resource
expr_stmt|;
return|return
name|endpoint
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDeliveryTransacted
parameter_list|(
name|Method
name|method
parameter_list|)
throws|throws
name|NoSuchMethodException
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
comment|// Activate an Endpoint
name|adapter
operator|.
name|endpointActivation
argument_list|(
name|messageEndpointFactory
argument_list|,
name|activationSpec
argument_list|)
expr_stmt|;
comment|// Give endpoint a moment to setup and register its listeners
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
argument_list|)
decl_stmt|;
name|slaveThreadStarted
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// force a failover before send
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping master to force failover.."
argument_list|)
expr_stmt|;
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
name|master
operator|=
literal|null
expr_stmt|;
name|assertTrue
argument_list|(
literal|"slave started ok"
argument_list|,
name|slave
operator|.
name|waitUntilStarted
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hello, again!"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Wait for the message to be delivered.
name|assertTrue
argument_list|(
name|messageDelivered
operator|.
name|await
argument_list|(
literal|5000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|StubBootstrapContext
implements|implements
name|BootstrapContext
block|{
annotation|@
name|Override
specifier|public
name|WorkManager
name|getWorkManager
parameter_list|()
block|{
return|return
operator|new
name|WorkManager
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doWork
parameter_list|(
name|Work
name|work
parameter_list|)
throws|throws
name|WorkException
block|{
operator|new
name|Thread
argument_list|(
name|work
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doWork
parameter_list|(
name|Work
name|work
parameter_list|,
name|long
name|arg1
parameter_list|,
name|ExecutionContext
name|arg2
parameter_list|,
name|WorkListener
name|arg3
parameter_list|)
throws|throws
name|WorkException
block|{
operator|new
name|Thread
argument_list|(
name|work
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|startWork
parameter_list|(
name|Work
name|work
parameter_list|)
throws|throws
name|WorkException
block|{
operator|new
name|Thread
argument_list|(
name|work
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|startWork
parameter_list|(
name|Work
name|work
parameter_list|,
name|long
name|arg1
parameter_list|,
name|ExecutionContext
name|arg2
parameter_list|,
name|WorkListener
name|arg3
parameter_list|)
throws|throws
name|WorkException
block|{
operator|new
name|Thread
argument_list|(
name|work
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|scheduleWork
parameter_list|(
name|Work
name|work
parameter_list|)
throws|throws
name|WorkException
block|{
operator|new
name|Thread
argument_list|(
name|work
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|scheduleWork
parameter_list|(
name|Work
name|work
parameter_list|,
name|long
name|arg1
parameter_list|,
name|ExecutionContext
name|arg2
parameter_list|,
name|WorkListener
name|arg3
parameter_list|)
throws|throws
name|WorkException
block|{
operator|new
name|Thread
argument_list|(
name|work
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|XATerminator
name|getXATerminator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Timer
name|createTimer
parameter_list|()
throws|throws
name|UnavailableException
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
class|class
name|StubMessageEndpoint
implements|implements
name|MessageEndpoint
implements|,
name|MessageListener
block|{
specifier|public
name|int
name|messageCount
decl_stmt|;
specifier|public
name|XAResource
name|xaresource
decl_stmt|;
specifier|public
name|Xid
name|xid
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|beforeDelivery
parameter_list|(
name|Method
name|method
parameter_list|)
throws|throws
name|NoSuchMethodException
throws|,
name|ResourceException
block|{
try|try
block|{
if|if
condition|(
name|xid
operator|==
literal|null
condition|)
block|{
name|xid
operator|=
name|createXid
argument_list|()
expr_stmt|;
block|}
name|xaresource
operator|.
name|start
argument_list|(
name|xid
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterDelivery
parameter_list|()
throws|throws
name|ResourceException
block|{
try|try
block|{
name|xaresource
operator|.
name|end
argument_list|(
name|xid
argument_list|,
name|XAResource
operator|.
name|TMSUCCESS
argument_list|)
expr_stmt|;
name|xaresource
operator|.
name|prepare
argument_list|(
name|xid
argument_list|)
expr_stmt|;
name|xaresource
operator|.
name|commit
argument_list|(
name|xid
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|ResourceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|release
parameter_list|()
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|messageCount
operator|++
expr_stmt|;
block|}
block|}
specifier|public
name|Xid
name|createXid
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|os
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
operator|++
name|txGenerator
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|byte
index|[]
name|bs
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
return|return
operator|new
name|Xid
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getFormatId
parameter_list|()
block|{
return|return
literal|86
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|getGlobalTransactionId
parameter_list|()
block|{
return|return
name|bs
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|getBranchQualifier
parameter_list|()
block|{
return|return
name|bs
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

