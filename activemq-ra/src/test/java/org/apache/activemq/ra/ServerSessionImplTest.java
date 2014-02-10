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
name|ExecutorService
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
name|Executors
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
name|Session
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
name|ActiveMQConnection
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
name|ActiveMQSession
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
name|ActiveMQTextMessage
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
name|MessageDispatch
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|Expectations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|Mockery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|api
operator|.
name|Action
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|api
operator|.
name|Invocation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|integration
operator|.
name|junit4
operator|.
name|JMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|lib
operator|.
name|legacy
operator|.
name|ClassImposteriser
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
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
annotation|@
name|RunWith
argument_list|(
name|JMock
operator|.
name|class
argument_list|)
specifier|public
class|class
name|ServerSessionImplTest
extends|extends
name|TestCase
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
name|ServerSessionImplTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_URL
init|=
literal|"vm://localhost?broker.persistent=false"
decl_stmt|;
specifier|private
name|ServerSessionImpl
name|serverSession
decl_stmt|;
specifier|private
name|ServerSessionPoolImpl
name|pool
decl_stmt|;
specifier|private
name|WorkManager
name|workManager
decl_stmt|;
specifier|private
name|MessageEndpointProxy
name|messageEndpoint
decl_stmt|;
specifier|private
name|ActiveMQConnection
name|con
decl_stmt|;
specifier|private
name|ActiveMQSession
name|session
decl_stmt|;
name|ActiveMQEndpointWorker
name|endpointWorker
decl_stmt|;
specifier|private
name|Mockery
name|context
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|context
operator|=
operator|new
name|Mockery
argument_list|()
block|{
block|{
name|setImposteriser
parameter_list|(
name|ClassImposteriser
operator|.
name|INSTANCE
parameter_list|)
constructor_decl|;
block|}
block|}
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
argument_list|(
name|BROKER_URL
argument_list|)
decl_stmt|;
name|con
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|con
operator|.
name|start
argument_list|()
expr_stmt|;
name|session
operator|=
operator|(
name|ActiveMQSession
operator|)
name|con
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
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
name|con
operator|!=
literal|null
condition|)
block|{
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRunDetectsStoppedSession
parameter_list|()
throws|throws
name|Exception
block|{
name|pool
operator|=
name|context
operator|.
name|mock
argument_list|(
name|ServerSessionPoolImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|workManager
operator|=
name|context
operator|.
name|mock
argument_list|(
name|WorkManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|messageEndpoint
operator|=
name|context
operator|.
name|mock
argument_list|(
name|MessageEndpointProxy
operator|.
name|class
argument_list|)
expr_stmt|;
name|serverSession
operator|=
operator|new
name|ServerSessionImpl
argument_list|(
operator|(
name|ServerSessionPoolImpl
operator|)
name|pool
argument_list|,
name|session
argument_list|,
operator|(
name|WorkManager
operator|)
name|workManager
argument_list|,
name|messageEndpoint
argument_list|,
literal|false
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|context
operator|.
name|checking
argument_list|(
operator|new
name|Expectations
argument_list|()
block|{
block|{
name|oneOf
argument_list|(
name|pool
argument_list|)
operator|.
name|removeFromPool
argument_list|(
name|with
argument_list|(
name|same
argument_list|(
name|serverSession
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|serverSession
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCloseCanStopActiveSession
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|maxMessages
init|=
literal|4000
decl_stmt|;
specifier|final
name|CountDownLatch
name|messageCount
init|=
operator|new
name|CountDownLatch
argument_list|(
name|maxMessages
argument_list|)
decl_stmt|;
specifier|final
name|MessageEndpointFactory
name|messageEndpointFactory
init|=
name|context
operator|.
name|mock
argument_list|(
name|MessageEndpointFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|MessageResourceAdapter
name|resourceAdapter
init|=
name|context
operator|.
name|mock
argument_list|(
name|MessageResourceAdapter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQEndpointActivationKey
name|key
init|=
name|context
operator|.
name|mock
argument_list|(
name|ActiveMQEndpointActivationKey
operator|.
name|class
argument_list|)
decl_stmt|;
name|messageEndpoint
operator|=
name|context
operator|.
name|mock
argument_list|(
name|MessageEndpointProxy
operator|.
name|class
argument_list|)
expr_stmt|;
name|workManager
operator|=
name|context
operator|.
name|mock
argument_list|(
name|WorkManager
operator|.
name|class
argument_list|)
expr_stmt|;
specifier|final
name|MessageActivationSpec
name|messageActivationSpec
init|=
name|context
operator|.
name|mock
argument_list|(
name|MessageActivationSpec
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|BootstrapContext
name|boostrapContext
init|=
name|context
operator|.
name|mock
argument_list|(
name|BootstrapContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|context
operator|.
name|checking
argument_list|(
operator|new
name|Expectations
argument_list|()
block|{
block|{
name|allowing
argument_list|(
name|boostrapContext
argument_list|)
operator|.
name|getWorkManager
argument_list|()
expr_stmt|;
name|will
argument_list|(
name|returnValue
argument_list|(
name|workManager
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|resourceAdapter
argument_list|)
operator|.
name|getBootstrapContext
argument_list|()
expr_stmt|;
name|will
argument_list|(
name|returnValue
argument_list|(
name|boostrapContext
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|messageEndpointFactory
argument_list|)
operator|.
name|isDeliveryTransacted
argument_list|(
name|with
argument_list|(
name|any
argument_list|(
name|Method
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|will
argument_list|(
name|returnValue
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|key
argument_list|)
operator|.
name|getMessageEndpointFactory
argument_list|()
expr_stmt|;
name|will
argument_list|(
name|returnValue
argument_list|(
name|messageEndpointFactory
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|key
argument_list|)
operator|.
name|getActivationSpec
argument_list|()
expr_stmt|;
name|will
argument_list|(
name|returnValue
argument_list|(
name|messageActivationSpec
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|messageActivationSpec
argument_list|)
operator|.
name|isUseJndi
argument_list|()
expr_stmt|;
name|will
argument_list|(
name|returnValue
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|messageActivationSpec
argument_list|)
operator|.
name|getDestinationType
argument_list|()
expr_stmt|;
name|will
argument_list|(
name|returnValue
argument_list|(
literal|"javax.jms.Queue"
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|messageActivationSpec
argument_list|)
operator|.
name|getDestination
argument_list|()
expr_stmt|;
name|will
argument_list|(
name|returnValue
argument_list|(
literal|"Queue"
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|messageActivationSpec
argument_list|)
operator|.
name|getAcknowledgeModeForSession
argument_list|()
expr_stmt|;
name|will
argument_list|(
name|returnValue
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|messageActivationSpec
argument_list|)
operator|.
name|getMaxSessionsIntValue
argument_list|()
expr_stmt|;
name|will
argument_list|(
name|returnValue
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|messageActivationSpec
argument_list|)
operator|.
name|getEnableBatchBooleanValue
argument_list|()
expr_stmt|;
name|will
argument_list|(
name|returnValue
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|messageActivationSpec
argument_list|)
operator|.
name|isUseRAManagedTransactionEnabled
argument_list|()
expr_stmt|;
name|will
argument_list|(
name|returnValue
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|messageEndpointFactory
argument_list|)
operator|.
name|createEndpoint
argument_list|(
name|with
argument_list|(
name|any
argument_list|(
name|XAResource
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|will
argument_list|(
name|returnValue
argument_list|(
name|messageEndpoint
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|workManager
argument_list|)
operator|.
name|scheduleWork
argument_list|(
operator|(
name|Work
operator|)
name|with
argument_list|(
name|anything
argument_list|()
argument_list|)
argument_list|,
operator|(
name|long
operator|)
name|with
argument_list|(
name|any
argument_list|(
name|long
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
name|with
argument_list|(
name|any
argument_list|(
name|ExecutionContext
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
name|with
argument_list|(
name|any
argument_list|(
name|WorkListener
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|will
argument_list|(
operator|new
name|Action
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|invoke
parameter_list|(
name|Invocation
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|describeTo
parameter_list|(
name|Description
name|description
parameter_list|)
block|{                 }
block|}
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|messageEndpoint
argument_list|)
operator|.
name|beforeDelivery
argument_list|(
operator|(
name|Method
operator|)
name|with
argument_list|(
name|anything
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|messageEndpoint
argument_list|)
operator|.
name|onMessage
argument_list|(
name|with
argument_list|(
name|any
argument_list|(
name|javax
operator|.
name|jms
operator|.
name|Message
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|will
argument_list|(
operator|new
name|Action
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|invoke
parameter_list|(
name|Invocation
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|messageCount
operator|.
name|countDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|messageCount
operator|.
name|getCount
argument_list|()
operator|<
name|maxMessages
operator|-
literal|11
condition|)
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|describeTo
parameter_list|(
name|Description
name|description
parameter_list|)
block|{
name|description
operator|.
name|appendText
argument_list|(
literal|"Keep message count"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|messageEndpoint
argument_list|)
operator|.
name|afterDelivery
argument_list|()
expr_stmt|;
name|allowing
argument_list|(
name|messageEndpoint
argument_list|)
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|endpointWorker
operator|=
operator|new
name|ActiveMQEndpointWorker
argument_list|(
name|resourceAdapter
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|endpointWorker
operator|.
name|setConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
name|pool
operator|=
operator|new
name|ServerSessionPoolImpl
argument_list|(
name|endpointWorker
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|endpointWorker
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|ServerSessionImpl
name|serverSession1
init|=
operator|(
name|ServerSessionImpl
operator|)
name|pool
operator|.
name|getServerSession
argument_list|()
decl_stmt|;
comment|// preload the session dispatch queue to keep the session active
name|ActiveMQSession
name|session1
init|=
operator|(
name|ActiveMQSession
operator|)
name|serverSession1
operator|.
name|getSession
argument_list|()
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
name|maxMessages
condition|;
name|i
operator|++
control|)
block|{
name|MessageDispatch
name|messageDispatch
init|=
operator|new
name|MessageDispatch
argument_list|()
decl_stmt|;
name|messageDispatch
operator|.
name|setMessage
argument_list|(
operator|new
name|ActiveMQTextMessage
argument_list|()
argument_list|)
expr_stmt|;
name|session1
operator|.
name|dispatch
argument_list|(
name|messageDispatch
argument_list|)
expr_stmt|;
block|}
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|runState
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|executorService
operator|.
name|execute
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
name|serverSession1
operator|.
name|run
argument_list|()
expr_stmt|;
name|runState
operator|.
name|countDown
argument_list|()
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
expr_stmt|;
while|while
condition|(
name|messageCount
operator|.
name|getCount
argument_list|()
operator|>
name|maxMessages
operator|-
literal|10
condition|)
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Closing pool on {}"
argument_list|,
name|messageCount
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|pool
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"run has completed"
argument_list|,
name|runState
operator|.
name|await
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"not all messages consumed"
argument_list|,
name|messageCount
operator|.
name|getCount
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

