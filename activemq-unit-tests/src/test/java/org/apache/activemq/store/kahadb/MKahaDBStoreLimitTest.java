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
name|store
operator|.
name|kahadb
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
name|region
operator|.
name|BaseDestination
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
name|usage
operator|.
name|StoreUsage
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
name|IOHelper
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
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
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
name|File
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
name|Executor
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|MKahaDBStoreLimitTest
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
name|MKahaDBStoreLimitTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQQueue
name|queueA
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q.A"
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQQueue
name|queueB
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q.B"
argument_list|)
decl_stmt|;
specifier|private
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
specifier|private
name|BrokerService
name|createBroker
parameter_list|(
name|MultiKahaDBPersistenceAdapter
name|persistenceAdapter
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
name|setPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
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
name|setSchedulerSupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
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
name|testPerDestUsage
parameter_list|()
throws|throws
name|Exception
block|{
comment|// setup multi-kaha adapter
name|MultiKahaDBPersistenceAdapter
name|persistenceAdapter
init|=
operator|new
name|MultiKahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|KahaDBPersistenceAdapter
name|kahaStore
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|kahaStore
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|5
argument_list|)
expr_stmt|;
name|kahaStore
operator|.
name|setCleanupInterval
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// set up a store per destination
name|FilteredKahaDBPersistenceAdapter
name|filtered
init|=
operator|new
name|FilteredKahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|StoreUsage
name|storeUsage
init|=
operator|new
name|StoreUsage
argument_list|()
decl_stmt|;
name|storeUsage
operator|.
name|setPercentLimit
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|storeUsage
operator|.
name|setTotal
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|10
argument_list|)
expr_stmt|;
name|filtered
operator|.
name|setUsage
argument_list|(
name|storeUsage
argument_list|)
expr_stmt|;
name|filtered
operator|.
name|setPersistenceAdapter
argument_list|(
name|kahaStore
argument_list|)
expr_stmt|;
name|filtered
operator|.
name|setPerDestination
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FilteredKahaDBPersistenceAdapter
argument_list|>
name|stores
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|stores
operator|.
name|add
argument_list|(
name|filtered
argument_list|)
expr_stmt|;
name|persistenceAdapter
operator|.
name|setFilteredPersistenceAdapters
argument_list|(
name|stores
argument_list|)
expr_stmt|;
name|createBroker
argument_list|(
name|persistenceAdapter
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|produceMessages
argument_list|(
name|queueA
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|produceMessages
argument_list|(
name|queueB
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Store global u: "
operator|+
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|+
literal|", %:"
operator|+
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"some usage"
argument_list|,
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|BaseDestination
name|baseDestinationA
init|=
operator|(
name|BaseDestination
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|queueA
argument_list|)
decl_stmt|;
name|BaseDestination
name|baseDestinationB
init|=
operator|(
name|BaseDestination
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|queueB
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Store A u: "
operator|+
name|baseDestinationA
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|+
literal|", %: "
operator|+
name|baseDestinationA
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|baseDestinationA
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|produceMessages
argument_list|(
name|queueB
argument_list|,
literal|40
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|baseDestinationB
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|baseDestinationB
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|>
name|baseDestinationA
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Store B u: "
operator|+
name|baseDestinationB
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|+
literal|", %: "
operator|+
name|baseDestinationB
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Store global u: "
operator|+
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|+
literal|", %:"
operator|+
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
name|consume
argument_list|(
name|queueA
argument_list|)
expr_stmt|;
name|consume
argument_list|(
name|queueB
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Store global u: "
operator|+
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|+
literal|", %:"
operator|+
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Store A u: "
operator|+
name|baseDestinationA
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|+
literal|", %: "
operator|+
name|baseDestinationA
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Store B u: "
operator|+
name|baseDestinationB
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|+
literal|", %: "
operator|+
name|baseDestinationB
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExplicitAdapter
parameter_list|()
throws|throws
name|Exception
block|{
name|MultiKahaDBPersistenceAdapter
name|persistenceAdapter
init|=
operator|new
name|MultiKahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|KahaDBPersistenceAdapter
name|kahaStore
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|kahaStore
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|25
argument_list|)
expr_stmt|;
name|FilteredKahaDBPersistenceAdapter
name|filtered
init|=
operator|new
name|FilteredKahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|StoreUsage
name|storeUsage
init|=
operator|new
name|StoreUsage
argument_list|()
decl_stmt|;
name|storeUsage
operator|.
name|setPercentLimit
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|storeUsage
operator|.
name|setTotal
argument_list|(
literal|512
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|filtered
operator|.
name|setUsage
argument_list|(
name|storeUsage
argument_list|)
expr_stmt|;
name|filtered
operator|.
name|setDestination
argument_list|(
name|queueA
argument_list|)
expr_stmt|;
name|filtered
operator|.
name|setPersistenceAdapter
argument_list|(
name|kahaStore
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FilteredKahaDBPersistenceAdapter
argument_list|>
name|stores
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|stores
operator|.
name|add
argument_list|(
name|filtered
argument_list|)
expr_stmt|;
name|persistenceAdapter
operator|.
name|setFilteredPersistenceAdapters
argument_list|(
name|stores
argument_list|)
expr_stmt|;
name|BrokerService
name|brokerService
init|=
name|createBroker
argument_list|(
name|persistenceAdapter
argument_list|)
decl_stmt|;
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|setTotal
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|produceMessages
argument_list|(
name|queueA
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Store global u: "
operator|+
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|+
literal|", %:"
operator|+
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"some usage"
argument_list|,
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|BaseDestination
name|baseDestinationA
init|=
operator|(
name|BaseDestination
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|queueA
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Store A u: "
operator|+
name|baseDestinationA
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|+
literal|", %: "
operator|+
name|baseDestinationA
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"limited store has more % usage than parent"
argument_list|,
name|baseDestinationA
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
operator|>
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExplicitAdapterBlockingProducer
parameter_list|()
throws|throws
name|Exception
block|{
name|MultiKahaDBPersistenceAdapter
name|persistenceAdapter
init|=
operator|new
name|MultiKahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|KahaDBPersistenceAdapter
name|kahaStore
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|kahaStore
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|8
argument_list|)
expr_stmt|;
name|kahaStore
operator|.
name|setIndexDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|FilteredKahaDBPersistenceAdapter
name|filtered
init|=
operator|new
name|FilteredKahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|StoreUsage
name|storeUsage
init|=
operator|new
name|StoreUsage
argument_list|()
decl_stmt|;
name|storeUsage
operator|.
name|setLimit
argument_list|(
literal|40
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|filtered
operator|.
name|setUsage
argument_list|(
name|storeUsage
argument_list|)
expr_stmt|;
name|filtered
operator|.
name|setDestination
argument_list|(
name|queueA
argument_list|)
expr_stmt|;
name|filtered
operator|.
name|setPersistenceAdapter
argument_list|(
name|kahaStore
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FilteredKahaDBPersistenceAdapter
argument_list|>
name|stores
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|stores
operator|.
name|add
argument_list|(
name|filtered
argument_list|)
expr_stmt|;
name|persistenceAdapter
operator|.
name|setFilteredPersistenceAdapters
argument_list|(
name|stores
argument_list|)
expr_stmt|;
name|BrokerService
name|brokerService
init|=
name|createBroker
argument_list|(
name|persistenceAdapter
argument_list|)
decl_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|AtomicBoolean
name|done
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
name|executor
operator|.
name|submit
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
name|produceMessages
argument_list|(
name|queueA
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|done
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{                 }
block|}
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"some messages got to dest"
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
name|BaseDestination
name|baseDestinationA
init|=
operator|(
name|BaseDestination
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|queueA
argument_list|)
decl_stmt|;
return|return
name|baseDestinationA
operator|!=
literal|null
operator|&&
name|baseDestinationA
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
operator|>
literal|4l
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|BaseDestination
name|baseDestinationA
init|=
operator|(
name|BaseDestination
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|queueA
argument_list|)
decl_stmt|;
comment|// loop till producer stalled
name|long
name|enqueues
init|=
literal|0l
decl_stmt|;
do|do
block|{
name|enqueues
operator|=
name|baseDestinationA
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Dest Enqueues: "
operator|+
name|enqueues
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|enqueues
operator|!=
name|baseDestinationA
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
condition|)
do|;
name|assertFalse
argument_list|(
literal|"expect producer to block"
argument_list|,
name|done
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Store global u: "
operator|+
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|+
literal|", %:"
operator|+
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"some usage"
argument_list|,
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Store A u: "
operator|+
name|baseDestinationA
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|+
literal|", %: "
operator|+
name|baseDestinationA
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"limited store has more % usage than parent"
argument_list|,
name|baseDestinationA
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
operator|>
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|consume
parameter_list|(
name|Destination
name|queue
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?create=false"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|cf
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
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
literal|5
condition|;
operator|++
name|i
control|)
block|{
name|assertNotNull
argument_list|(
literal|"message["
operator|+
name|i
operator|+
literal|"]"
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|produceMessages
parameter_list|(
name|Destination
name|queue
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?create=false"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|cf
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|BytesMessage
name|bytesMessage
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|bytesMessage
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|1
operator|*
literal|1024
index|]
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
name|count
condition|;
operator|++
name|i
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|bytesMessage
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

