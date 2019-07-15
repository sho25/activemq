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
name|bugs
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
name|Destination
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
name|store
operator|.
name|PersistenceAdapter
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
name|store
operator|.
name|TransactionIdTransformer
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
name|store
operator|.
name|kahadb
operator|.
name|FilteredKahaDBPersistenceAdapter
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
name|store
operator|.
name|kahadb
operator|.
name|KahaDBPersistenceAdapter
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
name|store
operator|.
name|kahadb
operator|.
name|MultiKahaDBPersistenceAdapter
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|MKahaDBTxRecoveryTest
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MKahaDBTxRecoveryTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|maxFileLength
init|=
literal|1024
operator|*
literal|1024
operator|*
literal|32
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PREFIX_DESTINATION_NAME
init|=
literal|"queue"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DESTINATION_NAME
init|=
name|PREFIX_DESTINATION_NAME
operator|+
literal|".test"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DESTINATION_NAME_2
init|=
name|PREFIX_DESTINATION_NAME
operator|+
literal|"2.test"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|CLEANUP_INTERVAL_MILLIS
init|=
literal|500
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|List
argument_list|<
name|KahaDBPersistenceAdapter
argument_list|>
name|kahadbs
init|=
operator|new
name|LinkedList
argument_list|<
name|KahaDBPersistenceAdapter
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
block|}
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|PersistenceAdapter
name|kaha
parameter_list|)
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
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
literal|"localhost"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCommitOutcomeDeliveryOnRecovery
parameter_list|()
throws|throws
name|Exception
block|{
name|prepareBrokerWithMultiStore
argument_list|(
literal|true
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
comment|// Ensure we have an Admin View.
name|assertTrue
argument_list|(
literal|"Broker doesn't have an Admin View."
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
operator|(
name|broker
operator|.
name|getAdminView
argument_list|()
operator|)
operator|!=
literal|null
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|AtomicBoolean
name|injectFailure
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|reps
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|TransactionIdTransformer
argument_list|>
name|delegate
init|=
operator|new
name|AtomicReference
argument_list|<
name|TransactionIdTransformer
argument_list|>
argument_list|()
decl_stmt|;
name|TransactionIdTransformer
name|faultInjector
init|=
operator|new
name|TransactionIdTransformer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TransactionId
name|transform
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
block|{
if|if
condition|(
name|injectFailure
operator|.
name|get
argument_list|()
operator|&&
name|reps
operator|.
name|incrementAndGet
argument_list|()
operator|>
literal|5
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bla"
argument_list|)
throw|;
block|}
return|return
name|delegate
operator|.
name|get
argument_list|()
operator|.
name|transform
argument_list|(
name|txid
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|// set up kahadb to fail after N ops
for|for
control|(
name|KahaDBPersistenceAdapter
name|pa
range|:
name|kahadbs
control|)
block|{
if|if
condition|(
name|delegate
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
name|delegate
operator|.
name|set
argument_list|(
name|pa
operator|.
name|getStore
argument_list|()
operator|.
name|getTransactionIdTransformer
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|pa
operator|.
name|setTransactionIdTransformer
argument_list|(
name|faultInjector
argument_list|)
expr_stmt|;
block|}
name|ActiveMQConnectionFactory
name|f
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAlwaysSyncSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Connection
name|c
init|=
name|f
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|c
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|s
init|=
name|c
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|s
operator|.
name|createProducer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|DESTINATION_NAME
operator|+
literal|","
operator|+
name|DESTINATION_NAME_2
argument_list|)
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|s
operator|.
name|createTextMessage
argument_list|(
literal|"HI"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|s
operator|.
name|commit
argument_list|()
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
name|assertNotNull
argument_list|(
name|broker
operator|.
name|getDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|DESTINATION_NAME
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
operator|.
name|getDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|DESTINATION_NAME_2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Destination
name|destination1
init|=
name|broker
operator|.
name|getDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|DESTINATION_NAME
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Destination
name|destination2
init|=
name|broker
operator|.
name|getDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|DESTINATION_NAME_2
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Partial commit - one dest has message"
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
name|destination2
operator|.
name|getMessageStore
argument_list|()
operator|.
name|getMessageCount
argument_list|()
operator|!=
name|destination1
operator|.
name|getMessageStore
argument_list|()
operator|.
name|getMessageCount
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// check completion on recovery
name|injectFailure
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// fire in many more local transactions to use N txStore journal files
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|s
operator|.
name|createTextMessage
argument_list|(
literal|"HI"
argument_list|)
argument_list|)
expr_stmt|;
name|s
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// fail recovery processing on first attempt
name|prepareBrokerWithMultiStore
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
comment|// longer than CleanupInterval
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|2
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Sorry"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_comment
comment|// second recovery attempt should sort it
end_comment

begin_expr_stmt
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|prepareBrokerWithMultiStore
argument_list|(
literal|false
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
end_expr_stmt

begin_comment
comment|// verify commit completed
end_comment

begin_decl_stmt
name|Destination
name|destination
init|=
name|broker
operator|.
name|getDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|DESTINATION_NAME
argument_list|)
argument_list|)
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|101
argument_list|,
name|destination
operator|.
name|getMessageStore
argument_list|()
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|destination
operator|=
name|broker
operator|.
name|getDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|DESTINATION_NAME_2
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|101
argument_list|,
name|destination
operator|.
name|getMessageStore
argument_list|()
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}       protected
name|KahaDBPersistenceAdapter
name|createStore
parameter_list|(
name|boolean
name|delete
parameter_list|)
throws|throws
name|IOException
block|{
name|KahaDBPersistenceAdapter
name|kaha
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|kaha
operator|.
name|setJournalMaxFileLength
argument_list|(
name|maxFileLength
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setCleanupInterval
argument_list|(
name|CLEANUP_INTERVAL_MILLIS
argument_list|)
expr_stmt|;
if|if
condition|(
name|delete
condition|)
block|{
name|kaha
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
block|}
name|kahadbs
operator|.
name|add
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
return|return
name|kaha
return|;
block|}
end_function

begin_function
specifier|public
name|void
name|prepareBrokerWithMultiStore
parameter_list|(
name|boolean
name|deleteAllMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|MultiKahaDBPersistenceAdapter
name|multiKahaDBPersistenceAdapter
init|=
operator|new
name|MultiKahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
if|if
condition|(
name|deleteAllMessages
condition|)
block|{
name|multiKahaDBPersistenceAdapter
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
block|}
name|ArrayList
argument_list|<
name|FilteredKahaDBPersistenceAdapter
argument_list|>
name|adapters
init|=
operator|new
name|ArrayList
argument_list|<
name|FilteredKahaDBPersistenceAdapter
argument_list|>
argument_list|()
decl_stmt|;
name|adapters
operator|.
name|add
argument_list|(
name|createFilteredKahaDBByDestinationPrefix
argument_list|(
name|PREFIX_DESTINATION_NAME
argument_list|,
name|deleteAllMessages
argument_list|)
argument_list|)
expr_stmt|;
name|adapters
operator|.
name|add
argument_list|(
name|createFilteredKahaDBByDestinationPrefix
argument_list|(
name|PREFIX_DESTINATION_NAME
operator|+
literal|"2"
argument_list|,
name|deleteAllMessages
argument_list|)
argument_list|)
expr_stmt|;
name|multiKahaDBPersistenceAdapter
operator|.
name|setFilteredPersistenceAdapters
argument_list|(
name|adapters
argument_list|)
expr_stmt|;
name|multiKahaDBPersistenceAdapter
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|4
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|multiKahaDBPersistenceAdapter
operator|.
name|setJournalCleanupInterval
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
name|multiKahaDBPersistenceAdapter
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|private
name|FilteredKahaDBPersistenceAdapter
name|createFilteredKahaDBByDestinationPrefix
parameter_list|(
name|String
name|destinationPrefix
parameter_list|,
name|boolean
name|deleteAllMessages
parameter_list|)
throws|throws
name|IOException
block|{
name|FilteredKahaDBPersistenceAdapter
name|template
init|=
operator|new
name|FilteredKahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|template
operator|.
name|setPersistenceAdapter
argument_list|(
name|createStore
argument_list|(
name|deleteAllMessages
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|destinationPrefix
operator|!=
literal|null
condition|)
block|{
name|template
operator|.
name|setQueue
argument_list|(
name|destinationPrefix
operator|+
literal|".>"
argument_list|)
expr_stmt|;
block|}
return|return
name|template
return|;
block|}
end_function

unit|}
end_unit
