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
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|AtomicLong
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
name|amq
operator|.
name|AMQPersistenceAdapterFactory
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
name|KahaDBStore
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
name|VerifySteadyEnqueueRate
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
name|VerifySteadyEnqueueRate
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|int
name|max_messages
init|=
literal|1000000
decl_stmt|;
specifier|private
name|String
name|destinationName
init|=
name|getName
argument_list|()
operator|+
literal|"_Queue"
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|final
name|boolean
name|useTopic
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|useAMQPStore
init|=
literal|false
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|payload
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
literal|24
index|]
argument_list|)
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|startBroker
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testEnqueueRateCanMeetSLA
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
literal|true
condition|)
block|{
return|return;
block|}
name|doTestEnqueue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTestEnqueue
parameter_list|(
specifier|final
name|boolean
name|transacted
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|long
name|min
init|=
literal|100
decl_stmt|;
specifier|final
name|AtomicLong
name|total
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|AtomicLong
name|slaViolations
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|AtomicLong
name|max
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numThreads
init|=
literal|6
decl_stmt|;
name|Runnable
name|runner
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|MessageSender
name|producer
init|=
operator|new
name|MessageSender
argument_list|(
name|destinationName
argument_list|,
name|createConnection
argument_list|()
argument_list|,
name|transacted
argument_list|,
name|useTopic
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
name|max_messages
condition|;
name|i
operator|++
control|)
block|{
name|long
name|startT
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|long
name|endT
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|duration
init|=
name|endT
operator|-
name|startT
decl_stmt|;
name|total
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|duration
operator|>
name|max
operator|.
name|get
argument_list|()
condition|)
block|{
name|max
operator|.
name|set
argument_list|(
name|duration
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|duration
operator|>
name|min
condition|)
block|{
name|slaViolations
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"SLA violation @ "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" "
operator|+
name|DateFormat
operator|.
name|getTimeInstance
argument_list|()
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|startT
argument_list|)
argument_list|)
operator|+
literal|" at message "
operator|+
name|i
operator|+
literal|" send time="
operator|+
name|duration
operator|+
literal|" - Total SLA violations: "
operator|+
name|slaViolations
operator|.
name|get
argument_list|()
operator|+
literal|"/"
operator|+
name|total
operator|.
name|get
argument_list|()
operator|+
literal|" ("
operator|+
name|String
operator|.
name|format
argument_list|(
literal|"%.6f"
argument_list|,
literal|100.0
operator|*
name|slaViolations
operator|.
name|get
argument_list|()
operator|/
name|total
operator|.
name|get
argument_list|()
argument_list|)
operator|+
literal|"%)"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Max Violation = "
operator|+
name|max
operator|+
literal|" - Total SLA violations: "
operator|+
name|slaViolations
operator|.
name|get
argument_list|()
operator|+
literal|"/"
operator|+
name|total
operator|.
name|get
argument_list|()
operator|+
literal|" ("
operator|+
name|String
operator|.
name|format
argument_list|(
literal|"%.6f"
argument_list|,
literal|100.0
operator|*
name|slaViolations
operator|.
name|get
argument_list|()
operator|/
name|total
operator|.
name|get
argument_list|()
argument_list|)
operator|+
literal|"%)"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newCachedThreadPool
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|executor
operator|.
name|execute
argument_list|(
name|runner
argument_list|)
expr_stmt|;
block|}
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|executor
operator|.
name|isTerminated
argument_list|()
condition|)
block|{
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Connection
name|createConnection
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
argument_list|)
decl_stmt|;
return|return
name|factory
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|private
name|void
name|startBroker
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
comment|//broker.setDeleteAllMessagesOnStartup(true);
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|useAMQPStore
condition|)
block|{
name|AMQPersistenceAdapterFactory
name|factory
init|=
operator|(
name|AMQPersistenceAdapterFactory
operator|)
name|broker
operator|.
name|getPersistenceFactory
argument_list|()
decl_stmt|;
comment|// ensure there are a bunch of data files but multiple entries in
comment|// each
comment|// factory.setMaxFileLength(1024 * 20);
comment|// speed up the test case, checkpoint an cleanup early and often
comment|// factory.setCheckpointInterval(500);
name|factory
operator|.
name|setCleanupInterval
argument_list|(
literal|1000
operator|*
literal|60
operator|*
literal|30
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setSyncOnWrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// int indexBinSize=262144; // good for 6M
name|int
name|indexBinSize
init|=
literal|1024
decl_stmt|;
name|factory
operator|.
name|setIndexMaxBinSize
argument_list|(
name|indexBinSize
operator|*
literal|2
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setIndexBinSize
argument_list|(
name|indexBinSize
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setIndexPageSize
argument_list|(
literal|192
operator|*
literal|20
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|KahaDBStore
name|kaha
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
name|kaha
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/activemq-data/kahadb"
argument_list|)
argument_list|)
expr_stmt|;
comment|// The setEnableJournalDiskSyncs(false) setting is a little dangerous right now, as I have not verified
comment|// what happens if the index is updated but a journal update is lost.
comment|// Index is going to be in consistent, but can it be repaired?
name|kaha
operator|.
name|setEnableJournalDiskSyncs
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Using a bigger journal file size makes he take fewer spikes as it is not switching files as often.
name|kaha
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|100
argument_list|)
expr_stmt|;
comment|// small batch means more frequent and smaller writes
name|kaha
operator|.
name|setIndexWriteBatchSize
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// do the index write in a separate thread
name|kaha
operator|.
name|setEnableIndexWriteAsync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
operator|.
name|setName
argument_list|(
literal|"Default"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting broker.."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

