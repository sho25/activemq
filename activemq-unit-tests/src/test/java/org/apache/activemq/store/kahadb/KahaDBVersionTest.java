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
name|util
operator|.
name|IOHelper
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|ProtectionDomain
import|;
end_import

begin_comment
comment|/**  * @author chirino  */
end_comment

begin_class
specifier|public
class|class
name|KahaDBVersionTest
extends|extends
name|TestCase
block|{
specifier|static
name|String
name|basedir
decl_stmt|;
static|static
block|{
try|try
block|{
name|ProtectionDomain
name|protectionDomain
init|=
name|KahaDBVersionTest
operator|.
name|class
operator|.
name|getProtectionDomain
argument_list|()
decl_stmt|;
name|basedir
operator|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|protectionDomain
operator|.
name|getCodeSource
argument_list|()
operator|.
name|getLocation
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|"../.."
argument_list|)
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|basedir
operator|=
literal|"."
expr_stmt|;
block|}
block|}
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|KahaDBVersionTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
specifier|static
name|File
name|VERSION_1_DB
init|=
operator|new
name|File
argument_list|(
name|basedir
operator|+
literal|"/src/test/resources/org/apache/activemq/store/kahadb/KahaDBVersion1"
argument_list|)
decl_stmt|;
specifier|final
specifier|static
name|File
name|VERSION_2_DB
init|=
operator|new
name|File
argument_list|(
name|basedir
operator|+
literal|"/src/test/resources/org/apache/activemq/store/kahadb/KahaDBVersion2"
argument_list|)
decl_stmt|;
specifier|final
specifier|static
name|File
name|VERSION_3_DB
init|=
operator|new
name|File
argument_list|(
name|basedir
operator|+
literal|"/src/test/resources/org/apache/activemq/store/kahadb/KahaDBVersion3"
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|KahaDBPersistenceAdapter
name|kaha
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
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
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
specifier|public
name|void
name|XtestCreateStore
parameter_list|()
throws|throws
name|Exception
block|{
name|KahaDBPersistenceAdapter
name|kaha
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
literal|"src/test/resources/org/apache/activemq/store/kahadb/KahaDBVersionX"
argument_list|)
decl_stmt|;
name|IOHelper
operator|.
name|deleteFile
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
init|=
name|createBroker
argument_list|(
name|kaha
argument_list|)
decl_stmt|;
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
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
name|setClientID
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|producerSomeMessages
argument_list|(
name|connection
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|producerSomeMessages
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|int
name|numToSend
parameter_list|)
throws|throws
name|Exception
block|{
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
name|Topic
name|topic
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"test.topic"
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"test.queue"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setPriority
argument_list|(
literal|9
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
name|numToSend
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test message:"
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"sent "
operator|+
name|numToSend
operator|+
literal|" to topic"
argument_list|)
expr_stmt|;
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
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
name|numToSend
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test message:"
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"sent "
operator|+
name|numToSend
operator|+
literal|" to queue"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testVersion1Conversion
parameter_list|()
throws|throws
name|Exception
block|{
name|doConvertRestartCycle
argument_list|(
name|VERSION_1_DB
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testVersion2Conversion
parameter_list|()
throws|throws
name|Exception
block|{
name|doConvertRestartCycle
argument_list|(
name|VERSION_2_DB
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testVersion3Conversion
parameter_list|()
throws|throws
name|Exception
block|{
name|doConvertRestartCycle
argument_list|(
name|VERSION_3_DB
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doConvertRestartCycle
parameter_list|(
name|File
name|existingStore
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|testDir
init|=
operator|new
name|File
argument_list|(
literal|"target/activemq-data/kahadb/versionDB"
argument_list|)
decl_stmt|;
name|IOHelper
operator|.
name|deleteFile
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
name|IOHelper
operator|.
name|copyFile
argument_list|(
name|existingStore
argument_list|,
name|testDir
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numToSend
init|=
literal|1000
decl_stmt|;
comment|// on repeat store will be upgraded
for|for
control|(
name|int
name|repeats
init|=
literal|0
init|;
name|repeats
operator|<
literal|3
condition|;
name|repeats
operator|++
control|)
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
name|setDirectory
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
init|=
name|createBroker
argument_list|(
name|kaha
argument_list|)
decl_stmt|;
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
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
name|setClientID
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
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
name|Topic
name|topic
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"test.topic"
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"test.queue"
argument_list|)
decl_stmt|;
if|if
condition|(
name|repeats
operator|>
literal|0
condition|)
block|{
comment|// upgraded store will be empty so generated some more messages
name|producerSomeMessages
argument_list|(
name|connection
argument_list|,
name|numToSend
argument_list|)
expr_stmt|;
block|}
name|MessageConsumer
name|queueConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
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
operator|(
name|repeats
operator|==
literal|0
condition|?
literal|1000
else|:
name|numToSend
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|queueConsumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|count
operator|++
expr_stmt|;
comment|//System.err.println(msg.getText());
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumed "
operator|+
name|count
operator|+
literal|" from queue"
argument_list|)
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|MessageConsumer
name|topicConsumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"test"
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
operator|(
name|repeats
operator|==
literal|0
condition|?
literal|1000
else|:
name|numToSend
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|topicConsumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|count
operator|++
expr_stmt|;
comment|//System.err.println(msg.getText());
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumed "
operator|+
name|count
operator|+
literal|" from topic"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
