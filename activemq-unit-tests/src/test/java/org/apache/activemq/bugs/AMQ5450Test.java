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
name|HashMap
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
name|AMQ5450Test
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
name|AMQ5450Test
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
name|POSTFIX_DESTINATION_NAME
init|=
literal|".dlq"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DESTINATION_NAME
init|=
literal|"test"
operator|+
name|POSTFIX_DESTINATION_NAME
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DESTINATION_NAME_2
init|=
literal|"2.test"
operator|+
name|POSTFIX_DESTINATION_NAME
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DESTINATION_NAME_3
init|=
literal|"3.2.test"
operator|+
name|POSTFIX_DESTINATION_NAME
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|DESTS
init|=
operator|new
name|String
index|[]
block|{
name|DESTINATION_NAME
block|,
name|DESTINATION_NAME_2
block|,
name|DESTINATION_NAME_3
block|,
name|DESTINATION_NAME
block|,
name|DESTINATION_NAME
block|}
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|HashMap
argument_list|<
name|Object
argument_list|,
name|PersistenceAdapter
argument_list|>
name|adapters
init|=
operator|new
name|HashMap
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
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createAndStartBroker
parameter_list|(
name|PersistenceAdapter
name|persistenceAdapter
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
literal|false
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
return|return
name|broker
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostFixMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestPostFixMatch
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostFixCompositeMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestPostFixMatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTestPostFixMatch
parameter_list|(
name|boolean
name|useComposite
parameter_list|)
throws|throws
name|Exception
block|{
name|prepareBrokerWithMultiStore
argument_list|(
name|useComposite
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|DESTINATION_NAME
argument_list|,
literal|"test 1"
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|DESTINATION_NAME_2
argument_list|,
literal|"test 1"
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|DESTINATION_NAME_3
argument_list|,
literal|"test 1"
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
name|assertNotNull
argument_list|(
name|broker
operator|.
name|getDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|DESTINATION_NAME_3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|dest
range|:
name|DESTS
control|)
block|{
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
name|dest
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|destination2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|destination2
operator|.
name|getMessageStore
argument_list|()
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|HashMap
name|numDests
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|PersistenceAdapter
name|pa
range|:
name|adapters
operator|.
name|values
argument_list|()
control|)
block|{
name|numDests
operator|.
name|put
argument_list|(
name|pa
operator|.
name|getDestinations
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|pa
argument_list|)
expr_stmt|;
block|}
comment|// ensure wildcard does not match any
name|assertTrue
argument_list|(
literal|"0 in wildcard matcher"
argument_list|,
name|adapters
operator|.
name|get
argument_list|(
literal|null
argument_list|)
operator|.
name|getDestinations
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"only two values"
argument_list|,
literal|2
argument_list|,
name|numDests
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"0 in others"
argument_list|,
name|numDests
operator|.
name|containsKey
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|useComposite
condition|)
block|{
name|assertTrue
argument_list|(
literal|"3 in one"
argument_list|,
name|numDests
operator|.
name|containsKey
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"1 in some"
argument_list|,
name|numDests
operator|.
name|containsKey
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
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
literal|5000
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
return|return
name|kaha
return|;
block|}
specifier|public
name|void
name|prepareBrokerWithMultiStore
parameter_list|(
name|boolean
name|compositeMatch
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
name|multiKahaDBPersistenceAdapter
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|compositeMatch
condition|)
block|{
name|StringBuffer
name|compositeDestBuf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|DESTS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|i
condition|;
name|j
operator|++
control|)
block|{
name|compositeDestBuf
operator|.
name|append
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|j
operator|+
literal|1
operator|==
name|i
operator|)
condition|)
block|{
name|compositeDestBuf
operator|.
name|append
argument_list|(
name|POSTFIX_DESTINATION_NAME
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|compositeDestBuf
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
operator|(
name|i
operator|+
literal|1
operator|>
name|DESTS
operator|.
name|length
operator|)
condition|)
block|{
name|compositeDestBuf
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
block|}
name|adapters
operator|.
name|add
argument_list|(
name|createFilteredKahaDBByDestinationPrefix
argument_list|(
name|compositeDestBuf
operator|.
name|toString
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// destination map does not do post fix wild card matches on paths, so we need to cover
comment|// each path length
name|adapters
operator|.
name|add
argument_list|(
name|createFilteredKahaDBByDestinationPrefix
argument_list|(
literal|"*"
operator|+
name|POSTFIX_DESTINATION_NAME
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|adapters
operator|.
name|add
argument_list|(
name|createFilteredKahaDBByDestinationPrefix
argument_list|(
literal|"*.*"
operator|+
name|POSTFIX_DESTINATION_NAME
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|adapters
operator|.
name|add
argument_list|(
name|createFilteredKahaDBByDestinationPrefix
argument_list|(
literal|"*.*.*"
operator|+
name|POSTFIX_DESTINATION_NAME
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|adapters
operator|.
name|add
argument_list|(
name|createFilteredKahaDBByDestinationPrefix
argument_list|(
literal|"*.*.*.*"
operator|+
name|POSTFIX_DESTINATION_NAME
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// ensure wildcard matcher is there for other dests
name|adapters
operator|.
name|add
argument_list|(
name|createFilteredKahaDBByDestinationPrefix
argument_list|(
literal|null
argument_list|,
literal|true
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
name|broker
operator|=
name|createAndStartBroker
argument_list|(
name|multiKahaDBPersistenceAdapter
argument_list|)
expr_stmt|;
block|}
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
argument_list|)
expr_stmt|;
block|}
name|adapters
operator|.
name|put
argument_list|(
name|destinationPrefix
argument_list|,
name|template
operator|.
name|getPersistenceAdapter
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|template
return|;
block|}
specifier|private
name|void
name|sendMessage
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
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
name|s
operator|.
name|createProducer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|destinationName
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
name|message
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
name|c
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

