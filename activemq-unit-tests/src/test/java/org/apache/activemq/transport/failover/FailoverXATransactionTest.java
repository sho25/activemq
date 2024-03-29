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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQXAConnectionFactory
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
name|BrokerPlugin
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
name|BrokerPluginSupport
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
name|ConnectionContext
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
name|region
operator|.
name|policy
operator|.
name|PolicyEntry
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
name|region
operator|.
name|policy
operator|.
name|PolicyMap
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
name|TransactionId
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
name|TestUtils
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
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XAConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XASession
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_class
specifier|public
class|class
name|FailoverXATransactionTest
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
name|FailoverXATransactionTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE_NAME
init|=
literal|"Failover.WithXaTx"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TRANSPORT_URI
init|=
literal|"tcp://localhost:0"
decl_stmt|;
specifier|private
name|String
name|url
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|stopBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
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
specifier|public
name|void
name|startBroker
parameter_list|(
name|boolean
name|deleteAllMessagesOnStartup
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|(
name|deleteAllMessagesOnStartup
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|startBroker
parameter_list|(
name|boolean
name|deleteAllMessagesOnStartup
parameter_list|,
name|String
name|bindAddress
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|(
name|deleteAllMessagesOnStartup
argument_list|,
name|bindAddress
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|BrokerService
name|createBroker
parameter_list|(
name|boolean
name|deleteAllMessagesOnStartup
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createBroker
argument_list|(
name|deleteAllMessagesOnStartup
argument_list|,
name|TRANSPORT_URI
argument_list|)
return|;
block|}
specifier|public
name|BrokerService
name|createBroker
parameter_list|(
name|boolean
name|deleteAllMessagesOnStartup
parameter_list|,
name|String
name|bindAddress
parameter_list|)
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
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessagesOnStartup
argument_list|)
expr_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|defaultEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|defaultEntry
operator|.
name|setUsePrefetchExtension
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|defaultEntry
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|url
operator|=
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
name|getConnectUri
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
annotation|@
name|org
operator|.
name|junit
operator|.
name|Test
specifier|public
name|void
name|testFailoverSendPrepareReplyLost
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|AtomicBoolean
name|first
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|broker
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
operator|new
name|BrokerPluginSupport
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|prepareTransaction
parameter_list|(
specifier|final
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|result
init|=
name|super
operator|.
name|prepareTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
decl_stmt|;
if|if
condition|(
name|first
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|context
operator|.
name|setDontSendReponse
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping broker on prepare"
argument_list|)
expr_stmt|;
try|try
block|{
name|context
operator|.
name|getConnection
argument_list|()
operator|.
name|stop
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
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
unit|}                          return
name|result
expr_stmt|;
end_expr_stmt

begin_empty_stmt
unit|}                 }         })
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_decl_stmt
name|ActiveMQXAConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQXAConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|url
operator|+
literal|")"
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
name|XAConnection
name|connection
init|=
name|cf
operator|.
name|createXAConnection
argument_list|()
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_decl_stmt
specifier|final
name|XASession
name|session
init|=
name|connection
operator|.
name|createXASession
argument_list|()
decl_stmt|;
end_decl_stmt

begin_decl_stmt
name|Queue
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
name|Xid
name|xid
init|=
name|TestUtils
operator|.
name|createXid
argument_list|()
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|session
operator|.
name|getXAResource
argument_list|()
operator|.
name|start
argument_list|(
name|xid
argument_list|,
name|XAResource
operator|.
name|TMNOFLAGS
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|produceMessage
argument_list|(
name|session
argument_list|,
name|destination
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|session
operator|.
name|getXAResource
argument_list|()
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
end_expr_stmt

begin_try
try|try
block|{
name|session
operator|.
name|getXAResource
argument_list|()
operator|.
name|prepare
argument_list|(
name|xid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{
name|expected
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
end_try

begin_try
try|try
block|{
name|session
operator|.
name|getXAResource
argument_list|()
operator|.
name|rollback
argument_list|(
name|xid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{
name|expected
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
end_try

begin_expr_stmt
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalMessageCount
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}      @
name|org
operator|.
name|junit
operator|.
name|Test
specifier|public
name|void
name|testFailoverSendCommitReplyLost
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|AtomicBoolean
name|first
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|broker
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
operator|new
name|BrokerPluginSupport
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|commitTransaction
parameter_list|(
specifier|final
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|,
name|boolean
name|onePhase
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|commitTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|,
name|onePhase
argument_list|)
expr_stmt|;
if|if
condition|(
name|first
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|context
operator|.
name|setDontSendReponse
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping broker on prepare"
argument_list|)
expr_stmt|;
try|try
block|{
name|context
operator|.
name|getConnection
argument_list|()
operator|.
name|stop
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
end_function

begin_empty_stmt
unit|})
empty_stmt|;
end_empty_stmt

begin_empty_stmt
unit|}                     }                 }         })
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_decl_stmt
name|ActiveMQXAConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQXAConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|url
operator|+
literal|")"
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
name|XAConnection
name|connection
init|=
name|cf
operator|.
name|createXAConnection
argument_list|()
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_decl_stmt
specifier|final
name|XASession
name|session
init|=
name|connection
operator|.
name|createXASession
argument_list|()
decl_stmt|;
end_decl_stmt

begin_decl_stmt
name|Queue
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
name|Xid
name|xid
init|=
name|TestUtils
operator|.
name|createXid
argument_list|()
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|session
operator|.
name|getXAResource
argument_list|()
operator|.
name|start
argument_list|(
name|xid
argument_list|,
name|XAResource
operator|.
name|TMNOFLAGS
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|produceMessage
argument_list|(
name|session
argument_list|,
name|destination
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|session
operator|.
name|getXAResource
argument_list|()
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
end_expr_stmt

begin_try
try|try
block|{
name|session
operator|.
name|getXAResource
argument_list|()
operator|.
name|prepare
argument_list|(
name|xid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{
name|expected
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
end_try

begin_try
try|try
block|{
name|session
operator|.
name|getXAResource
argument_list|()
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
name|Exception
name|expected
parameter_list|)
block|{
name|expected
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
end_try

begin_expr_stmt
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalMessageCount
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}      private
name|void
name|produceMessage
parameter_list|(
specifier|final
name|Session
name|producerSession
parameter_list|,
name|Queue
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
name|MessageProducer
name|producer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|(
literal|"Test message"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
end_function

unit|}
end_unit

